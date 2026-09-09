<template>
  <el-card title="我的问诊列表" shadow="hover">
    <div class="search-bar" style="margin-bottom:16px">
      <el-input
        v-model="keyword"
        placeholder="搜索症状/科室"
        clearable
        style="width:240px"
        @keyup.enter="loadData"
      />
      <el-input
        v-model="searchId"
        placeholder="输入问诊ID筛选"
        clearable
        style="width:200px"
        @keyup.enter="loadData"
      />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="待回复" name="pending">
        <el-table :data="filterPendingList" border stripe v-loading="loading">
          <el-table-column label="ID" prop="id" width="80" />
          <el-table-column label="症状标题" prop="title" width="160" show-overflow-tooltip />
          <el-table-column label="症状描述" prop="symptom" show-overflow-tooltip />
          <el-table-column label="提交时间" prop="createTime" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="primary" size="small" @click="goReply(scope.row.id)">去回复</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="历史回复" name="history">
        <el-table :data="filterHistoryList" border stripe v-loading="loading">
          <el-table-column label="ID" prop="id" width="80" />
          <el-table-column label="症状标题" prop="title" width="160" show-overflow-tooltip />
          <el-table-column label="症状描述" prop="symptom" show-overflow-tooltip />
          <el-table-column label="提交时间" prop="createTime" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="scope">
              <el-button type="info" size="small" @click="goReply(scope.row.id)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getWaitConsultList, getHistoryConsultList } from '@/api/consult'
import { ElMessage } from 'element-plus'

const router = useRouter()
const activeTab = ref('pending')
const loading = ref(false)
const pendingList = ref<any[]>([])
const historyList = ref<any[]>()

const keyword = ref('')
const searchId = ref('')

// 前端过滤 - 待回复列表
const filterPendingList = computed(() => {
  let arr = pendingList.value
  if (!arr) return []
  if (searchId.value.trim()) {
    const targetId = Number(searchId.value)
    arr = arr.filter(item => Number(item.id) === targetId)
  }
  if (keyword.value.trim()) {
    const kw = keyword.value.toLowerCase()
    arr = arr.filter(item =>
      (item.department && item.department.includes(kw)) ||
      (item.title && item.title.toLowerCase().includes(kw)) ||
      (item.symptom && item.symptom.toLowerCase().includes(kw))
    )
  }
  return arr
})

// 前端过滤 - 历史回复列表
const filterHistoryList = computed(() => {
  let arr = historyList.value
  if (!arr) return []
  if (searchId.value.trim()) {
    const targetId = Number(searchId.value)
    arr = arr.filter(item => Number(item.id) === targetId)
  }
  if (keyword.value.trim()) {
    const kw = keyword.value.toLowerCase()
    arr = arr.filter(item =>
      (item.department && item.department.includes(kw)) ||
      (item.title && item.title.toLowerCase().includes(kw)) ||
      (item.symptom && item.symptom.toLowerCase().includes(kw))
    )
  }
  return arr
})

const loadData = async () => {
  loading.value = true
  try {
    if (activeTab.value === 'pending') {
      const res: any = await getWaitConsultList()
      if (res.code === 200) {
        pendingList.value = res.data
      } else {
        ElMessage.error(res.msg || '加载待回复列表失败')
      }
    } else if (activeTab.value === 'history') {
      const res: any = await getHistoryConsultList()
      if (res.code === 200) {
        historyList.value = res.data
      } else {
        ElMessage.error(res.msg || '加载历史回复列表失败')
      }
    }
  } catch {
    ElMessage.error('接口请求异常')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  loadData()
}

const resetSearch = () => {
  keyword.value = ''
  searchId.value = ''
}

const goReply = (id: number) => {
  router.push(`/chat/${id}`)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 12px;
  align-items: center;
}
</style>