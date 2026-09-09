<template>
  <el-container class="doctor-layout">
    <el-aside width="220px">
      <div class="logo">医生工作台</div>
      <el-menu :default-active="$route.path" router>
        <el-menu-item index="/doctor/hall">
          <el-icon><ChatDotRound /></el-icon>
          <span>问诊大厅</span>
        </el-menu-item>
        <el-menu-item index="/doctor/list">
          <el-icon><List /></el-icon>
          <span>我的问诊列表</span>
        </el-menu-item>
        <el-menu-item index="/doctor/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
        <el-menu-item index="/doctor/apply">
          <el-icon><Document /></el-icon>
          <span>资质管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header>
        <div class="header-right">
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
          <el-button style="margin-left:16px" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ChatDotRound, List, User, Document, Bell } from '@element-plus/icons-vue'
import { getUnReadCount, getUnReadConsultList, setRead } from '@/api/consult'

const router = useRouter()
const unReadNum = ref<number>(0)
const unReadConsultIdList = ref<number[]>([])

// 加载未读消息数量和问诊ID列表
async function loadUnRead() {
  const countRes = await getUnReadCount() as any
  if(countRes.code === 200){
    unReadNum.value = countRes.data
  }
  const listRes = await getUnReadConsultList() as any
  if(listRes.code === 200){
    unReadConsultIdList.value = listRes.data
  }
}

// 点击消息跳转聊天页，标记已读并刷新红点
async function goChat(consultId: number) {
  await setRead(consultId) as any
  router.push(`/doctor/chat/${consultId}`)
  loadUnRead()
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

onMounted(()=>{
  loadUnRead()
  setInterval(loadUnRead,30000)
})
</script>

<style scoped>
.doctor-layout {
  height: 100vh;
}
.el-aside {
  background: #304156;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
}
.el-menu {
  border-right: none;
  background: #304156;
}
:deep(.el-menu-item) {
  color: #bfcbd9;
}
:deep(.el-menu-item.is-active) {
  background: #409eff;
  color: #fff;
}
.el-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right:24px;
}
.header-right{
  display:flex;
  align-items:center;
}
.el-main {
  background: #f0f2f5;
}
</style>