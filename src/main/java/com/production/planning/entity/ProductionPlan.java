package com.production.planning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.YearMonth;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "production_plans")
public class ProductionPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private String month;

    private String week;

    @Column(nullable = false)
    private Integer totalProduction;

    @OneToMany(mappedBy = "productionPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionPlanDetail> productionPlanDetails;
}
