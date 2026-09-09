<template>
  <div>
    <el-card title="个人头像设置" shadow="hover" style="max-width:500px;margin-bottom:20px">
      <el-form label-width="120px">
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :show-file-list="false"
            :before-upload="handleBeforeUpload"
            :http-request="uploadAvatarHandler"
          >
            <img v-if="avatarUrl" :src="avatarUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card title="账号安全设置" shadow="hover">
      <el-form :model="form" label-width="120px" style="max-width:500px">
        <el-form-item label="原密码">
          <el-input v-model="form.oldPwd" type="password" show-password placeholder="请输入原密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.newPwd" type="password" show-password placeholder="请输入新密码"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="form.rePwd" type="password" show-password placeholder="请再次输入新密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="changePwd" :loading="loading">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { changePassword, uploadAvatar, updateUserAvatar } from '@/api/user'

const loading = ref(false)
const avatarLoading = ref(false)
const avatarUrl = ref('')

const form = ref({
  oldPwd: '',
  newPwd: '',
  rePwd: ''
})

const loadUserAvatar = () => {
  const userStr = localStorage.getItem('userInfo')
  if (userStr) {
    const userInfo = JSON.parse(userStr)
    if (userInfo.avatar) {
      avatarUrl.value = userInfo.avatar
    }
  }
}

const handleBeforeUpload = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件！')
    return false
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('图片大小不能超过10MB！')
    return false
  }
  return true
}

const uploadAvatarHandler = async (options: any) => {
  avatarLoading.value = true
  try {
    // 类型断言 as any，消除TS波浪线警告，业务逻辑完全不变
    const res = await uploadAvatar(options.file) as any
    if (res.code === 200) {
      const saveRes = await updateUserAvatar(res.data) as any
      if (saveRes.code === 200) {
        ElMessage.success('头像更新成功')
        const userStr = localStorage.getItem('userInfo')
        if (userStr) {
          const userObj = JSON.parse(userStr)
          userObj.avatar = res.data
          localStorage.setItem('userInfo', JSON.stringify(userObj))
          avatarUrl.value = res.data
        }
      } else {
        ElMessage.error(saveRes.msg || '保存头像到数据库失败')
      }
    } else {
      ElMessage.error(res.msg || '头像上传失败')
    }
  } catch {
    ElMessage.error('网络请求异常，上传失败')
  } finally {
    avatarLoading.value = false
  }
}

const changePwd = async () => {
  if (!form.value.oldPwd || !form.value.newPwd || !form.value.rePwd) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  if (form.value.newPwd !== form.value.rePwd) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  if (form.value.newPwd.length < 6 || form.value.newPwd.length > 16) {
    ElMessage.warning('新密码长度需要为6‑16位')
    return
  }
  loading.value = true
  try {
    const res = await changePassword({
      oldPwd: form.value.oldPwd,
      newPwd: form.value.newPwd
    }) as any
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      setTimeout(() => {
        localStorage.clear()
        window.location.href = '/login'
      }, 1000)
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  } catch (e) {
    ElMessage.error('网络请求异常，修改失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadUserAvatar()
})
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 50%;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  border: 1px dashed #dcdcdc;
  border-radius: 50%;
  display: block;
}
</style>