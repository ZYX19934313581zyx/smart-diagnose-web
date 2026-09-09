<template>
  <el-card title="个人资料" shadow="hover">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="120px"
      style="max-width:600px"
    >
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入昵称"></el-input>
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
      </el-form-item>
      <el-form-item label="性别">
        <el-radio-group v-model="form.gender">
          <el-radio label="男">男</el-radio>
          <el-radio label="女">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="年龄">
        <el-input-number v-model="form.age" :min="0" :max="150"></el-input-number>
      </el-form-item>
      <el-form-item label="既往病史">
        <el-input v-model="form.diseaseHistory" type="textarea" rows="3" placeholder="填写既往疾病记录"></el-input>
      </el-form-item>
      <el-form-item label="手术史">
        <el-input v-model="form.operationHistory" type="textarea" rows="3" placeholder="填写过往手术记录"></el-input>
      </el-form-item>
      <el-form-item label="药物过敏史">
        <el-input v-model="form.allergy" type="textarea" rows="3" placeholder="填写药物、食物过敏情况"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save" :loading="loading">保存修改</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElForm } from 'element-plus'
import { getPatientProfile, savePatientProfile } from '@/api/user'

const formRef = ref<InstanceType<typeof ElForm>>()
const loading = ref(false)

const form = ref({
  nickname: '',
  phone: '',
  gender: '男',
  age: 18,
  diseaseHistory: '',
  operationHistory: '',
  allergy: ''
})

// 手机号校验规则：非必填，填写则必须是合法11位手机号
const rules = ref({
  phone: [
    {
      validator: (_: any, value: string, callback: any) => {
        if (!value) {
          callback()
          return
        }
        const reg = /^1[3-9]\d{9}$/
        if (!reg.test(value)) {
          callback(new Error('请输入正确的11位手机号码'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

const loadInfo = async () => {
  try {
    const res: any = await getPatientProfile()
    if (res.code === 200 && res.data) {
      form.value = res.data
    }
  } catch {
    ElMessage.warning('加载个人资料失败')
  }
}

onMounted(() => {
  loadInfo()
})

const save = async () => {
  // 先执行前端表单校验
  await formRef.value?.validate()
  loading.value = true
  try {
    const res: any = await savePatientProfile(form.value)
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (e) {
    ElMessage.error('请求异常，保存失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>

</style>