<template>
  <div class="hall-page">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>问诊大厅</span>
          <div class="search-group">
            <el-input
              v-model="keyword"
              placeholder="搜索症状/科室"
              style="width:200px;margin-right:10px"
              clearable
              @keyup.enter="search"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-input
              v-model="searchId"
              placeholder="输入问诊ID筛选"
              style="width:200px"
              clearable
            />
          </div>
        </div>
      </template>

      <el-table :data="filteredList" border stripe style="width:100%">
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="科室" prop="department" width="160" />
        <el-table-column label="症状描述" prop="symptom" show-overflow-tooltip />
        <el-table-column label="发布时间" prop="createTime" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goDetail(scope.row.id)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filteredList.length === 0" description="暂无公开问诊记录" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPublicConsult } from '@/api/consult'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const list = ref<any[]>([])
const keyword = ref('')
const searchId = ref('')

const filteredList = computed(() => {
  let arr = list.value
  // ID精准过滤
  if (searchId.value.trim()) {
    const targetId = Number(searchId.value)
    arr = arr.filter(item => Number(item.id) === targetId)
  }
  // 关键词模糊过滤（科室/症状）
  if (keyword.value.trim()) {
    const kw = keyword.value.toLowerCase()
    arr = arr.filter(item =>
      (item.department && item.department.includes(kw)) ||
      (item.symptom && item.symptom.toLowerCase().includes(kw))
    )
  }
  return arr
})

const loadList = async () => {
  try {
    const res: any = await getPublicConsult()
    if (res.code === 200) {
      list.value = res.data || []
    }
  } catch (e) {
    console.error('加载大厅列表失败', e)
  }
}

const search = () => {}
const goDetail = (id: number) => {
  router.push(`/chat/${id}`)
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.hall-page {
  padding: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-group {
  display: flex;
  align-items: center;
}
</style>