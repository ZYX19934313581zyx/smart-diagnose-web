<template>
  <div class="container">
    <el-card shadow="hover" style="border-radius: 0; border: none;">
      <template #header>
        <h3>医生资质审批</h3>
      </template>

      <el-table :data="tableData" border stripe style="width:100%" v-loading="loading">
        <el-table-column label="ID" prop="userId" width="80" />
        <el-table-column label="姓名" prop="realName" />
        <el-table-column label="科室" prop="department" />
        <el-table-column label="医院" prop="hospital" />
        <el-table-column label="职称" prop="title" />
        <el-table-column label="审核状态" prop="auditStatus" width="100">
          <template #default="scope">
            <el-tag
              :type="
                scope.row.auditStatus === 'pass'
                  ? 'success'
                  : scope.row.auditStatus === 'reject'
                  ? 'danger'
                  : 'warning'
              "
            >
              {{
                scope.row.auditStatus === 'pass'
                  ? '已通过'
                  : scope.row.auditStatus === 'reject'
                  ? '已驳回'
                  : '待审核'
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="资质证明" width="120">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="viewQualification(scope.row)">
              查看
            </el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button
              type="success"
              size="small"
              @click="handleAudit(scope.row.userId, 'pass')"
              :disabled="scope.row.auditStatus === 'pass'"
            >
              通过
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleAudit(scope.row.userId, 'reject')"
              :disabled="scope.row.auditStatus === 'reject'"
            >
              驳回
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @change="loadData"
        style="margin-top:16px"
      />
    </el-card>

    <!-- 资质图片预览弹窗 -->
    <el-dialog v-model="imgVisible" title="资质证明" width="600px">
      <img v-if="qualificationImg" :src="qualificationImg" style="width:100%;" alt="资质证明" />
      <div v-else>暂无资质图片</div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDoctorAuditList, auditDoctorStatus } from '@/api/consult'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref<any[]>([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const imgVisible = ref(false)
const qualificationImg = ref('')

const loadData = async () => {
  loading.value = true
  try {
    const res: any = await getDoctorAuditList({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg || '获取列表失败')
    }
  } catch {
    ElMessage.error('接口请求异常')
  } finally {
    loading.value = false
  }
}

const handleAudit = async (id: number, status: string) => {
  loading.value = true
  try {
    const res: any = await auditDoctorStatus({ userId: id, auditStatus: status })
    if (res.code === 200) {
      ElMessage.success(status === 'pass' ? '已通过审核' : '已驳回')
      loadData()
    } else {
      ElMessage.error(res.msg || '审核操作失败')
    }
  } catch {
    ElMessage.error('接口请求异常')
  } finally {
    loading.value = false
  }
}

const viewQualification = (row: any) => {
  qualificationImg.value = row.qualificationImg || ''
  imgVisible.value = true
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.container {
  padding: 0;
}
</style>