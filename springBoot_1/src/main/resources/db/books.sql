/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50717
Source Host           : 127.0.0.1:3306
Source Database       : books

Target Server Type    : MYSQL
Target Server Version : 50717
File Encoding         : 65001

Date: 2019-12-28 09:36:49
*/

SET FOREIGN_KEY_CHECKS=0;

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
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES ('2', '四书五经', '古典', '50', '50');
INSERT INTO `book` VALUES ('3', '三言二拍', '明代', '50', '50');
INSERT INTO `book` VALUES ('4', '道德经', '李耳', '50', '50');
INSERT INTO `book` VALUES ('6', '梦溪笔谈', '沈括', '50', '50');
INSERT INTO `book` VALUES ('7', '菜根谭', '明', '50', '50');
INSERT INTO `book` VALUES ('8', '东京梦华录', '宋.孟元老', '50', '50');
INSERT INTO `book` VALUES ('9', '浮生若梦', '当代', '50', '50');
INSERT INTO `book` VALUES ('11', '鬼谷子', '战国', '50', '50');
INSERT INTO `book` VALUES ('12', '孙子兵法', '孙膑', '50', '50');
INSERT INTO `book` VALUES ('13', '夏至未至', '郭敬明', '20', '20');

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of borrow
-- ----------------------------

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of libraryborrow
-- ----------------------------

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
-- Records of listno
-- ----------------------------
INSERT INTO `listno` VALUES ('1', '已借阅列表', '/borrow/borrowlist.html', '2');
INSERT INTO `listno` VALUES ('2', '馆藏图书列表', '/book/booklist.html', '2');
INSERT INTO `listno` VALUES ('3', '历史借阅列表', '/libraryborrow/librarylist.html', '2');
INSERT INTO `listno` VALUES ('4', '用户信息管理', '/user/alluserlist.html', '3');
INSERT INTO `listno` VALUES ('5', '图书信息管理', '/book/allbooklist.html', '3');
INSERT INTO `listno` VALUES ('6', '用户借阅历史', '/libraryborrow/alllibrarylist.html', '3');
INSERT INTO `listno` VALUES ('7', '用户共享资源', '/upLoad/Filelist.html', '3');
INSERT INTO `listno` VALUES ('8', '我的共享资源', '/upLoad/Filelist.html', '2');

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
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES ('1', '普通会员', '2017-09-27 20:23:29');
INSERT INTO `role` VALUES ('2', 'vip会员', '2017-09-27 20:23:29');
INSERT INTO `role` VALUES ('3', '系统管理员', '2017-09-27 20:23:29');

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
-- Records of uploadfile
-- ----------------------------

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
  `createDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `picPath` varchar(255) DEFAULT NULL,
  `isDelete` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userName` (`userName`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin', 'root', '管理员', '北京', '3', '2017-10-05 21:55:58', null, '0');
INSERT INTO `user` VALUES ('2', '123', 'hha', '用户1', '上海', '1', '2019-12-08 11:40:01', null, '0');
INSERT INTO `user` VALUES ('3', '11', '11', '11', '地址1', '1', '2019-12-22 21:54:02', null, '0');
INSERT INTO `user` VALUES ('4', '22', '33', '33', '44', '1', '2019-12-22 21:54:25', null, '0');
INSERT INTO `user` VALUES ('9', '用户111111', 'root', '111', '地址1', '1', '2019-12-22 22:57:18', null, '0');
INSERT INTO `user` VALUES ('10', '用户2', '222', '1', '地址2', '1', '2019-12-22 22:57:40', null, '0');
INSERT INTO `user` VALUES ('11', '用户3', '333', '对对对', '地址3', '1', '2019-12-22 22:57:59', null, '1');
INSERT INTO `user` VALUES ('12', '用户6', '6', '666', '地址33', '1', '2019-12-22 22:58:12', null, '0');

-- ----------------------------
-- Table structure for user_note
-- ----------------------------
DROP TABLE IF EXISTS `user_note`;
CREATE TABLE `user_note` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `note_title` varchar(10) DEFAULT '' COMMENT '标题',
  `note_type` tinyint(1) DEFAULT '1' COMMENT '花销类型',
  `note_address` varchar(100) default '河南省' COMMENT '地址',
  `note_name` varchar(50) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '用户名字',
  `note_remark` varchar(255) DEFAULT '' COMMENT '备注',
  `note_money` decimal(10,2) DEFAULT '0.00' COMMENT '金额',
  `creater_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `creater` varchar(100) CHARACTER SET utf8mb4 NOT NULL DEFAULT '' COMMENT '创建者',
  `modifier_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `modifier` varchar(100) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '修改者',
  `record_version` tinyint(1) NOT NULL DEFAULT '0' COMMENT '版本记录',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1210508367135318019 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_note
-- ----------------------------
INSERT INTO `user_note` VALUES ('1', '测试标题', '4','上海', '贺鸿飞', '我旅行花了1.1元', '1.10', '2019-12-25 10:09:40', '贺鸿飞', '2019-12-27 18:17:30', 'system', '0', '0');
INSERT INTO `user_note` VALUES ('2', '标题2', '1', '河南省','测试组', '我买衣服花了99.9元', '99.90', '2019-12-26 12:25:35', '', '2019-12-27 18:17:27', 'system', '0', '0');