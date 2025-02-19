package com.production.planning.repository;

import com.production.planning.entity.ProductionPlan;
import com.production.planning.entity.ProductionPlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProductionPlanDetailRepository extends JpaRepository<ProductionPlanDetail, Long> {
    List<ProductionPlanDetail> findByProductionPlan(ProductionPlan productionPlan);
    List<ProductionPlanDetail> findAllByProductionPlanIdIn(List<Long> productionPlanIds);
    List<ProductionPlanDetail> findByModelId(Long modelId);

    @Query("SELECT ppd FROM ProductionPlanDetail ppd " +
            "JOIN ppd.productionPlan pp " +
            "WHERE pp.project.id = :projectId " +
            "AND pp.month BETWEEN :startDate AND :endDate")
    List<ProductionPlanDetail> findByProjectIdAndDateRange(
            @Param("projectId") Long projectId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    @Modifying
    @Transactional
    @Query("UPDATE ProductionPlanDetail p SET p.percentage = :percentage WHERE p.model.id = :modelId AND p.productionPlan.month = :month")
    void updatePercentageByModelIdAndMonth(@Param("modelId") Long modelId, @Param("month") String month, @Param("percentage") Double percentage);

}
