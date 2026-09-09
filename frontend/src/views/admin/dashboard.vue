<template>
  <div class="dashboard-wrap">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" style="--card-color:#409eff">
          <div class="stat-icon">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.patientTotal || 0 }}</div>
            <div class="stat-label">患者总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" style="--card-color:#67c23a">
          <div class="stat-icon">
            <el-icon><Avatar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.doctorTotal || 0 }}</div>
            <div class="stat-label">医生总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" style="--card-color:#e6a23c">
          <div class="stat-icon">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.consultTotal || 0 }}</div>
            <div class="stat-label">问诊总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card" style="--card-color:#f56c6c">
          <div class="stat-icon">
            <el-icon><Clock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.todayConsult || 0 }}</div>
            <div class="stat-label">今日新增问诊</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 待审核医生卡片 -->
    <el-card shadow="hover" style="margin-top:20px">
      <template #header>
        <span class="card-title">待审核医生</span>
      </template>
      <el-table :data="pendingDoctors" border stripe v-loading="loading">
        <el-table-column label="ID" min-width="80" align="left" header-align="left">
          <template #default="{ row }">
            {{ row.id }}
          </template>
        </el-table-column>
        <el-table-column label="姓名" min-width="120" align="left" header-align="left">
          <template #default="{ row }">
            {{ row.real_name }}
          </template>
        </el-table-column>
        <el-table-column label="科室" min-width="120" align="left" header-align="left">
          <template #default="{ row }">
            {{ row.department }}
          </template>
        </el-table-column>
        <el-table-column label="提交时间" min-width="180" align="left" header-align="left">
          <template #default="{ row }">
            {{ row.create_time }}
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="140" align="left" header-align="left">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="$router.push('/admin/doctorAudit?id=' + row.user_id)">
              去审核
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="pendingDoctors.length === 0 && !loading" description="暂无待审核医生" />
    </el-card>

    <!-- 问诊记录卡片 -->
    <el-card shadow="hover" style="margin-top:20px">
      <template #header>
        <span class="card-title">问诊记录</span>
      </template>
      <el-table :data="recentConsults" border stripe v-loading="loading">
        <el-table-column label="ID" min-width="80" align="left" header-align="left">
          <template #default="{ row }">
            {{ row.id }}
          </template>
        </el-table-column>
        <el-table-column label="科室" min-width="120" align="left" header-align="left">
          <template #default="{ row }">
            {{ row.dept_name }}
          </template>
        </el-table-column>
        <el-table-column label="症状描述" min-width="300" align="left" header-align="left">
          <template #default="{ row }">
            <span :title="row.symptom">{{ row.symptom }}</span>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" min-width="200" align="left" header-align="left">
          <template #default="{ row }">
            {{ row.create_time }}
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="recentConsults.length === 0 && !loading" description="暂无问诊记录" />
      <el-pagination
        v-model:current-page="consultPageNum"
        v-model:page-size="consultPageSize"
        :total="consultTotal"
        :page-sizes="[5,10,20,50]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top:16px; text-align:right"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getDashboardStats, getWaitDoctorList, getAdminConsultPage } from '@/api/admin'
import { User, Avatar, Document, Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const stats = ref<any>({})
const pendingDoctors = ref<any[]>([])
const recentConsults = ref<any[]>([])

const consultPageNum = ref(1)
const consultPageSize = ref(10)
const consultTotal = ref(0)

const loadData = async () => {
  loading.value = true
  try {
    const resStats: any = await getDashboardStats()
    if (resStats.code === 200) {
      stats.value = resStats.data || {}
    }

    const resDoctor: any = await getWaitDoctorList()
    if (resDoctor.code === 200) {
      pendingDoctors.value = resDoctor.data || []
    }

    const resConsult: any = await getAdminConsultPage({
      page: consultPageNum.value,
      size: consultPageSize.value
    })
    if (resConsult.code === 200) {
      recentConsults.value = resConsult.data.records || []
      consultTotal.value = resConsult.data.total || 0
    }
  } catch {
    ElMessage.error('接口请求异常')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard-wrap {
  padding: 20px;
}

/* 卡片整体使用自定义颜色铺满 */
.stat-card :deep(.el-card__body) {
  background: var(--card-color);
  display: flex;
  align-items: center;
  gap: 16px;
  width: 100%;
  padding: 24px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 28px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.2);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #ffffff;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 4px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
</style>