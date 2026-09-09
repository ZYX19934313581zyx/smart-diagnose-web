<template>
  <el-container style="height:100vh;width:100%">
    <!-- 侧边栏紧贴屏幕最左侧 -->
    <el-aside width="220px" style="background-color: #304156;">
      <el-menu
        router
        default-active="$route.path"
        background-color="#304156"
        text-color="#ffffff"
        active-text-color="#409eff"
        border-right="none"
      >
        <el-menu-item index="/admin/dashboard">系统审计工作台</el-menu-item>
        <div style="border-bottom:1px solid #48576a;"></div>
        <el-menu-item index="/admin/doctor">医生信息管理</el-menu-item>
        <div style="border-bottom:1px solid #48576a;"></div>
        <el-menu-item index="/admin/patient">患者信息管理</el-menu-item>
        <div style="border-bottom:1px solid #48576a;"></div>
        <el-menu-item index="/admin/doctorAudit">医生资质审批</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container style="height:100vh;">
      <!-- 头部去掉下边框，和下方内容无缝连接 -->
      <el-header style="background:#ffffff; border:none; padding:0 24px; display:flex; justify-content:space-between; align-items:center; height:60px;">
        <span style="font-size:16px;color:#333;">管理后台</span>
        <el-button type="text" @click="logout">退出登录</el-button>
      </el-header>
      <!-- 主内容区清除内边距，白底铺满 -->
      <el-main style="padding:0; background:#ffffff;">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const logout = () => {
  localStorage.removeItem('token')
  router.push('/login')
}
</script>