<template>
	<view class="ai-container">
		<scroll-view scroll-y class="messages-container" :scroll-top="scrollTop">
			<view v-for="(message, index) in messages" :key="index" 
				:class="['message', message.role === 'user' ? 'user-message' : 'ai-message']">
				<view class="message-content">
					<view v-if="message.role === 'assistant'" class="ai-avatar">
						<image src="/static/ai-avatar.jpg" mode="aspectFill"></image>
					</view>
					<view class="text-content">
						<view v-if="message.role === 'assistant'" class="ai-name">AI 助手</view>
						<template v-if="message.role === 'assistant' && message.reasoning">
							<view class="reasoning-section">
								<view class="reasoning-header">思考过程：</view>
								<view class="reasoning-content">{{message.reasoning}}</view>
							</view>
							<view class="content-section">
								<view class="content-header">回答：</view>
								<view class="message-text" v-html="formatMarkdown(message.content)"></view>
							</view>
						</template>
						<view v-else class="message-text" v-html="formatMarkdown(message.content)"></view>
					</view>
				</view>
			</view>
		</scroll-view>
		
		<view class="input-container">
			<view class="input-wrapper">
				<view class="deep-thinking-switch">
					<switch :checked="isDeepThinking" @change="e => isDeepThinking = e.detail.value" scale="0.8" />
					<text class="switch-label">深度思考</text>
				</view>
				<input type="text" 
					v-model="question" 
					placeholder="请输入您的问题" 
					:disabled="isLoading"
					class="input-box"
					@confirm="isLoading ? stopGenerating() : sendMessage()" 
				/>
				<button 
					@tap="isLoading ? stopGenerating() : sendMessage()" 
					:class="['action-button', isLoading ? 'stop-button' : 'send-button']"
				>
					{{isLoading ? '停止' : '发送'}}
				</button>
			</view>
		</view>
	</view>
</template>

<script>
	import { fetchDeepSeekStream, fetchDeepSeekDeepStream } from '@/utils/deepseek.js';
	
	export default {
		data() {
			return {
				question: '',
				messages: [],
				scrollTop: 0,
				isLoading: false,
				isDeepThinking: false,
				currentStopFn: null,
				lastQuestion: '',
				markdownCache: new Map(),
			};
		},
		methods: {
			formatMarkdown(text) {
				if (!text) return '';
				
				// 检查缓存
				const cached = this.markdownCache.get(text);
				if (cached) return cached;
				
				try {
					// 使用正则表达式的替换规则数组
					const rules = [
						// 代码块
						{
							regex: /```(\w*)\n([\s\S]*?)```/g,
							replace: (match, lang, code) => 
								`<div class="code-block"><div class="code-lang">${lang || 'text'}</div><pre class="markdown-code">${this.escapeHtml(code.trim())}</pre></div>`
						},
						// 行内代码
						{
							regex: /`([^`]+)`/g,
							replace: (match, code) => 
								`<code class="markdown-inline-code">${this.escapeHtml(code)}</code>`
						},
						// 标题
						{
							regex: /#{1,6}\s+([^\n]+)/g,
							replace: (match, content) => {
								const level = match.match(/^#+/)[0].length;
								return `<h${level} class="markdown-heading">${content.trim()}</h${level}>`;
							}
						},
						// 列表
						{
							regex: /^\s*[-*+]\s+([^\n]+)/gm,
							replace: '<li>$1</li>'
						},
						// 引用
						{
							regex: /^\s*>\s+([^\n]+)/gm,
							replace: '<blockquote>$1</blockquote>'
						},
						// 粗体
						{
							regex: /\*\*([^*]+)\*\*/g,
							replace: '<strong>$1</strong>'
						},
						// 斜体
						{
							regex: /\*([^*]+)\*/g,
							replace: '<em>$1</em>'
						}
					];
					
					// 应用所有规则
					let formattedText = text;
					for (const rule of rules) {
						if (typeof rule.replace === 'string') {
							formattedText = formattedText.replace(rule.regex, rule.replace);
						} else {
							formattedText = formattedText.replace(rule.regex, rule.replace);
						}
					}
					
					// 处理换行
					formattedText = formattedText.replace(/\n/g, '<br>');
					
					// 存入缓存
					this.markdownCache.set(text, formattedText);
					
					// 控制缓存大小
					if (this.markdownCache.size > 100) {
						const firstKey = this.markdownCache.keys().next().value;
						this.markdownCache.delete(firstKey);
					}
					
					return formattedText;
				} catch (e) {
					console.error('Markdown转换失败:', e);
					return text;
				}
			},
			
			escapeHtml(text) {
				const entityMap = {
					'&': '&amp;',
					'<': '&lt;',
					'>': '&gt;',
					'"': '&quot;',
					"'": '&#39;',
					'/': '&#x2F;',
					'`': '&#x60;',
					'=': '&#x3D;'
				};
				return text.replace(/[&<>"'`=\/]/g, s => entityMap[s]);
			},
			
			updateMessage(index, updates) {
				// 使用Vue的响应式方法更新消息
				this.messages = [
					...this.messages.slice(0, index),
					{ ...this.messages[index], ...updates },
					...this.messages.slice(index + 1)
				];
			},
			
			async sendMessage() {
				if (!this.question.trim() || this.isLoading) return;
				
				const userMessage = this.question.trim();
				this.lastQuestion = userMessage;
				
				// 添加用户消息
				this.messages = [...this.messages, {
					role: 'user',
					content: userMessage
				}];
				
				this.question = '';
				this.isLoading = true;
				await this.scrollToBottom();
				
				try {
					// 添加AI回答占位
					const aiMessage = {
						role: 'assistant',
						content: '',
						...(this.isDeepThinking ? { reasoning: '' } : {})
					};
					const aiMessageIndex = this.messages.length;
					this.messages = [...this.messages, aiMessage];
					
					// 开始流式请求
					this.currentStopFn = this.isDeepThinking
						? fetchDeepSeekDeepStream(
							userMessage,
							(result) => {
								console.log('收到内容更新:', result);
								this.updateMessage(aiMessageIndex, {
									reasoning: result.thinking,
									content: result.content
								});
								this.$nextTick(() => this.scrollToBottom());
							},
							() => {
								console.log('请求完成');
								this.isLoading = false;
								this.currentStopFn = null;
							},
							(error) => {
								console.error('请求失败:', error);
								this.updateMessage(aiMessageIndex, {
									content: '抱歉，请求失败了，请稍后重试。',
									error: true
								});
								uni.showToast({
									title: '请求失败',
									icon: 'none'
								});
								this.isLoading = false;
							}
						)
						: fetchDeepSeekStream(
							userMessage,
							(content) => {
								console.log('收到内容更新:', content);
								this.updateMessage(aiMessageIndex, {
									content: content
								});
								this.$nextTick(() => this.scrollToBottom());
							},
							() => {
								console.log('请求完成');
								this.isLoading = false;
								this.currentStopFn = null;
							},
							(error) => {
								console.error('请求失败:', error);
								this.updateMessage(aiMessageIndex, {
									content: '抱歉，请求失败了，请稍后重试。',
									error: true
								});
								uni.showToast({
									title: '请求失败',
									icon: 'none'
								});
								this.isLoading = false;
							}
						);
				} catch (error) {
					console.error('发起请求失败:', error);
					this.updateMessage(this.messages.length - 1, {
						content: '抱歉，发起请求失败，请检查网络连接。',
						error: true
					});
					uni.showToast({
						title: '发起请求失败',
						icon: 'none'
					});
					this.isLoading = false;
				}
			},
			
			stopGenerating() {
				if (this.currentStopFn) {
					this.currentStopFn();
					this.currentStopFn = null;
				}
				this.isLoading = false;
				this.question = this.lastQuestion;
			},
			
			scrollToBottom() {
				setTimeout(() => {
					this.scrollTop = 9999999;
				}, 100);
			}
		}
	}
</script>

<style lang="scss">
.ai-container {
	display: flex;
	flex-direction: column;
	height: 100vh;
	background-color: #f5f5f5;
	
	.messages-container {
		flex: 1;
		padding: 20rpx;
		padding-bottom: 140rpx;
		box-sizing: border-box;
		width: 100%;
		
		.message {
			margin-bottom: 20rpx;
			max-width: 94%;
			word-break: break-word;
			box-sizing: border-box;
			
			.message-content {
				display: flex;
				gap: 12rpx;
				max-width: 100%;
				box-sizing: border-box;
				
				.ai-avatar {
					margin-left: -8rpx;
					image {
						width: 60rpx;
						height: 60rpx;
						border-radius: 50%;
					}
				}
				
				.text-content {
					max-width: calc(100% - 64rpx);
					overflow-wrap: break-word;
					box-sizing: border-box;
					padding: 20rpx;
					border-radius: 16rpx;
				}
			}
			
			&.user-message {
				margin-left: auto;
				margin-right: 20rpx;
				max-width: 75%;
				
				.message-content {
					flex-direction: row-reverse;
					
					.text-content {
						background: #007AFF;
						color: white;
					}
				}
			}
			
			&.ai-message {
				margin-left: 12rpx;
				max-width: 94%;
			}
			
			.text-content {
				background: #fff;
				padding: 24rpx;
				border-radius: 24rpx;
				
				.ai-name {
					font-size: 28rpx;
					color: #666;
					margin-bottom: 8rpx;
				}
				
				.reasoning-section {
					margin-bottom: 24rpx;
					padding-bottom: 24rpx;
					border-bottom: 2rpx dashed #ddd;
					
					.reasoning-header {
						font-size: 24rpx;
						color: #666;
						margin-bottom: 8rpx;
					}
					
					.reasoning-content {
						color: #666;
						font-style: italic;
					}
				}
				
				.content-section {
					.content-header {
						font-size: 24rpx;
						color: #666;
						margin-bottom: 8rpx;
					}
				}
				
				.message-text {
					font-size: 28rpx;
					line-height: 1.6;
					
					.code-block {
						margin: 16rpx 0;
						border-radius: 8rpx;
						overflow: hidden;
						background: #f8f9fa;
						
						.code-lang {
							padding: 8rpx 16rpx;
							background: #e9ecef;
							color: #666;
							font-size: 24rpx;
							border-bottom: 2rpx solid #dee2e6;
						}
						
						.markdown-code {
							padding: 16rpx;
							margin: 0;
							background: transparent;
							border-radius: 0;
							font-family: Consolas, Monaco, 'Andale Mono', monospace;
							white-space: pre;
							word-wrap: normal;
							overflow-x: auto;
							tab-size: 4;
							-moz-tab-size: 4;
							-o-tab-size: 4;
							-webkit-tab-size: 4;
						}
					}
					
					.markdown-inline-code {
						background: #f8f9fa;
						padding: 4rpx 8rpx;
						border-radius: 4rpx;
						font-family: Consolas, Monaco, 'Andale Mono', monospace;
						color: #e83e8c;
					}
					
					.markdown-heading {
						margin: 24rpx 0 16rpx;
						font-weight: bold;
						
						&:first-child {
							margin-top: 0;
						}
					}
					
					ul {
						padding-left: 32rpx;
						margin: 16rpx 0;
						list-style-type: disc;
					}
					
					blockquote {
						border-left: 8rpx solid #ddd;
						padding-left: 16rpx;
						color: #666;
						margin: 16rpx 0;
					}
					
					strong {
						font-weight: bold;
					}
					
					em {
						font-style: italic;
					}
				}
			}
		}
	}
	
	.input-container {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background-color: #fff;
		border-top: 2rpx solid #eee;
		padding: 20rpx;
		
		.input-wrapper {
			display: flex;
			align-items: center;
			gap: 16rpx;
			
			.deep-thinking-switch {
				display: flex;
				align-items: center;
				gap: 8rpx;
				flex-shrink: 0;
				
				.switch-label {
					font-size: 24rpx;
					color: #666;
					white-space: nowrap;
				}
			}
			
			.input-box {
				flex: 1;
				height: 72rpx;
				background-color: #f5f5f5;
				border-radius: 36rpx;
				padding: 0 24rpx;
				font-size: 28rpx;
			}
			
			.action-button {
				width: 120rpx;
				height: 72rpx;
				line-height: 72rpx;
				text-align: center;
				border-radius: 36rpx;
				font-size: 28rpx;
				padding: 0;
				margin: 0;
				flex-shrink: 0;
				
				&.send-button {
					background-color: #007AFF;
					color: #fff;
					
					&:active {
						background-color: #0056b3;
					}
				}
				
				&.stop-button {
					background-color: #dc3545;
					color: #fff;
					
					&:active {
						background-color: #c82333;
					}
				}
				
				&:disabled {
					background-color: #ccc;
					color: #666;
				}
			}
		}
	}
}
</style>
