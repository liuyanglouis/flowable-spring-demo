package com.example.demo;

import com.example.demo.entity.LeaveApplication;
import com.example.demo.service.LeaveApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final LeaveApplicationService leaveApplicationService;

    @Override
    public void run(String... args) throws Exception {
        log.info("初始化演示数据...");
        
        // 创建一些演示请假申请
        createSampleLeaveApplications();
        
        log.info("演示数据初始化完成");
    }

    private void createSampleLeaveApplications() {
        // 检查是否已有数据
        if (leaveApplicationService.getAllApplications().isEmpty()) {
            // 示例请假申请1
            LeaveApplication app1 = new LeaveApplication();
            app1.setApplicant("张三");
            app1.setDepartment("技术部");
            app1.setLeaveType("年假");
            app1.setStartDate(LocalDate.now().plusDays(1));
            app1.setEndDate(LocalDate.now().plusDays(5));
            app1.setReason("家庭旅游");
            app1.setStatus("DRAFT");
            leaveApplicationService.createLeaveApplication(app1);

            // 示例请假申请2
            LeaveApplication app2 = new LeaveApplication();
            app2.setApplicant("李四");
            app2.setDepartment("市场部");
            app2.setLeaveType("病假");
            app2.setStartDate(LocalDate.now());
            app2.setEndDate(LocalDate.now().plusDays(3));
            app2.setReason("感冒发烧");
            app2.setStatus("DRAFT");
            leaveApplicationService.createLeaveApplication(app2);

            // 示例请假申请3
            LeaveApplication app3 = new LeaveApplication();
            app3.setApplicant("王五");
            app3.setDepartment("销售部");
            app3.setLeaveType("事假");
            app3.setStartDate(LocalDate.now().plusDays(7));
            app3.setEndDate(LocalDate.now().plusDays(8));
            app3.setReason("办理个人事务");
            app3.setStatus("APPROVED");
            leaveApplicationService.createLeaveApplication(app3);

            log.info("创建了3个演示请假申请");
        }
    }
}
