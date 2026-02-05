<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background-color: #304156">
      <div class="logo">
        <h2 style="color: white; text-align: center; padding: 20px 0">分销管理系统</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
        router
      >
        <el-menu-item index="/data-fetch">
          <el-icon><Upload /></el-icon>
          <span>数据抓取</span>
        </el-menu-item>
        <el-menu-item index="/daily-report">
          <el-icon><Document /></el-icon>
          <span>每日分销报表</span>
        </el-menu-item>
        <el-menu-item index="/monthly-report">
          <el-icon><DataAnalysis /></el-icon>
          <span>月度汇总报表</span>
        </el-menu-item>
        <el-menu-item index="/weekly-report">
          <el-icon><Calendar /></el-icon>
          <span>周度汇总报表</span>
        </el-menu-item>
        <el-menu-item index="/config">
          <el-icon><Setting /></el-icon>
          <span>系统配置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="background-color: #fff; box-shadow: 0 1px 4px rgba(0,21,41,.08)">
        <div style="display: flex; align-items: center; height: 100%">
          <h3>{{ pageTitle }}</h3>
        </div>
      </el-header>
      <el-main style="background-color: #f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const activeMenu = computed(() => route.path)
const pageTitle = computed(() => route.meta.title || '分销员订单数据管理系统')
</script>

<style scoped>
.el-menu-vertical {
  border-right: none;
}

.el-header {
  display: flex;
  align-items: center;
  padding: 0 20px;
}

.el-main {
  padding: 20px;
}
</style>
