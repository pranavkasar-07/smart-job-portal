package com.smartjobportal.repository;
import com.smartjobportal.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {
    boolean existsByCandidateIdAndJobId(Long candidateId, Long jobId);
    List<JobApplication> findByCandidateIdOrderByAppliedAtDesc(Long candidateId);
    List<JobApplication> findByJobIdAndJobRecruiterIdOrderByAppliedAtDesc(Long jobId, Long recruiterId);
    Optional<JobApplication> findByIdAndJobRecruiterId(Long id, Long recruiterId);
}
