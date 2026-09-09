import request from '@/utils/request'

// 获取科室列表
export function getDeptList() {
  return request({
    url: '/api/department/list',
    method: 'get'
  })
}

// 获取症状列表
export function getSymptomList() {
  return request({
    url: '/api/symptom/list',
    method: 'get'
  })
}

// 提交新增问诊
export function addConsult(data: Record<string, any>) {
  return request({
    url: '/api/consult/add',
    method: 'post',
    data
  })
}

// 获取公开问诊大厅列表（不分页）
export function getPublicConsult() {
  return request({
    url: '/api/consult/public/list',
    method: 'get'
  })
}

// 患者公开分页大厅（旧接口）
export function getPublicConsultPage(params: { pageNum: number; pageSize: number; keyword?: string }) {
  return request({
    url: '/api/consult/hall',
    method: 'get',
    params: {
      page: params.pageNum,
      size: params.pageSize,
      keyword: params.keyword
    }
  })
}

// ✅ 新增：医生专属问诊大厅（带科室优先排序）
export function getDoctorConsultHallPage(params: { pageNum: number; pageSize: number; keyword?: string }) {
  return request({
    url: '/api/consult/doctor/hall',
    method: 'get',
    params: {
      page: params.pageNum,
      size: params.pageSize,
      keyword: params.keyword
    }
  })
}

// 根据id获取问诊详情
export function getConsultDetail(id: string | number) {
  return request({
    url: `/api/consult/${id}`,
    method: 'get'
  })
}

// 获取我的问诊列表
export function getMyConsult(userId: string | number) {
  return request({
    url: `/api/consult/user/list/${userId}`,
    method: 'get'
  })
}

// 获取医生收到的问诊
export function getDoctorConsult(doctorId: string | number) {
  return request({
    url: `/api/consult/doctor/list/${doctorId}`,
    method: 'get'
  })
}

// ✅ 医生待回复问诊列表（无需传doctorId，后端从token获取）
export function getWaitConsultList() {
  return request({
    url: '/api/consult/doctor/wait',
    method: 'get'
  })
}

// ✅ 医生历史回复问诊列表（无需传doctorId，后端从token获取）
export function getHistoryConsultList() {
  return request({
    url: '/api/consult/doctor/history',
    method: 'get'
  })
}

// ✅ 提交/修改医生资质申请
export function saveDoctorApply(data: Record<string, any>) {
  return request({
    url: '/api/doctor/save',
    method: 'post',
    data
  })
}

// ✅ 管理员：分页获取待审核医生列表（参数改为 _params 消除未使用警告）
export function getDoctorAuditList(_params: { pageNum: number; pageSize: number }) {
  return request({
    url: '/api/doctor/audit/page',
    method: 'get'
  })
}

// ✅ 管理员：审核医生资质（通过/驳回）
export function auditDoctorStatus(data: { userId: number; auditStatus: string }) {
  return request({
    url: '/api/doctor/audit',
    method: 'put',
    data
  })
}

// ✅ 发送聊天回复
export function sendReply(data: Record<string, any>) {
  return request({
    url: '/api/reply/add',
    method: 'post',
    data
  })
}

// ✅ 获取对话聊天记录
export function getChatList(consultId: string | number) {
  return request({
    url: `/api/reply/list/${consultId}`,
    method: 'get'
  })
}

// ============ 未读消息新接口（后端自动读取token，不用传userId）============
// 获取未读消息红点数量
export function getUnReadCount() {
  return request({
    url: '/api/reply/unread/count',
    method: 'get'
  })
}

// 获取存在未读消息的问诊id列表（铃铛弹窗使用）
export function getUnReadConsultList() {
  return request({
    url: '/api/reply/unread/consultList',
    method: 'get'
  })
}

// 标记指定问诊全部消息为已读（不用传userId）
export function setRead(consultId: string | number) {
  return request({
    url: '/api/reply/read',
    method: 'put',
    params: { consultId }
  })
}

// ✅ AI对话发送（旧的post请求，废弃）
export function sendAiMsg(data: Record<string, any>) {
  return request({
    url: '/api/aichat/send',
    method: 'post',
    data
  })
}

// ✅ 获取AI聊天历史
export function getAiHistory(userId: string | number) {
  return request({
    url: `/api/aichat/history/${userId}`,
    method: 'get'
  })
}

export const getAiReply = (data: { content: string }) => {
  return request({
    url: '/api/consult/ai',
    method: 'post',
    data
  })
}

export function updateConsult(data:any){
  return request({
    url:'/api/consult/update',
    method:'put',
    data
  })
}

// 【新增】问诊提交后，AI分析病情 + 推荐对应科室医生
export function getAiRecommend(consultId: number | string) {
  return request({
    url: `/api/consult/ai/recommend/${consultId}`,
    method: 'get'
  })
}