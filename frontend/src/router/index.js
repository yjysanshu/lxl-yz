import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/daily-report',
    children: [
      {
        path: '/data-fetch',
        name: 'DataFetch',
        component: () => import('@/views/DataFetch.vue'),
        meta: { title: '数据抓取' }
      },
      {
        path: '/daily-report',
        name: 'DailyReport',
        component: () => import('@/views/DailyReport.vue'),
        meta: { title: '每日分销报表' }
      },
      {
        path: '/monthly-report',
        name: 'MonthlyReport',
        component: () => import('@/views/MonthlyReport.vue'),
        meta: { title: '月度汇总报表' }
      },
      {
        path: '/weekly-report',
        name: 'WeeklyReport',
        component: () => import('@/views/WeeklyReport.vue'),
        meta: { title: '周度汇总报表' }
      },
      {
        path: '/config',
        name: 'Config',
        component: () => import('@/views/Config.vue'),
        meta: { title: '系统配置' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
