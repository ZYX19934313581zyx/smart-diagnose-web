<template>
  <el-container style="height: 100vh">
    <el-header style="display:flex;justify-content:space-between;align-items:center;background:#fff;border-bottom:1px solid #eee;padding:0 30px">
      <div style="font-size:20px;font-weight:bold">线上问诊平台</div>
      <div style="display:flex;gap:24px;align-items:center">
        <el-link type="primary" @click="$router.push('/user/hall')">问诊大厅</el-link>
        <el-link type="primary" @click="$router.push('/user/publish')">发布问诊</el-link>
        <el-link type="primary" @click="$router.push('/user/ai')">AI智能问诊</el-link>

        <!-- 下拉个人菜单，替换原来单独的【我的问诊】 -->
        <el-dropdown @command="handleCommand">
          <span style="color:#409eff;cursor:pointer">我 ▾</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="myConsult">我的问诊记录</el-dropdown-item>
              <el-dropdown-item command="profile">个人资料</el-dropdown-item>
              <el-dropdown-item command="setting">账号设置</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <!-- 铃铛通知下拉弹窗 -->
        <el-dropdown trigger="click" @command="goChat">
          <el-badge :value="unReadNum" :hidden="unReadNum === 0">
            <el-icon size="22"><Bell /></el-icon>
          </el-badge>
          <template #dropdown>
            <el-dropdown-menu>
              <div v-if="unReadConsultIdList.length === 0" style="padding:10px 16px;color:#999">暂无新消息</div>
              <el-dropdown-item v-for="cid in unReadConsultIdList" :key="cid" :command="cid">
                问诊ID：{{ cid }}（有新回复）
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>

        <el-button size="small" type="danger" @click="logout">退出登录</el-button>
      </div>
    </el-header>
    <el-main style="background:#f5f7fa;overflow:auto">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { getUnReadCount, getUnReadConsultList, setRead } from '@/api/consult'
import { useRouter } from 'vue-router'

const router = useRouter()
const unReadNum = ref<number>(0)
const unReadConsultIdList = ref<number[]>([])

// 加载未读数量 + 未读问诊ID列表
async function loadUnRead() {
  // 获取红点数字
  const countRes = await getUnReadCount() as any
  if(countRes.code === 200){
    unReadNum.value = countRes.data
  }
  // 获取带未读消息的问诊id
  const listRes = await getUnReadConsultList() as any
  if(listRes.code === 200){
    unReadConsultIdList.value = listRes.data
  }
}

// 点击消息项，跳转聊天页面，并且标记为已读
async function goChat(consultId: number) {
  // 请求接口标记这条问诊全部消息为已读
  await setRead(consultId) as any
  // 跳转对话页面
  router.push(`/user/chat/${consultId}`)
  // 刷新未读状态，清除红点
  loadUnRead()
}

// 下拉菜单跳转逻辑
const handleCommand = (val: string) => {
  if (val === 'myConsult') router.push('/user/my')
  if (val === 'profile') router.push('/user/profile')
  if (val === 'setting') router.push('/user/setting')
}

function logout() {
  localStorage.clear()
  ElMessage.success('已退出')
  router.push('/login')
}

onMounted(()=>{
  loadUnRead()
  // 30秒自动轮询刷新消息
  setInterval(loadUnRead,30000)
})
</script>

<style scoped>
.el-header{
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
</style>