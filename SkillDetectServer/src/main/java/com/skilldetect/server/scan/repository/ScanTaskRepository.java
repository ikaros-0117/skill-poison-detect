package com.skilldetect.server.scan.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skilldetect.server.scan.domain.ScanTaskEntity;

public interface ScanTaskRepository extends JpaRepository<ScanTaskEntity, Long> {

    Optional<ScanTaskEntity> findByTaskNo(String taskNo);

    Page<ScanTaskEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ScanTaskEntity> findByStatusIn(Collection<String> statuses);

    List<ScanTaskEntity> findByFinishedAtBeforeAndStatusIn(Instant finishedAt, Collection<String> statuses);

    @Modifying
    @Query("update ScanTaskEntity t set t.status = 'RUNNING', t.startedAt = :now where t.taskNo = :taskNo and t.status = 'QUEUED'")
    int claimForDispatch(@Param("taskNo") String taskNo, @Param("now") Instant now);
}
