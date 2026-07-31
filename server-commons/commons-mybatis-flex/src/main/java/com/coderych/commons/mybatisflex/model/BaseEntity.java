package com.coderych.commons.mybatisflex.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 实体基类，提供主键、创建/更新信息、逻辑删除标记和乐观锁版本号等通用字段。
 *
 * @author YCH
 */
@Getter
@Setter
public abstract class BaseEntity {
    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updater;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记，0-未删除，1-已删除
     */
    private Long deleted;

    /**
     * 乐观锁版本号
     */
    private Integer version;
}
