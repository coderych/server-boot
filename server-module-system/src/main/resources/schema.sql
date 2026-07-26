-- ============================================================
-- 多租户后台管理系统 数据库初始化脚本
-- ============================================================

-- 租户套餐表
CREATE TABLE IF NOT EXISTS `sys_tenant_package`
(
    `id`          varchar(64)  NOT NULL COMMENT '主键',
    `name`        varchar(100) NOT NULL COMMENT '套餐名称',
    `permission_ids` text               COMMENT '关联权限ID（JSON数组）',
    `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time` datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_package_name` (`name`, `deleted`)
) ENGINE = InnoDB COMMENT = '租户套餐表';

-- 租户表
CREATE TABLE IF NOT EXISTS `sys_tenant`
(
    `id`            varchar(64)  NOT NULL COMMENT '主键',
    `package_id`    varchar(64)           DEFAULT NULL COMMENT '套餐ID',
    `code`          varchar(100) NOT NULL COMMENT '租户编码',
    `name`          varchar(100) NOT NULL COMMENT '租户名称',
    `account`       varchar(64) NOT NULL COMMENT '登录账户',
    `contact_name`  varchar(50)           DEFAULT NULL COMMENT '联系人',
    `contact_phone` varchar(20)           DEFAULT NULL COMMENT '联系电话',
    `contact_email` varchar(100)          DEFAULT NULL COMMENT '联系邮箱',
    `expire_time`   datetime              DEFAULT NULL COMMENT '过期时间',
    `account_limit` int          NOT NULL DEFAULT -1 COMMENT '账号数量限制（-1不限）',
    `status`        tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `domain`        varchar(200)          DEFAULT NULL COMMENT '绑定域名',
    `logo`          varchar(500)          DEFAULT NULL COMMENT 'Logo地址',
    `remark`        varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`       varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time`   datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`       varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time`   datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`       tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`       int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_code` (`code`, `deleted`),
    UNIQUE KEY `uk_tenant_account` (`account`, `deleted`)
) ENGINE = InnoDB COMMENT = '租户表';

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user`
(
    `id`          varchar(64)  NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64)  NOT NULL COMMENT '租户ID',
    `username`    varchar(100) NOT NULL COMMENT '用户名',
    `password`    varchar(200) NOT NULL COMMENT '密码',
    `salt`        varchar(64)  NOT NULL COMMENT '密码盐值',
    `name`    varchar(100)          DEFAULT NULL COMMENT '姓名',
    `avatar`      varchar(500)          DEFAULT NULL COMMENT '头像地址',
    `phone`       varchar(20)           DEFAULT NULL COMMENT '手机号',
    `email`       varchar(100)          DEFAULT NULL COMMENT '邮箱',
    `gender`      tinyint               DEFAULT 0 COMMENT '性别（0未知 1男 2女）',
    `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `login_ip`    varchar(50)           DEFAULT NULL COMMENT '最后登录IP',
    `login_time`  datetime              DEFAULT NULL COMMENT '最后登录时间',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time` datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    UNIQUE KEY `uk_tenant_username` (`tenant_id`, `username`, `deleted`),
    UNIQUE KEY `uk_tenant_phone` (`tenant_id`, `phone`, `deleted`),
    UNIQUE KEY `uk_tenant_email` (`tenant_id`, `email`, `deleted`)
) ENGINE = InnoDB COMMENT = '用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role`
(
    `id`          varchar(64)  NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64)  NOT NULL COMMENT '租户ID',
    `name`        varchar(100) NOT NULL COMMENT '角色名称',
    `code`        varchar(100) NOT NULL COMMENT '角色编码',
    `sort`        int          NOT NULL DEFAULT 0 COMMENT '排序',
    `data_scope`  tinyint      NOT NULL DEFAULT 1 COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
    `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time` datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    UNIQUE KEY `uk_tenant_role_code` (`tenant_id`, `code`, `deleted`)
) ENGINE = InnoDB COMMENT = '角色表';

-- 权限表（目录/菜单/按钮）
CREATE TABLE IF NOT EXISTS `sys_permission`
(
    `id`                varchar(64)  NOT NULL COMMENT '主键',
    `parent_id`         varchar(64)  NOT NULL DEFAULT '0' COMMENT '父级ID',
    `ancestors`         varchar(500)          DEFAULT '' COMMENT '祖级列表',
    `level`             int          NOT NULL DEFAULT 0 COMMENT '层级',
    `name`              varchar(100) NOT NULL COMMENT '权限名称',
    `code`              varchar(200)          DEFAULT NULL COMMENT '权限标识',
    `path`              varchar(200)          DEFAULT NULL COMMENT '路由地址',
    `component`         varchar(200)          DEFAULT NULL COMMENT '组件路径',
    `redirect`          varchar(200)          DEFAULT NULL COMMENT '重定向地址',
    `icon`              varchar(100)          DEFAULT NULL COMMENT '图标',
    `sort`              int          NOT NULL DEFAULT 0 COMMENT '排序',
    `type`              tinyint      NOT NULL COMMENT '类型（0目录 1菜单 2按钮）',
    `hide_in_menu`      tinyint      NOT NULL DEFAULT 0 COMMENT '是否隐藏菜单（0否 1是）',
    `hide_in_breadcrumb` tinyint     NOT NULL DEFAULT 0 COMMENT '是否隐藏面包屑（0否 1是）',
    `hide_in_tab`       tinyint      NOT NULL DEFAULT 0 COMMENT '是否隐藏标签页（0否 1是）',
    `keep_alive`        tinyint      NOT NULL DEFAULT 0 COMMENT '是否缓存（0否 1是）',
    `affix`             tinyint      NOT NULL DEFAULT 0 COMMENT '是否固定标签页（0否 1是）',
    `disabled`          tinyint      NOT NULL DEFAULT 0 COMMENT '是否禁用（0否 1是）',
    `layout`            varchar(100)          DEFAULT NULL COMMENT '布局组件',
    `active_menu`       varchar(200)          DEFAULT NULL COMMENT '高亮菜单路径',
    `transition_name`   varchar(100)          DEFAULT NULL COMMENT '过渡动画名称',
    `status`            tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`            varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`           varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time`       datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`           varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time`       datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`           tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`           int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`code`, `deleted`),
    KEY `idx_permission_parent` (`parent_id`),
    KEY `idx_permission_ancestors` (`ancestors`)
) ENGINE = InnoDB COMMENT = '权限表';

-- 部门表
CREATE TABLE IF NOT EXISTS `sys_dept`
(
    `id`          varchar(64)  NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64)  NOT NULL COMMENT '租户ID',
    `parent_id`   varchar(64)  NOT NULL DEFAULT '0' COMMENT '父级ID',
    `ancestors`   varchar(500)          DEFAULT '' COMMENT '祖级列表',
    `level`       int          NOT NULL DEFAULT 0 COMMENT '层级',
    `name`        varchar(100) NOT NULL COMMENT '部门名称',
    `alias`       varchar(100)          DEFAULT NULL COMMENT '部门别名',
    `sort`        int          NOT NULL DEFAULT 0 COMMENT '排序',
    `leader`      varchar(50)           DEFAULT NULL COMMENT '负责人',
    `phone`       varchar(20)           DEFAULT NULL COMMENT '联系电话',
    `email`       varchar(100)          DEFAULT NULL COMMENT '邮箱',
    `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time` datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    KEY `idx_dept_parent` (`tenant_id`, `parent_id`),
    KEY `idx_dept_ancestors` (`tenant_id`, `ancestors`)
) ENGINE = InnoDB COMMENT = '部门表';

-- 岗位表
CREATE TABLE IF NOT EXISTS `sys_post`
(
    `id`          varchar(64)  NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64)  NOT NULL COMMENT '租户ID',
    `code`        varchar(100) NOT NULL COMMENT '岗位编码',
    `name`        varchar(100) NOT NULL COMMENT '岗位名称',
    `sort`        int          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time` datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    UNIQUE KEY `uk_tenant_post_code` (`tenant_id`, `code`, `deleted`)
) ENGINE = InnoDB COMMENT = '岗位表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role`
(
    `id`          varchar(64) NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64) NOT NULL COMMENT '租户ID',
    `user_id`     varchar(64) NOT NULL COMMENT '用户ID',
    `role_id`     varchar(64) NOT NULL COMMENT '角色ID',
    `creator`     varchar(64)          DEFAULT NULL COMMENT '创建者',
    `create_time` datetime             DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)          DEFAULT NULL COMMENT '更新者',
    `update_time` datetime             DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint     NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int         NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_role` (`role_id`),
    UNIQUE KEY `uk_user_role` (`tenant_id`, `user_id`, `role_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '用户角色关联表';

-- 用户岗位关联表
CREATE TABLE IF NOT EXISTS `sys_user_post`
(
    `id`          varchar(64) NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64) NOT NULL COMMENT '租户ID',
    `user_id`     varchar(64) NOT NULL COMMENT '用户ID',
    `post_id`     varchar(64) NOT NULL COMMENT '岗位ID',
    `creator`     varchar(64)          DEFAULT NULL COMMENT '创建者',
    `create_time` datetime             DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)          DEFAULT NULL COMMENT '更新者',
    `update_time` datetime             DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint     NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int         NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_post` (`post_id`),
    UNIQUE KEY `uk_user_post` (`tenant_id`, `user_id`, `post_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '用户岗位关联表';

-- 用户部门关联表
CREATE TABLE IF NOT EXISTS `sys_user_dept`
(
    `id`          varchar(64) NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64) NOT NULL COMMENT '租户ID',
    `user_id`     varchar(64) NOT NULL COMMENT '用户ID',
    `dept_id`     varchar(64) NOT NULL COMMENT '部门ID',
    `creator`     varchar(64)          DEFAULT NULL COMMENT '创建者',
    `create_time` datetime             DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)          DEFAULT NULL COMMENT '更新者',
    `update_time` datetime             DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint     NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int         NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_user` (`user_id`),
    KEY `idx_dept` (`dept_id`),
    UNIQUE KEY `uk_user_dept` (`tenant_id`, `user_id`, `dept_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '用户部门关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `sys_role_permission`
(
    `id`            varchar(64) NOT NULL COMMENT '主键',
    `tenant_id`     varchar(64) NOT NULL COMMENT '租户ID',
    `role_id`       varchar(64) NOT NULL COMMENT '角色ID',
    `permission_id` varchar(64) NOT NULL COMMENT '权限ID',
    `creator`       varchar(64)          DEFAULT NULL COMMENT '创建者',
    `create_time`   datetime             DEFAULT NULL COMMENT '创建时间',
    `updater`       varchar(64)          DEFAULT NULL COMMENT '更新者',
    `update_time`   datetime             DEFAULT NULL COMMENT '更新时间',
    `deleted`       tinyint     NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`       int         NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role_id`),
    KEY `idx_permission` (`permission_id`),
    UNIQUE KEY `uk_role_permission` (`tenant_id`, `role_id`, `permission_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '角色权限关联表';

-- 角色部门关联表（数据权限）
CREATE TABLE IF NOT EXISTS `sys_role_dept`
(
    `id`          varchar(64) NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64) NOT NULL COMMENT '租户ID',
    `role_id`     varchar(64) NOT NULL COMMENT '角色ID',
    `dept_id`     varchar(64) NOT NULL COMMENT '部门ID',
    `creator`     varchar(64)          DEFAULT NULL COMMENT '创建者',
    `create_time` datetime             DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)          DEFAULT NULL COMMENT '更新者',
    `update_time` datetime             DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint     NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int         NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_role` (`role_id`),
    KEY `idx_dept` (`dept_id`),
    UNIQUE KEY `uk_role_dept` (`tenant_id`, `role_id`, `dept_id`, `deleted`)
) ENGINE = InnoDB COMMENT = '角色部门关联表';

-- 字典表
CREATE TABLE IF NOT EXISTS `sys_dict`
(
    `id`          varchar(64)  NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64)  NOT NULL COMMENT '租户ID',
    `name`        varchar(100) NOT NULL COMMENT '字典名称',
    `type`        varchar(100) NOT NULL COMMENT '字典类型',
    `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time` datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_type` (`tenant_id`, `type`, `deleted`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '字典表';

-- 字典数据表
CREATE TABLE IF NOT EXISTS `sys_dict_data`
(
    `id`          varchar(64)  NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64)  NOT NULL COMMENT '租户ID',
    `dict_type`   varchar(100) NOT NULL COMMENT '字典类型',
    `label`       varchar(200) NOT NULL COMMENT '字典标签',
    `value`       varchar(200) NOT NULL COMMENT '字典值',
    `sort`        int          NOT NULL DEFAULT 0 COMMENT '排序',
    `css_class`   varchar(100)          DEFAULT NULL COMMENT '样式属性',
    `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time` datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant_type` (`tenant_id`, `dict_type`),
    UNIQUE KEY `uk_tenant_dict_value` (`tenant_id`, `dict_type`, `value`, `deleted`)
) ENGINE = InnoDB COMMENT = '字典数据表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config`
(
    `id`          varchar(64)  NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64)  NOT NULL COMMENT '租户ID',
    `name`        varchar(100) NOT NULL COMMENT '配置名称',
    `key`         varchar(200) NOT NULL COMMENT '配置键',
    `value`       text                  COMMENT '配置值',
    `type`        tinyint      NOT NULL DEFAULT 0 COMMENT '类型（0系统内置 1自定义）',
    `status`      tinyint      NOT NULL DEFAULT 1 COMMENT '状态（0停用 1正常）',
    `remark`      varchar(500)          DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)           DEFAULT NULL COMMENT '创建者',
    `create_time` datetime              DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)           DEFAULT NULL COMMENT '更新者',
    `update_time` datetime              DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint      NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int          NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_key` (`tenant_id`, `key`, `deleted`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '系统配置表';

-- 通知公告表
CREATE TABLE IF NOT EXISTS `sys_notice`
(
    `id`          varchar(64)   NOT NULL COMMENT '主键',
    `tenant_id`   varchar(64)   NOT NULL COMMENT '租户ID',
    `title`       varchar(200)  NOT NULL COMMENT '标题',
    `content`     longtext               COMMENT '内容',
    `type`        tinyint       NOT NULL DEFAULT 0 COMMENT '类型（0通知 1公告）',
    `status`      tinyint       NOT NULL DEFAULT 0 COMMENT '状态（0草稿 1发布 2撤回）',
    `remark`      varchar(500)           DEFAULT NULL COMMENT '备注',
    `creator`     varchar(64)            DEFAULT NULL COMMENT '创建者',
    `create_time` datetime               DEFAULT NULL COMMENT '创建时间',
    `updater`     varchar(64)            DEFAULT NULL COMMENT '更新者',
    `update_time` datetime               DEFAULT NULL COMMENT '更新时间',
    `deleted`     tinyint       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`     int           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '通知公告表';

-- 定时任务表
CREATE TABLE IF NOT EXISTS `sys_job`
(
    `id`                varchar(64)   NOT NULL COMMENT '主键',
    `tenant_id`         varchar(64)   NOT NULL COMMENT '租户ID',
    `name`              varchar(200)  NOT NULL COMMENT '任务名称',
    `group`             varchar(100)  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务分组',
    `invoke_target`     varchar(500)  NOT NULL COMMENT '调用目标（Bean名称.方法名）',
    `cron_expression`   varchar(200)  NOT NULL COMMENT 'Cron表达式',
    `misfire_policy`    tinyint       NOT NULL DEFAULT 3 COMMENT '执行策略（1立即执行 2执行一次 3放弃执行）',
    `concurrent`        tinyint       NOT NULL DEFAULT 0 COMMENT '是否并发执行（0否 1是）',
    `status`            tinyint       NOT NULL DEFAULT 1 COMMENT '状态（0暂停 1运行）',
    `remark`            varchar(500)           DEFAULT NULL COMMENT '备注',
    `creator`           varchar(64)            DEFAULT NULL COMMENT '创建者',
    `create_time`       datetime               DEFAULT NULL COMMENT '创建时间',
    `updater`           varchar(64)            DEFAULT NULL COMMENT '更新者',
    `update_time`       datetime               DEFAULT NULL COMMENT '更新时间',
    `deleted`           tinyint       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`           int           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '定时任务表';

-- 日志表
CREATE TABLE IF NOT EXISTS `sys_log`
(
    `id`            varchar(64)   NOT NULL COMMENT '主键',
    `tenant_id`     varchar(64)            DEFAULT NULL COMMENT '租户ID',
    `type`          tinyint       NOT NULL COMMENT '类型（1登录日志 2操作日志 3异常日志 4定时任务日志）',
    `title`         varchar(200)           DEFAULT NULL COMMENT '操作标题',
    `method`        varchar(200)           DEFAULT NULL COMMENT '方法名称',
    `url`           varchar(500)           DEFAULT NULL COMMENT '请求URL',
    `request_method` varchar(10)           DEFAULT NULL COMMENT '请求方式',
    `params`        longtext               COMMENT '请求参数',
    `result`        longtext               COMMENT '返回结果',
    `status`        tinyint       NOT NULL DEFAULT 1 COMMENT '状态（0失败 1成功）',
    `error_msg`     longtext               COMMENT '错误信息',
    `ip`            varchar(50)            DEFAULT NULL COMMENT 'IP地址',
    `browser`       varchar(100)           DEFAULT NULL COMMENT '浏览器',
    `os`            varchar(100)           DEFAULT NULL COMMENT '操作系统',
    `duration`      bigint                 DEFAULT 0 COMMENT '耗时（毫秒）',
    `operator`      varchar(64)            DEFAULT NULL COMMENT '操作人',
    `creator`       varchar(64)            DEFAULT NULL COMMENT '创建者',
    `create_time`   datetime               DEFAULT NULL COMMENT '创建时间',
    `updater`       varchar(64)            DEFAULT NULL COMMENT '更新者',
    `update_time`   datetime               DEFAULT NULL COMMENT '更新时间',
    `deleted`       tinyint       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`       int           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`),
    KEY `idx_type` (`type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB COMMENT = '日志表';

-- 文件表
CREATE TABLE IF NOT EXISTS `sys_file`
(
    `id`            varchar(64)   NOT NULL COMMENT '主键',
    `tenant_id`     varchar(64)   NOT NULL COMMENT '租户ID',
    `name`          varchar(200)  NOT NULL COMMENT '文件名称',
    `original_name` varchar(200)           DEFAULT NULL COMMENT '原始文件名',
    `url`           varchar(500)  NOT NULL COMMENT '文件地址',
    `path`          varchar(500)           DEFAULT NULL COMMENT '文件路径',
    `size`          bigint                 DEFAULT 0 COMMENT '文件大小（字节）',
    `type`          varchar(100)           DEFAULT NULL COMMENT '文件类型（MIME）',
    `extension`     varchar(20)            DEFAULT NULL COMMENT '文件扩展名',
    `storage_type`  tinyint       NOT NULL DEFAULT 0 COMMENT '存储类型（0本地 1OSS 2S3）',
    `md5`           varchar(64)            DEFAULT NULL COMMENT '文件MD5',
    `remark`        varchar(500)           DEFAULT NULL COMMENT '备注',
    `creator`       varchar(64)            DEFAULT NULL COMMENT '创建者',
    `create_time`   datetime               DEFAULT NULL COMMENT '创建时间',
    `updater`       varchar(64)            DEFAULT NULL COMMENT '更新者',
    `update_time`   datetime               DEFAULT NULL COMMENT '更新时间',
    `deleted`       tinyint       NOT NULL DEFAULT 0 COMMENT '删除标记（0存在 1删除）',
    `version`       int           NOT NULL DEFAULT 1 COMMENT '版本号',
    PRIMARY KEY (`id`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE = InnoDB COMMENT = '文件表';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 默认租户
INSERT INTO `sys_tenant` (`id`, `package_id`, `code`, `name`, `account`, `status`, `account_limit`)
VALUES ('1', '1', 'default', '默认租户', 'admin', 1, -1);

-- 默认租户套餐
INSERT INTO `sys_tenant_package` (`id`, `name`, `status`)
VALUES ('1', '默认套餐', 1);

-- 默认部门
INSERT INTO `sys_dept` (`id`, `tenant_id`, `parent_id`, `ancestors`, `level`, `name`, `sort`, `status`)
VALUES ('1', '1', '0', '1', 1, '总公司', 0, 1);

-- 超级管理员角色
INSERT INTO `sys_role` (`id`, `tenant_id`, `name`, `code`, `sort`, `data_scope`, `status`)
VALUES ('1', '1', '超级管理员', 'admin', 0, 1, 1);

-- 普通角色
INSERT INTO `sys_role` (`id`, `tenant_id`, `name`, `code`, `sort`, `data_scope`, `status`)
VALUES ('2', '1', '普通用户', 'common', 1, 5, 1);

-- 超级管理员用户（密码: admin123）
INSERT INTO `sys_user` (`id`, `tenant_id`, `username`, `password`, `salt`, `name`, `status`)
VALUES ('1', '1', 'admin', '$2a$12$Q6r2ag5PQUq7pli.DHqgXeC9S1JXOoDjdGu4KUB36RHvc3u/yxQY2', 'default-salt', '超级管理员', 1);

-- 管理员部门关联
INSERT INTO `sys_user_dept` (`id`, `tenant_id`, `user_id`, `dept_id`)
VALUES ('1', '1', '1', '1');

-- 管理员角色关联
INSERT INTO `sys_user_role` (`id`, `tenant_id`, `user_id`, `role_id`)
VALUES ('1', '1', '1', '1');
