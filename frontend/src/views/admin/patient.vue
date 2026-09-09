<template>
  <el-card title="患者信息管理" shadow="hover">
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索昵称"
        clearable
        style="width:240px"
        @keyup.enter="loadData"
      />
      <el-input
        v-model="searchId"
        placeholder="输入患者ID筛选"
        clearable
        style="width:200px"
        @keyup.enter="loadData"
      />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table
      :data="list"
      border
      stripe
      style="width:100%;margin-top:16px"
      v-loading="loading"
      table-layout="fixed"
    >
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="昵称" prop="nickname" min-width="120" />
      <el-table-column label="性别" prop="gender" width="80" />
      <el-table-column label="年龄" prop="age" width="80" />
      <el-table-column label="手机号" prop="phone" min-width="140" />
      <el-table-column label="注册时间" prop="createTime" min-width="180" />
    </el-table>

    <div class="pagination-wrap">
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
import { getPatientPage } from '@/api/admin'
import { ElMessage } from 'element-plus'

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
    const res: any = await getPatientPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      nickname: keyword.value || undefined,
      userId: searchId.value ? Number(searchId.value) : undefined
    })
    if (res.code === 200) {
      list.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载失败')
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
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>