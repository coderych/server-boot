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
    private String id;

    private String creator;

    private LocalDateTime createTime;

    private String updater;

    private LocalDateTime updateTime;

    private Long deleted;

    private Integer version;
}
