<template>
  <div class="my-page">
    <el-card shadow="hover">
      <template #header>
        <div class="header-wrap">
          <span>我的问诊记录</span>
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
              placeholder="请输入问诊ID筛选"
              style="width:200px"
              clearable
            />
          </div>
        </div>
      </template>

      <el-table :data="filterList" border stripe style="width:100%">
        <el-table-column label="ID" prop="id" width="80" />
        <el-table-column label="科室" prop="department" width="160" />
        <el-table-column label="症状描述" prop="symptom" show-overflow-tooltip />
        <el-table-column label="发布时间" prop="createTime" width="180" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="small" @click="goDetail(scope.row.id)">查看对话</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="filterList.length === 0" description="暂无问诊记录，去发布一条吧" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getMyConsult } from '@/api/consult'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const myList = ref<any[]>([])
const searchId = ref('')
const keyword = ref('')

// 读取本地用户信息
const userJson = localStorage.getItem('userInfo')
const loginUser = userJson ? JSON.parse(userJson) : null
const loginUserId = loginUser?.id

// 组合过滤：ID精准匹配 + 科室/症状模糊搜索
const filterList = computed(() => {
  let arr = myList.value
  if (searchId.value.trim()) {
    const targetId = Number(searchId.value)
    arr = arr.filter(item => Number(item.id) === targetId)
  }
  if (keyword.value.trim()) {
    const kw = keyword.value.toLowerCase()
    arr = arr.filter(item =>
      (item.department && item.department.includes(kw)) ||
      (item.symptom && item.symptom.toLowerCase().includes(kw))
    )
  }
  return arr
})

const loadMyList = async () => {
  if (!loginUserId) {
    console.log('未登录')
    return
  }
  const res: any = await getMyConsult(loginUserId)
  if(res.code === 200){
    myList.value = res.data || []
  }
}

const search = () => {}

const goDetail = (id:number)=>{
  router.push(`/chat/${id}`)
}

onMounted(()=>{
  loadMyList()
})
</script>

<style scoped>
.my-page {
  padding: 16px;
}
.header-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-group {
  display: flex;
  align-items: center;
}
</style>