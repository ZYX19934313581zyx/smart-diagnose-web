<template>
  <div class="chat-page-wrap">
    <!-- 顶部栏 -->
    <div class="chat-header">
      <div>
        <el-button type="primary" size="small" @click="detailDialog = true">查看问诊资料</el-button>
      </div>
      <el-button @click="$router.back()">返回列表</el-button>
    </div>

    <!-- 问诊详情弹窗 -->
    <el-dialog v-model="detailDialog" title="问诊完整资料" width="650px">
      <el-descriptions border v-if="consultInfo">
        <el-descriptions-item label="科室">{{ consultInfo.department || '未选择' }}</el-descriptions-item>
        <el-descriptions-item label="匿名模式">
          {{ consultInfo.isAnonymous ? '开启匿名' : '不匿名' }}
        </el-descriptions-item>
        <el-descriptions-item label="症状描述" :span="2">{{ consultInfo.symptom || '-' }}</el-descriptions-item>
        <el-descriptions-item label="既往病史" :span="2">{{ consultInfo.diseaseHistory || '无' }}</el-descriptions-item>
        <el-descriptions-item label="手术史" :span="2">{{ consultInfo.operationHistory || '无' }}</el-descriptions-item>
        <el-descriptions-item label="过敏史" :span="2">{{ consultInfo.allergyHistory || '无' }}</el-descriptions-item>
        <el-descriptions-item label="症状附件图片" :span="2">
          <div v-if="consultInfo.imgUrl" class="img-preview-wrap">
            <el-image
              v-for="(img, index) in getImgList(consultInfo.imgUrl)"
              :key="index"
              style="width:120px;height:120px;margin-right:8px;margin-bottom:8px"
              :src="getImgSrc(img)"
              :preview-src-list="getPreviewList(consultInfo.imgUrl)"
              :initial-index="index"
              fit="cover"
            />
          </div>
          <span v-else>无上传附件</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 聊天容器 -->
    <div class="chat-scroll" ref="chatScroll">
      <template v-for="item in renderList" :key="item.key">
        <!-- 居中时间分割条 -->
        <div v-if="item.isTime" class="time-divider">
          <span class="time-text">{{ item.timeStr }}</span>
        </div>

        <!-- 消息项 -->
        <div v-else :class="['msg-item', item.msg.senderId == loginUserId ? 'self' : 'other']">
          <div class="avatar-circle">
            <!-- 规则：患者 + 匿名 = 不展示头像；医生 / 非匿名患者才加载头像 -->
            <el-image
              v-if="canShowAvatar(item.msg)"
              class="avatar-img"
              :src="getImgSrc(item.msg.avatar)"
              fit="cover"
            />
            <span v-else>{{ item.msg.senderType === 1 ? '医' : '患' }}</span>
          </div>
          <div class="msg-body">
            <!-- 名称渲染逻辑 -->
            <div class="msg-name">
              <span v-if="item.msg.senderType === 0">
                {{ consultInfo.isAnonymous ? '患者' : (item.msg.nickname || '患者') }}
              </span>
              <span v-else>
                {{ item.msg.realName || '医生' }}
              </span>
            </div>
            <div class="msg-bubble">{{ item.msg.content }}</div>
          </div>
        </div>
      </template>
    </div>

    <!-- 输入区域 -->
    <div class="chat-input-area">
      <el-input
        v-model="sendText"
        type="textarea"
        placeholder="输入消息内容..."
        @keyup.ctrl.enter="sendMsg"
      />
      <el-button type="primary" :loading="sending" @click="sendMsg">发送(Ctrl+Enter)</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getChatList, sendReply, getConsultDetail, setRead } from '@/api/consult'

const route = useRoute()
const consultId = Number(route.params.id || route.query.id)
const loginUserId = Number(localStorage.getItem('userId'))

const chatScroll = ref<HTMLElement|null>(null)
const chatList = ref<any[]>([])
const sendText = ref('')
const sending = ref(false)

// 问诊详情 & 弹窗控制
const detailDialog = ref(false)
const consultInfo = ref<any>(null)

// 判断是否允许展示真实头像
const canShowAvatar = (msg: any) => {
  // 如果是患者并且问诊开启匿名 → 禁止显示头像
  if (msg.senderType === 0 && consultInfo.value?.isAnonymous) {
    return false
  }
  // 其余情况：必须后端存在头像地址才渲染图片
  return !!msg.avatar
}

// 分割图片地址数组
const getImgList = (urlStr: string) => {
  if (!urlStr) return []
  return urlStr.split(',').filter(item => !!item.trim())
}

// 拼接完整预览地址数组
const getPreviewList = (urlStr: string) => {
  return getImgList(urlStr).map(u => getImgSrc(u))
}

// ==========【修改好的图片拼接函数】==========
const getImgSrc = (url:string) => {
  if(!url) return ''
  if(url.startsWith('http')) return url
  // 后端返回自带 /，直接拼接域名，不再额外添加斜杠
  return `http://localhost:8081${url}`
}

function formatDateTime(raw:string){
  if(!raw) return ''
  return raw.replace('T',' ').substring(0,16)
}

const renderList = computed(()=>{
  const res:any[] = []
  let prevTime:number|null = null
  const gap = 5 * 60 * 1000
  chatList.value.forEach((msg, idx)=>{
    const timeStr = formatDateTime(msg.createTime)
    const curr = new Date(msg.createTime).getTime()
    if(prevTime === null || curr - prevTime > gap){
      res.push({key:`t-${idx}`, isTime:true, timeStr})
    }
    res.push({key:`m-${msg.id}`, isTime:false, msg})
    prevTime = curr
  })
  return res
})

async function scrollBottom(){
  await nextTick()
  if(chatScroll.value){
    chatScroll.value.scrollTop = chatScroll.value.scrollHeight
  }
}

async function loadChat(){
  if(isNaN(consultId) || consultId <= 0) return
  const detailRes = await getConsultDetail(consultId)
  consultInfo.value = detailRes.data
  const chatRes = await getChatList(consultId)
  chatList.value = chatRes.data
  scrollBottom()
  // 进入聊天页面自动标记为已读
  await setRead(consultId)
}

async function sendMsg(){
  const text = sendText.value.trim()
  if(!text || sending.value) return
  sending.value = true
  try {
    const res: any = await sendReply({
      consultId,
      content: text,
      replyScope:1
    })
    if(res?.code === 200){
      sendText.value = ''
      await loadChat()
    }else{
      // 后端校验失败（如无权回复）时保留输入内容并提示
      ElMessage.error(res?.msg || '发送失败，请稍后重试')
    }
  }catch(e){
    ElMessage.error('网络异常，发送失败，请稍后重试')
  }finally {
    sending.value = false
  }
}

onMounted(()=>{
  loadChat()
})
</script>

<style scoped>
.chat-page-wrap{
  height:100vh;
  display:flex;
  flex-direction:column;
  background:#f5f7fa;
}
.chat-header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:12px 24px;
  background:#fff;
  border-bottom:1px solid #eee;
}
.chat-scroll{
  flex:1;
  overflow-y:auto;
  padding:24px 12%;
}
.time-divider{
  display:flex;
  justify-content:center;
  margin:16px 0;
}
.time-text{
  font-size:12px;
  color:#999;
  background:#e8edf2;
  padding:4px 10px;
  border-radius:6px;
}
.msg-item{
  display:flex;
  gap:12px;
  margin-bottom:18px;
}
.msg-item.self{
  flex-direction:row-reverse;
}
.avatar-circle{
  width:36px;
  height:36px;
  border-radius:50%;
  background:#409eff;
  color:#fff;
  display:flex;
  align-items:center;
  justify-content:center;
  flex-shrink:0;
  overflow:hidden;
}
.avatar-img{
  width:100%;
  height:100%;
}
.msg-body{
  max-width:55%;
}
.msg-item.self .msg-body{
  display:flex;
  flex-direction:column;
  align-items:flex-end;
}
.msg-name{
  font-size:12px;
  color:#999;
  margin-bottom:4px;
}
.msg-bubble{
  padding:10px 16px;
  border-radius:8px;
  background:#ffffff;
  box-shadow:0 1px 3px #00000014;
  word-break:break-word;
}
.msg-item.self .msg-bubble{
  background:#409eff;
  color:#fff;
}
.chat-input-area{
  display:flex;
  gap:12px;
  padding:16px 12%;
  background:#fff;
  border-top:1px solid #eee;
  align-items:flex-end;
}
.chat-input-area :deep(.el-textarea){
  flex:1;
}
.img-preview-wrap{
  display:flex;
  flex-wrap:wrap;
  gap:8px;
}
</style>