package com.lxl.yz.repository;

import com.lxl.yz.entity.Distributor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistributorRepository extends JpaRepository<Distributor, Long> {
    List<Distributor> findByTeamId(Long teamId);
}
