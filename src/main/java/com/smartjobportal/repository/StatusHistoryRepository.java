package com.smartjobportal.repository;
import com.smartjobportal.model.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Long> {
    List<ApplicationStatusHistory> findByApplicationIdOrderByChangedAtAsc(Long applicationId);
}
