package com.production.planning.repository;

import com.production.planning.entity.ModelPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelPartRepository extends JpaRepository<ModelPart, Long> {
}
