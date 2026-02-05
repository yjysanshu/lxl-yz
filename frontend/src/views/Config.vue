<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>系统配置管理</span>
      </div>
    </template>
    
    <el-form :model="configForm" label-width="120px" style="max-width: 600px">
      <el-form-item label="店铺Token">
        <el-input
          v-model="configForm.shopToken"
          placeholder="请输入店铺Token"
          type="textarea"
          :rows="3"
        />
      </el-form-item>
      <el-form-item label="配置说明">
        <div style="color: #909399">用于调用第三方API获取订单数据</div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSave" :loading="loading">保存配置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getConfig, updateConfig } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const configForm = ref({
  shopToken: ''
})

const loadConfig = async () => {
  try {
    const res = await getConfig('shop_token')
    if (res.data) {
      configForm.value.shopToken = res.data.configValue || ''
    }
  } catch (error) {
    console.error('加载配置失败:', error)
  }
}

const handleSave = async () => {
  loading.value = true
  try {
    await updateConfig('shop_token', {
      configValue: configForm.value.shopToken
    })
    ElMessage.success('配置保存成功')
  } catch (error) {
    console.error('保存配置失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadConfig()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
