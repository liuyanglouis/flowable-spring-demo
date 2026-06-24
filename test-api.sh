#!/bin/bash

# Flowable Spring Demo API测试脚本
# 请确保应用已启动：mvn spring-boot:run

BASE_URL="http://localhost:8080/api/leave"
echo "🚀 Flowable Spring Demo API测试脚本"
echo "==================================="

# 等待应用启动
echo "等待应用启动..."
sleep 5

echo ""
echo "1. 获取所有请假申请"
curl -s "$BASE_URL" | python3 -m json.tool

echo ""
echo "2. 创建新的请假申请"
CREATE_RESPONSE=$(curl -s -X POST "$BASE_URL" \
  -H "Content-Type: application/json" \
  -d '{
    "applicant": "测试用户",
    "department": "测试部门",
    "leaveType": "年假",
    "startDate": "2024-06-26",
    "endDate": "2024-06-30",
    "reason": "测试请假"
  }')

echo "$CREATE_RESPONSE" | python3 -m json.tool

# 获取新创建的请假申请ID
APPLICATION_ID=$(echo "$CREATE_RESPONSE" | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "创建的请假申请ID: $APPLICATION_ID"

echo ""
echo "3. 启动请假审批流程"
if [ -n "$APPLICATION_ID" ]; then
    curl -s -X POST "$BASE_URL/$APPLICATION_ID/start-process" | python3 -m json.tool
fi

echo ""
echo "4. 查询待办任务"
curl -s "$BASE_URL/tasks" | python3 -m json.tool

echo ""
echo "5. 获取流程历史（需要获取到流程实例ID后手动测试）"
echo "curl -s \"$BASE_URL/process/{processInstanceId}/history\" | python3 -m json.tool"

echo ""
echo "📋 API端点列表："
echo "1. GET    $BASE_URL                     - 获取所有请假申请"
echo "2. POST   $BASE_URL                     - 创建请假申请"
echo "3. GET    $BASE_URL/{id}                - 获取指定请假申请"
echo "4. POST   $BASE_URL/{id}/start-process  - 启动审批流程"
echo "5. GET    $BASE_URL/tasks               - 获取待办任务"
echo "6. POST   $BASE_URL/tasks/{taskId}/complete - 完成任务"
echo "7. GET    $BASE_URL/process/{processInstanceId}/history - 流程历史"

echo ""
echo "💡 提示："
echo "- 启动应用: mvn spring-boot:run"
echo "- 访问H2控制台: http://localhost:8080/h2-console"
echo "- API文档: http://localhost:8080"
echo "- 默认部门经理: department_manager"
