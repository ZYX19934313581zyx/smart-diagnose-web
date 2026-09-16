import request from '@/utils/request'

// 登录
export function login(data: any) {
  return request({
    url: '/api/user/login',
    method: 'post',
    data
  })
}

// 获取医生分页列表
export function getDoctorList(params: any) {
  return request({
    url: '/api/doctor/page',
    method: 'get',
    params
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

// 管理员：获取全部问诊分页（包含私密问诊）
export const getAdminConsultPage = (params: any) => {
  return request({
    url: '/api/admin/consult/page',
    method: 'get',
    params
  })
}
