package org.example.hondasupercub.repo;

import org.example.hondasupercub.entity.VinHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminVinHistoryRepo extends JpaRepository<VinHistory, Integer> {
}