import request from '@/utils/request'

// 获取科室列表
export function getDeptList() {
  return request({
    url: '/api/department/list',
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

// 医生专属问诊大厅（带科室优先排序）
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

// 医生待回复问诊列表（无需传doctorId，后端从token获取）
export function getWaitConsultList() {
  return request({
    url: '/api/consult/doctor/wait',
    method: 'get'
  })
}

// 医生历史回复问诊列表（无需传doctorId，后端从token获取）
export function getHistoryConsultList() {
  return request({
    url: '/api/consult/doctor/history',
    method: 'get'
  })
}

// 发送聊天回复
export function sendReply(data: Record<string, any>) {
  return request({
    url: '/api/reply/add',
    method: 'post',
    data
  })
}

// 获取对话聊天记录
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

// 发布前AI预分析（不依赖问诊id，弹窗选择AI分析时调用）
export function getAiPreRecommend(data: Record<string, any>) {
  return request({
    url: '/api/consult/ai/pre-recommend',
    method: 'post',
    data
  })
}
