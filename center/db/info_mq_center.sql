/*
 Navicat Premium Data Transfer

 Source Server         : 47.116.74.175
 Source Server Type    : MySQL
 Source Server Version : 50734
 Source Host           : 47.116.74.175:3306
 Source Schema         : info_mq_center

 Target Server Type    : MySQL
 Target Server Version : 50734
 File Encoding         : 65001

 Date: 22/07/2021 00:42:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for center_consumer_group
-- ----------------------------
DROP TABLE IF EXISTS `center_consumer_group`;
CREATE TABLE `center_consumer_group`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `topicName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'Topic',
  `groupName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消费者组',
  `offsetReset` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'Offset Reset',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '描述',
  `notifier` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '错误通知人',
  `par_assign_strategy` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '分区策略',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for center_consumer_task
-- ----------------------------
DROP TABLE IF EXISTS `center_consumer_task`;
CREATE TABLE `center_consumer_task`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `taskCode` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务编码',
  `getType` tinyint(10) NOT NULL COMMENT '获取方式1 单条 2批量',
  `status` tinyint(10) NOT NULL COMMENT '状态 0 禁用 1 启用',
  `operateType` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调用类型 restful dubbo',
  `operateUrl` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '调用地址',
  `batchNumber` int(10) NOT NULL COMMENT '每次poll拉取数',
  `operateOutTime` int(10) NOT NULL COMMENT '调用超时时间（ms）',
  `groupId` int(10) NOT NULL COMMENT '消费者组id',
  `hea_int_ms` int(10) NOT NULL,
  `ses_timeout_ms` int(10) NOT NULL,
  `max_poll_ms` int(10) NOT NULL,
  `modifytime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `execCode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for center_errormessage_list
-- ----------------------------
DROP TABLE IF EXISTS `center_errormessage_list`;
CREATE TABLE `center_errormessage_list`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `topicName` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'topic名称',
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '插入时间',
  `mesBody` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '消息结构体',
  `status` int(255) NOT NULL COMMENT '状态 0 未处理 1已处理 2忽略',
  `taskCode` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '任务编码',
  `errorMsg` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '报错信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_time_status_topic`(`createTime`, `status`, `topicName`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 126 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for center_execution_list
-- ----------------------------
DROP TABLE IF EXISTS `center_execution_list`;
CREATE TABLE `center_execution_list`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行器地址',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行器名称',
  `status` tinyint(255) NOT NULL COMMENT '状态 0-禁用 1-启用',
  `code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '执行器code',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for center_topic
-- ----------------------------
DROP TABLE IF EXISTS `center_topic`;
CREATE TABLE `center_topic`  (
  `topicName` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'kafka中的topic',
  `description` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'topic的用途描述',
  `partition` tinyint(4) NOT NULL COMMENT '对应主题的分区数',
  `replicationFactor` tinyint(4) NOT NULL COMMENT '对应主题的副本数',
  PRIMARY KEY (`topicName`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
