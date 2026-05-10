-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(64) NOT NULL COMMENT '用户名',
    `password` VARCHAR(128) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(64) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试数据
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `email`, `phone`, `status`) VALUES
('admin', '123456', '管理员', 'admin@example.com', '13800138000', 1),
('user1', '123456', '用户1', 'user1@example.com', '13800138001', 1),
('user2', '123456', '用户2', 'user2@example.com', '13800138002', 0);
