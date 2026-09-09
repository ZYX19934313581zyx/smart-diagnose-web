<template>
  <el-card title="医生信息管理" shadow="hover">
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索姓名/科室"
        clearable
        style="width:240px"
        @keyup.enter="loadData"
      />
      <el-input
        v-model="searchId"
        placeholder="输入医生ID筛选"
        clearable
        style="width:200px"
        @keyup.enter="loadData"
      />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table :data="list" border stripe style="width:100%;margin-top:16px" v-loading="loading">
      <el-table-column label="ID" prop="id" width="80" />
      <el-table-column label="姓名" prop="realName" width="120" />
      <el-table-column label="科室" prop="department" width="140" />
      <el-table-column label="职称" prop="title" width="120" />
      <el-table-column label="任职医院" prop="hospital" show-overflow-tooltip />
      <el-table-column label="审核状态" width="100">
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
                ? '已拒绝'
                : '待审核'
            }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="scope">
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
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
import { getDoctorList, deleteDoctor } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'

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
    const res: any = await getDoctorList({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      name: keyword.value || undefined,
      id: searchId.value ? Number(searchId.value) : undefined
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

const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定删除医生【${row.realName}】吗？`, '删除提示', { type: 'warning' })
    const res: any = await deleteDoctor(row.userId)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch {
    //取消操作不提示
  }
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