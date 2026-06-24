package com.example.demo.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "leave_application")
public class LeaveApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String applicant;  // 申请人
    
    @Column(nullable = false)
    private String department; // 部门
    
    @Column(nullable = false)
    private String leaveType;  // 请假类型
    
    @Column(nullable = false)
    private LocalDate startDate; // 开始日期
    
    @Column(nullable = false)
    private LocalDate endDate;   // 结束日期
    
    @Column
    private Double duration;     // 请假时长（天）
    
    @Column
    private String reason;       // 请假原因
    
    @Column
    private String status;       // 状态：DRAFT, PENDING, APPROVED, REJECTED
    
    @Column
    private String approver;     // 审批人
    
    @Column
    private String approvalComments; // 审批意见
    
    @Column
    private LocalDateTime createTime; // 创建时间
    
    @Column
    private LocalDateTime updateTime; // 更新时间
    
    @Column
    private String processInstanceId; // 流程实例ID
    
    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (status == null) {
            status = "DRAFT";
        }
    }
    
    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}
