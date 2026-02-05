<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>月度汇总报表</span>
      </div>
    </template>
    
    <el-form :inline="true" :model="queryForm" class="query-form">
      <el-form-item label="月份">
        <el-date-picker
          v-model="queryForm.yearMonth"
          type="month"
          placeholder="选择月份"
          format="YYYY-MM"
          value-format="YYYY-MM"
        />
      </el-form-item>
      <el-form-item label="团队">
        <el-select v-model="queryForm.teamId" placeholder="请选择团队" clearable>
          <el-option label="全部" :value="null" />
          <el-option
            v-for="team in teamList"
            :key="team.teamId"
            :label="team.teamName"
            :value="team.teamId"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" border stripe style="width: 100%">
      <el-table-column prop="distributorName" label="分销员名称" width="150" />
      <el-table-column prop="customerCount" label="成交人数" width="120" />
      <el-table-column prop="orderCount" label="已支付订单数" width="150" />
      <el-table-column prop="totalAmount" label="订单总金额（元）" width="150">
        <template #default="{ row }">
          ¥{{ row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column prop="avgPrice" label="客单价（元）">
        <template #default="{ row }">
          ¥{{ row.avgPrice }}
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMonthlyReport, getTeamList } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const teamList = ref([])

// 获取当前年月
const getCurrentYearMonth = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  return `${year}-${month}`
}

const queryForm = ref({
  yearMonth: getCurrentYearMonth(),
  teamId: null
})

const loadTeamList = async () => {
  try {
    const res = await getTeamList()
    teamList.value = res.data || []
  } catch (error) {
    console.error('加载团队列表失败:', error)
  }
}

const handleQuery = async () => {
  if (!queryForm.value.yearMonth) {
    ElMessage.warning('请选择月份')
    return
  }
  
  loading.value = true
  try {
    const params = {
      yearMonth: queryForm.value.yearMonth
    }
    if (queryForm.value.teamId) {
      params.teamId = queryForm.value.teamId
    }
    const res = await getMonthlyReport(params)
    tableData.value = res.data || []
  } catch (error) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTeamList()
  handleQuery()
})
</script>

<style scoped>
.query-form {
  margin-bottom: 20px;
}
</style>
