package com.example.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HrRecordService implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String businessKey = execution.getProcessInstanceBusinessKey();
        String applicant = (String) execution.getVariable("applicant");
        String leaveType = (String) execution.getVariable("leaveType");
        Double duration = (Double) execution.getVariable("duration");
        String reason = (String) execution.getVariable("reason");
        
        log.info("人事备案处理 - 流程实例ID: {}", processInstanceId);
        log.info("请假申请信息 - 申请人: {}, 请假类型: {}, 时长: {}天", 
                 applicant, leaveType, duration);
        log.info("请假原因: {}", reason);
        log.info("业务键: {}", businessKey);
        
        // 这里可以添加实际的人事备案逻辑，如：
        // 1. 保存到人事系统
        // 2. 发送邮件通知
        // 3. 更新考勤记录
        // 4. 发送系统通知等
        
        log.info("人事备案完成");
    }
}
