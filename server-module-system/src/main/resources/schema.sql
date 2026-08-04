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
-- 超级管理员用户（密码: admin123）
INSERT INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `salt`, `name`, `status`)
VALUES (
    '1',
    '1',
    'admin',
    '$2a$10$BhhB0wYIAvXVQE3jrHdMpufW.AuLoQiOS38HCXAdRWr3UFLnJpRjG',
    'vMis6wBD',
    '超级管理员',
    1
);
