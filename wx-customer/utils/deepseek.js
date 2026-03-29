export function fetchDeepSeekStream(question, onProgress, onComplete, onError, onAbort) {
	const API_URL = 'http://110.40.159.129:8445/api/chatStream';
 
	let isStopped = false;
	let buffer = '';
	let aiResponse = '';
 
	// #ifdef MP-WEIXIN
	const requestTask = uni.request({
		url: `${API_URL}?question=${encodeURIComponent(question)}`,
		enableChunked: true,
		responseType: 'arraybuffer',
		method: "GET",
		timeout: 300000,
		header: {
			'Accept': 'text/event-stream',
			'Cache-Control': 'no-cache',
			'Connection': 'keep-alive',
			'X-Requested-With': 'XMLHttpRequest'
		},
		success: (res) => {
			console.log("请求完成", res);
		},
		fail: (err) => {
			console.error("请求失败", err);
			onError && onError(err);
		}
	});
 
	requestTask.onChunkReceived((response) => {
		try {
			// 将ArrayBuffer转换为字符串
			const uint8Array = new Uint8Array(response.data);
			let text = String.fromCharCode.apply(null, uint8Array);
			text = decodeURIComponent(escape(text));
			console.log('接收到的数据:', text);
			
			// 处理缓冲区
			buffer += text;
			
			// 按行分割并处理每一行数据
			const lines = buffer.split('\n');
			buffer = lines.pop() || ''; // 保留最后一个可能不完整的行
			
			for (const line of lines) {
				const trimmedLine = line.trim();
				if (!trimmedLine) continue;
				
				if (trimmedLine === 'data:[DONE]') {
					console.log('接收完成');
					onComplete && onComplete(aiResponse);
					continue;
				}
				
				if (trimmedLine.startsWith('data:')) {
					try {
						const jsonStr = trimmedLine.slice(5).trim();
						const data = JSON.parse(jsonStr);
						console.log('解析的数据:', data);
						
						if (data.choices && data.choices[0].delta && data.choices[0].delta.content) {
							const content = data.choices[0].delta.content;
							aiResponse += content;
							console.log('当前累积的回答:', aiResponse);
							onProgress && onProgress(aiResponse);
						}
					} catch (e) {
						console.error('解析JSON失败:', e, '原始数据:', trimmedLine);
					}
				}
			}
		} catch (error) {
			console.error("处理数据块失败:", error);
			onError && onError(error);
		}
	});
	// #endif
 
	return () => {
		console.log('中断请求...');
		isStopped = true;
		requestTask.abort();
		onAbort && onAbort();
	};
}

export function fetchDeepSeekDeepStream(question, onProgress, onComplete, onError, onAbort) {
	const API_URL = 'http://110.40.159.129:8445/api/chatDeepStream';
 
	let isStopped = false;
	let buffer = '';
	let aiResponse = {
		thinking: '',
		content: ''
	};
 
	// #ifdef MP-WEIXIN
	const requestTask = uni.request({
		url: `${API_URL}?question=${encodeURIComponent(question)}`,
		enableChunked: true,
		responseType: 'arraybuffer',
		method: "GET",
		timeout: 30000,
		header: {
			'Accept': 'text/event-stream',
			'Cache-Control': 'no-cache',
			'Connection': 'keep-alive',
			'X-Requested-With': 'XMLHttpRequest'
		},
		success: (res) => {
			console.log("请求完成", res);
		},
		fail: (err) => {
			console.error("请求失败", err);
			onError && onError(err);
		}
	});
 
	requestTask.onChunkReceived((response) => {
		try {
			// 将ArrayBuffer转换为字符串
			const uint8Array = new Uint8Array(response.data);
			let text = String.fromCharCode.apply(null, uint8Array);
			text = decodeURIComponent(escape(text));
			console.log('接收到的数据:', text);
			
			// 处理缓冲区
			buffer += text;
			
			// 按行分割并处理每一行数据
			const lines = buffer.split('\n');
			buffer = lines.pop() || ''; // 保留最后一个可能不完整的行
			
			for (const line of lines) {
				const trimmedLine = line.trim();
				if (!trimmedLine) continue;
				
				if (trimmedLine === 'data:[DONE]') {
					console.log('接收完成');
					onComplete && onComplete(aiResponse);
					continue;
				}
				
				if (trimmedLine.startsWith('data:')) {
					try {
						const jsonStr = trimmedLine.slice(5).trim();
						const data = JSON.parse(jsonStr);
						console.log('解析的数据:', data);
						
						if (data.choices && data.choices[0].delta) {
							const delta = data.choices[0].delta;
							
							if (delta.reasoning_content) {
								aiResponse.thinking += delta.reasoning_content;
							}
							if (delta.content) {
								aiResponse.content += delta.content;
							}
							
							if (delta.reasoning_content || delta.content) {
								console.log('当前累积的回答:', aiResponse);
								onProgress && onProgress({
									thinking: aiResponse.thinking,
									content: aiResponse.content
								});
							}
						}
					} catch (e) {
						console.error('解析JSON失败:', e, '原始数据:', trimmedLine);
					}
				}
			}
		} catch (error) {
			console.error("处理数据块失败:", error);
			onError && onError(error);
		}
	});
	// #endif
 
	return () => {
		console.log('中断请求...');
		isStopped = true;
		requestTask.abort();
		onAbort && onAbort();
	};
} 