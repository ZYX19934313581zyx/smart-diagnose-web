<template>
  <div class="publish-page">
    <!-- 急诊风险弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="急诊风险提示"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <p style="color:#f56c6c;font-size:15px">
        若您存在急症、突发重伤、剧烈胸痛、呼吸困难等紧急情况，请立刻前往线下医院就诊，线上问诊不能替代急诊救治。
      </p>
      <template #footer>
        <el-button type="primary" :disabled="count > 0" @click="confirmTip">
          {{ count > 0 ? `请等待 ${count} 秒` : '我已阅读并知晓' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 问诊表单 -->
    <div v-if="showForm">
      <el-card title="发布问诊咨询" shadow="hover">
        <el-form :model="form" label-width="120px" ref="formRef" :rules="rules">
          <!-- 匿名 -->
          <el-form-item label="匿名发布">
            <el-switch v-model="form.anonymous" active-text="开启匿名（他人无法看到你的身份）"></el-switch>
          </el-form-item>

          <!-- 科室（从后端拉取，可不选） -->
          <el-form-item label="选择科室">
            <el-select v-model="form.department" placeholder="不知道属于哪个科室可以不选" clearable style="width:100%">
              <el-option
                v-for="item in departmentList"
                :key="item.id"
                :label="item.name"
                :value="item.name"
              />
            </el-select>
          </el-form-item>

          <!-- 快捷症状勾选（可不选） -->
          <el-form-item label="常见症状">
            <el-checkbox-group v-model="form.quickSymptoms">
              <el-checkbox v-for="s in symptomList" :key="s" :label="s">{{ s }}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>

          <!-- 详细描述（必填） -->
          <el-form-item label="症状描述" prop="description">
            <el-input v-model="form.description" type="textarea" rows="5" placeholder="请详细描述你的身体情况（必填）"></el-input>
          </el-form-item>

          <!-- 既往病史 -->
          <el-form-item label="既往病史">
            <el-input v-model="form.diseaseHistory" type="textarea" rows="2" placeholder="填写既往疾病记录（选填）"></el-input>
          </el-form-item>

          <!-- 手术史 -->
          <el-form-item label="手术史">
            <el-input v-model="form.operationHistory" type="textarea" rows="2" placeholder="填写过往手术记录（选填）"></el-input>
          </el-form-item>

          <!-- 过敏史 + 一键填充按钮 -->
          <el-form-item label="药物过敏史">
            <el-input v-model="form.allergy" type="textarea" rows="2" placeholder="药物、食物过敏等（选填）"></el-input>
            <el-button link type="primary" @click="autoFillHistory" style="margin-top:4px">从个人信息自动填入三项病史</el-button>
          </el-form-item>

          <!-- 附件上传（阿里云OSS上传） -->
          <el-form-item label="症状图片">
            <el-upload
              :http-request="handleUpload"
              list-type="picture-card"
              :file-list="uploadFileList"
              :limit="6"
              @remove="handleRemoveFile"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </el-form-item>

          <!-- 提交按钮 -->
          <el-form-item>
            <el-button type="primary" @click="submit" :loading="loading">提交问诊</el-button>
            <el-button @click="$router.back()">返回</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 发布范围选择弹窗（仅前端预览，此时数据库无数据） -->
      <el-dialog v-model="afterSubmitVisible" title="选择发布方式" width="500px">
        <!-- AI分析 -->
        <el-form-item label="是否需要AI分析">
          <el-radio-group v-model="aiOption">
            <el-radio label="yes">让AI分析症状并推荐医生</el-radio>
            <el-radio label="no">不需要</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- AI分析中提示 -->
        <el-card v-if="aiLoading" style="margin-bottom:16px;background:#f5f7fa">
          <p style="color:#909399;font-size:14px">AI正在分析症状并匹配科室医生，请稍候...</p>
        </el-card>

        <!-- AI分析结果展示 -->
        <el-card v-if="aiResult" style="margin-bottom:16px;background:#f5f7fa">
          <h4>AI分析建议：</h4>
          <p>{{ aiResult.aiAnalysis }}</p>
          <h4>推荐医生：</h4>
          <el-tag v-for="d in aiResult.doctorList" :key="d.userId" style="margin-right:8px">
            {{ d.realName }}（{{ d.department }}）
          </el-tag>
        </el-card>

        <!-- 发布范围 -->
        <el-form-item label="发送至">
          <el-radio-group v-model="publishTarget">
            <el-radio label="hall">问诊大厅（所有医生可见）</el-radio>
            <el-radio label="doctor">指定医生一对一发送</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 选择指定医生 -->
        <el-form-item v-if="publishTarget === 'doctor'" label="选择医生">
          <el-select v-model="selectedDoctor" placeholder="请选择医生" style="width:100%">
            <el-option
              v-for="d in doctorList"
              :key="d.userId"
              :label="`${d.realName} - ${d.department}`"
              :value="d.userId"
            ></el-option>
          </el-select>
        </el-form-item>

        <!-- 大厅是否公开 -->
        <el-form-item v-if="publishTarget === 'hall'" label="是否公开">
          <el-radio-group v-model="isPublic">
            <el-radio label="public">公开（医生和患者都可见）</el-radio>
            <el-radio label="private">不公开（仅医生可见，患者不可见）</el-radio>
          </el-radio-group>
        </el-form-item>

        <template #footer>
          <el-button @click="afterSubmitVisible = false">返回修改</el-button>
          <el-button type="primary" @click="confirmPublish" :loading="publishLoading">确认发布</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { addConsult, getDeptList, getAiPreRecommend } from '@/api/consult'
import { uploadFile } from '@/api/user'
import { getPassDoctorList } from '@/api/doctor'
import { ElMessage, ElForm } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const publishLoading = ref(false)
const formRef = ref<InstanceType<typeof ElForm>>()

// 科室下拉数据源
const departmentList = ref<any[]>([])
// 真实后端医生列表
const doctorList = ref<any[]>([])

// 上传展示列表
const uploadFileList = ref<any[]>([])
// 后端返回永久图片路径数组
const uploadedUrlList = ref<string[]>([])

// 弹窗控制
const dialogVisible = ref(true)
const showForm = ref(false)
const count = ref(5)
let timer: number | null = null

const userJson = localStorage.getItem('userInfo')
const loginUser = userJson ? JSON.parse(userJson) : {}
const realUserId = ref<number | null>(loginUser.id ?? null)

// 加载科室字典
const loadDepartment = async () => {
  const res = await getDeptList()
  departmentList.value = res.data
}

// 加载全部审核通过医生
const loadPassDoctor = async () => {
  const res: any = await getPassDoctorList()
  if (res.code === 200) {
    doctorList.value = res.data
  }
}

onMounted(async () => {
  await loadDepartment()
  await loadPassDoctor()
  timer = window.setInterval(() => {
    count.value--
    if (count.value <= 0 && timer) clearInterval(timer)
  }, 1000)
})

const confirmTip = () => {
  dialogVisible.value = false
  showForm.value = true
}

// 删除图片同步移除OSS地址
const handleRemoveFile = (fileItem: any) => {
  const delUrl = fileItem.url
  uploadedUrlList.value = uploadedUrlList.value.filter(url => url !== delUrl)
}

// 自定义阿里云OSS上传，文件夹为consult
async function handleUpload(options: any) {
  try {
    const res: any = await uploadFile(options.file, 'consult')
    if (res.code === 200) {
      uploadedUrlList.value.push(res.data)
      uploadFileList.value.push({ name: options.file.name, url: res.data })
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch {
    ElMessage.error('图片上传异常')
  }
}

// 快捷症状列表
const symptomList = ['头痛', '发热', '咳嗽', '腹痛', '腹泻', '恶心', '乏力', '失眠', '皮疹', '关节痛']

// 表单数据
const form = ref({
  anonymous: false,
  department: '',
  quickSymptoms: [] as string[],
  description: '',
  diseaseHistory: '',
  operationHistory: '',
  allergy: ''
})

// 表单校验
const rules = {
  description: [{ required: true, message: '请填写症状描述', trigger: 'blur' }]
}

// 一键填充三项病史
const autoFillHistory = () => {
  const userStr = localStorage.getItem('userInfo')
  if (userStr) {
    const user = JSON.parse(userStr)
    if(user.diseaseHistory) form.value.diseaseHistory = user.diseaseHistory
    if(user.operationHistory) form.value.operationHistory = user.operationHistory
    if(user.allergy) form.value.allergy = user.allergy
    ElMessage.success('✅三项病史自动填充完成')
  } else {
    ElMessage.warning('未找到个人信息，请先完善个人资料')
  }
}

// 提交后弹窗相关
const afterSubmitVisible = ref(false)
const aiOption = ref('no')
const aiResult = ref<any>(null)
const aiLoading = ref(false)

// 选择"让AI分析"时，调用发布前AI预分析接口（不依赖问诊id）
watch(aiOption, async (val) => {
  if (val === 'yes' && !aiResult.value && !aiLoading.value) {
    aiLoading.value = true
    try {
      const res: any = await getAiPreRecommend({
        title: form.value.description.slice(0, 20),
        symptom: form.value.description,
        pastMedical: form.value.diseaseHistory
      })
      if (res.code === 200) {
        aiResult.value = res.data
      } else {
        ElMessage.error(res.msg || 'AI分析失败')
      }
    } catch {
      ElMessage.error('AI分析请求异常')
    } finally {
      aiLoading.value = false
    }
  }
})
const publishTarget = ref('hall')
const selectedDoctor = ref<number | null>(null)
const isPublic = ref('public')

// 第一步：仅校验表单，打开发布选择弹窗，**不保存数据库**
const submit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  loading.value = true
  aiResult.value = null
  aiOption.value = 'no'
  loading.value = false
  afterSubmitVisible.value = true
}

// 第二步：确认发布，一次性提交完整问诊数据
const confirmPublish = async () => {
  publishLoading.value = true
  try {
    let publishType: number
    let doctorId: number | null = null
    let isPublicNum: number

    if (publishTarget.value === 'hall') {
      publishType = 1
      isPublicNum = isPublic.value === 'public' ? 1 : 0
    } else {
      if (!selectedDoctor.value) {
        ElMessage.warning("请选择一位医生");
        publishLoading.value = false
        return
      }
      publishType = 2
      doctorId = selectedDoctor.value
      isPublicNum = 0
    }

    const imgUrlStr = uploadedUrlList.value.join(',')
    const submitData = {
      userId: realUserId.value,
      doctorId: doctorId,
      title: form.value.description.slice(0, 20),
      symptom: form.value.description,
      departmentId: null,
      diseaseHistory: form.value.diseaseHistory,
      operationHistory: form.value.operationHistory,
      allergyHistory: form.value.allergy,
      imgUrl: imgUrlStr,
      isAnonymous: form.value.anonymous ? 1 : 0,
      publishType: publishType,
      isPublic: isPublicNum,
      aiSuggest: aiResult.value?.aiAnalysis || '',
      status: 1
    }
    const res: any = await addConsult(submitData)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      afterSubmitVisible.value = false
      router.push('/user/my')
    } else {
      ElMessage.error(res.msg || '发布失败')
    }
  } catch (e) {
    ElMessage.error('发布请求异常')
  } finally {
    publishLoading.value = false
  }
}
</script>

<style scoped>
.publish-page {
  padding: 16px;
}
</style>
