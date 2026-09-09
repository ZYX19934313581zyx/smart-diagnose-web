import { createRouter, createWebHistory } from 'vue-router'
import axios from 'axios'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register.vue')
  },

  // 患者端布局
  {
    path: '/user',
    name: 'UserLayout',
    component: () => import('@/views/user/layout.vue'),
    redirect: '/user/hall',
    children: [
      {
        path: 'hall',
        name: 'ConsultHall',
        component: () => import('@/views/consult/ConsultHall.vue'),
        meta: { title: '问诊大厅', role: 'patient' }
      },
      {
        path: 'publish',
        name: 'PublishConsult',
        component: () => import('@/views/consult/PublishConsult.vue'),
        meta: { title: '发起问诊', role: 'patient' }
      },
      {
        path: 'ai',
        name: 'AiChat',
        component: () => import('@/views/consult/AiChat.vue'),
        meta: { title: 'AI问诊助手', role: 'patient' }
      },
      {
        path: 'my',
        name: 'MyConsult',
        component: () => import('@/views/consult/MyConsult.vue'),
        meta: { title: '我的问诊记录', role: 'patient' }
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/profile.vue'),
        meta: { title: '个人资料', role: 'patient' }
      },
      {
        path: 'setting',
        name: 'UserSetting',
        component: () => import('@/views/user/setting.vue'),
        meta: { title: '账号设置', role: 'patient' }
      }
    ]
  },

  // 医生端布局
  {
    path: '/doctor',
    name: 'DoctorLayout',
    component: () => import('@/views/doctor/layout.vue'),
    children: [
      {
        path: 'apply',
        name: 'DoctorApply',
        component: () => import('@/views/doctor/index.vue'),
        meta: { title: '提交医生资质', role: 'doctor' }
      },
      {
        path: 'hall',
        name: 'DoctorHall',
        component: () => import('@/views/doctor/ConsultHall.vue'),
        meta: { title: '问诊大厅', role: 'doctor', needAudit: true }
      },
      {
        path: 'list',
        name: 'DoctorConsultList',
        component: () => import('@/views/doctor/ConsultList.vue'),
        meta: { title: '我的问诊列表', role: 'doctor', needAudit: true }
      },
      {
        path: 'profile',
        name: 'DoctorProfile',
        component: () => import('@/views/doctor/Profile.vue'),
        meta: { title: '个人中心', role: 'doctor', needAudit: true }
      }
    ]
  },

  // 管理员后台
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/layout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/dashboard.vue'),
        meta: { title: '工作台', role: 'admin' }
      },
      {
        path: 'doctor',
        name: 'DoctorManage',
        component: () => import('@/views/admin/doctor.vue'),
        meta: { title: '医生信息管理', role: 'admin' }
      },
      {
        path: 'patient',
        name: 'PatientManage',
        component: () => import('@/views/admin/patient.vue'),
        meta: { title: '患者信息管理', role: 'admin' }
      },
      {
        path: 'doctorAudit',
        name: 'DoctorAudit',
        component: () => import('@/views/admin/doctorAudit.vue'),
        meta: { title: '医生资质审批', role: 'admin' }
      }
    ]
  },

  // 公共对话页面
  {
    path: '/chat/:id',
    name: 'ChatPage',
    component: () => import('@/views/consult/ChatPage.vue'),
    meta: { title: '问诊对话' }
  },

  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 把未使用的 from 改成 _from 消除TS6133警告
router.beforeEach(async (to, _from, next) => {
  const token = localStorage.getItem('token')
  const whiteList = ['/login', '/register']

  if (!whiteList.includes(to.path) && !token) {
    return next('/login')
  }
  if (whiteList.includes(to.path)) {
    return next()
  }

  const userStr = localStorage.getItem('userInfo')
  if (!userStr) return next('/login')
  const user = JSON.parse(userStr)
  const targetMeta = to.meta

  if (targetMeta.role && targetMeta.role !== user.role) {
    if(user.role === 'patient') return next('/user/hall')
    if(user.role === 'doctor') return next('/doctor/apply')
    if(user.role === 'admin') return next('/admin/dashboard')
  }

  if(user.role === 'doctor'){
    try {
      const res = await axios.get('/api/doctor/my',{
        headers:{token}
      })
      const auditStatus = res.data.data?.auditStatus
      if(to.path === '/doctor'){
        if(auditStatus === 'pass'){
          return next('/doctor/hall')
        }else{
          return next('/doctor/apply')
        }
      }
      if(targetMeta.needAudit === true){
        if(auditStatus !== 'pass'){
          return next('/doctor/apply')
        }
      }
    }catch (e){
      return next('/doctor/apply')
    }
  }

  next()
})

export default router