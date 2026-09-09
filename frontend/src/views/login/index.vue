<template>
  <div class="login-wrap">
    <el-card class="login-card" shadow="hover">
      <h2 class="title">登录</h2>
      <div class="form-box">
        <el-form :model="form" label-width="68px" label-align="right">
          <el-form-item label="账号">
            <el-input v-model="form.username" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width:100%" @click="handleLogin" :loading="loading">
              登录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="register-tip">
          <el-link type="primary" @click="$router.push('/register')">没有账号？去注册</el-link>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '@/api/admin'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }
  loading.value = true
  try {
    const res: any = await login(form.value)
    if (res.code === 200) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))
      localStorage.setItem('userId', res.data.userInfo.id)
      ElMessage.success('登录成功')

      const role = res.data.userInfo.role
      // 兼容后端小写格式
      if (role === 'PATIENT' || role === 'patient') {
        router.push('/user/hall')
      } else if (role === 'DOCTOR' || role === 'doctor') {
        router.push('/doctor/apply')
      } else if (role === 'ADMIN' || role === 'admin') {
        router.push('/admin/dashboard')
      }
    } else {
      ElMessage.error(res.msg || '登录失败')
    }
  } catch (e) {
    ElMessage.error('登录请求异常')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
}
.login-card {
  width: 420px;
}
.form-box {
  padding: 16px 20px;
}
.title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 28px;
  font-weight: 700;
  color: #000000 !important;
}
.register-tip {
  margin-top: 12px;
  text-align: right;
}
</style>