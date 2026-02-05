package com.lxl.yz.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_config", uniqueConstraints = {
    @UniqueConstraint(name = "uk_config_key", columnNames = "config_key")
})
@Data
public class Config {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "config_key", nullable = false, unique = true)
    private String configKey;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;

    @Column(name = "config_desc")
    private String configDesc;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}
