-- ============================================================
-- 多租户后台管理系统 数据库初始化脚本
-- ============================================================

-- 租户套餐表
CREATE TABLE IF NOT EXISTS `sys_tenant_package` (
    `id`             VARCHAR(64)   NOT NULL COMMENT '主键',
    `name`           VARCHAR(100)  NOT NULL COMMENT '套餐名称',
    `permission_ids` TEXT COMMENT '关联权限ID（JSON数组）',
    `status`         TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`         VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`        VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time`    DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`        VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time`    DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`        TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`        INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_package_name` (`name`, `deleted`)
) ENGINE = InnoDB COMMENT = '租户套餐表';

-- 租户表
CREATE TABLE IF NOT EXISTS `sys_tenant` (
    `id`            VARCHAR(64)   NOT NULL COMMENT '主键',
    `package_id`    VARCHAR(64)   DEFAULT NULL COMMENT '套餐ID',
    `code`          VARCHAR(100)  NOT NULL COMMENT '租户编码',
    `name`          VARCHAR(100)  NOT NULL COMMENT '租户名称',
    `account`       VARCHAR(64)   NOT NULL COMMENT '登录账户',
    `contact_name`  VARCHAR(50)   DEFAULT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
    `contact_email` VARCHAR(100)  DEFAULT NULL COMMENT '联系邮箱',
    `expire_time`   DATETIME      DEFAULT NULL COMMENT '过期时间',
    `account_limit` INT           NOT NULL DEFAULT -1 COMMENT '账号数量限制（-1不限）',
    `status`        TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `domain`        VARCHAR(200)  DEFAULT NULL COMMENT '绑定域名',
    `logo`          VARCHAR(500)  DEFAULT NULL COMMENT 'Logo地址',
    `remark`        VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`       VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`       VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time`   DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`       INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`code`, `deleted`),
    UNIQUE KEY `uk_tenant_account` (`account`, `deleted`)
) ENGINE = InnoDB COMMENT = '租户表';

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `username`    VARCHAR(100)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(200)  NOT NULL COMMENT '密码',
    `salt`        VARCHAR(64)   NOT NULL COMMENT '密码盐值',
    `name`        VARCHAR(100)  DEFAULT NULL COMMENT '姓名',
    `avatar`      VARCHAR(500)  DEFAULT NULL COMMENT '头像地址',
    `phone`       VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
    `email`       VARCHAR(100)  DEFAULT NULL COMMENT '邮箱',
    `gender`      TINYINT       DEFAULT 0 COMMENT '性别（0未知 1男 2女）',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `login_ip`    VARCHAR(50)   DEFAULT NULL COMMENT '最后登录IP',
    `login_time`  DATETIME      DEFAULT NULL COMMENT '最后登录时间',
    `remark`      VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`, `deleted`),
    UNIQUE KEY `uk_tenant_phone` (`tenant_id`, `phone`, `deleted`),
    UNIQUE KEY `uk_tenant_email` (`tenant_id`, `email`, `deleted`)
) ENGINE = InnoDB COMMENT = '用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `name`        VARCHAR(100)  NOT NULL COMMENT '角色名称',
    `code`        VARCHAR(100)  NOT NULL COMMENT '角色编码',
    `sort`        INT           NOT NULL DEFAULT 0 COMMENT '排序',
    `data_scope`  TINYINT       NOT NULL DEFAULT 1 COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    UNIQUE KEY `uk_tenant_role_code` (`tenant_id`, `code`, `deleted`)
) ENGINE = InnoDB COMMENT = '角色表';

-- 权限表（目录/菜单/按钮）
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id`                 VARCHAR(64)   NOT NULL COMMENT '主键',
    `parent_id`          VARCHAR(64)   NOT NULL DEFAULT '0' COMMENT '父级ID',
    `ancestors`          VARCHAR(500)  DEFAULT '' COMMENT '祖级列表',
    `level`              INT           NOT NULL DEFAULT 0 COMMENT '层级',
    `name`               VARCHAR(100)  NOT NULL COMMENT '权限名称',
    `code`               VARCHAR(200)  DEFAULT NULL COMMENT '权限标识',
    `path`               VARCHAR(200)  DEFAULT NULL COMMENT '路由地址',
    `component`          VARCHAR(200)  DEFAULT NULL COMMENT '组件路径',
    `redirect`           VARCHAR(200)  DEFAULT NULL COMMENT '重定向地址',
    `icon`               VARCHAR(100)  DEFAULT NULL COMMENT '图标',
    `sort`               INT           NOT NULL DEFAULT 0 COMMENT '排序',
    `type`               TINYINT       NOT NULL COMMENT '类型（0目录 1菜单 2按钮）',
    `hide_in_menu`       TINYINT       NOT NULL DEFAULT 0 COMMENT '是否隐藏菜单（0否 1是）',
    `hide_in_breadcrumb` TINYINT       NOT NULL DEFAULT 0 COMMENT '是否隐藏面包屑（0否 1是）',
    `hide_in_tab`        TINYINT       NOT NULL DEFAULT 0 COMMENT '是否隐藏标签页（0否 1是）',
    `keep_alive`         TINYINT       NOT NULL DEFAULT 0 COMMENT '是否缓存（0否 1是）',
    `affix`              TINYINT       NOT NULL DEFAULT 0 COMMENT '是否固定标签页（0否 1是）',
    `disabled`           TINYINT       NOT NULL DEFAULT 0 COMMENT '是否禁用（0否 1是）',
    `layout`             VARCHAR(100)  DEFAULT NULL COMMENT '布局组件',
    `active_menu`        VARCHAR(200)  DEFAULT NULL COMMENT '高亮菜单路径',
    `transition_name`    VARCHAR(100)  DEFAULT NULL COMMENT '过渡动画名称',
    `status`             TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`             VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`            VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time`        DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`            VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time`        DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`            TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`            INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`code`, `deleted`),
    KEY `idx_permission_parent` (`parent_id`),
    KEY `idx_permission_ancestors` (`ancestors`)
) ENGINE = InnoDB COMMENT = '权限表';

-- 部门表
CREATE TABLE IF NOT EXISTS `sys_dept` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `parent_id`   VARCHAR(64)   NOT NULL DEFAULT '0' COMMENT '父级ID',
    `ancestors`   VARCHAR(500)  DEFAULT '' COMMENT '祖级列表',
    `level`       INT           NOT NULL DEFAULT 0 COMMENT '层级',
    `name`        VARCHAR(100)  NOT NULL COMMENT '部门名称',
    `code`        VARCHAR(100)  NOT NULL COMMENT '部门编码',
    `alias`       VARCHAR(100)  DEFAULT NULL COMMENT '部门别名',
    `sort`        INT           NOT NULL DEFAULT 0 COMMENT '排序',
    `leader`      VARCHAR(50)   DEFAULT NULL COMMENT '负责人',
    `phone`       VARCHAR(20)   DEFAULT NULL COMMENT '联系电话',
    `email`       VARCHAR(100)  DEFAULT NULL COMMENT '邮箱',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    UNIQUE KEY `uk_tenant_dept_code` (`tenant_id`, `code`, `deleted`),
    KEY `idx_dept_parent` (`tenant_id`, `parent_id`),
    KEY `idx_dept_ancestors` (`tenant_id`, `ancestors`)
) ENGINE = InnoDB COMMENT = '部门表';

-- 岗位表
CREATE TABLE IF NOT EXISTS `sys_post` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `code`        VARCHAR(100)  NOT NULL COMMENT '岗位编码',
    `name`        VARCHAR(100)  NOT NULL COMMENT '岗位名称',
    `sort`        INT           NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    UNIQUE KEY `uk_tenant_post_code` (`tenant_id`, `code`, `deleted`)
) ENGINE = InnoDB COMMENT = '岗位表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `user_id`     VARCHAR(64)   NOT NULL COMMENT '用户ID',
    `role_id`     VARCHAR(64)   NOT NULL COMMENT '角色ID',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_role` (`role_id`),
    UNIQUE KEY `uk_user_role` (`tenant_id`, `user_id`, `role_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '用户角色关联表';

-- 用户岗位关联表
CREATE TABLE IF NOT EXISTS `sys_user_post` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `user_id`     VARCHAR(64)   NOT NULL COMMENT '用户ID',
    `post_id`     VARCHAR(64)   NOT NULL COMMENT '岗位ID',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_post` (`post_id`),
    UNIQUE KEY `uk_user_post` (`tenant_id`, `user_id`, `post_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '用户岗位关联表';

-- 用户部门关联表
CREATE TABLE IF NOT EXISTS `sys_user_dept` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `user_id`     VARCHAR(64)   NOT NULL COMMENT '用户ID',
    `dept_id`     VARCHAR(64)   NOT NULL COMMENT '部门ID',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_dept` (`dept_id`),
    UNIQUE KEY `uk_user_dept` (`tenant_id`, `user_id`, `dept_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '用户部门关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id`            VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`     VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `role_id`       VARCHAR(64)   NOT NULL COMMENT '角色ID',
    `permission_id` VARCHAR(64)   NOT NULL COMMENT '权限ID',
    `creator`       VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`       VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time`   DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`       INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role_id`),
    KEY `idx_permission` (`permission_id`),
    UNIQUE KEY `uk_role_permission` (`tenant_id`, `role_id`, `permission_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '角色权限关联表';

-- 角色部门关联表（数据权限）
CREATE TABLE IF NOT EXISTS `sys_role_dept` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `role_id`     VARCHAR(64)   NOT NULL COMMENT '角色ID',
    `dept_id`     VARCHAR(64)   NOT NULL COMMENT '部门ID',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role_id`),
    KEY `idx_dept` (`dept_id`),
    UNIQUE KEY `uk_role_dept` (`tenant_id`, `role_id`, `dept_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '角色部门关联表';

-- 字典表
CREATE TABLE IF NOT EXISTS `sys_dict` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `name`        VARCHAR(100)  NOT NULL COMMENT '字典名称',
    `type`        VARCHAR(100)  NOT NULL COMMENT '字典类型',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_type` (`tenant_id`, `type`, `deleted`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '字典表';

-- 字典数据表
CREATE TABLE IF NOT EXISTS `sys_dict_data` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `dict_type`   VARCHAR(100)  NOT NULL COMMENT '字典类型',
    `label`       VARCHAR(200)  NOT NULL COMMENT '字典标签',
    `value`       VARCHAR(200)  NOT NULL COMMENT '字典值',
    `sort`        INT           NOT NULL DEFAULT 0 COMMENT '排序',
    `css_class`   VARCHAR(100)  DEFAULT NULL COMMENT '样式属性',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_type` (`tenant_id`, `dict_type`),
    UNIQUE KEY `uk_tenant_dict_value` (`tenant_id`, `dict_type`, `value`, `deleted`)
) ENGINE = InnoDB COMMENT = '字典数据表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `name`        VARCHAR(100)  NOT NULL COMMENT '配置名称',
    `key`         VARCHAR(200)  NOT NULL COMMENT '配置键',
    `value`       TEXT COMMENT '配置值',
    `group`       VARCHAR(50)   NOT NULL DEFAULT 'system' COMMENT '分组（system:系统配置 custom:自定义）',
    `status`      TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_key` (`key`, `deleted`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '系统配置表';

-- 通知公告表
CREATE TABLE IF NOT EXISTS `sys_notice` (
    `id`          VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`   VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `title`       VARCHAR(200)  NOT NULL COMMENT '标题',
    `content`     LONGTEXT COMMENT '内容',
    `type`        TINYINT       NOT NULL DEFAULT 0 COMMENT '类型（0通知 1公告）',
    `status`      TINYINT       NOT NULL DEFAULT 0 COMMENT '状态（0草稿 1发布 2撤回）',
    `remark`      VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`     VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`     VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`     TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '通知公告表';

-- 定时任务表
CREATE TABLE IF NOT EXISTS `sys_job` (
    `id`              VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`       VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `name`            VARCHAR(200)  NOT NULL COMMENT '任务名称',
    `group`           VARCHAR(100)  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务分组',
    `invoke_target`   VARCHAR(500)  NOT NULL COMMENT '调用目标（Bean名称.方法名）',
    `cron_expression` VARCHAR(200)  NOT NULL COMMENT 'Cron表达式',
    `misfire_policy`  TINYINT       NOT NULL DEFAULT 3 COMMENT '执行策略（1立即执行 2执行一次 3放弃执行）',
    `concurrent`      TINYINT       NOT NULL DEFAULT 0 COMMENT '是否并发执行（0否 1是）',
    `status`          TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0暂停 1运行）',
    `remark`          VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`         VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`         VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time`     DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`         TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`         INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '定时任务表';

-- 日志表
CREATE TABLE IF NOT EXISTS `sys_log` (
    `id`             VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`      VARCHAR(64)   DEFAULT NULL COMMENT '租户ID',
    `type`           TINYINT       NOT NULL COMMENT '类型（1登录日志 2操作日志 3异常日志 4定时任务日志）',
    `title`          VARCHAR(200)  DEFAULT NULL COMMENT '操作标题',
    `method`         VARCHAR(200)  DEFAULT NULL COMMENT '方法名称',
    `url`            VARCHAR(500)  DEFAULT NULL COMMENT '请求URL',
    `request_method` VARCHAR(10)   DEFAULT NULL COMMENT '请求方式',
    `params`         LONGTEXT COMMENT '请求参数',
    `result`         LONGTEXT COMMENT '返回结果',
    `status`         TINYINT       NOT NULL DEFAULT 1 COMMENT '状态（0失败 1成功）',
    `error_msg`      LONGTEXT COMMENT '错误信息',
    `ip`             VARCHAR(50)   DEFAULT NULL COMMENT 'IP地址',
    `browser`        VARCHAR(100)  DEFAULT NULL COMMENT '浏览器',
    `os`             VARCHAR(100)  DEFAULT NULL COMMENT '操作系统',
    `duration`       BIGINT        DEFAULT 0 COMMENT '耗时（毫秒）',
    `operator`       VARCHAR(64)   DEFAULT NULL COMMENT '操作人',
    `creator`        VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time`    DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`        VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time`    DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`        TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`        INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    KEY `idx_type` (`type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB COMMENT = '日志表';

-- 文件表
CREATE TABLE IF NOT EXISTS `sys_file` (
    `id`            VARCHAR(64)   NOT NULL COMMENT '主键',
    `tenant_id`     VARCHAR(64)   NOT NULL COMMENT '租户ID',
    `name`          VARCHAR(200)  NOT NULL COMMENT '文件名称',
    `original_name` VARCHAR(200)  DEFAULT NULL COMMENT '原始文件名',
    `url`           VARCHAR(500)  NOT NULL COMMENT '文件地址',
    `path`          VARCHAR(500)  DEFAULT NULL COMMENT '文件路径',
    `size`          BIGINT        DEFAULT 0 COMMENT '文件大小（字节）',
    `type`          VARCHAR(100)  DEFAULT NULL COMMENT '文件类型（MIME）',
    `extension`     VARCHAR(20)   DEFAULT NULL COMMENT '文件扩展名',
    `platform`      VARCHAR(100)  DEFAULT NULL COMMENT '存储平台标识',
    `storage_type`  TINYINT       NOT NULL DEFAULT 0 COMMENT '存储类型（0本地 1OSS 2S3）',
    `md5`           VARCHAR(64)   DEFAULT NULL COMMENT '文件MD5',
    `remark`        VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `creator`       VARCHAR(64)   DEFAULT NULL COMMENT '创建者',
    `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
    `updater`       VARCHAR(64)   DEFAULT NULL COMMENT '更新者',
    `update_time`   DATETIME      DEFAULT NULL COMMENT '更新时间',
    `deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`       INT           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '文件表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 默认租户套餐
INSERT INTO `sys_tenant_package` (`id`, `name`, `status`)
VALUES ('2083480854738399232', '默认套餐', 1);

-- 默认租户
INSERT INTO `sys_tenant` (`id`, `package_id`, `code`, `name`, `account`, `status`, `account_limit`)
VALUES ('2083480854738399233', '2083480854738399232', 'default', '默认租户', 'admin', 1, -1);

-- 默认部门
INSERT INTO `sys_dept` (`id`, `tenant_id`, `parent_id`, `ancestors`, `level`, `name`, `sort`, `status`)
VALUES ('2083480854738399234', '2083480854738399233', '0', '2083480854738399234', 1, '总公司', 0, 1);

-- 超级管理员角色
INSERT INTO `sys_role` (`id`, `tenant_id`, `name`, `code`, `sort`, `data_scope`, `status`)
VALUES ('2083480854738399235', '2083480854738399233', '超级管理员', 'admin', 0, 1, 1);

-- 普通角色
INSERT INTO `sys_role` (`id`, `tenant_id`, `name`, `code`, `sort`, `data_scope`, `status`)
VALUES ('2083480854738399236', '2083480854738399233', '普通用户', 'common', 1, 5, 1);

-- 超级管理员用户（密码: admin123）
INSERT INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `salt`, `name`, `status`)
VALUES (
    '2083480854738399237',
    '2083480854738399233',
    'admin',
    '$2a$10$qflemWctt7cNQBxDT0a6zObdqN0k0MTT6t2qWS4oZyT/aFMmzZedi',
    '39d86e7b26bf4bf79aa6fdd150d9ab2d',
    '超级管理员',
    1
);

-- 管理员部门关联
INSERT INTO `sys_user_dept` (`id`, `tenant_id`, `user_id`, `dept_id`)
VALUES ('2083480854738399238', '2083480854738399233', '2083480854738399237', '2083480854738399234');

-- 管理员角色关联
INSERT INTO `sys_user_role` (`id`, `tenant_id`, `user_id`, `role_id`)
VALUES ('2083480854738399239', '2083480854738399233', '2083480854738399237', '2083480854738399235');

-- 系统配置初始化数据（value 为默认值，可在管理端修改）

-- system 分组配置
INSERT INTO `sys_config` (`id`, `tenant_id`, `name`, `key`, `value`, `group`, `status`)
VALUES
    ('2083480854738399240', '2083480854738399233', '站点名称', 'siteName', '多租户后台管理系统', 'system', 1),
    ('2083480854738399241', '2083480854738399233', '站点描述', 'siteDesc', '多租户后台管理系统', 'system', 1),
    ('2083480854738399242', '2083480854738399233', '站点Logo', 'siteLogo', '', 'system', 1),
    ('2083480854738399243', '2083480854738399233', '版权信息', 'copyright', '© 2026 YCH', 'system', 1),
    ('2083480854738399244', '2083480854738399233', 'ICP备案号', 'icp', '', 'system', 1),
    ('2083480854738399245', '2083480854738399233', '启用水印', 'watermarkEnabled', 'false', 'system', 1),
    ('2083480854738399246', '2083480854738399233', '水印类型', 'watermarkType', 'text', 'system', 1),
    ('2083480854738399247', '2083480854738399233', '水印文本', 'watermarkText', '内部资料', 'system', 1),
    ('2083480854738399248', '2083480854738399233', '水印透明度', 'watermarkOpacity', '0.3', 'system', 1),
-- register 分组配置
    ('2083480854738399249', '2083480854738399233', '开放注册', 'registerEnabled', 'true', 'register', 1),
    ('2083480854738399250', '2083480854738399233', '邮箱验证', 'verifyEmail', 'false', 'register', 1),
    ('2083480854738399251', '2083480854738399233', '手机验证', 'verifyPhone', 'false', 'register', 1),
    ('2083480854738399252', '2083480854738399233', '默认角色', 'defaultRole', '2083480854738399236', 'register', 1),
    ('2083480854738399253', '2083480854738399233', '注册审核', 'needAudit', 'false', 'register', 1),
-- login 分组配置
    ('2083480854738399254', '2083480854738399233', '启用验证码', 'captchaEnabled', 'true', 'login', 1),
    ('2083480854738399255', '2083480854738399233', '验证码类型', 'captchaType', 'math', 'login', 1),
    ('2083480854738399256', '2083480854738399233', '最大重试次数', 'maxRetry', '5', 'login', 1),
    ('2083480854738399257', '2083480854738399233', '锁定时长（分钟）', 'lockDuration', '30', 'login', 1),
    ('2083480854738399258', '2083480854738399233', '允许记住我', 'rememberMe', 'true', 'login', 1),
    ('2083480854738399259', '2083480854738399233', '单点登录', 'singleLogin', 'false', 'login', 1),
-- password 分组配置
    ('2083480854738399260', '2083480854738399233', '最小长度', 'minLength', '6', 'password', 1),
    ('2083480854738399261', '2083480854738399233', '最大长度', 'maxLength', '20', 'password', 1),
    ('2083480854738399262', '2083480854738399233', '必须含大写', 'requireUppercase', 'false', 'password', 1),
    ('2083480854738399263', '2083480854738399233', '必须含小写', 'requireLowercase', 'false', 'password', 1),
    ('2083480854738399264', '2083480854738399233', '必须含数字', 'requireNumber', 'true', 'password', 1),
    ('2083480854738399265', '2083480854738399233', '必须含特殊字符', 'requireSpecial', 'false', 'password', 1),
    ('2083480854738399266', '2083480854738399233', '有效期（天）', 'expireDays', '0', 'password', 1),
-- email 分组配置
    ('2083480854738399267', '2083480854738399233', 'SMTP地址', 'smtpHost', '', 'email', 1),
    ('2083480854738399268', '2083480854738399233', 'SMTP端口', 'smtpPort', '465', 'email', 1),
    ('2083480854738399269', '2083480854738399233', '邮箱账号', 'smtpUser', '', 'email', 1),
    ('2083480854738399270', '2083480854738399233', '邮箱密码', 'smtpPass', '', 'email', 1),
    ('2083480854738399271', '2083480854738399233', '发件人名称', 'fromName', '多租户后台管理系统', 'email', 1),
    ('2083480854738399272', '2083480854738399233', '启用SSL', 'sslEnabled', 'true', 'email', 1),
    ('2083480854738399273', '2083480854738399233', '启用邮件', 'emailEnabled', 'false', 'email', 1),
-- emailTemplate 分组配置
    ('2083480854738399274', '2083480854738399233', '验证码模板', 'emailVerifyCode', '您的验证码为{code}，请于{minutes}分钟内完成验证。', 'emailTemplate', 1),
    ('2083480854738399275', '2083480854738399233', '重置密码模板', 'emailResetPassword', '您的密码已重置为{password}，请于{minutes}分钟内完成验证。', 'emailTemplate', 1),
    ('2083480854738399276', '2083480854738399233', '欢迎模板', 'welcome', '欢迎注册{siteName}，祝您使用愉快！', 'emailTemplate', 1),
-- sms 分组配置
    ('2083480854738399277', '2083480854738399233', '服务商', 'smsProvider', 'aliyun', 'sms', 1),
    ('2083480854738399278', '2083480854738399233', '访问密钥ID', 'accessKey', '', 'sms', 1),
    ('2083480854738399279', '2083480854738399233', '访问密钥', 'secretKey', '', 'sms', 1),
    ('2083480854738399280', '2083480854738399233', '短信签名', 'signName', '', 'sms', 1),
    ('2083480854738399281', '2083480854738399233', '腾讯云AppId', 'tencentAppId', '', 'sms', 1),
    ('2083480854738399282', '2083480854738399233', '验证码模板ID', 'templateVerify', '', 'sms', 1),
    ('2083480854738399283', '2083480854738399233', '重置密码模板ID', 'templateReset', '', 'sms', 1),
    ('2083480854738399284', '2083480854738399233', '通知模板ID', 'templateNotice', '', 'sms', 1),
    ('2083480854738399285', '2083480854738399233', '启用短信', 'smsEnabled', 'false', 'sms', 1),
-- smsTemplate 分组配置
    ('2083480854738399286', '2083480854738399233', '验证码模板内容', 'smsVerifyCode', '您的验证码为{code}，请于{minutes}分钟内完成验证。', 'smsTemplate', 1),
    ('2083480854738399287', '2083480854738399233', '重置密码模板内容', 'smsResetPassword', '您的密码已重置为{password}，请登录后尽快修改。', 'smsTemplate', 1),
    ('2083480854738399288', '2083480854738399233', '通知模板内容', 'notification', '', 'smsTemplate', 1),
-- storage 分组配置
    ('2083480854738399289', '2083480854738399233', '存储服务商', 'storageProvider', 'local', 'storage', 1),
    ('2083480854738399290', '2083480854738399233', '访问域名', 'domain', '', 'storage', 1),
    ('2083480854738399291', '2083480854738399233', '本地存储路径', 'localPath', './upload', 'storage', 1),
    ('2083480854738399292', '2083480854738399233', '文件大小上限', 'maxSize', '104857600', 'storage', 1),
    ('2083480854738399293', '2083480854738399233', '允许的文件类型', 'allowTypes', 'jpg,jpeg,png,gif,webp,pdf,doc,docx,xls,xlsx,zip', 'storage', 1),
    ('2083480854738399294', '2083480854738399233', 'MinIO地址', 'minioEndpoint', '', 'storage', 1),
    ('2083480854738399295', '2083480854738399233', 'MinIO访问密钥', 'minioAccessKey', '', 'storage', 1),
    ('2083480854738399296', '2083480854738399233', 'MinIO密钥', 'minioSecretKey', '', 'storage', 1),
    ('2083480854738399297', '2083480854738399233', 'MinIO存储桶', 'minioBucket', '', 'storage', 1),
    ('2083480854738399298', '2083480854738399233', 'OSS地址', 'ossEndpoint', '', 'storage', 1),
    ('2083480854738399299', '2083480854738399233', 'OSS访问密钥', 'ossAccessKey', '', 'storage', 1),
    ('2083480854738399300', '2083480854738399233', 'OSS密钥', 'ossSecretKey', '', 'storage', 1),
    ('2083480854738399301', '2083480854738399233', 'OSS存储桶', 'ossBucket', '', 'storage', 1),
    ('2083480854738399302', '2083480854738399233', 'COS密钥ID', 'cosSecretId', '', 'storage', 1),
    ('2083480854738399303', '2083480854738399233', 'COS密钥', 'cosSecretKey', '', 'storage', 1),
    ('2083480854738399304', '2083480854738399233', 'COS存储桶', 'cosBucket', '', 'storage', 1),
    ('2083480854738399305', '2083480854738399233', 'COS区域', 'cosRegion', '', 'storage', 1),
    ('2083480854738399306', '2083480854738399233', 'RustFS地址', 'rustfsEndpoint', '', 'storage', 1),
    ('2083480854738399307', '2083480854738399233', 'RustFS访问密钥', 'rustfsAccessKey', '', 'storage', 1),
    ('2083480854738399308', '2083480854738399233', 'RustFS密钥', 'rustfsSecretKey', '', 'storage', 1),
    ('2083480854738399309', '2083480854738399233', 'RustFS存储桶', 'rustfsBucket', '', 'storage', 1),
-- push 分组配置
    ('2083480854738399310', '2083480854738399233', '钉钉签名', 'dingtalkSign', '', 'push', 1),
    ('2083480854738399311', '2083480854738399233', '钉钉Token', 'dingtalkToken', '', 'push', 1),
    ('2083480854738399312', '2083480854738399233', '飞书签名', 'feishuSign', '', 'push', 1),
    ('2083480854738399313', '2083480854738399233', '飞书Token', 'feishuToken', '', 'push', 1),
    ('2083480854738399314', '2083480854738399233', '企业微信签名', 'wechatWorkSign', '', 'push', 1),
    ('2083480854738399315', '2083480854738399233', '企业微信Token', 'wechatWorkToken', '', 'push', 1),
-- thirdParty 分组配置
    ('2083480854738399316', '2083480854738399233', '启用微信', 'thirdPartyWechatEnabled', 'false', 'thirdParty', 1),
    ('2083480854738399317', '2083480854738399233', '微信AppID', 'thirdPartyWechatAppId', '', 'thirdParty', 1),
    ('2083480854738399318', '2083480854738399233', '微信密钥', 'wechatSecret', '', 'thirdParty', 1),
    ('2083480854738399319', '2083480854738399233', '启用支付宝', 'thirdPartyAlipayEnabled', 'false', 'thirdParty', 1),
    ('2083480854738399320', '2083480854738399233', '支付宝AppID', 'thirdPartyAlipayAppId', '', 'thirdParty', 1),
    ('2083480854738399321', '2083480854738399233', '支付宝私钥', 'thirdPartyAlipayPrivateKey', '', 'thirdParty', 1),
    ('2083480854738399322', '2083480854738399233', '支付宝公钥', 'thirdPartyAlipayPublicKey', '', 'thirdParty', 1),
    ('2083480854738399323', '2083480854738399233', '启用GitHub', 'githubEnabled', 'false', 'thirdParty', 1),
    ('2083480854738399324', '2083480854738399233', 'GitHub客户端ID', 'githubClientId', '', 'thirdParty', 1),
    ('2083480854738399325', '2083480854738399233', 'GitHub密钥', 'githubSecret', '', 'thirdParty', 1),
-- wechatMiniProgram 分组配置
    ('2083480854738399326', '2083480854738399233', '启用小程序', 'wechatMiniProgramEnabled', 'false', 'wechatMiniProgram', 1),
    ('2083480854738399327', '2083480854738399233', 'AppID', 'wechatMiniProgramAppId', '', 'wechatMiniProgram', 1),
    ('2083480854738399328', '2083480854738399233', '密钥', 'wechatMiniProgramSecret', '', 'wechatMiniProgram', 1),
-- wechatMp 分组配置
    ('2083480854738399329', '2083480854738399233', '启用公众号', 'wechatMpEnabled', 'false', 'wechatMp', 1),
    ('2083480854738399330', '2083480854738399233', 'AppID', 'wechatMpAppId', '', 'wechatMp', 1),
    ('2083480854738399331', '2083480854738399233', '密钥', 'wechatMpSecret', '', 'wechatMp', 1),
    ('2083480854738399332', '2083480854738399233', '消息Token', 'token', '', 'wechatMp', 1),
    ('2083480854738399333', '2083480854738399233', '加密密钥', 'aesKey', '', 'wechatMp', 1),
    ('2083480854738399334', '2083480854738399233', '回调地址', 'callbackUrl', '', 'wechatMp', 1),
    ('2083480854738399335', '2083480854738399233', '授权回调地址', 'oauthRedirect', '', 'wechatMp', 1),
    ('2083480854738399336', '2083480854738399233', '自定义菜单', 'menuConfig', '', 'wechatMp', 1),
-- payment 分组配置
    ('2083480854738399337', '2083480854738399233', '启用微信支付', 'paymentWechatEnabled', 'false', 'payment', 1),
    ('2083480854738399338', '2083480854738399233', '商户号', 'wechatMchId', '', 'payment', 1),
    ('2083480854738399339', '2083480854738399233', 'AppID', 'paymentWechatAppId', '', 'payment', 1),
    ('2083480854738399340', '2083480854738399233', 'APIv3密钥', 'wechatApiV3Key', '', 'payment', 1),
    ('2083480854738399341', '2083480854738399233', '商户私钥', 'wechatPrivateKey', '', 'payment', 1),
    ('2083480854738399342', '2083480854738399233', '证书序列号', 'wechatCertSerial', '', 'payment', 1),
    ('2083480854738399343', '2083480854738399233', '回调地址', 'wechatNotifyUrl', '', 'payment', 1),
    ('2083480854738399344', '2083480854738399233', '启用支付宝', 'paymentAlipayEnabled', 'false', 'payment', 1),
    ('2083480854738399345', '2083480854738399233', 'AppID', 'paymentAlipayAppId', '', 'payment', 1),
    ('2083480854738399346', '2083480854738399233', '应用私钥', 'paymentAlipayPrivateKey', '', 'payment', 1),
    ('2083480854738399347', '2083480854738399233', '公钥', 'paymentAlipayPublicKey', '', 'payment', 1),
    ('2083480854738399348', '2083480854738399233', '签名类型', 'alipaySignType', 'RSA2', 'payment', 1),
    ('2083480854738399349', '2083480854738399233', '网关地址', 'alipayGateway', 'https://openapi.alipay.com/gateway.do', 'payment', 1),
    ('2083480854738399350', '2083480854738399233', '回调地址', 'alipayNotifyUrl', '', 'payment', 1),
    ('2083480854738399351', '2083480854738399233', '跳转地址', 'alipayReturnUrl', '', 'payment', 1),
-- security 分组配置
    ('2083480854738399352', '2083480854738399233', '启用接口加密', 'encryptEnabled', 'false', 'security', 1),
    ('2083480854738399353', '2083480854738399233', '加密范围', 'encryptScope', '', 'security', 1),
    ('2083480854738399354', '2083480854738399233', 'RSA公钥', 'publicKey', '', 'security', 1),
    ('2083480854738399355', '2083480854738399233', 'RSA私钥', 'privateKey', '', 'security', 1),
    ('2083480854738399356', '2083480854738399233', 'XSS过滤', 'xssFilter', 'true', 'security', 1),
    ('2083480854738399357', '2083480854738399233', 'SQL注入防护', 'sqlInject', 'true', 'security', 1),
    ('2083480854738399358', '2083480854738399233', '禁止调试', 'disableDevtool', 'false', 'security', 1),
    ('2083480854738399359', '2083480854738399233', 'Token名称', 'tokenName', 'Authorization', 'security', 1),
    ('2083480854738399360', '2083480854738399233', 'Token有效期', 'tokenTimeout', '86400', 'security', 1),
    ('2083480854738399361', '2083480854738399233', '活跃超时', 'tokenActiveTimeout', '1800', 'security', 1),
    ('2083480854738399362', '2083480854738399233', '允许多端登录', 'tokenAllowMulti', 'true', 'security', 1),
    ('2083480854738399363', '2083480854738399233', '共用Token', 'tokenShared', 'false', 'security', 1),
    ('2083480854738399364', '2083480854738399233', 'Token风格', 'tokenStyle', 'uuid', 'security', 1),
    ('2083480854738399365', '2083480854738399233', '记录日志', 'tokenLogEnabled', 'false', 'security', 1),
    ('2083480854738399366', '2083480854738399233', '从Body读取', 'tokenReadBody', 'false', 'security', 1),
    ('2083480854738399367', '2083480854738399233', '从Cookie读取', 'tokenReadCookie', 'false', 'security', 1),
    ('2083480854738399368', '2083480854738399233', '从Header读取', 'tokenReadHeader', 'true', 'security', 1),
    ('2083480854738399369', '2083480854738399233', '打印版本信息', 'tokenPrintVersion', 'false', 'security', 1),
    ('2083480854738399370', '2083480854738399233', '写入响应Header', 'tokenWriteHeader', 'false', 'security', 1);
