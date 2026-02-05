# 开发指南

## 项目架构

### 技术架构
```
┌─────────────┐      HTTP       ┌─────────────┐
│             │ ◀────────────▶  │             │
│   Vue 3     │   REST API      │  Spring     │
│  Frontend   │                 │   Boot 3    │
│             │                 │   Backend   │
└─────────────┘                 └──────┬──────┘
                                       │
                                       │ JDBC
                                       ▼
                                ┌─────────────┐
                                │             │
                                │    MySQL    │
                                │             │
                                └─────────────┘
```

### 后端架构

```
Controller Layer (控制器层)
    ↓
Service Layer (业务逻辑层)
    ↓
Repository Layer (数据访问层)
    ↓
Entity Layer (实体层)
```

## 开发环境搭建

### IDE 推荐

**后端开发：**
- IntelliJ IDEA（推荐）
- Eclipse

**前端开发：**
- Visual Studio Code（推荐）
- WebStorm

### 必要的 IDE 插件

**IntelliJ IDEA：**
- Lombok（必需）
- Spring Boot Helper
- MyBatisX

**VS Code：**
- Volar（Vue 3 支持）
- ESLint
- Prettier

## 代码规范

### Java 代码规范

1. **命名规范**
   - 类名：大驼峰（PascalCase）
   - 方法名和变量：小驼峰（camelCase）
   - 常量：全大写，下划线分隔（UPPER_SNAKE_CASE）

2. **注释规范**
   ```java
   /**
    * 类功能描述
    * @author your-name
    * @date 2024-01-01
    */
   public class Example {
       
       /**
        * 方法功能描述
        * @param param1 参数说明
        * @return 返回值说明
        */
       public String method(String param1) {
           // 代码注释
           return param1;
       }
   }
   ```

3. **异常处理**
   - 使用 try-catch 捕获异常
   - 记录详细的错误日志
   - 向前端返回友好的错误信息

### JavaScript/Vue 代码规范

1. **命名规范**
   - 组件名：大驼峰（PascalCase）
   - 变量和方法：小驼峰（camelCase）
   - 常量：全大写，下划线分隔

2. **Vue 组件结构**
   ```vue
   <template>
     <!-- HTML 模板 -->
   </template>

   <script setup>
   // 导入
   import { ref } from 'vue'
   
   // 响应式数据
   const data = ref(null)
   
   // 方法
   const method = () => {
     // ...
   }
   </script>

   <style scoped>
   /* 样式 */
   </style>
   ```

## 核心功能开发

### 1. 添加新的实体类

**步骤：**

1. 在 `backend/src/main/java/com/lxl/yz/entity/` 创建实体类
   ```java
   @Entity
   @Table(name = "t_example")
   @Data
   public class Example {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       @Column(name = "name")
       private String name;
   }
   ```

2. 在数据库中创建对应的表
   ```sql
   CREATE TABLE t_example (
       id BIGINT PRIMARY KEY AUTO_INCREMENT,
       name VARCHAR(255)
   );
   ```

### 2. 添加新的 Repository

在 `backend/src/main/java/com/lxl/yz/repository/` 创建接口：

```java
@Repository
public interface ExampleRepository extends JpaRepository<Example, Long> {
    // 自定义查询方法
    List<Example> findByName(String name);
}
```

### 3. 添加新的 Service

在 `backend/src/main/java/com/lxl/yz/service/` 创建服务类：

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class ExampleService {
    
    private final ExampleRepository exampleRepository;
    
    public List<Example> getAllExamples() {
        return exampleRepository.findAll();
    }
}
```

### 4. 添加新的 Controller

在 `backend/src/main/java/com/lxl/yz/controller/` 创建控制器：

```java
@RestController
@RequestMapping("/api/example")
@Slf4j
@RequiredArgsConstructor
public class ExampleController {
    
    private final ExampleService exampleService;
    
    @GetMapping("/list")
    public ApiResponse<List<Example>> list() {
        try {
            List<Example> examples = exampleService.getAllExamples();
            return ApiResponse.success(examples);
        } catch (Exception e) {
            log.error("查询失败", e);
            return ApiResponse.error("查询失败：" + e.getMessage());
        }
    }
}
```

### 5. 添加新的前端页面

**步骤：**

1. 在 `frontend/src/views/` 创建页面组件
   ```vue
   <template>
     <el-card>
       <template #header>
         <span>示例页面</span>
       </template>
       <div>内容</div>
     </el-card>
   </template>

   <script setup>
   import { ref, onMounted } from 'vue'
   
   const data = ref([])
   
   onMounted(() => {
     // 加载数据
   })
   </script>
   ```

2. 在 `frontend/src/router/index.js` 添加路由
   ```javascript
   {
     path: '/example',
     name: 'Example',
     component: () => import('@/views/Example.vue'),
     meta: { title: '示例页面' }
   }
   ```

3. 在 `frontend/src/components/Layout.vue` 添加菜单项
   ```vue
   <el-menu-item index="/example">
     <el-icon><Document /></el-icon>
     <span>示例页面</span>
   </el-menu-item>
   ```

### 6. 添加新的 API 接口

在 `frontend/src/api/index.js` 添加：

```javascript
export function getExampleList() {
  return request({
    url: '/example/list',
    method: 'get'
  })
}
```

## 调试技巧

### 后端调试

1. **日志调试**
   ```java
   log.info("信息日志");
   log.debug("调试日志");
   log.error("错误日志", exception);
   ```

2. **断点调试**
   - 在 IDE 中设置断点
   - 以调试模式启动应用
   - 使用步进功能追踪代码执行

3. **SQL 调试**
   在 `application.properties` 启用 SQL 日志：
   ```properties
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.format_sql=true
   logging.level.org.hibernate.SQL=DEBUG
   ```

### 前端调试

1. **Console 调试**
   ```javascript
   console.log('调试信息', data)
   console.error('错误信息', error)
   ```

2. **Vue Devtools**
   - 安装 Vue Devtools 浏览器扩展
   - 查看组件状态和事件

3. **Network 调试**
   - 打开浏览器开发者工具
   - 查看 Network 标签页
   - 检查 API 请求和响应

## 测试指南

### 后端单元测试

创建测试类 `backend/src/test/java/com/lxl/yz/service/ExampleServiceTest.java`：

```java
@SpringBootTest
public class ExampleServiceTest {
    
    @Autowired
    private ExampleService exampleService;
    
    @Test
    public void testGetAllExamples() {
        List<Example> examples = exampleService.getAllExamples();
        assertNotNull(examples);
    }
}
```

### API 测试

使用 Postman 或 curl 测试 API：

```bash
# 测试每日报表
curl "http://localhost:8080/api/report/daily?date=2024-01-01"

# 测试数据抓取
curl -X POST "http://localhost:8080/api/data/fetch"
```

## 常用开发命令

### Maven 命令

```bash
# 清理编译
mvn clean compile

# 打包（跳过测试）
mvn clean package -DskipTests

# 运行测试
mvn test

# 查看依赖树
mvn dependency:tree
```

### npm 命令

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

## Git 工作流

### 分支管理

```bash
# 创建功能分支
git checkout -b feature/new-feature

# 提交代码
git add .
git commit -m "feat: 添加新功能"

# 推送分支
git push origin feature/new-feature
```

### 提交信息规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构代码
test: 测试相关
chore: 构建/工具相关
```

## 性能优化

### 后端优化

1. **数据库查询优化**
   - 使用索引
   - 避免 N+1 查询
   - 使用批量操作

2. **缓存策略**
   - 添加 Redis 缓存
   - 使用 @Cacheable 注解

3. **异步处理**
   - 使用 @Async 注解
   - CompletableFuture

### 前端优化

1. **组件懒加载**
   ```javascript
   component: () => import('@/views/Page.vue')
   ```

2. **图片优化**
   - 使用合适的图片格式
   - 图片懒加载

3. **代码分割**
   - Vite 自动进行代码分割
   - 合理组织组件结构

## 安全最佳实践

1. **SQL 注入防护**
   - 使用参数化查询
   - JPA 已提供保护

2. **XSS 防护**
   - Vue 3 默认转义 HTML
   - 避免使用 v-html

3. **CSRF 防护**
   - 配置 Spring Security（如需要）

4. **敏感信息保护**
   - 不在前端存储敏感信息
   - 使用环境变量管理配置

## 部署清单

部署前检查：
- [ ] 数据库配置正确
- [ ] 日志级别适合生产环境
- [ ] 关闭 SQL 日志输出
- [ ] 配置好错误处理
- [ ] 测试所有核心功能
- [ ] 准备好数据库备份方案
- [ ] 配置监控和告警

## 资源链接

- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [Vue 3 文档](https://vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)
- [MySQL 文档](https://dev.mysql.com/doc/)

## 获取帮助

遇到问题时：
1. 查看日志文件
2. 搜索错误信息
3. 查阅官方文档
4. 提交 Issue

祝开发愉快！
