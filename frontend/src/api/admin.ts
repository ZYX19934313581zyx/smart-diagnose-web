import request from '@/utils/request'

// 管理员登录
export function login(data: any) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

// 获取医生分页列表【修改接口路径】
export function getDoctorList(params: any) {
  return request({
    url: '/api/doctor/page',
    method: 'get',
    params
  })
}

// 审核医生
export function auditDoctor(userId: number, auditStatus: string) {
  return request({
    url: '/api/admin/doctor/audit',
    method: 'put',
    params: { userId, auditStatus }
  })
}

// 编辑医生
export function editDoctor(data: any) {
  return request({
    url: '/api/admin/doctor/edit',
    method: 'put',
    data
  })
}

// 删除医生
export function deleteDoctor(userId: number) {
  return request({
    url: '/api/admin/doctor/remove',
    method: 'delete',
    params: { userId }
  })
}

// ========== 新增接口 ==========
// 工作台统计数据
export const getDashboardStats = () => {
  return request({
    url: '/api/admin/dashboard/stats',
    method: 'get'
  })
}

// 获取待审核医生列表
export const getWaitDoctorList = () => {
  return request({
    url: '/api/admin/doctor/wait',
    method: 'get'
  })
}

// 患者分页列表
export const getPatientPage = (params: any) => {
  return request({
    url: '/api/admin/patient/page',
    method: 'get',
    params
  })
}

// 修改患者账号状态
export const updatePatientStatus = (id: number, status: number) => {
  return request({
    url: `/api/admin/patient/status/${id}`,
    method: 'put',
    data: { status }
  })
}

// 修改医生账号状态
export const updateDoctorStatus = (id: number, status: number) => {
  return request({
    url: `/api/admin/doctor/status/${id}`,
    method: 'put',
    data: { status }
  })
}

// 管理员：获取全部问诊分页（包含私密问诊）
export const getAdminConsultPage = (params: any) => {
  return request({
    url: '/api/admin/consult/page',
    method: 'get',
    params
  })
}