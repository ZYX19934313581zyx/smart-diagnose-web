<template>
  <div class="ai-chat-page">
    <!-- 头部标题栏 -->
    <div class="chat-header">
      <div class="header-avatar">AI</div>
      <div class="header-info">
        <div class="title">AI智能问诊助手</div>
        <div class="desc">仅供健康参考，不能替代线下医师诊断</div>
      </div>
    </div>

    <!-- 对话滚动区域 -->
    <div class="chat-container" ref="chatBox">
      <div v-for="item in msgList" :key="item.id" class="msg-wrap">
        <!-- AI消息 居左 -->
        <div v-if="item.role === 'ai'" class="ai-message">
          <div class="ai-avatar">AI</div>
          <div class="ai-bubble">
            <span v-if="item.loadingDots && !item.content">...</span>
            <span v-html="formatText(item.content)"></span>
            <span v-if="item.typing && item.content" class="cursor-blink">|</span>
          </div>
        </div>
        <!-- 用户消息 居右 -->
        <div v-else class="user-message">
          <div class="user-bubble">{{ item.content }}</div>
          <div class="user-avatar">我</div>
        </div>
      </div>
    </div>

    <!-- 底部输入区域 -->
    <div class="chat-input-area">
      <el-input
        v-model="inputText"
        type="textarea"
        :rows="2"
        placeholder="请描述你的身体症状，Ctrl + Enter 发送"
        resize="none"
        @keydown.ctrl.enter="sendAiMsg"
        :disabled="loading"
        class="chat-textarea"
      />
      <div class="btn-row">
        <el-button type="primary" @click="sendAiMsg" :loading="loading" class="send-btn">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, onBeforeUnmount } from 'vue'

const chatBox = ref<HTMLElement | null>(null)
const inputText = ref('')
const loading = ref(false)
let eventSource: EventSource | null = null

const msgList = ref([
  {
    id: 1,
    role: 'ai',
    content: '您好！我是AI问诊助手，请描述您身体哪里不舒服，我会给您一些初步参考建议。\n温馨提示：AI内容仅供健康参考，不能替代专业医生诊断，急症请及时前往医院就诊。',
    typing: false,
    loadingDots: false
  }
])

// 换行处理
const formatText = (str: string) => {
  return str.replace(/\n/g, '<br/>')
}

// 自动滚动到底部
const scrollBottom = async () => {
  await nextTick()
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight
  }
}

async function sendAiMsg() {
  if (!inputText.value.trim()) {
    alert('请输入症状描述')
    return
  }
  // 如果上一轮流还在连接，先关闭
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }

  const sendVal = inputText.value.trim()
  // 添加用户消息
  msgList.value.push({
    id: Date.now(),
    role: 'user',
    content: sendVal,
    typing: false,
    loadingDots: false
  })
  // 新建空白AI消息：初始状态显示省略号
  const aiMsgItem: any = {
    id: Date.now() + 1,
    role: 'ai',
    content: '',
    typing: true,
    loadingDots: true
  }
  msgList.value.push(aiMsgItem)

  inputText.value = ''
  loading.value = true
  await scrollBottom()

  // 建立SSE长连接，流式接收文字
  const url = `/api/consult/ai/chat/stream?msg=${encodeURIComponent(sendVal)}`
  eventSource = new EventSource(url)

  // 收到第一条分片，关闭省略号，开始拼接文字
  eventSource.onmessage = (e) => {
    aiMsgItem.loadingDots = false
    aiMsgItem.content += e.data
    scrollBottom()
  }

  // 流结束事件
  eventSource.addEventListener('end', () => {
    aiMsgItem.typing = false
    aiMsgItem.loadingDots = false
    loading.value = false
    eventSource?.close()
    eventSource = null
    scrollBottom()
  })

  eventSource.onerror = () => {
    aiMsgItem.typing = false
    aiMsgItem.loadingDots = false
    alert('AI服务连接失败，请重新登录')
    loading.value = false
    eventSource?.close()
    eventSource = null
  }
}

// 页面卸载时关闭连接
onBeforeUnmount(() => {
  if (eventSource) {
    eventSource.close()
    eventSource = null
  }
})
</script>

<style scoped>
.ai-chat-page {
  display: flex;
  flex-direction: column;
  width: 100%;
  height: calc(100vh - 60px);
  overflow: hidden;
  background: #ffffff;
}
/* 头部 */
.chat-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  border-bottom: 1px solid #eee;
  background-color: #fafafa;
}
.header-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #722ed1;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size:14px;
}
.header-info .title {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}
.header-info .desc {
  font-size: 12px;
  color: #888;
  margin-top: 2px;
}
/* 对话容器 */
.chat-container {
  flex: 1;
  padding: 14px 20px;
  overflow-y: auto;
  background-color: #f7f8fa;
}
.msg-wrap {
  margin-bottom: 14px;
}
/* AI消息 */
.ai-message {
  display: flex;
  gap: 10px;
}
.ai-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #722ed1;
  color: white;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size:13px;
}
.ai-bubble {
  max-width: 68%;
  max-height: 55vh;
  overflow-y:auto;
  padding: 10px 14px;
  background: #ffffff;
  border-radius: 0 12px 12px 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.08);
  white-space: pre-wrap;
  color:#333;
  line-height:1.55;
  font-size:14px;
}
/* 打字光标闪烁 */
.cursor-blink {
  display: inline-block;
  width: 2px;
  height: 15px;
  background: #722ed1;
  margin-left: 2px;
  animation: blink 1s infinite;
  vertical-align: middle;
}
@keyframes blink {
  0%, 50% { opacity: 1; }
  51%, 100% { opacity: 0; }
}
/* 用户消息 */
.user-message {
  display: flex;
  gap:10px;
  justify-content: flex-end;
}
.user-bubble {
  max-width: 68%;
  padding:10px 14px;
  background: #409eff;
  color:#fff;
  border-radius: 12px 0 12px 12px;
  line-height:1.55;
  font-size:14px;
}
.user-avatar {
  width:32px;
  height:32px;
  border-radius:50%;
  background:#67c23a;
  color:#fff;
  flex-shrink:0;
  display:flex;
  align-items:center;
  justify-content:center;
  font-size:13px;
}
/* 输入区域 */
.chat-input-area {
  padding:12px 20px;
  border-top:1px solid #eee;
  background:#fff;
}
.chat-textarea :deep(.el-textarea__inner) {
  border-radius:10px;
  background:#f7f8fa;
  border:1px solid #e5e7eb;
  min-height:44px;
}
.btn-row {
  display:flex;
  justify-content:flex-end;
  margin-top:8px;
}
.send-btn {
  padding: 6px 20px;
  font-size:14px;
}
</style>