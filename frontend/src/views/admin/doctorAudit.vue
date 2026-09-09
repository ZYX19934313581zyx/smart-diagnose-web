<template>
  <el-card class="page">
    <template #header>
      <div class="header">
        <h3>医生资质审核管理</h3>
      </div>
    </template>

    <!--搜索区-->
    <el-row :gutter="16" class="search-row">
      <el-col :span="6">
        <el-input v-model="query.name" placeholder="输入姓名搜索"/>
      </el-col>
      <el-col :span="6">
        <el-input v-model="query.department" placeholder="科室筛选"/>
      </el-col>
      <el-col :span="4">
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-col>
    </el-row>

    <el-table :data="tableData" border stripe style="width:100%;margin-top:16px">
      <el-table-column prop="id" label="主键ID" width="80"/>
      <el-table-column prop="userId" label="绑定用户ID" width="110"/>
      <el-table-column prop="realName" label="真实姓名"/>
      <el-table-column prop="department" label="科室"/>
      <el-table-column prop="hospital" label="就职医院"/>
      <el-table-column prop="title" label="职称"/>
      <el-table-column label="审核状态" width="110">
        <template #default="scope">
          <el-tag v-if="scope.row.auditStatus==='wait'" type="warning">待审核</el-tag>
          <el-tag v-if="scope.row.auditStatus==='pass'" type="success">审核通过</el-tag>
          <el-tag v-if="scope.row.auditStatus==='reject'" type="danger">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="scope">
            <el-button 
                size="small" 
                type="primary"
                @click="openAttachDialog(scope.row.id)"
            >查看附件</el-button>
            <el-button 
                size="small" 
                type="success" 
                v-if="scope.row.auditStatus === 'wait'"
                @click="handleAudit(scope.row,'pass')"
            >通过</el-button>
            <el-button 
                size="small" 
                type="danger" 
                v-if="scope.row.auditStatus === 'wait'"
                @click="handleAudit(scope.row,'reject')"
            >驳回</el-button>
            <el-button size="small" type="info" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination
      v-model:current-page="query.pageNum"
      v-model:page-size="query.pageSize"
      :total="total"
      layout="total,prev,pager,next"
      @current-change="loadData"
      style="margin-top:16px;text-align:right"
    />

    <!--附件弹窗-->
    <el-dialog v-model="attachVisible" title="资质附件列表" width="60%">
      <el-table :data="attachList" border stripe>
        <el-table-column label="文件名" prop="fileName"/>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button link type="primary" @click="previewFile(scope.row.fileUrl)">预览/下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </el-card>
</template>

<script setup lang="ts">
import {ref,onMounted} from 'vue'
import {getDoctorPage,auditDoctor,deleteDoctor} from '@/api/doctor'
import {getAttachmentList} from '@/api/doctorAttachment'
import {ElMessage,ElMessageBox} from 'element-plus'

const tableData = ref<any[]>([])
const total = ref(0)
const query = ref({
  name:'',
  department:'',
  pageNum:1,
  pageSize:10
})

//附件弹窗
const attachVisible = ref(false)
const attachList = ref<any[]>([])

//加载列表
async function loadData(){
  // 整体断言绕过类型校验
  const res: any = await getDoctorPage(query.value as any)
  if(res.code===200){
    tableData.value = res.data.records
    total.value = res.data.total
  }
}

//打开附件弹窗
async function openAttachDialog(doctorId:number){
  const res:any = await getAttachmentList(doctorId)
  if(res.code === 200){
    attachList.value = res.data
    attachVisible.value = true
  }
}

//预览文件，拼接后端地址
function previewFile(url:string){
  window.open("http://localhost:8081" + url)
}

//重置搜索
function resetQuery(){
  query.value.name=''
  query.value.department=''
  query.value.pageNum=1
  loadData()
}

//审核
async function handleAudit(row:any,status:string){
  await ElMessageBox.confirm(`确定${status==='pass'?'通过':'驳回'}该医生申请？`)
  await auditDoctor(row.userId, status)
  ElMessage.success('审核完成')
  loadData()
}

//删除
async function handleDelete(row:any){
  await ElMessageBox.confirm('确认删除这条资质记录？','提示',{type:'warning'})
  await deleteDoctor(row.userId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(()=>loadData())
</script>

<style scoped>
.search-row{
  display:flex;
  align-items:center;
}
</style>