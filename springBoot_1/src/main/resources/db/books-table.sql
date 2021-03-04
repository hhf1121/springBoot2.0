/*
Navicat MySQL Data Transfer

Source Server         : mysql7
Source Server Version : 50729
Source Host           : localhost:3307
Source Database       : books

Target Server Type    : MYSQL
Target Server Version : 50729
File Encoding         : 65001

Date: 2021-03-04 08:56:48
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for base_config
-- ----------------------------
DROP TABLE IF EXISTS `base_config`;
CREATE TABLE `base_config` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键',
  `config_name` varchar(100) DEFAULT '' COMMENT '配置名称',
  `config_code` varchar(50) DEFAULT '' COMMENT '配置编码',
  `type_label` varchar(50) DEFAULT '' COMMENT '某配置的名',
  `type_value` tinyint(1) DEFAULT '0' COMMENT '某配置的值',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `last_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `color` varchar(50) DEFAULT '' COMMENT '统计图展示颜色',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for base_district
-- ----------------------------
DROP TABLE IF EXISTS `base_district`;
CREATE TABLE `base_district` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` bigint(50) NOT NULL DEFAULT '0' COMMENT '行政区域编码',
  `parent_code` varchar(10) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '上级区域编码',
  `name` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '区域名称',
  `merger_name` varchar(200) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '全名称',
  `level_type` varchar(45) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '区域等级0-国家1-省2-市3-区县4-乡镇',
  `is_delete` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`,`code`),
  KEY `idx_code` (`code`) USING BTREE,
  KEY `idx_level_type` (`level_type`) USING BTREE,
  KEY `idx_parent_code` (`parent_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6645943871169626113 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for base_msg
-- ----------------------------
DROP TABLE IF EXISTS `base_msg`;
CREATE TABLE `base_msg` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '用户名称',
  `from_id` int(11) NOT NULL DEFAULT '0' COMMENT '发送方id',
  `to_id` int(11) NOT NULL DEFAULT '0' COMMENT '接收方id',
  `msg` varchar(255) NOT NULL DEFAULT '' COMMENT '消息',
  `is_delete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态:0,未读1,已读',
  `last_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `count` int(10) DEFAULT '0',
  `countSize` int(10) DEFAULT '10',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for borrow
-- ----------------------------
DROP TABLE IF EXISTS `borrow`;
CREATE TABLE `borrow` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) DEFAULT NULL,
  `bookName` varchar(255) DEFAULT NULL,
  `userId` int(10) NOT NULL,
  `bookId` int(10) NOT NULL,
  `borrowTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `bakeTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for libraryborrow
-- ----------------------------
DROP TABLE IF EXISTS `libraryborrow`;
CREATE TABLE `libraryborrow` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) DEFAULT NULL,
  `bookName` varchar(255) DEFAULT NULL,
  `userId` int(10) NOT NULL,
  `bookId` int(10) NOT NULL,
  `borrowTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `bakeTime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for listno
-- ----------------------------
DROP TABLE IF EXISTS `listno`;
CREATE TABLE `listno` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `listName` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  `yesNo` int(5) NOT NULL COMMENT '权限级别',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for read_book_pd
-- ----------------------------
DROP TABLE IF EXISTS `read_book_pd`;
CREATE TABLE `read_book_pd` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8 DEFAULT NULL,
  `en_name` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `author` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `imgurl` varchar(255) DEFAULT NULL,
  `discription` text CHARACTER SET utf8,
  `create_time` datetime DEFAULT NULL,
  `creator` int(11) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` int(4) NOT NULL DEFAULT '1' COMMENT '1正常，-1删除，0下架',
  `comment_num` int(11) DEFAULT NULL COMMENT '评论数',
  `price` int(11) DEFAULT NULL COMMENT '价格，分',
  `category` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7290 DEFAULT CHARSET=latin1 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `roleName` varchar(255) NOT NULL,
  `creationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for sell_goods
-- ----------------------------
DROP TABLE IF EXISTS `sell_goods`;
CREATE TABLE `sell_goods` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键ID',
  `sell_title` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '商品标题',
  `sell_content` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '商品内容',
  `sell_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '商品类型1.家电2.衣物3.代步工具..',
  `goods_fee` decimal(12,2) NOT NULL DEFAULT '0.00' COMMENT '商品金额',
  `sell_category` tinyint(1) NOT NULL DEFAULT '0' COMMENT '商品类别1.求购2.销售',
  `sell_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '商品状态1.已买2.已卖3.下架4.正常',
  `user_code` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '联系人编码',
  `user_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '联系人名字',
  `user_phone` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '电话',
  `user_address` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '地址',
  `remark` varchar(550) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '备注',
  `version` int(10) NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creater` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '更新人',
  `latest_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据最后变更时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='商品发布表';

-- ----------------------------
-- Table structure for sell_goods_photos
-- ----------------------------
DROP TABLE IF EXISTS `sell_goods_photos`;
CREATE TABLE `sell_goods_photos` (
  `id` bigint(20) NOT NULL DEFAULT '0' COMMENT '主键ID',
  `goods_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '商品id',
  `photo_name` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '图片名称',
  `goods_photo` varchar(500) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '商品图片',
  `is_show` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否展示',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creater` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(50) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '更新人',
  `latest_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据最后变更时间',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='商品预览图表';

-- ----------------------------
-- Table structure for undo_log
-- ----------------------------
DROP TABLE IF EXISTS `undo_log`;
CREATE TABLE `undo_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `branch_id` bigint(20) NOT NULL,
  `xid` varchar(100) NOT NULL,
  `context` varchar(128) NOT NULL,
  `rollback_info` longblob NOT NULL,
  `log_status` int(11) NOT NULL,
  `log_created` datetime NOT NULL,
  `log_modified` datetime NOT NULL,
  `ext` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for uploadfile
-- ----------------------------
DROP TABLE IF EXISTS `uploadfile`;
CREATE TABLE `uploadfile` (
  `id` int(5) NOT NULL AUTO_INCREMENT,
  `userid` varchar(255) NOT NULL,
  `upName` varchar(255) NOT NULL,
  `path` varchar(255) NOT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) NOT NULL,
  `passWord` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `yes` int(11) DEFAULT '1',
  `brithday` date DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `picPath` varchar(255) DEFAULT NULL,
  `photoData` mediumblob,
  `isDelete` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userName` (`userName`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_1
-- ----------------------------
DROP TABLE IF EXISTS `user_1`;
CREATE TABLE `user_1` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) NOT NULL,
  `passWord` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `yes` int(11) DEFAULT '1',
  `brithday` date DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `picPath` varchar(255) DEFAULT NULL,
  `photoData` mediumblob,
  `isDelete` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=554015524007186433 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_2
-- ----------------------------
DROP TABLE IF EXISTS `user_2`;
CREATE TABLE `user_2` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) NOT NULL,
  `passWord` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `address` varchar(255) NOT NULL,
  `yes` int(11) DEFAULT '1',
  `brithday` date DEFAULT NULL,
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `picPath` varchar(255) DEFAULT NULL,
  `photoData` mediumblob,
  `isDelete` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=554015523893940226 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_note
-- ----------------------------
DROP TABLE IF EXISTS `user_note`;
CREATE TABLE `user_note` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `note_title` varchar(10) DEFAULT '' COMMENT '标题',
  `note_type` tinyint(1) DEFAULT '1' COMMENT '花销类型',
  `note_address_name` varchar(100) DEFAULT NULL,
  `note_address` varchar(50) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '地址',
  `note_name` varchar(50) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '用户名字',
  `note_remark` varchar(255) DEFAULT '' COMMENT '备注',
  `note_money` decimal(10,2) DEFAULT '0.00' COMMENT '金额',
  `creater_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creater` varchar(100) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '创建者',
  `modifier_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `modifier` varchar(100) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '修改者',
  `record_version` tinyint(1) NOT NULL DEFAULT '0' COMMENT '版本记录',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `img_code` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Procedure structure for in_param
-- ----------------------------
DROP PROCEDURE IF EXISTS `in_param`;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `in_param`(in x int)
begin
select x;
set x=2;
select x;
end
;;
DELIMITER ;
