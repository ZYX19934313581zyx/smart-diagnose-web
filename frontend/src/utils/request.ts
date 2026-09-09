import axios from 'axios'

const service = axios.create({
  baseURL: 'http://localhost:8081', // 指定后端服务地址
  timeout: 5000
})

// 请求拦截器：自动带上token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers!.token = token
  }
  return config
})

// 响应拦截器
service.interceptors.response.use(res => {
  return res.data
}, err => {
  return Promise.reject(err)
})

export default service