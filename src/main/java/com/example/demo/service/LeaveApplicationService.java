package com.example.demo.service;

import com.example.demo.entity.LeaveApplication;
import com.example.demo.repository.LeaveApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveApplicationService {

    private final LeaveApplicationRepository leaveApplicationRepository;

    /**
     * 创建请假申请
     */
    public LeaveApplication createLeaveApplication(LeaveApplication leaveApplication) {
        // 计算请假时长
        if (leaveApplication.getStartDate() != null && leaveApplication.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(leaveApplication.getStartDate(), leaveApplication.getEndDate());
            leaveApplication.setDuration((double) days);
        }
        return leaveApplicationRepository.save(leaveApplication);
    }

    /**
     * 获取所有请假申请
     */
    public List<LeaveApplication> getAllApplications() {
        return leaveApplicationRepository.findAll();
    }

    /**
     * 根据ID获取请假申请
     */
    public LeaveApplication getById(Long id) {
        return leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("请假申请不存在"));
    }

    /**
     * 根据申请人获取请假申请
     */
    public List<LeaveApplication> getByApplicant(String applicant) {
        return leaveApplicationRepository.findByApplicant(applicant);
    }

    /**
     * 根据状态获取请假申请
     */
    public List<LeaveApplication> getByStatus(String status) {
        return leaveApplicationRepository.findByStatus(status);
    }

    /**
     * 根据流程实例ID获取请假申请
     */
    public LeaveApplication findByProcessInstanceId(String processInstanceId) {
        return leaveApplicationRepository.findByProcessInstanceId(processInstanceId);
    }

    /**
     * 更新请假申请
     */
    public LeaveApplication update(LeaveApplication leaveApplication) {
        if (!leaveApplicationRepository.existsById(leaveApplication.getId())) {
            throw new RuntimeException("请假申请不存在");
        }
        return leaveApplicationRepository.save(leaveApplication);
    }

    /**
     * 删除请假申请
     */
    public void delete(Long id) {
        if (!leaveApplicationRepository.existsById(id)) {
            throw new RuntimeException("请假申请不存在");
        }
        leaveApplicationRepository.deleteById(id);
    }

    /**
     * 保存请假申请
     */
    public LeaveApplication save(LeaveApplication leaveApplication) {
        return leaveApplicationRepository.save(leaveApplication);
    }
}
