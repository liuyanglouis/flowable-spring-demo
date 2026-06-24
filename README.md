# Flowable Spring Boot Demo - 请假审批流程示例

一个使用Flowable工作流引擎和Spring Boot的入门案例，实现了请假审批流程。

## 项目简介

本项目演示了如何使用Flowable BPMN工作流引擎集成到Spring Boot应用中，实现一个完整的请假审批流程。通过这个示例，您可以快速了解：

- Flowable与Spring Boot的集成方式
- BPMN 2.0流程定义
- 流程实例的启动和管理
- 用户任务的处理
- 流程历史追踪
- RESTful API设计

## 技术栈

- **Spring Boot 3.2.5** - 应用框架
- **Flowable 7.0.2** - 工作流引擎
- **H2 Database** - 嵌入式数据库
- **Spring Data JPA** - 数据持久化
- **Lombok** - 简化代码

## 功能特性

### 请假审批流程
1. **开始事件** - 提交请假申请
2. **部门经理审批** - 用户任务，需要部门经理审批
3. **审批决定** - 排他网关，根据审批结果决定下一步
4. **人事备案** - 服务任务，审批通过后进行人事备案
5. **结束事件** - 流程结束

### API接口
- 请假申请管理（CRUD操作）
- 流程启动和管理
- 待办任务查询和处理
- 流程历史查询

## 快速开始

### 1. 环境要求
- Java 17+
- Maven 3.8+

### 2. 克隆项目
```bash
git clone https://github.com/yourusername/flowable-spring-demo.git
cd flowable-spring-demo
```

### 3. 编译和运行
```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

应用启动后，访问：
- 主页面：http://localhost:8080
- H2数据库控制台：http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (空)

### 4. 演示数据
应用启动时会自动创建3个演示请假申请：
1. 张三 - 技术部 - 年假 - 待审批
2. 李四 - 市场部 - 病假 - 待审批  
3. 王五 - 销售部 - 事假 - 已批准

## API使用示例

### 1. 创建请假申请
```bash
curl -X POST http://localhost:8080/api/leave \
  -H "Content-Type: application/json" \
  -d '{
    "applicant": "赵六",
    "department": "财务部",
    "leaveType": "事假",
    "startDate": "2024-06-25",
    "endDate": "2024-06-28",
    "reason": "处理个人事务"
  }'
```

### 2. 启动审批流程
```bash
curl -X POST http://localhost:8080/api/leave/1/start-process
```

### 3. 查询待办任务
```bash
curl "http://localhost:8080/api/leave/tasks?assignee=department_manager"
```

### 4. 完成任务
```bash
curl -X POST http://localhost:8080/api/leave/tasks/{taskId}/complete \
  -H "Content-Type: application/json" \
  -d '{
    "approvalDecision": "approve",
    "approvalComments": "同意请假申请"
  }'
```

### 5. 查询流程历史
```bash
curl "http://localhost:8080/api/leave/process/{processInstanceId}/history"
```

## 项目结构

```
flowable-spring-demo/
├── src/main/java/com/example/demo/
│   ├── FlowableSpringDemoApplication.java    # Spring Boot应用入口
│   ├── DataInitializer.java                  # 数据初始化器
│   ├── controller/
│   │   └── LeaveApplicationController.java   # REST控制器
│   ├── entity/
│   │   └── LeaveApplication.java             # 请假申请实体
│   ├── repository/
│   │   └── LeaveApplicationRepository.java   # 数据访问层
│   └── service/
│       ├── LeaveApplicationService.java      # 请假申请服务
│       ├── LeaveProcessService.java          # 流程处理服务
│       └── HrRecordService.java              # 人事备案服务
├── src/main/resources/
│   ├── application.yml                       # 应用配置
│   ├── processes/
│   │   └── leave-request.bpmn20.xml          # BPMN流程定义
│   └── templates/
│       └── api-docs.html                     # API文档页面
├── pom.xml                                   # Maven依赖配置
└── README.md                                 # 项目说明文档
```

## 流程定义详解

### BPMN文件位置
`src/main/resources/processes/leave-request.bpmn20.xml`

### 主要流程元素
1. **startEvent** - 开始节点，收集请假信息
2. **userTask** - 用户任务，部门经理审批
3. **exclusiveGateway** - 排他网关，根据审批结果分支
4. **serviceTask** - 服务任务，人事备案处理
5. **endEvent** - 结束节点

### 流程变量
- `applicant` - 申请人
- `leaveType` - 请假类型
- `duration` - 请假时长
- `reason` - 请假原因
- `approvalDecision` - 审批决定 (approve/reject)
- `approvalComments` - 审批意见

## Flowable集成要点

### 1. 依赖配置
```xml
<dependency>
    <groupId>org.flowable</groupId>
    <artifactId>flowable-spring-boot-starter</artifactId>
    <version>7.0.2</version>
</dependency>
```

### 2. 配置参数
```yaml
flowable:
  async-executor-activate: false
  history-level: audit
  db-history-used: true
  check-process-definitions: false
  database-schema-update: true
```

### 3. 核心服务
- **RuntimeService** - 流程运行时管理
- **TaskService** - 任务管理
- **HistoryService** - 历史数据查询
- **RepositoryService** - 流程定义管理

## 学习资源

### Flowable官方文档
- [Flowable官方文档](https://www.flowable.com/open-source/docs/)
- [BPMN 2.0规范](https://www.omg.org/spec/BPMN/2.0/)

### Spring Boot文档
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Spring Data JPA文档](https://spring.io/projects/spring-data-jpa)

## 常见问题

### 1. 启动时Flowable相关错误
- 检查数据库连接配置
- 确保H2依赖正确引入
- 验证BPMN文件格式正确

### 2. 流程不执行
- 检查流程定义是否正确部署
- 确认流程变量是否正确设置
- 验证任务指派表达式

### 3. 数据不持久化
- 检查JPA配置
- 确认@Entity注解正确
- 验证@Repository接口

## 贡献指南

欢迎提交Issue和Pull Request来改进这个项目。

## 许可证

MIT License
