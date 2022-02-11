DROP TABLE IF EXISTS `emergrp_accident_base_station`;
CREATE TABLE `emergrp_accident_base_station`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`     bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `ruleId`     bigint(64)     DEFAULT NULL COMMENT '规则ID',
    `province`   varchar(100)   DEFAULT NULL COMMENT '省份',
    `city`       varchar(100)   DEFAULT NULL COMMENT '城市',
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
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_base_station
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_escape_danger
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_escape_danger`;
CREATE TABLE `emergrp_accident_escape_danger`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`     bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `ruleId`     bigint(64)     DEFAULT NULL COMMENT '规则ID',
    `time`       datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`     varchar(50)    DEFAULT NULL COMMENT '手机号码',
    `IMSI`       varchar(50)    DEFAULT NULL COMMENT 'IMSI码',
    `LACorTAC`   varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`    varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `longitude`  decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`   decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `createTime` datetime       DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_escape_danger
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_personnel
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_personnel`;
CREATE TABLE `emergrp_accident_personnel`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `taskId`     bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `ruleId`     bigint(64)     DEFAULT NULL COMMENT '规则ID',
    `time`       datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`     varchar(50)    DEFAULT NULL COMMENT '手机号码',
    `IMSI`       varchar(50)    DEFAULT NULL COMMENT 'IMSI',
    `LACorTAC`   varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`    varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `longitude`  decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`   decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `createTime` datetime       DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `IMSI` (`IMSI`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_personnel
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_personnel_control
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_personnel_control`;
CREATE TABLE `emergrp_accident_personnel_control`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `taskId`     bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `ruleId`     bigint(64)     DEFAULT NULL COMMENT '规则ID',
    `time`       datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`     varchar(50)    DEFAULT NULL COMMENT '手机号码',
    `IMSI`       varchar(50)    DEFAULT NULL COMMENT 'IMSI码',
    `LACorTAC`   varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`    varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `MOorMT`     tinyint(2)     DEFAULT NULL COMMENT 'MO/MT',
    `RAT`        tinyint(16)    DEFAULT NULL COMMENT '承载网络',
    `servicType` varchar(32)    DEFAULT NULL COMMENT '业务类型',
    `longitude`  decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`   decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `createTime` datetime       DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_personnel_control
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_personnel_person
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_personnel_person`;
CREATE TABLE `emergrp_accident_personnel_person`
(
    `id`             bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`         bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `ruleId`         bigint(64)     DEFAULT NULL COMMENT '规则ID',
    `time`           datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`         varchar(50)    DEFAULT NULL COMMENT '手机号码',
    `IMSI`           varchar(50)    DEFAULT NULL COMMENT 'IMSI',
    `LACorTAC`       varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`        varchar(32)    DEFAULT 'CI/ECI',
    `RAT`            tinyint(10)    DEFAULT NULL COMMENT '承载网络',
    `longitude`      decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`       decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `flowType`       varchar(32)    DEFAULT NULL COMMENT '流量类型',
    `coordinateType` tinyint(2)     DEFAULT NULL COMMENT '坐标系',
    `createTime`     datetime       DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_personnel_person
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_rescue_personnel
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_rescue_personnel`;
CREATE TABLE `emergrp_accident_rescue_personnel`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`     bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `ruleId`     bigint(64)     DEFAULT NULL COMMENT '规则ID',
    `time`       datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`     varchar(50)    DEFAULT NULL COMMENT '手机号码',
    `IMSI`       varchar(50)    DEFAULT NULL COMMENT 'IMSI',
    `LACorTAC`   varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`    varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `longitude`  decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`   decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `createTime` datetime       DEFAULT NULL COMMENT '����ʱ��',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_rescue_personnel
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_stat
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_stat`;
CREATE TABLE `emergrp_accident_stat`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`     bigint(64)     DEFAULT NULL COMMENT 'taskId',
    `ruleId`     bigint(64)     DEFAULT NULL COMMENT '规则Id',
    `type`       tinyint(2)     DEFAULT NULL COMMENT '类型 1-总人数,2-脱险人数,3-涉藏人数,4-涉疆人数',
    `num`        decimal(10, 7) DEFAULT NULL COMMENT '数量',
    `createTime` datetime       DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_stat
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_stat_category
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_stat_category`;
CREATE TABLE `emergrp_accident_stat_category`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`     bigint(64) DEFAULT NULL COMMENT '任务Id',
    `ruleId`     bigint(64) DEFAULT NULL COMMENT '规则Id',
    `type`       tinyint(2) DEFAULT NULL COMMENT '类型 1-年龄分布,2-性别分布',
    `category`   tinyint(2) DEFAULT NULL COMMENT '类别',
    `num`        bigint(64) DEFAULT NULL COMMENT '数量',
    `createTime` datetime   DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_stat_category
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_stat_personnel
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_stat_personnel`;
CREATE TABLE `emergrp_accident_stat_personnel`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`     bigint(64)     DEFAULT NULL COMMENT 'taskId',
    `ruleId`     bigint(64)     DEFAULT NULL COMMENT '规则Id',
    `type`       tinyint(2)     DEFAULT NULL COMMENT '类型 1:脱险人员,2-救援人员',
    `province`   varchar(100)   DEFAULT NULL COMMENT '省份',
    `city`       varchar(100)   DEFAULT NULL COMMENT '市',
    `area`       varchar(128)   DEFAULT NULL COMMENT '区/县',
    `town`       varchar(128)   DEFAULT NULL COMMENT '乡/镇',
    `num`        decimal(10, 7) DEFAULT NULL COMMENT '数量',
    `createTime` datetime       DEFAULT NULL COMMENT '创建事件',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_stat_personnel
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_stat_source
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_stat_source`;
CREATE TABLE `emergrp_accident_stat_source`
(
    `id`           bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`       bigint(64)   DEFAULT NULL COMMENT 'taskId',
    `ruleId`       bigint(64)   DEFAULT NULL COMMENT '规则Id',
    `provinceCode` tinyint(2)   DEFAULT NULL COMMENT '类型',
    `provinceName` varchar(100) DEFAULT NULL COMMENT '名称',
    `type`         tinyint(2)   DEFAULT NULL COMMENT '类型 1-总人数,2-脱险人数,3-涉藏人数,4-涉疆人数',
    `num`          bigint(64)   DEFAULT NULL COMMENT '数量',
    `createTime`   datetime     DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_stat_source
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_accident_suspected_missing
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_accident_suspected_missing`;
CREATE TABLE `emergrp_accident_suspected_missing`
(
    `id`          bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`      bigint(64)     DEFAULT NULL COMMENT '任务ID',
    `ruleId`      bigint(64)     DEFAULT NULL COMMENT '规则ID',
    `time`        datetime       DEFAULT NULL COMMENT '时间',
    `MSISDN`      varchar(50)    DEFAULT NULL COMMENT '手机号码',
    `IMSI`        varchar(50)    DEFAULT NULL COMMENT 'IMSI',
    `LACorTAC`    varchar(32)    DEFAULT NULL COMMENT 'LAC/TAC',
    `CIorECI`     varchar(32)    DEFAULT NULL COMMENT 'CI/ECI',
    `longitude`   decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`    decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `status`      tinyint(2)     DEFAULT NULL COMMENT '状态 0：待核实，1：失联，2：正常',
    `missingTime` datetime       DEFAULT NULL COMMENT '失联时间',
    `createTime`  datetime       DEFAULT NULL COMMENT '创建时间',
    `updateTime`  datetime       DEFAULT NULL COMMENT '修改时间',
    `updateUser`  bigint(64)     DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_accident_suspected_missing
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_person_data_info
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_person_data_info`;
CREATE TABLE `emergrp_person_data_info`
(
    `id`          bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `dataName`    varchar(200) DEFAULT NULL COMMENT '数据名称',
    `previewPath` varchar(200) DEFAULT NULL COMMENT '预览地址',
    `taskId`      bigint(64)   DEFAULT NULL COMMENT '任务ID',
    `dataType`    tinyint(2)   DEFAULT NULL COMMENT '数据类型',
    `bucketName`  varchar(200) DEFAULT NULL COMMENT '桶名',
    `fileName`    varchar(500) DEFAULT NULL COMMENT '对象名',
    `status`      tinyint(2)   DEFAULT NULL COMMENT '状态  1：已分析，2：未分析',
    `createTime`  datetime     DEFAULT NULL COMMENT '创建时间',
    `updateTime`  datetime     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_person_data_info
-- ----------------------------

-- ----------------------------
-- Table structure for emergrp_person_info
-- ----------------------------
DROP TABLE IF EXISTS `emergrp_person_info`;
CREATE TABLE `emergrp_person_info`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `province`   varchar(100) DEFAULT NULL COMMENT '省份',
    `city`       varchar(100) DEFAULT NULL COMMENT '城市',
    `MSISDN`     varchar(20)  DEFAULT NULL COMMENT '手机号码',
    `IMSI`       varchar(100) DEFAULT NULL COMMENT 'IMSI',
    `age`        tinyint(4)   DEFAULT NULL COMMENT '年龄',
    `sex`        tinyint(2)   DEFAULT NULL COMMENT '性别',
    `IDType`     varchar(20)  DEFAULT NULL COMMENT '证件类型',
    `IDNumber`   varchar(32)  DEFAULT NULL COMMENT '证件号码',
    `createTime` datetime     DEFAULT NULL COMMENT '创建时间',
    `updateTime` datetime     DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `IMSI` (`IMSI`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emergrp_person_info
-- ----------------------------

-- ----------------------------
-- Table structure for emerg_accident_rule
-- ----------------------------
DROP TABLE IF EXISTS `emerg_accident_rule`;
CREATE TABLE `emerg_accident_rule`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`     bigint(64) NOT NULL COMMENT '任务ID',
    `time`       datetime(6)    DEFAULT NULL COMMENT '事故发生时间',
    `raduis`     decimal(10, 2) DEFAULT NULL COMMENT '半径',
    `longitude`  decimal(10, 7) DEFAULT NULL COMMENT '经度',
    `latitude`   decimal(10, 7) DEFAULT NULL COMMENT '纬度',
    `createTime` datetime   NOT NULL COMMENT '创建时间',
    `createUser` bigint(64) NOT NULL COMMENT '创建人ID',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emerg_accident_rule
-- ----------------------------

-- ----------------------------
-- Table structure for emerg_missing_oper_task
-- ----------------------------
DROP TABLE IF EXISTS `emerg_missing_oper_task`;
CREATE TABLE `emerg_missing_oper_task`
(
    `id`         bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `taskId`     bigint(64) NOT NULL COMMENT '任务ID',
    `taskName`   varchar(200)   DEFAULT NULL COMMENT '任务名称',
    `eventId`    bigint(64) NOT NULL COMMENT '事件ID',
    `eventName`  varchar(200)   DEFAULT NULL COMMENT '事件名称',
    `startTime`  datetime       DEFAULT NULL COMMENT '开始时间',
    `memo`       varchar(512)   DEFAULT NULL COMMENT '备注说明',
    `status`     tinyint(4) NOT NULL COMMENT '状态',
    `longitude`  decimal(10, 7) DEFAULT NULL COMMENT '事故经度',
    `latitude`   decimal(10, 7) DEFAULT NULL COMMENT '事故纬度',
    `raduis`     decimal(10, 2) DEFAULT NULL COMMENT '事故半径',
    `progress`   decimal(4, 2)  DEFAULT NULL COMMENT '执行进度',
    `createTime` datetime   NOT NULL COMMENT '创建时间',
    `createUser` bigint(64) NOT NULL COMMENT '创建人ID',
    `updateTime` datetime       DEFAULT NULL COMMENT '修改时间',
    `updateUser` bigint(64)     DEFAULT NULL COMMENT '修改人ID',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of emerg_missing_oper_task
-- ----------------------------
