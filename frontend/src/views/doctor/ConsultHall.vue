<template>
  <el-card title="问诊大厅" shadow="hover">
    <el-alert title="这里展示与您科室匹配的公开问诊记录，点击可回复" type="info" :closable="false" style="margin-bottom:16px" />

    <div class="search-bar">
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

    <el-table :data="list" border stripe style="width:100%;margin-top:16px" v-loading="loading">
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="科室" prop="dept_name" width="100" />
        <el-table-column label="症状描述" prop="symptom" show-overflow-tooltip />
        <el-table-column label="发布时间" prop="create_time" width="180" />
        <el-table-column label="操作" width="120">
            <template #default="scope">
            <el-button type="primary" size="small" @click="goReply(scope.row.id)">回复</el-button>
            </template>
        </el-table-column>
    </el-table> 

    <div class="pagination-wrap" style="margin-top:20px;display:flex;justify-content:flex-end">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10,20,50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getDoctorConsultHallPage } from '@/api/consult'
import { ElMessage } from 'element-plus'

const router = useRouter()
const loading = ref(false)
const keyword = ref('')
const searchId = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const list = ref<any[]>([])

const loadData = async () => {
  loading.value = true
  try {
    // 基础分页参数
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    } as any
    // 有输入问诊ID才赋值
    if (searchId.value.trim()) {
      params.id = Number(searchId.value)
    }
    const res = await getDoctorConsultHallPage(params) as any
    if (res.code === 200) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '数据加载失败')
    }
  } catch {
    ElMessage.error('接口请求异常')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  keyword.value = ''
  searchId.value = ''
  pageNum.value = 1
  loadData()
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