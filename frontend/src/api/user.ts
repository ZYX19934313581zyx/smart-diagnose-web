import request from '@/utils/request'

// 注册接口
export const register = (data: any) => {
  return request({
    url: '/api/user/register',
    method: 'post',
    data
  })
}

// 修改密码接口
export const changePassword = (data: any) => {
  return request({
    url: '/api/user/changePwd',
    method: 'post',
    data
  })
}

// 获取当前登录患者资料
export function getPatientProfile() {
  return request({
    url: '/api/profile/patient',
    method: 'get'
  })
}

// 保存修改患者资料
export function savePatientProfile(data: any) {
  return request({
    url: '/api/profile/patient',
    method: 'put',
    data
  })
}

// ========== 统一阿里云上传函数（全局共用） ==========
export function uploadFile(file: File, folder: string) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/upload',
    method: 'post',
    data: formData,
    params: {
      folder
    },
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 头像上传（调用通用OSS接口，存入avatar文件夹）
export function uploadAvatar(file: File) {
  return uploadFile(file, 'avatar')
}

// 更新用户头像到数据库
export function updateUserAvatar(avatarUrl: string) {
  return request({
    url: '/api/user/update/avatar',
    method: 'put',
    params: {
      avatarUrl
    }
  })
}
