package com.smartjobportal.repository;
import com.smartjobportal.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {}
