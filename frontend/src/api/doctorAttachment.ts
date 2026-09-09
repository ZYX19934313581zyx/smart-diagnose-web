import request from '@/utils/request'

// 文件上传
export function uploadDoctorFile(formData: FormData): Promise<any> {
  return request({
    url: '/api/doctorAttachment/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 保存附件数据库记录
export function saveAttachment(data: any): Promise<any> {
  return request({
    url: '/api/doctorAttachment/save',
    method: 'post',
    data
  })
}

// 根据医生资质id获取附件列表
export function getAttachmentList(doctorId: number): Promise<any> {
  return request({
    url: `/api/doctorAttachment/list/${doctorId}`,
    method: 'get'
  })
}

// 删除单个附件
export function deleteAttachment(id: number): Promise<any> {
  return request({
    url: `/api/doctorAttachment/${id}`,
    method: 'delete'
  })
}

// 删除该资质全部附件
export function deleteAllAttachment(doctorId: number): Promise<any> {
  return request({
    url: `/api/doctorAttachment/all/${doctorId}`,
    method: 'delete'
  })
}