DROP TABLE IF EXISTS `emergrp_accident_base_station`;
CREATE TABLE `emergrp_accident_base_station`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`     bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `province`   bigint(64)     DEFAULT NULL COMMENT '省份',
    `city`       bigint(64)     DEFAULT NULL COMMENT '市',
    `name`       varchar(256)   DEFAULT NULL COMMENT '基站名称',
    `longitude`  decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`   decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `LACorTAC`   varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`    varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `system`     varchar(32)    DEFAULT NULL COMMENT '小区制式',
    `ISP`        varchar(32)    DEFAULT NULL COMMENT '运营商',
    `createTime` datetime       DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `emergrp_accident_personnel`;
CREATE TABLE `emergrp_accident_personnel`
(
    `id`              bigint(64) NOT NULL COMMENT 'ID',
    `taskId`          bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `time`            datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`          char(11)       DEFAULT NULL COMMENT '手机号码',
    `IMSI`            bigint(20)     DEFAULT NULL COMMENT 'IMSI码',
    `LACorTAC`        varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`         varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `longitude`       decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`        decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `lastStationName` varchar(256)   DEFAULT NULL COMMENT '最后出现基站名称',
    `lastLongitude`   decimal(10, 7) DEFAULT NULL COMMENT '最后出现的经度',
    `lastLatitude`    decimal(10, 7) DEFAULT NULL COMMENT '最后出现的纬度',
    `lastLACorTAC`    varchar(32)    DEFAULT NULL COMMENT '最后出现的LAC/TAC',
    `lastCIorECI`     varchar(32)    DEFAULT NULL COMMENT '最后出现的CI/ECI',
    `createTime`      datetime       DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `emergrp_accident_personnel_control`;
CREATE TABLE `emergrp_accident_personnel_control`
(
    `id`         bigint(64) NOT NULL COMMENT 'id',
    `taskId`     bigint(64)  DEFAULT NULL COMMENT '任务ID',
    `time`       datetime    DEFAULT NULL COMMENT '时间',
    `MSISDN`     char(11)    DEFAULT NULL COMMENT '手机号码',
    `LACorTAC`   varchar(32) DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`    varchar(32) DEFAULT NULL COMMENT 'CI/ECI',
    `MOorMT`     tinyint(2)  DEFAULT NULL COMMENT 'MO/MT',
    `servicType` varchar(32) DEFAULT NULL COMMENT '业务类型',
    `createTime` datetime    DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `emergrp_accident_personnel_person`;
CREATE TABLE `emergrp_accident_personnel_person`
(
    `id`             bigint(64) NOT NULL COMMENT '主键',
    `taskId`         bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `time`           datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`         char(11)       DEFAULT NULL COMMENT '手机号码',
    `IMSI`           bigint(20)     DEFAULT NULL COMMENT 'IMSI码',
    `LACorTAC`       varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`        varchar(32)    DEFAULT 'CI/ECI',
    `RAT`            varchar(16)    DEFAULT '' COMMENT '承载网络',
    `longitude`      decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`       decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `flowType`       varchar(32)    DEFAULT NULL COMMENT '流量类型',
    `coordinateType` tinyint(2)     DEFAULT NULL COMMENT '坐标系',
    `createTime`     datetime       DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `emergrp_accident_suspected_missing`;
CREATE TABLE `emergrp_accident_suspected_missing`
(
    `id`              bigint(64) NOT NULL COMMENT '主键',
    `taskId`          bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `time`            datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`          char(11)       DEFAULT NULL COMMENT '手机号码',
    `IMSI`            bigint(20)     DEFAULT NULL COMMENT 'IMSI码',
    `LACorTAC`        varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`         varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `longitude`       decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`        decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `lastStationName` varchar(256)   DEFAULT NULL COMMENT '最后出现基站名称',
    `lastLongitude`   decimal(10, 7) DEFAULT NULL COMMENT '最后出现的经度',
    `lastLatitude`    decimal(10, 7) DEFAULT NULL COMMENT '最后出现的纬度',
    `lastLACorTAC`    varchar(32)    DEFAULT NULL COMMENT '最后出现的LAC/TAC',
    `lastCIorECI`     varchar(32)    DEFAULT NULL,
    `status`          tinyint(2)     DEFAULT NULL COMMENT '状态0：待核实，1：失联，2：正常',
    `missingTime`     datetime       DEFAULT NULL COMMENT '失联时间',
    `createTime`      datetime       DEFAULT NULL COMMENT '创建时间',
    `updateTime`      datetime       DEFAULT NULL COMMENT '修改时间',
    `updateUser`      bigint(64)     DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `emergrp_person_info`;
CREATE TABLE `emergrp_person_info`
(
    `id`         bigint(64) NOT NULL COMMENT '主键',
    `province`   bigint(20)  DEFAULT NULL COMMENT '省份',
    `city`       bigint(20)  DEFAULT NULL COMMENT '市',
    `MSISDN`     char(11)    DEFAULT NULL COMMENT '手机号码',
    `IMSI`       bigint(20)  DEFAULT NULL COMMENT 'IMSI码',
    `age`        tinyint(4)  DEFAULT NULL COMMENT '年龄',
    `sex`        tinyint(2)  DEFAULT NULL COMMENT '性别',
    `IDType`     tinyint(2)  DEFAULT NULL COMMENT '证件类型',
    `IDNumber`   varchar(32) DEFAULT NULL COMMENT '证件号码',
    `createTime` datetime    DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime    DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

DROP TABLE IF EXISTS `emergrp_accident_escape_danger`;
CREATE TABLE `emergrp_accident_escape_danger`
(
    `id`              bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`          bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `time`            datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`          varchar(50)    DEFAULT NULL COMMENT '手机号码',
    `IMSI`            varchar(50)    DEFAULT NULL COMMENT 'IMSI码',
    `LACorTAC`        varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`         varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `longitude`       decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`        decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `lastStationName` varchar(256)   DEFAULT NULL COMMENT '最后出现基站名称',
    `lastLongitude`   decimal(10, 7) DEFAULT NULL COMMENT '最后出现的经度',
    `lastLatitude`    decimal(10, 7) DEFAULT NULL COMMENT '最后出现的纬度',
    `lastLACorTAC`    varchar(32)    DEFAULT NULL COMMENT '最后出现的LAC/TAC',
    `lastCIorECI`     varchar(32)    DEFAULT NULL COMMENT '最后出现的CI/ECI',
    `status`          tinyint(2)     DEFAULT NULL COMMENT '状态0：待核实，1：失联，2：正常',
    `missingTime`     datetime       DEFAULT NULL COMMENT '失联时间',
    `createTime`      datetime       DEFAULT NULL COMMENT '创建时间',
    `updateTime`      datetime       DEFAULT NULL COMMENT '修改时间',
    `updateUser`      bigint(64)     DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `emergrp_accident_escape_direction`;
CREATE TABLE `emergrp_accident_escape_direction`
(
    `id`                bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`            bigint(64)    DEFAULT NULL COMMENT '任务ID',
    `directionProvince` varchar(8)    DEFAULT '' COMMENT '去向省份',
    `directionCity`     varchar(8)    DEFAULT NULL COMMENT '去向城市',
    `directionCounty`   varchar(8)    DEFAULT NULL COMMENT '去向区县',
    `directionTown`     varchar(16)   DEFAULT NULL COMMENT '去向乡镇',
    `escapeNum`         bigint(12)    DEFAULT NULL COMMENT '脱险人员人数',
    `escapeProp`        decimal(8, 8) DEFAULT NULL COMMENT '脱险人员数量占总脱险人员比例',
    `label`             varchar(8)    DEFAULT NULL COMMENT '安置地/迁入地标签',
    `createTime`        datetime      DEFAULT NULL COMMENT '创建时间',
    `updateTime`        datetime      DEFAULT NULL COMMENT '修改时间',
    `updateUser`        bigint(64)    DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
 DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS `emergrp_accident_escape_location`;
CREATE TABLE `emergrp_accident_escape_location`
(
    `id`                bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`            bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `time`              datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`            varchar(50)    DEFAULT NULL COMMENT '手机号码',
    `IMSI`              varchar(50)    DEFAULT NULL COMMENT 'IMSI码',
    `LACorTAC`          varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`           varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `longitude`         decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`          decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `directionProvince` varchar(8)     DEFAULT '' COMMENT '去向省份',
    `directionCity`     varchar(8)     DEFAULT NULL COMMENT '去向城市',
    `directionCounty`   varchar(8)     DEFAULT NULL COMMENT '去向区县',
    `directionTown`     varchar(16)    DEFAULT NULL COMMENT '去向乡镇',
    `createTime`        datetime       DEFAULT NULL COMMENT '创建时间',
    `updateTime`        datetime       DEFAULT NULL COMMENT '修改时间',
    `updateUser`        bigint(64)     DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

ALTER TABLE emergrp_accident_escape_direction ADD directionAdds varchar(64) NULL COMMENT '去向详细地址';
ALTER TABLE emergrp_accident_escape_location ADD directionAdds varchar(64) NULL COMMENT '去向详细地址';
ALTER TABLE emergrp_accident_escape_direction MODIFY COLUMN escapeProp decimal(8, 7);

ALTER TABLE emergrp_accident_escape_direction ADD tibetOrXingjiangNum bigint(12) NULL DEFAULT 0 COMMENT '涉疆涉藏人数';

ALTER TABLE emergrp_accident_escape_direction DROP COLUMN tibetOrXingjiangNum;
ALTER TABLE emergrp_accident_escape_direction ADD tibetNum bigint(12) NULL DEFAULT 0 COMMENT '涉藏人数';
ALTER TABLE emergrp_accident_escape_direction ADD xinjiangNum bigint(12) NULL DEFAULT 0 COMMENT '涉疆人数';

ALTER TABLE emergrp_accident_escape_direction ADD status bigint(1) NULL COMMENT '状态 0为安置/迁入地,1为援灾乡镇';

CREATE INDEX IMSI ON emergrp_accident_personnel (IMSI) USING BTREE;
CREATE INDEX IMSI ON emergrp_person_info (IMSI) USING BTREE;
