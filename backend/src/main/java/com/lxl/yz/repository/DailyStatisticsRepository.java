package com.lxl.yz.repository;

import com.lxl.yz.entity.DailyStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyStatisticsRepository extends JpaRepository<DailyStatistics, Long> {
    Optional<DailyStatistics> findByDistributorIdAndStatDate(Long distributorId, LocalDate statDate);
    
    List<DailyStatistics> findByStatDate(LocalDate statDate);
}
