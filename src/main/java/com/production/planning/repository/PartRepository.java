package com.production.planning.repository;

import com.production.planning.entity.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {
    @Query("SELECT p FROM Part p WHERE p.active = true")
    List<Part> findAllActiveParts();
}
