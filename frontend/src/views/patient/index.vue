<template>
  <el-card class="page" style="border-radius: 0; border: none;">
    <template #header>
      <div class="header">
        <h3>患者信息管理</h3>
        <el-button type="primary" @click="openAdd">新增患者</el-button>
      </div>
    </template>

    <el-table :data="tableData" border stripe style="width:100%">
      <el-table-column prop="id" label="ID" width="70"/>
      <el-table-column prop="userId" label="绑定用户ID" width="110"/>
      <el-table-column prop="realName" label="真实姓名"/>
      <el-table-column prop="age" label="年龄" width="70"/>
      <el-table-column prop="gender" label="性别" width="70"/>
      <el-table-column prop="allergy" label="过敏史"/>
      <el-table-column prop="diseaseHistory" label="既往病史"/>
      <el-table-column prop="operationHistory" label="手术史"/>
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button size="small" type="primary" @click="openEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDel(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum"
      v-model:page-size="pageSize"
      :total="total"
      layout="total, prev, pager, next"
      style="margin-top:16px"
      @change="loadData"
    />

    <el-dialog v-model="dialogVisible" title="患者信息">
      <el-form :model="form" label-width="100px">
        <el-form-item label="绑定用户ID">
          <el-input v-model.number="form.userId"/>
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName"/>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model.number="form.age"/>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="男" value="男"/>
            <el-option label="女" value="女"/>
          </el-select>
        </el-form-item>
        <el-form-item label="过敏史">
          <el-input v-model="form.allergy" type="textarea"/>
        </el-form-item>
        <el-form-item label="既往病史">
          <el-input v-model="form.diseaseHistory" type="textarea"/>
        </el-form-item>
        <el-form-item label="手术史">
          <el-input v-model="form.operationHistory" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'

const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const dialogVisible = ref(false)

const form = ref({
  id: null,
  userId: null,
  realName: '',
  age: null,
  gender: '',
  allergy: '',
  diseaseHistory: '',
  operationHistory: ''
})

const loadData = () => {
  tableData.value = []
  total.value = 0
}

const openAdd = () => {
  form.value = {
    id: null, userId: null, realName: '', age: null,
    gender: '', allergy: '', diseaseHistory: '', operationHistory: ''
  }
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.value = { ...row }
  dialogVisible.value = true
}

const submit = () => {
  dialogVisible.value = false
}
const handleDel = () => {}

loadData()
</script>

<style scoped>
.page{
  padding: 0;
}
.header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:16px 24px;
}
</style>