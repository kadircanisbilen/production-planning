package com.production.planning.repository;

import com.production.planning.entity.ProductionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, Long> {
    List<ProductionPlan> findByProjectId(Long projectId);
}
