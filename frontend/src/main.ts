import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 新增路由导入
import router from './router'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)  // 注册路由
app.mount('#app')