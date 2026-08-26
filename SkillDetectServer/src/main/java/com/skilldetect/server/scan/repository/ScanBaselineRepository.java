package com.skilldetect.server.scan.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skilldetect.server.scan.domain.ScanBaselineEntity;

public interface ScanBaselineRepository extends JpaRepository<ScanBaselineEntity, Long> {

    List<ScanBaselineEntity> findAllByOrderByCreatedAtDesc();
}
