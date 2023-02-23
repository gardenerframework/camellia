CREATE TABLE `api_option`
(
    `id`               varchar(512) NOT NULL COMMENT '选项id(bean)名称',
    `option`           json         NOT NULL COMMENT '存储的选项',
    `version_number`   varchar(128) NOT NULL COMMENT '版本号',
    `creator`          varchar(256) DEFAULT NULL COMMENT '创建人',
    `created_time`     datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`          varchar(256) DEFAULT NULL COMMENT '更新人',
    `last_update_time` datetime     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='api选项表';

CREATE TABLE `blocked_user`
(
    `id`               varchar(256) NOT NULL DEFAULT '',
    `principal`        varbinary(2048) DEFAULT NULL,
    `expire_at`        datetime              DEFAULT NULL,
    `created_time`     datetime              DEFAULT NULL,
    `last_update_time` datetime              DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
