package com.smartjobportal.repository;
import com.smartjobportal.model.Interview;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.List;
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByCandidateIdOrderByStartTimeAsc(Long candidateId);
    @Query("select count(i) from Interview i where i.candidate.id=:candidateId and i.cancelled=false and i.startTime < :endTime and i.endTime > :startTime")
    long countOverlaps(@Param("candidateId") Long candidateId, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
}
