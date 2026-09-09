<template>
  <el-card shadow="hover" style="border-radius:0">
    <template #header>
      <h3>我的医生资质申请</h3>
    </template>

    <!-- 如果已有申请记录，展示详情；没有则展示表单 -->
    <div v-if="info" v-loading="loading">
      <el-descriptions border>
        <el-descriptions-item label="姓名">{{ info.realName }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ info.department }}</el-descriptions-item>
        <el-descriptions-item label="任职医院">{{ info.hospital }}</el-descriptions-item>
        <el-descriptions-item label="职称">{{ info.title }}</el-descriptions-item>
        <el-descriptions-item label="简介">{{ info.intro || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag
            :type="
              info.auditStatus === 'pass'
                ? 'success'
                : info.auditStatus === 'reject'
                ? 'danger'
                : 'warning'
            "
          >
            {{
              info.auditStatus === 'wait'
                ? '待审核'
                : info.auditStatus === 'pass'
                ? '审核通过'
                : '驳回'
            }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 附件展示区域 -->
      <div style="margin:16px 0;">
        <h4>资质附件列表</h4>
        <el-table :data="attachList" border stripe style="width:100%">
          <el-table-column label="文件名称" prop="fileName" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button link type="primary" @click="openFile(scope.row.fileUrl)">预览/下载</el-button>
              <el-button link type="danger" v-if="info.auditStatus === 'wait'" @click="handleDelAttach(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 只有待审核才能编辑提交 -->
      <el-button
        v-if="info.auditStatus === 'wait'"
        type="primary"
        style="margin-top:16px"
        @click="openEdit = true"
      >
        修改申请
      </el-button>
    </div>

    <el-form
      v-else-if="openEdit === false"
      :model="form"
      label-width="100px"
      v-loading="loading"
    >
      <el-form-item label="真实姓名">
        <el-input v-model="form.realName"></el-input>
      </el-form-item>
      <el-form-item label="科室">
        <el-select v-model="form.department" placeholder="请选择科室" style="width:100%">
          <el-option
            v-for="item in departmentList"
            :key="item.id"
            :label="item.name"
            :value="item.name"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="任职医院">
        <el-input v-model="form.hospital"></el-input>
      </el-form-item>
      <el-form-item label="职称">
        <el-input v-model="form.title"></el-input>
      </el-form-item>
      <el-form-item label="个人简介">
        <el-input v-model="form.intro" type="textarea"></el-input>
      </el-form-item>
      <!-- 上传附件 -->
      <el-form-item label="上传资质附件">
        <el-upload
          :http-request="handleUpload"
          show-file-list
          :file-list="uploadFileList"
        >
          <el-button type="primary">选择文件</el-button>
        </el-upload>
      </el-form-item>
      <el-button type="primary" @click="submit">提交申请</el-button>
    </el-form>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="openEdit" title="修改医生申请">
      <el-form :model="form">
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName"></el-input>
        </el-form-item>
        <el-form-item label="科室">
          <el-select v-model="form.department" placeholder="请选择科室" style="width:100%">
            <el-option
              v-for="item in departmentList"
              :key="item.id"
              :label="item.name"
              :value="item.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="任职医院">
          <el-input v-model="form.hospital"></el-input>
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="form.title"></el-input>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="form.intro" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="补充附件">
          <el-upload
            :http-request="handleUpload"
            show-file-list
            :file-list="uploadFileList"
          >
            <el-button type="primary">选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="openEdit = false">取消</el-button>
        <el-button type="primary" @click="submit">保存修改</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getMyDoctorInfo, submitDoctorForm } from '@/api/doctor'
import { saveAttachment, getAttachmentList, deleteAttachment } from '@/api/doctorAttachment'
import { uploadFile } from '@/api/user'
import { getDeptList } from '@/api/consult'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const userId = ref<number | null>(null)
const info = ref<any>(null)
const openEdit = ref(false)
// 科室下拉数据源
const departmentList = ref<any[]>([])
// 附件列表（后端查询出来）
const attachList = ref<any[]>([])
// 本次新增待提交的上传文件列表
const uploadFileList = ref<any[]>([])
// 上传成功得到的url集合，保存表单时批量入库
const tempFileUrlList = ref<string[]>([])

const form = ref({
  userId: 0,
  realName: '',
  department: '',
  hospital: '',
  title: '',
  intro: ''
})

// 获取本地登录用户id
const getLoginUserId = () => {
  const userStr = localStorage.getItem('userInfo')
  if (!userStr) return null
  const user = JSON.parse(userStr)
  return user.id
}

// 加载科室字典
const loadDepartment = async () => {
  const res = await getDeptList()
  departmentList.value = res.data
}

// 加载本人医生信息 + 附件
const loadInfo = async () => {
  const uid = getLoginUserId()
  userId.value = uid
  if (!uid) {
    ElMessage.warning('请先登录')
    return
  }
  loading.value = true
  try {
    const res: any = await getMyDoctorInfo()
    if (res.code === 200 && res.data) {
      info.value = res.data
      form.value = { ...res.data }
      // 查询附件
      const attachRes = await getAttachmentList(res.data.id)
      if (attachRes.code === 200) {
        attachList.value = attachRes.data
      }
    } else {
      info.value = null
      form.value.userId = uid
      attachList.value = []
    }
  } catch {
    info.value = null
    form.value.userId = uid
    attachList.value = []
  } finally {
    loading.value = false
  }
}

// 阿里云OSS通用上传，资质文件存入 doctorCert 文件夹
async function handleUpload(options: any) {
  try {
    const res: any = await uploadFile(options.file, 'doctorCert')
    if (res.code === 200) {
      tempFileUrlList.value.push(res.data)
      uploadFileList.value.push({ name: options.file.name, url: res.data })
      ElMessage.success('文件上传成功')
    } else {
      ElMessage.error('上传失败')
    }
  } catch {
    ElMessage.error('上传异常')
  }
}

// 删除单个附件
async function handleDelAttach(id: number) {
  await deleteAttachment(id)
  ElMessage.success('删除成功')
  loadInfo()
}

// 新窗口预览OSS完整链接，不再拼接本地localhost地址
function openFile(url: string) {
  window.open(url)
}

// 提交表单（新增/修改共用接口）
const submit = async () => {
  loading.value = true
  try {
    const res: any = await submitDoctorForm(form.value)
    if (res.code === 200) {
      // 获取保存后的医生资质主键id，循环保存附件记录
      const doctorId = res.data
      for (const fileUrl of tempFileUrlList.value) {
        const fileItem = uploadFileList.value.find(item => item.url === fileUrl)
        await saveAttachment({
          doctorId,
          fileName: fileItem.name,
          fileUrl: fileUrl
        })
      }
      ElMessage.success('提交成功，等待管理员审核')
      openEdit.value = false
      // 清空本次上传缓存
      uploadFileList.value = []
      tempFileUrlList.value = []
      await loadInfo()
    } else {
      ElMessage.error(res.msg || '提交失败')
    }
  } catch {
    ElMessage.error('接口请求异常')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadDepartment()
  await loadInfo()
})
</script>

<style scoped>

</style>