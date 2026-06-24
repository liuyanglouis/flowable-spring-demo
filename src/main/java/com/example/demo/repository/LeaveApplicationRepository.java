package com.example.demo.repository;

import com.example.demo.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByApplicant(String applicant);
    List<LeaveApplication> findByStatus(String status);
    LeaveApplication findByProcessInstanceId(String processInstanceId);
}
