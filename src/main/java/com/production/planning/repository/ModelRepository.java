package com.production.planning.repository;

import com.production.planning.entity.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {
    @Query("SELECT m FROM Model m WHERE m.active = true")
    List<Model> findAllActiveModels();
}
