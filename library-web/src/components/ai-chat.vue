<template>
  <div class="ai-chat-container">
    <div class="messages-container" ref="messagesContainer">
      <div v-for="(message, index) in messages" :key="index" 
           :class="['message', message.role === 'user' ? 'user-message' : 'ai-message']">
        <div class="message-content">
          <div v-if="message.role === 'assistant'" class="ai-avatar">
            <img src="../assets/ai-avatar.jpg" alt="AI" />
          </div>
          <div class="text-content">
            <div v-if="message.role === 'assistant'" class="ai-name">AI 助手</div>
            <template v-if="message.role === 'assistant' && message.reasoning">
              <div class="reasoning-section">
                <div class="reasoning-header">思考过程：</div>
                <div class="message-text markdown-body reasoning-content" v-html="formatMessage(message.reasoning)"></div>
              </div>
              <div class="content-section">
                <div class="content-header">回答：</div>
                <div class="message-text markdown-body" v-html="formatMessage(message.content)"></div>
              </div>
            </template>
            <div v-else class="message-text markdown-body" v-html="formatMessage(message.content)"></div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="input-container">
      <div class="input-wrapper">
        <label class="deep-thinking-switch">
          <input type="checkbox" v-model="isDeepThinking">
          <span class="switch-label">深度思考</span>
        </label>
        <textarea
          v-model="userInput"
          @keydown.enter.prevent="handleEnterKey"
          placeholder="请输入您的问题..."
          :disabled="isLoading"
          rows="3"
        ></textarea>
      </div>
      <button 
        @click="isLoading ? stopGenerating() : sendMessage()" 
        :class="['action-button', isLoading ? 'stop-button' : 'send-button']"
      >
        {{ isLoading ? '停止' : '发送' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const messages = ref([])
const userInput = ref('')
const isLoading = ref(false)
const messagesContainer = ref(null)
const isDeepThinking = ref(false)
const abortController = ref(null)
const lastUserMessage = ref('')

// 配置 marked
marked.setOptions({
  breaks: true,
  gfm: true,
  headerIds: false,
  mangle: false,
})

// 格式化消息内容（支持 Markdown）
const formatMessage = (content) => {
  if (!content) return ''
  const htmlContent = marked(content)
  return DOMPurify.sanitize(htmlContent)
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

// 处理回车键
const handleEnterKey = (event) => {
  if (event.shiftKey) return // 允许 Shift+Enter 换行
  if (isLoading.value) {
    stopGenerating()
  } else {
    sendMessage()
  }
}

// 停止生成
const stopGenerating = () => {
  if (abortController.value) {
    abortController.value.abort()
    abortController.value = null
  }
  isLoading.value = false
  userInput.value = lastUserMessage.value // 恢复上一次的输入
}

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim() || isLoading.value) return

  const userMessage = userInput.value.trim()
  lastUserMessage.value = userMessage // 保存当前输入
  messages.value.push({
    role: 'user',
    content: userMessage
  })
  
  userInput.value = ''
  isLoading.value = true
  await scrollToBottom()

  try {
    console.log('开始发送请求:', userMessage)
    abortController.value = new AbortController() // 创建新的 AbortController

    const endpoint = isDeepThinking.value ? 'chatDeepStream' : 'chatStream'
    const response = await fetch(`http://110.40.159.129:8445/api/${endpoint}?question=${encodeURIComponent(userMessage)}`, {
      method: 'GET',
      headers: {
        'Accept': 'text/event-stream',
        'Cache-Control': 'no-cache',
        'Connection': 'keep-alive',
      },
      signal: abortController.value.signal // 添加 signal
    })

    if (!response.ok) {
      throw new Error(`请求失败: ${response.status} ${response.statusText}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let aiResponse = ''
    let reasoningResponse = ''
    let buffer = ''
    let isFirstChunk = true

    try {
      // eslint-disable-next-line no-constant-condition
      while (true) {
        const { done, value } = await reader.read()
        
        if (done) {
          console.log('流式响应结束')
          break
        }

        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')
        buffer = lines.pop() || ''

        for (const line of lines) {
          if (line.trim() === '') continue
          if (line.startsWith('data:')) {
            try {
              const jsonStr = line.slice(5).trim()
              if (!jsonStr) continue
              
              const data = JSON.parse(jsonStr)
              console.log('收到数据:', data)
              
              if (isDeepThinking.value) {
                if (data.choices && data.choices[0].delta.reasoning_content) {
                  reasoningResponse += data.choices[0].delta.reasoning_content
                  if (messages.value[messages.value.length - 1]?.role === 'assistant') {
                    messages.value[messages.value.length - 1].reasoning = reasoningResponse
                  } else if (isFirstChunk) {
                    messages.value.push({
                      role: 'assistant',
                      reasoning: reasoningResponse,
                      content: ''
                    })
                    isFirstChunk = false
                  }
                } else if (data.choices && data.choices[0].delta.content) {
                  aiResponse += data.choices[0].delta.content
                  if (messages.value[messages.value.length - 1]?.role === 'assistant') {
                    messages.value[messages.value.length - 1].content = aiResponse
                  }
                }
              } else {
                if (data.choices && data.choices[0].delta.content) {
                  aiResponse += data.choices[0].delta.content
                  if (messages.value[messages.value.length - 1]?.role === 'assistant') {
                    messages.value[messages.value.length - 1].content = aiResponse
                  } else if (isFirstChunk) {
                    messages.value.push({
                      role: 'assistant',
                      content: aiResponse
                    })
                    isFirstChunk = false
                  }
                }
              }
              
              await scrollToBottom()
            } catch (e) {
              console.error('解析响应数据失败:', e, '原始数据:', line)
            }
          }
        }
      }
    } catch (error) {
      if (error.name === 'AbortError') {
        console.log('用户中止了生成')
        return // 用户主动中止，不显示错误消息
      }
      throw error // 其他错误继续抛出
    }
  } catch (error) {
    console.error('请求错误:', error)
    if (error.name !== 'AbortError') {
      messages.value.push({
        role: 'assistant',
        content: `抱歉，发生了错误：${error.message}`
      })
    }
  } finally {
    isLoading.value = false
    abortController.value = null
    await scrollToBottom()
  }
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.message {
  margin-bottom: 20px;
  max-width: 80%;
}

.message-content {
  display: flex;
  gap: 12px;
}

.user-message {
  margin-left: auto;
}

.user-message .message-content {
  flex-direction: row-reverse;
}

.ai-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.text-content {
  background: #f5f5f5;
  padding: 12px 16px;
  border-radius: 12px;
}

.user-message .text-content {
  background: #007AFF;
  color: white;
}

.ai-name {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.reasoning-section {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px dashed #ddd;
}

.reasoning-header,
.content-header {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.reasoning-content {
  color: #666;
  font-style: italic;
}

.message-text {
  line-height: 1.5;
}

.input-container {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 12px;
}

.input-wrapper {
  flex: 1;
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.deep-thinking-switch {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 12px 0;
  white-space: nowrap;
}

.switch-label {
  font-size: 14px;
  color: #666;
}

textarea {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
}

textarea:focus {
  outline: none;
  border-color: #007AFF;
}

.action-button {
  padding: 0 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.send-button {
  background: #007AFF;
  color: white;
}

.send-button:hover {
  background: #0056b3;
}

.stop-button {
  background: #dc3545;
  color: white;
}

.stop-button:hover {
  background: #c82333;
}

.action-button:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* Markdown 样式 */
.markdown-body {
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif;
  font-size: 14px;
  line-height: 1.6;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4),
.markdown-body :deep(h5),
.markdown-body :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-body :deep(h1) { font-size: 1.5em; }
.markdown-body :deep(h2) { font-size: 1.3em; }
.markdown-body :deep(h3) { font-size: 1.2em; }

.markdown-body :deep(p) {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body :deep(code) {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27,31,35,0.05);
  border-radius: 3px;
  font-family: "SFMono-Regular", Consolas, "Liberation Mono", Menlo, Courier, monospace;
}

.markdown-body :deep(pre) {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 3px;
  margin-bottom: 16px;
}

.markdown-body :deep(pre code) {
  padding: 0;
  margin: 0;
  background-color: transparent;
  border: 0;
  word-break: normal;
  white-space: pre;
}

.markdown-body :deep(blockquote) {
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
  margin: 0 0 16px 0;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  padding-left: 2em;
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body :deep(table) {
  border-spacing: 0;
  border-collapse: collapse;
  margin-bottom: 16px;
}

.markdown-body :deep(table th),
.markdown-body :deep(table td) {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.markdown-body :deep(table tr) {
  background-color: #fff;
  border-top: 1px solid #c6cbd1;
}

.markdown-body :deep(table tr:nth-child(2n)) {
  background-color: #f6f8fa;
}
</style>
