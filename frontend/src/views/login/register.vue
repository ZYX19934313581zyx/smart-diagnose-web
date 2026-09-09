<template>
  <div class="register-wrap">
    <el-card class="register-card" shadow="hover">
      <h2 class="title">账号注册</h2>
      <div class="form-box">
        <el-form :model="form" label-width="110px" label-align="right">
          <el-form-item label="用户名">
            <el-input v-model="form.username" placeholder="请设置登录账号" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="form.password" type="password" placeholder="密码长度6‑16位" show-password />
          </el-form-item>
          <el-form-item label="再次输入密码">
            <el-input v-model="form.rePassword" type="password" placeholder="再次输入密码" show-password />
          </el-form-item>
          <el-form-item label="身份">
            <el-select v-model="form.role" placeholder="请选择身份" style="width:100%">
              <el-option label="患者" value="PATIENT"></el-option>
              <el-option label="医生" value="DOCTOR"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" style="width:100%" @click="handleRegister" :loading="loading">注册</el-button>
          </el-form-item>
          <el-form-item>
            <el-button style="width:100%" @click="$router.push('/login')">返回登录</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const form = ref({
  username: '',
  password: '',
  rePassword: '',
  role: ''
})

const handleRegister = async () => {
  // 用户名校验：中文、英文、数字、下划线，长度1‑16
  const usernameReg = /^[\u4e00-\u9fa5a-zA-Z0-9_]{1,16}$/
  if (!usernameReg.test(form.value.username)) {
    ElMessage.warning('用户名支持中文、英文、数字、下划线，长度1‑16位')
    return
  }

  // 1.非空判断
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('用户名和密码不能为空')
    return
  }
  // 判断身份是否选择
  if (!form.value.role) {
    ElMessage.warning('请选择注册身份')
    return
  }
  // 2.密码长度限制：6~16位
  if(form.value.password.length < 6 || form.value.password.length > 16){
    ElMessage.warning('密码长度必须在6‑16位之间')
    return
  }
  // 3.两次密码一致校验
  if(form.value.password !== form.value.rePassword){
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  
  loading.value = true
  try {
    const res: any = await register(form.value)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.msg || '注册失败')
    }
  } catch (e) {
    ElMessage.error('注册请求异常')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-wrap {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
}
.register-card {
  width: 500px;
}
.form-box {
  padding:16px 20px;
}
.title {
  text-align: center;
  margin-bottom: 24px;
  font-size: 28px;
  font-weight: 700;
  color: #000000 !important;
}
/* 深度选择器，强制禁止label换行 */
:deep(.el-form-item__label) {
  white-space: nowrap !important;
}
</style>