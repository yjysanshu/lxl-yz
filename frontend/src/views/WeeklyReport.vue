<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>周度汇总报表</span>
      </div>
    </template>
    
    <el-form :inline="true" :model="queryForm" class="query-form">
      <el-form-item label="年份">
        <el-select v-model="queryForm.year" placeholder="请选择年份">
          <el-option
            v-for="year in yearOptions"
            :key="year"
            :label="year"
            :value="year"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="周数">
        <el-select v-model="queryForm.week" placeholder="请选择周数">
          <el-option
            v-for="week in weekOptions"
            :key="week"
            :label="`第${week}周`"
            :value="week"
          />
        </el-select>
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
import { getWeeklyReport, getTeamList } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const teamList = ref([])

// 生成年份选项（当前年和前后两年）
const yearOptions = ref([])
const currentYear = new Date().getFullYear()
for (let i = currentYear - 2; i <= currentYear + 1; i++) {
  yearOptions.value.push(i)
}

// 生成周数选项（1-53周）
const weekOptions = ref([])
for (let i = 1; i <= 53; i++) {
  weekOptions.value.push(i)
}

// 获取当前周数
const getCurrentWeek = () => {
  const now = new Date()
  const start = new Date(now.getFullYear(), 0, 1)
  const diff = now - start + ((start.getTimezoneOffset() - now.getTimezoneOffset()) * 60 * 1000)
  const oneWeek = 1000 * 60 * 60 * 24 * 7
  return Math.floor(diff / oneWeek) + 1
}

const queryForm = ref({
  year: currentYear,
  week: getCurrentWeek(),
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
  if (!queryForm.value.year || !queryForm.value.week) {
    ElMessage.warning('请选择年份和周数')
    return
  }
  
  loading.value = true
  try {
    const params = {
      year: queryForm.value.year,
      week: queryForm.value.week
    }
    if (queryForm.value.teamId) {
      params.teamId = queryForm.value.teamId
    }
    const res = await getWeeklyReport(params)
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
