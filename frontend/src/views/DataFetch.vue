<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>数据管理</span>
      </div>
    </template>
    <div class="content">
      <el-alert
        title="数据抓取说明"
        type="info"
        description="点击下方按钮手动触发数据抓取，系统也会每小时自动执行一次数据抓取任务。"
        :closable="false"
        style="margin-bottom: 20px"
      />
      <el-button type="primary" :loading="loading" @click="handleFetch" size="large">
        <el-icon><Refresh /></el-icon>
        <span>手动抓取数据</span>
      </el-button>
      <div v-if="lastFetchTime" style="margin-top: 20px; color: #909399">
        最后抓取时间：{{ lastFetchTime }}
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchData } from '@/api'

const loading = ref(false)
const lastFetchTime = ref('')

const handleFetch = async () => {
  loading.value = true
  try {
    await fetchData()
    ElMessage.success('数据抓取成功')
    lastFetchTime.value = new Date().toLocaleString('zh-CN')
  } catch (error) {
    console.error('数据抓取失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.content {
  padding: 20px;
}
</style>
