package com.lxl.yz.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_distributor", indexes = {
    @Index(name = "idx_team_id", columnList = "team_id")
})
@Data
public class Distributor {

    @Id
    @Column(name = "distributor_id")
    private Long distributorId;

    @Column(name = "distributor_name", nullable = false)
    private String distributorName;

    @Column(name = "team_id")
    private Long teamId;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
