-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: localhost    Database: arinternship
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
-- Table structure for table `login_logs`
--

DROP TABLE IF EXISTS `login_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_timestamp` (`timestamp`),
  CONSTRAINT `login_logs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_logs`
--

LOCK TABLES `login_logs` WRITE;
/*!40000 ALTER TABLE `login_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `login_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spring_session`
--

DROP TABLE IF EXISTS `spring_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spring_session` (
  `PRIMARY_ID` char(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `SESSION_ID` char(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `CREATION_TIME` bigint NOT NULL,
  `LAST_ACCESS_TIME` bigint NOT NULL,
  `MAX_INACTIVE_INTERVAL` int NOT NULL,
  `EXPIRY_TIME` bigint NOT NULL,
  `PRINCIPAL_NAME` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`PRIMARY_ID`),
  UNIQUE KEY `SPRING_SESSION_IX1` (`SESSION_ID`),
  KEY `SPRING_SESSION_IX2` (`EXPIRY_TIME`),
  KEY `SPRING_SESSION_IX3` (`PRINCIPAL_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spring_session`
--

LOCK TABLES `spring_session` WRITE;
/*!40000 ALTER TABLE `spring_session` DISABLE KEYS */;
/*!40000 ALTER TABLE `spring_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spring_session_attributes`
--

DROP TABLE IF EXISTS `spring_session_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spring_session_attributes` (
  `SESSION_PRIMARY_ID` char(36) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ATTRIBUTE_NAME` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ATTRIBUTE_BYTES` blob NOT NULL,
  PRIMARY KEY (`SESSION_PRIMARY_ID`,`ATTRIBUTE_NAME`),
  CONSTRAINT `SPRING_SESSION_ATTRIBUTES_FK` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `spring_session` (`PRIMARY_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spring_session_attributes`
--

LOCK TABLES `spring_session_attributes` WRITE;
/*!40000 ALTER TABLE `spring_session_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `spring_session_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `fullname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `university` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `faculty` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `major` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_number` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `intern_department` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `intern_duration` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `attached_project` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `grade` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_by` bigint DEFAULT NULL,
  `profile_file` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `project_file` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `project_file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ชื่อไฟล์',
  `project_file_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'ประเภทไฟล์ (MIME type เช่น application/pdf)',
  `project_file_data` longblob COMMENT 'เก็บข้อมูลไฟล์จริงแบบ Binary',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students` DISABLE KEYS */;
INSERT INTO `students` VALUES (1,'ปภาวิน','มหาวิทยาลัยเทคโนโลยีราชมงคลธัญบุรี','บริหารธุรกิจ','สารสนเทศ','082-948-5411','frame2346@gmail.com','AR Soft','07/07/2568 - 31/10/2568','reportNetcrud_IS.doc','A',3,'profile/e3093a4e-82cd-4d5f-b32c-ca7a55889e26.jpeg','project/7c93a7aa-a410-4500-97a6-99dfa6701f62.doc','2025-10-29 14:22:07','2025-10-31 09:09:26',NULL,NULL,NULL),(2,'ภาวิตา ประหยัดศรี','มหาวิทยาลัยศรีนครินทรวิโรฒ','บริหารธุรกิจบัณฑิต','ท่องเที่ยวและการโรงแรม','092-541-3697','phawita.p@gmail.com','AR DI','01/05/2568 - 31/09/2568','3d4c234b-14f3-41c8-86da-a50a84f28217.pdf','B+',1,'profile/4411ffc0-3ab0-48d2-b34d-953c2d94282f.jpg','project/3f45646c-c85d-43fd-a261-66674870d3d7.pdf','2025-10-29 14:29:34','2025-10-29 19:52:33',NULL,NULL,NULL),(3,'นัชชา โหตรวภานนท์','มหาวิทยาลัยศรีนครินทรวิโรฒ','คณะวิทยาศาสตร์','วิทยาการคอมพิวเตอร์','094-126-5874','nutcha.h@gmail.com','UX/UI','01/02/2568 - 31/06/2568','3d4c234b-14f3-41c8-86da-a50a84f28217.pdf','D+',1,'profile/6c5cb100-2bf5-429c-a8ec-34841402691f.jpg','project/78b4d445-b616-4e47-8fbf-961878143c43.pdf','2025-10-29 14:36:16','2025-10-29 19:54:43',NULL,NULL,NULL),(4,'วีรวุฒิ พูลทรัยพ์','มหาวิทยาลัยเทคโนโลยีราชมงคลธัญบุรี','วิศวะกรรมศาสตร์','คอมพิวเตอร์','095-458-8123','weerawut.p@gmail.com','Tester','07/07/2568 - 31/10/2568','476e0777-1be2-4843-8509-c13d70f2394f.doc','D',1,'profile/898a2bf8-d97e-4770-a84e-cf192139e182.jpg','project/d08f8ccf-897f-4265-8f08-e4c0f53ac55c.doc','2025-10-29 14:40:00','2025-10-29 19:54:14',NULL,NULL,NULL),(5,'ธนากรณ์ คมขำ','มหาวิทยาลัยเทคโนโลยีราชมงคลธัญบุรี','บริหารธุริจ','สารสนเทศ','093-746-0033','thanakornkhommkham@gmail.com','AR Soft','07/07/2025 - 31/10/2025','reportNetcrud_IS.doc','A',2,'profile/03506d92-fb1e-448a-b8e4-216a6d4a48a7.jpg','project/0365989e-18d6-496e-88a5-c049ad17127a.doc','2025-10-29 15:13:03','2025-10-29 19:50:41',NULL,NULL,NULL),(6,'นภัทร รุ่งโรจน์','มหาวิทยาลัยเทคโนโลยีราชมงคลธัญบุรี','บริหารธุรกิจ','สารสนเทศ','098-539-7515','napatrungroj@gmail.com','AR Soft','07/07/2025 - 31/10/2025','grok_report.pdf','A',2,'profile/cc6fbba4-6bcd-46da-91bd-35f56f65b4d1.jpg','project/dedc9265-cd81-4fc4-b161-69adfdd5bfb1.pdf','2025-10-29 15:17:39','2025-10-29 19:50:48',NULL,NULL,NULL),(7,'วรเมธ อยู่นาค','มหาวิทยาลัยเทคโนโลยีราชมงคลธัญบุรี','บริการธุรกิจ','สารสนเทศ','096-656-8440','woramate.yn@gmail.com','AR Soft','07/07/2025 - 31/10/2025','reportCoop_IS.doc','A',2,'profile/d77e4ceb-befb-4945-8ea0-1e22f2ee51bd.jpg','project/57ea38d9-e6c3-4294-b0bd-7bb806a02b55.doc','2025-10-29 15:20:11','2025-10-30 02:55:53',NULL,NULL,NULL),(8,'ธันวา ศรีบัวทอง','มหาวิทยาลัยเทคโนโลยีพระจอมเกล้าธนบุรี','วิศวกรรมศาสตร์','วิศวกรรมซอฟต์แวร์','091-234-5678','thanwa.srib@gmail.com','Human Resources','01/01/2025 - 31/03/2025','grok_report.pdf',NULL,1,'profile/d1288664-97ee-4103-acd6-2480a1ab689f.jpg','project/f695c0c4-71b4-4995-8501-79a16b502b55.pdf','2025-10-29 19:13:11','2025-10-30 02:11:45',NULL,NULL,NULL),(9,'อาริษา ทองนาค','มหาวิทยาลัยเชียงใหม่','วิทยาศาสตร์','วิทยาการคอมพิวเตอร์','089-555-1244','arisa.tn@gmail.com','Accounting','01/02/2568 - 31/06/2568','ใบเสร็จ1_1.pdf',NULL,1,'profile/0be42057-9db7-490d-bae9-9d55c883afb6.jpg','project/f6abde0a-acd2-46b2-bb08-ef6a635d6f3f.pdf','2025-10-29 19:15:38','2025-10-30 10:13:01',NULL,NULL,NULL),(10,'ปัณณธร วงศ์วัฒน์','มหาวิทยาลัยศิลปากร','เทคโนโลยีสารสนเทศ','เทคโนโลยีสารสนเทศ','092-441-9088','pannathorn.ww@gmail.com','Accounting','01/02/2568 - 31/06/2568','1ed3c535-c2aa-4cc1-8a3f-a579aecc3f75.doc',NULL,1,'profile/467495df-ca96-47d8-a585-a8bcf7606a3c.jpg','project/f062451f-516c-43a6-bfd0-6e7bc0e37b96.doc','2025-10-29 19:21:19','2025-10-30 10:13:01',NULL,NULL,NULL),(11,'พิชญ์นันท์ ภูวเดช','มหาวิทยาลัยเกษตรศาสตร์','วิศวกรรมศาสตร์','วิศวกรรมคอมพิวเตอร์','083-882-3451','pitchanan.pv@gmail.com','Human Resources','01/05/2568 - 31/09/2568','ใบเสร็จ2_2.pdf',NULL,1,'profile/626d424c-7db8-49d1-8c48-bb2ad55db2bc.jpg','project/0c533086-62c0-43ed-a1b1-476cf5d94097.pdf','2025-10-29 19:23:00','2025-10-30 10:13:01',NULL,NULL,NULL),(12,'กัญญารัตน์ แสงจันทร์','มหาวิทยาลัยมหิดล','วิทยาศาสตร์','วิทยาการข้อมูล','089-327-4490','kanyarut.sc@gmail.com','AR DI','01/05/2568 - 31/09/2568','ใบเสร็จ1_2.pdf','B+',1,'profile/453d966c-2386-4777-b20f-2557a6fae7f6.jpg','project/18e75ac2-3d1b-4bb2-894d-c271fc15f631.pdf','2025-10-29 19:25:06','2025-10-30 10:13:01',NULL,NULL,NULL),(13,'ธนวัฒน์ พูลทรัพย์','มหาวิทยาลัยเทคโนโลยีมหานคร','วิศวกรรมศาสตร์','วิศวกรรมอุตสาหการ','098-114-5520','thanawat.ps@gmail.com','IT Support','01/01/2025 - 31/03/2025','ใบเสร็จ2_1.pdf','B',1,'profile/660d1954-e193-49a8-b88e-939c8898acd9.jpg','project/bfc7eb93-a426-485c-8864-6e1083e05b0c.pdf','2025-10-29 19:26:55','2025-10-30 06:49:09',NULL,NULL,NULL),(14,'ภูริชญา รัตนานนท์','มหาวิทยาลัยธรรมศาสตร์','พาณิชยศาสตร์และการบัญชี','สถิติประยุกต์','081-225-9011','phurichaya.rn@gmail.com','AR DI','01/01/2025 - 31/03/2025','b617ce00-d097-476a-9140-ddfaa0d6bbd6.pdf','B',1,'profile/0e0a5cf3-63d7-4f57-8d35-0b1b9204fe90.jpg','project/08122e5f-7fc2-449c-b964-826968434774.pdf','2025-10-29 19:30:01','2025-10-29 19:53:10',NULL,NULL,NULL),(15,'ชนาธิป ศุภกิจ','มหาวิทยาลัยขอนแก่น','วิทยาศาสตร์','วิทยาการข้อมูล','083-551-7780','chanathip.sk@gmail.com','AR DI','01/01/2025 - 31/03/2025','grok_report.pdf','C+',1,'profile/556f72cf-6815-4161-8a50-7283f06ca408.jpg','project/1a49e6e5-8da4-4f8b-aace-7fca09115800.pdf','2025-10-29 19:31:58','2025-10-29 19:53:24',NULL,NULL,NULL),(16,'พิชญา อินทรสวัสดิ์','มหาวิทยาลัยราชภัฏสวนสุนันทา','เทคโนโลยีอุตสาหกรรม','เทคโนโลยีสารสนเทศ','095-442-3361','pitchaya.is@gmail.com','Tester','01/05/2568 - 31/09/2568','reportCoop_IS.doc','D',1,'profile/1d75be18-8eb5-4bd5-a41a-92b68a519ad6.jpg','project/0d9742ff-f626-439f-a8db-c31c838c5b6e.doc','2025-10-29 19:35:13','2025-10-30 10:12:26',NULL,NULL,NULL),(17,'ณัฐชา พงษ์พิพัฒน์','มหาวิทยาลัยเทคโนโลยีสุรนารี','วิทยาศาสตร์','เทคโนโลยีสารสนเทศ','082-454-6699','nutcha.pp@gmail.com','Tester','01/05/2568 - 31/09/2568','ใบเสร็จ4_1.pdf','F',1,'profile/1f34ae14-960d-41fb-a709-0c50403e1b2f.jpg','project/2905fcb3-b75b-4add-8621-07241fa5db97.pdf','2025-10-29 19:37:02','2025-10-30 10:12:26',NULL,NULL,NULL),(18,'รวิกานต์ ชัยเดช','มหาวิทยาลัยกรุงเทพ','สถาปัตยกรรมศาสตร์','การออกแบบนิเทศศิลป์','081-770-3345','rawikan.cd@gmail.com','UX/UI','01/02/2568 - 31/06/2568','ใบเสร็จ3_1.pdf','D+',1,'profile/09cfa7b8-94b3-4348-bd48-748304313333.jpg','project/a8f0df0b-1e45-4e68-95f6-94d235802b86.pdf','2025-10-29 19:38:35','2025-10-30 10:12:26',NULL,NULL,NULL),(19,'ปัณณวิชญ์ แสงทวี','มหาวิทยาลัยรังสิต','ศิลปกรรมศาสตร์','การออกแบบดิจิทัล','083-909-2268','pannawit.st@gmail.com','UX/UI','01/02/2568 - 31/06/2568','ใบเสร็จ3_2.pdf','D',1,'profile/e515c4ad-fd51-4bc1-aa6e-2d74931a60f2.jpg','project/1bbe9456-c880-448a-a700-b7c6f7bb7b8d.pdf','2025-10-29 19:41:00','2025-10-30 10:12:26',NULL,NULL,NULL),(20,'ชนิกานต์ ศรีรัตน์','มหาวิทยาลัยหอการค้าไทย','พาณิชยศาสตร์และการบัญชี','การบัญชี','081-334-4419','peerawit.tr@gmail.com','Accounting','01/02/2568 - 31/06/2568','d84745cc-0093-4765-aa02-86855105b71f.doc','C',1,'profile/7d0098e4-8bac-479f-a318-3a47ab49d811.jpg','project/9144b4de-b3a4-4141-8c34-bbd281732c18.doc','2025-10-29 19:42:47','2025-10-30 10:12:26',NULL,NULL,NULL),(21,'พีรวิชญ์ ธรรมรัตน์','มหาวิทยาลัยธรรมศาสตร์','พาณิชยศาสตร์และการบัญชี','การเงิน','082-772-5501','peerawit.tr@gmail.com','Accounting','01/05/2568 - 31/09/2568','b617ce00-d097-476a-9140-ddfaa0d6bbd6.pdf','C',1,'profile/a42a8fa4-bfaf-4cfe-bb74-616eb9a1dca1.jpg','project/a0bd1da7-cadb-4621-bc54-d28fb47d71ea.pdf','2025-10-29 19:44:24','2025-10-30 10:12:26',NULL,NULL,NULL),(22,'กฤติเดช วงศ์แก้ว','มหาวิทยาลัยราชภัฏเชียงใหม่','มนุษยศาสตร์และสังคมศาสตร์','การบริหารทรัพยากรมนุษย์','083-551-9974','kritidet.wk@gmail.com','Human Resources','01/05/2568 - 31/09/2568','ef9d3bb3-c6b0-4b7b-be8e-d9fda4fa534f.doc','A',1,'profile/7b57f7c8-5647-4c57-90d4-de156374bc5f.jpg','project/4d1b1da7-820b-4162-95e6-6deaf6456758.doc','2025-10-29 19:45:49','2025-10-30 10:12:26',NULL,NULL,NULL),(23,'ธันวิน พรหมพิทักษ์','มหาวิทยาลัยศรีปทุม','บริหารธุรกิจ','การจัดการ','086-448-5522','thanawin.pp@gmail.com','Human Resources','01/01/2025 - 31/03/2025','1ed3c535-c2aa-4cc1-8a3f-a579aecc3f75.doc','B+',1,'profile/97943163-9fcb-4d31-ba51-cef6bc214e2f.jpeg','project/8b9a099b-a26f-43ab-8384-34d727ebb31b.doc','2025-10-29 19:47:15','2025-10-29 19:55:17',NULL,NULL,NULL);
/*!40000 ALTER TABLE `students` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_activities`
--

DROP TABLE IF EXISTS `user_activities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_activities` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `activity_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `activity_detail` text COLLATE utf8mb4_unicode_ci,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` text COLLATE utf8mb4_unicode_ci,
  `timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_activity_type` (`activity_type`),
  KEY `idx_timestamp` (`timestamp`),
  CONSTRAINT `user_activities_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_activities`
--

LOCK TABLES `user_activities` WRITE;
/*!40000 ALTER TABLE `user_activities` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_activities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `fullname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` datetime(6) DEFAULT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','admin','admin@gmail.com','$2a$10$Qxr7.sEfc3YCv/RV6YgLyOjA1RLZg/B5xh0sSSo3MvxTq6gZTZfX2','ADMIN',1,'2025-10-29 14:00:04','2025-10-31 09:02:27','2025-10-31 16:02:27.451572',NULL,_binary ''),(2,'ธนากรณ์ คมขำ','tatathanakorn','tata@gmail.com','$2a$10$hZSZOMU5WOuGt/twaM9r8.w3Vn9aRx0OHnPmEAP3Iorw3vIPR3GQ.','USER',1,'2025-10-29 14:01:11','2025-10-31 08:50:13','2025-10-31 15:50:12.743907',NULL,_binary ''),(3,'ปภาวิน ธงสันเทียะ','papawint','frame2346@gmail.com','$2a$10$Udo5Sm.mAMN/VnkKdHuRd.SapC2cw0plWm9reEpn38EJrssSQY2Ly','USER',1,'2025-10-29 14:01:21','2025-10-31 09:00:47','2025-10-31 16:00:47.047155',NULL,_binary '');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'arinternship'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-31 16:16:41
