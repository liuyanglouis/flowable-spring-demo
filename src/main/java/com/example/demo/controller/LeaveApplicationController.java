package com.example.demo.controller;

import com.example.demo.entity.LeaveApplication;
import com.example.demo.service.LeaveApplicationService;
import com.example.demo.service.LeaveProcessService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LeaveApplicationController {

    private final LeaveApplicationService leaveApplicationService;
    private final LeaveProcessService leaveProcessService;

    /**
     * 创建请假申请
     */
    @PostMapping
    public ResponseEntity<LeaveApplication> createLeaveApplication(
            @RequestBody LeaveApplication leaveApplication) {
        LeaveApplication created = leaveApplicationService.createLeaveApplication(leaveApplication);
        return ResponseEntity.ok(created);
    }

    /**
     * 启动请假流程
     */
    @PostMapping("/{id}/start-process")
    public ResponseEntity<Map<String, Object>> startLeaveProcess(@PathVariable Long id) {
        LeaveApplication leaveApplication = leaveApplicationService.getById(id);
        ProcessInstance processInstance = leaveProcessService.startLeaveProcess(leaveApplication);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "请假流程已启动");
        response.put("processInstanceId", processInstance.getId());
        response.put("processDefinitionId", processInstance.getProcessDefinitionId());
        response.put("leaveApplication", leaveApplication);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有请假申请
     */
    @GetMapping("getAllApp")
    public ResponseEntity<List<LeaveApplication>> getAllApplications() {
        List<LeaveApplication> applications = leaveApplicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    /**
     * 根据ID获取请假申请
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeaveApplication> getApplicationById(@PathVariable Long id) {
        LeaveApplication application = leaveApplicationService.getById(id);
        return ResponseEntity.ok(application);
    }

    /**
     * 根据申请人获取请假申请
     */
    @GetMapping("/applicant/{applicant}")
    public ResponseEntity<List<LeaveApplication>> getApplicationsByApplicant(
            @PathVariable String applicant) {
        List<LeaveApplication> applications = leaveApplicationService.getByApplicant(applicant);
        return ResponseEntity.ok(applications);
    }

    /**
     * 获取待办任务
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<Task>> getPendingTasks(
            @RequestParam(required = false, defaultValue = "department_manager") String assignee) {
        List<Task> tasks = leaveProcessService.getPendingTasks(assignee);
        return ResponseEntity.ok(tasks);
    }

    /**
     * 完成任务
     */
    @PostMapping("/tasks/{taskId}/complete")
    public ResponseEntity<Map<String, Object>> completeTask(
            @PathVariable String taskId,
            @RequestBody Map<String, Object> variables) {
        leaveProcessService.completeTask(taskId, variables);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "任务已完成");
        response.put("taskId", taskId);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取流程历史
     */
    @GetMapping("/process/{processInstanceId}/history")
    public ResponseEntity<List<Map<String, Object>>> getProcessHistory(
            @PathVariable String processInstanceId) {
        List<Map<String, Object>> history = leaveProcessService.getProcessHistory(processInstanceId);
        return ResponseEntity.ok(history);
    }

    /**
     * 更新请假申请
     */
    @PutMapping("/{id}")
    public ResponseEntity<LeaveApplication> updateLeaveApplication(
            @PathVariable Long id,
            @RequestBody LeaveApplication leaveApplication) {
        leaveApplication.setId(id);
        LeaveApplication updated = leaveApplicationService.update(leaveApplication);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除请假申请
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteLeaveApplication(@PathVariable Long id) {
        leaveApplicationService.delete(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "请假申请已删除");
        response.put("id", id);
        
        return ResponseEntity.ok(response);
    }
}
