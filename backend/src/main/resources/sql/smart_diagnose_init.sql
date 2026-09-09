-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: smart_diagnose
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ai_chat`
--

DROP TABLE IF EXISTS `ai_chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_chat` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `question` text NOT NULL,
  `answer` text,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='独立AI助手聊天记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_chat`
--

LOCK TABLES `ai_chat` WRITE;
/*!40000 ALTER TABLE `ai_chat` DISABLE KEYS */;
/*!40000 ALTER TABLE `ai_chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consult`
--

DROP TABLE IF EXISTS `consult`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consult` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问诊id',
  `user_id` bigint NOT NULL COMMENT '发起问诊的患者用户id',
  `doctor_id` bigint DEFAULT NULL COMMENT '一对一指定医生id，大厅发布为null',
  `title` varchar(100) DEFAULT NULL COMMENT '问诊标题',
  `symptom` text NOT NULL COMMENT '详细症状描述',
  `department_id` bigint DEFAULT NULL COMMENT '选择的科室id',
  `past_medical` text COMMENT '既往病史',
  `allergy_history` text COMMENT '过敏史',
  `img_url` varchar(500) DEFAULT NULL COMMENT '多张图片地址，逗号分隔',
  `is_anonymous` tinyint DEFAULT '0' COMMENT '0不匿名，1匿名',
  `publish_type` tinyint NOT NULL COMMENT '0一对一发给医生，1发布问诊大厅',
  `is_public` tinyint DEFAULT '0' COMMENT '大厅模式：1全部可见，0仅医患可见',
  `ai_suggest` text COMMENT 'AI分析建议（后期对接大模型填充）',
  `status` tinyint DEFAULT '0' COMMENT '0待回复，1已回复，2已关闭',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `operation_history` text COMMENT '手术史',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问诊主表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consult`
--

LOCK TABLES `consult` WRITE;
/*!40000 ALTER TABLE `consult` DISABLE KEYS */;
/*!40000 ALTER TABLE `consult` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `consult_reply`
--

DROP TABLE IF EXISTS `consult_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `consult_reply` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `consult_id` bigint NOT NULL COMMENT '所属问诊单id',
  `sender_id` bigint NOT NULL COMMENT '发送人id',
  `sender_type` tinyint NOT NULL COMMENT '1患者，2医生',
  `content` text NOT NULL COMMENT '回复文字内容',
  `reply_scope` tinyint NOT NULL COMMENT '1全部可见，2仅问诊发起者可见',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `is_read` int DEFAULT '0' COMMENT '0未读，1已读',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='问诊对话追评表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `consult_reply`
--

LOCK TABLES `consult_reply` WRITE;
/*!40000 ALTER TABLE `consult_reply` DISABLE KEYS */;
/*!40000 ALTER TABLE `consult_reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `department`
--

DROP TABLE IF EXISTS `department`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `department` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '科室名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `department`
--

LOCK TABLES `department` WRITE;
/*!40000 ALTER TABLE `department` DISABLE KEYS */;
INSERT INTO `department` VALUES (1,'内科'),(2,'心血管内科'),(3,'消化内科'),(4,'呼吸内科'),(5,'神经内科'),(6,'肾内科'),(7,'内分泌科'),(8,'风湿免疫科'),(9,'外科'),(10,'肝胆外科'),(11,'胃肠外科'),(12,'胸外科'),(13,'泌尿外科'),(14,'神经外科'),(15,'整形外科'),(16,'骨科'),(17,'皮肤科'),(18,'儿科'),(19,'妇科'),(20,'产科'),(21,'眼科'),(22,'耳鼻喉科'),(23,'口腔科'),(24,'急诊科'),(25,'重症医学科'),(26,'康复医学科'),(27,'中医科'),(28,'精神心理科');
/*!40000 ALTER TABLE `department` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_attachment`
--

DROP TABLE IF EXISTS `doctor_attachment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_attachment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件主键',
  `doctor_id` bigint NOT NULL COMMENT '关联医生资质id',
  `file_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_url` varchar(512) NOT NULL COMMENT '文件访问地址',
  `file_size` bigint DEFAULT NULL COMMENT '文件大小字节',
  `file_type` varchar(100) DEFAULT NULL COMMENT '文件MIME类型',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='医生资质附件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_attachment`
--

LOCK TABLES `doctor_attachment` WRITE;
/*!40000 ALTER TABLE `doctor_attachment` DISABLE KEYS */;
/*!40000 ALTER TABLE `doctor_attachment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor_info`
--

DROP TABLE IF EXISTS `doctor_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `doctor_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `department` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `hospital` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `audit_status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'wait',
  `intro` text COLLATE utf8mb4_unicode_ci COMMENT '个人简介',
  `audit_msg` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审核驳回理由',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `cert_file` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '医生资质附件OSS地址，多个文件逗号隔开',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_doctor_uid` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor_info`
--

LOCK TABLES `doctor_info` WRITE;
/*!40000 ALTER TABLE `doctor_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `doctor_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient_info`
--

DROP TABLE IF EXISTS `patient_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `patient_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `age` int DEFAULT NULL,
  `gender` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `allergy` text COLLATE utf8mb4_unicode_ci,
  `disease_history` text COLLATE utf8mb4_unicode_ci,
  `operation_history` text COLLATE utf8mb4_unicode_ci,
  `nickname` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_patient_userid` (`user_id`),
  UNIQUE KEY `uk_patient_uid` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient_info`
--

LOCK TABLES `patient_info` WRITE;
/*!40000 ALTER TABLE `patient_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `patient_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `symptom_dict`
--

DROP TABLE IF EXISTS `symptom_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `symptom_dict` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '快捷症状名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `symptom_dict`
--

LOCK TABLES `symptom_dict` WRITE;
/*!40000 ALTER TABLE `symptom_dict` DISABLE KEYS */;
INSERT INTO `symptom_dict` VALUES (1,'发烧'),(2,'咳嗽'),(3,'头痛'),(4,'腹痛'),(5,'皮肤瘙痒'),(6,'关节疼痛');
/*!40000 ALTER TABLE `symptom_dict` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `role` varchar(20) NOT NULL COMMENT '角色：patient患者 / doctor医生 / admin管理员',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `status` int DEFAULT '0' COMMENT '账号状态 0=启用 1=禁用',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像OSS链接',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (3,'zyx','10051005','admin','2026-09-03 01:31:57',0,NULL);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-09 14:34:51
