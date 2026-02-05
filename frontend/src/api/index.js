import request from '@/utils/request'

// 手动抓取数据
export function fetchData() {
  return request({
    url: '/data/fetch',
    method: 'post'
  })
}

// 获取每日报表
export function getDailyReport(params) {
  return request({
    url: '/report/daily',
    method: 'get',
    params
  })
}

// 获取月度报表
export function getMonthlyReport(params) {
  return request({
    url: '/report/monthly',
    method: 'get',
    params
  })
}

// 获取周度报表
export function getWeeklyReport(params) {
  return request({
    url: '/report/weekly',
    method: 'get',
    params
  })
}

// 获取团队列表
export function getTeamList() {
  return request({
    url: '/team/list',
    method: 'get'
  })
}

// 获取配置
export function getConfig(key) {
  return request({
    url: `/config/${key}`,
    method: 'get'
  })
}

// 更新配置
export function updateConfig(key, data) {
  return request({
    url: `/config/${key}`,
    method: 'put',
    data
  })
}
