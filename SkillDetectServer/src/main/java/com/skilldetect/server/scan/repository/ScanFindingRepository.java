package com.skilldetect.server.scan.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.skilldetect.server.scan.domain.ScanFindingEntity;

public interface ScanFindingRepository
        extends JpaRepository<ScanFindingEntity, Long>, JpaSpecificationExecutor<ScanFindingEntity> {

    List<ScanFindingEntity> findByTaskId(Long taskId);

    void deleteByTaskIdIn(Collection<Long> taskIds);
}
