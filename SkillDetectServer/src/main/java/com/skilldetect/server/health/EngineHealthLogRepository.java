package com.skilldetect.server.health;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EngineHealthLogRepository extends JpaRepository<EngineHealthLogEntity, Long> {
}
