package com.example.demo.service;

import com.example.demo.entity.LeaveApplication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeaveProcessService {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private LeaveApplicationService leaveApplicationService;

    /**
     * 启动请假流程
     */
    @Transactional
    public ProcessInstance startLeaveProcess(LeaveApplication leaveApplication) {
        // 设置流程变量
        Map<String, Object> variables = new HashMap<>();
        variables.put("applicant", leaveApplication.getApplicant());
        variables.put("department", leaveApplication.getDepartment());
        variables.put("leaveType", leaveApplication.getLeaveType());
        variables.put("duration", leaveApplication.getDuration());
        variables.put("reason", leaveApplication.getReason());
        variables.put("manager", "department_manager"); // 部门经理（实际中可以从数据库获取）

        // 启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
            "leaveRequest", 
            String.valueOf(leaveApplication.getId()), // 业务键
            variables
        );

        // 更新请假申请的状态和流程实例ID
        leaveApplication.setProcessInstanceId(processInstance.getId());
        leaveApplication.setStatus("PENDING");
        leaveApplicationService.save(leaveApplication);

        return processInstance;
    }

    /**
     * 获取待办任务列表
     */
    public List<Task> getPendingTasks(String assignee) {
        return taskService.createTaskQuery()
            .taskAssignee(assignee)
            .orderByTaskCreateTime().desc()
            .list();
    }

    /**
     * 完成任务
     */
    @Transactional
    public void completeTask(String taskId, Map<String, Object> variables) {
        // 完成任务
        taskService.complete(taskId, variables);

        // 获取任务对应的请假申请
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task != null) {
            String processInstanceId = task.getProcessInstanceId();
            LeaveApplication leaveApplication = leaveApplicationService
                .findByProcessInstanceId(processInstanceId);
            
            // 根据审批结果更新状态
            String decision = (String) variables.get("approvalDecision");
            if ("approve".equals(decision)) {
                leaveApplication.setStatus("APPROVED");
            } else if ("reject".equals(decision)) {
                leaveApplication.setStatus("REJECTED");
            }
            
            leaveApplication.setApprovalComments((String) variables.get("approvalComments"));
            leaveApplicationService.save(leaveApplication);
        }
    }

    /**
     * 获取流程历史信息
     */
    public List<Map<String, Object>> getProcessHistory(String processInstanceId) {
        return historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId)
            .orderByHistoricActivityInstanceStartTime().asc()
            .list()
            .stream()
            .map(activity -> {
                Map<String, Object> activityInfo = new HashMap<>();
                activityInfo.put("activityId", activity.getActivityId());
                activityInfo.put("activityName", activity.getActivityName());
                activityInfo.put("activityType", activity.getActivityType());
                activityInfo.put("startTime", activity.getStartTime());
                activityInfo.put("endTime", activity.getEndTime());
                activityInfo.put("duration", activity.getDurationInMillis());
                return activityInfo;
            })
            .toList();
    }
}
