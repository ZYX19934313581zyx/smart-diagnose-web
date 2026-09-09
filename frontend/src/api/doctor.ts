import request from '@/utils/request'

/**
 * 获取当前登录医生本人资质信息
 */
export const getMyDoctorInfo = () => {
  return request({
    url: '/api/doctor/my',
    method: 'GET'
  })
}

/**
 * 提交/保存医生资质申请表单
 * @param data 医生表单数据
 */
export const submitDoctorForm = (data: Record<string, any>) => {
  return request({
    url: '/api/doctor/submit',
    method: 'PUT',
    data
  })
}

/**
 * 管理员审核医生资质
 * @param userId 用户id
 * @param auditStatus 审核状态 pass / reject / wait
 */
export const auditDoctor = (userId: number, auditStatus: string) => {
  return request({
    url: '/api/doctor/audit',
    method: 'PUT',
    params: { userId, auditStatus }
  })
}

/**
 * 删除医生资质
 * @param userId 用户id
 */
export const deleteDoctor = (userId: number) => {
  return request({
    url: '/api/doctor/delete',
    method: 'DELETE',
    params: { userId }
  })
}

/**
 * 管理员分页模糊查询医生列表
 * @param name 姓名
 * @param department 科室
 * @param pageNum 页码
 * @param pageSize 页大小
 */
export const getDoctorPage = (
  name?: string,
  department?: string,
  pageNum = 1,
  pageSize = 10
) => {
  return request({
    url: '/api/doctor/page',
    method: 'GET',
    params: { name, department, pageNum, pageSize }
  })
}

/**
 * 患者端下拉选择医生：查询所有审核通过的医生
 */
export const getPassDoctorList = () => {
  return request({
    url: '/api/doctor/list/pass',
    method: 'GET'
  })
}