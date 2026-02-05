-- 创建数据库
CREATE DATABASE IF NOT EXISTS distributor_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE distributor_db;

-- 团队表
CREATE TABLE IF NOT EXISTS t_team (
    team_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    team_name VARCHAR(255) NOT NULL UNIQUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队表';

-- 分销员表
CREATE TABLE IF NOT EXISTS t_distributor (
    distributor_id BIGINT PRIMARY KEY,
    distributor_name VARCHAR(255) NOT NULL,
    team_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_team_id (team_id),
    FOREIGN KEY (team_id) REFERENCES t_team(team_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分销员表';

-- 订单表
CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id VARCHAR(255) NOT NULL UNIQUE,
    distributor_id BIGINT NOT NULL,
    order_amount DECIMAL(10,2) NOT NULL,
    order_status VARCHAR(50) NOT NULL,
    order_date DATETIME NOT NULL,
    customer_name VARCHAR(255),
    customer_id VARCHAR(255) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_order_id (order_id),
    INDEX idx_distributor_date_status (distributor_id, order_date, order_status),
    INDEX idx_customer_id (customer_id),
    FOREIGN KEY (distributor_id) REFERENCES t_distributor(distributor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 每日统计表
CREATE TABLE IF NOT EXISTS t_daily_statistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    distributor_id BIGINT NOT NULL,
    stat_date DATE NOT NULL,
    order_count INT DEFAULT 0,
    total_amount DECIMAL(10,2) DEFAULT 0.00,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_distributor_date (distributor_id, stat_date),
    INDEX idx_stat_date (stat_date),
    FOREIGN KEY (distributor_id) REFERENCES t_distributor(distributor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日统计表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS t_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    config_key VARCHAR(255) NOT NULL UNIQUE,
    config_value TEXT,
    config_desc VARCHAR(500),
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX uk_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入初始配置数据
INSERT INTO t_config (config_key, config_value, config_desc) 
VALUES ('shop_token', 'default_token_value', '店铺Token')
ON DUPLICATE KEY UPDATE config_desc = '店铺Token';

-- 插入示例团队数据
INSERT INTO t_team (team_name) VALUES ('销售一队') ON DUPLICATE KEY UPDATE team_name = '销售一队';
INSERT INTO t_team (team_name) VALUES ('销售二队') ON DUPLICATE KEY UPDATE team_name = '销售二队';
INSERT INTO t_team (team_name) VALUES ('销售三队') ON DUPLICATE KEY UPDATE team_name = '销售三队';

-- 插入示例分销员数据
INSERT INTO t_distributor (distributor_id, distributor_name, team_id) 
VALUES (1001, '张三', 1) ON DUPLICATE KEY UPDATE distributor_name = '张三';
INSERT INTO t_distributor (distributor_id, distributor_name, team_id) 
VALUES (1002, '李四', 1) ON DUPLICATE KEY UPDATE distributor_name = '李四';
INSERT INTO t_distributor (distributor_id, distributor_name, team_id) 
VALUES (1003, '王五', 2) ON DUPLICATE KEY UPDATE distributor_name = '王五';
INSERT INTO t_distributor (distributor_id, distributor_name, team_id) 
VALUES (1004, '赵六', 2) ON DUPLICATE KEY UPDATE distributor_name = '赵六';
INSERT INTO t_distributor (distributor_id, distributor_name, team_id) 
VALUES (1005, '钱七', 3) ON DUPLICATE KEY UPDATE distributor_name = '钱七';

-- 插入示例订单数据（测试数据，生产环境建议删除或更新为实际数据）
-- 使用 CURDATE() 插入当天的示例数据
INSERT INTO t_order (order_id, distributor_id, order_amount, order_status, order_date, customer_name, customer_id) 
VALUES ('ORD_SAMPLE_001', 1001, 299.00, '已支付', CONCAT(CURDATE(), ' 10:00:00'), '客户A', 'CUST001')
ON DUPLICATE KEY UPDATE order_amount = 299.00;

INSERT INTO t_order (order_id, distributor_id, order_amount, order_status, order_date, customer_name, customer_id) 
VALUES ('ORD_SAMPLE_002', 1001, 499.00, '已支付', CONCAT(CURDATE(), ' 11:00:00'), '客户B', 'CUST002')
ON DUPLICATE KEY UPDATE order_amount = 499.00;

INSERT INTO t_order (order_id, distributor_id, order_amount, order_status, order_date, customer_name, customer_id) 
VALUES ('ORD_SAMPLE_003', 1002, 199.00, '已支付', CONCAT(CURDATE(), ' 12:00:00'), '客户C', 'CUST003')
ON DUPLICATE KEY UPDATE order_amount = 199.00;

INSERT INTO t_order (order_id, distributor_id, order_amount, order_status, order_date, customer_name, customer_id) 
VALUES ('ORD_SAMPLE_004', 1003, 399.00, '已支付', CONCAT(CURDATE(), ' 13:00:00'), '客户D', 'CUST004')
ON DUPLICATE KEY UPDATE order_amount = 399.00;

INSERT INTO t_order (order_id, distributor_id, order_amount, order_status, order_date, customer_name, customer_id) 
VALUES ('ORD_SAMPLE_005', 1001, 599.00, '已支付', CONCAT(CURDATE(), ' 14:00:00'), '客户A', 'CUST001')
ON DUPLICATE KEY UPDATE order_amount = 599.00;
