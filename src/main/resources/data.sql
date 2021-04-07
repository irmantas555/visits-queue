-- MySQL dump 10.13  Distrib 8.0.23, for Linux (x86_64)
--
-- Host: 192.168.1.67    Database: nfq
-- ------------------------------------------------------
-- Server version	5.5.47-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(40) NOT NULL,
  `first_name` varchar(40) NOT NULL DEFAULT '',
  `last_name` varchar(40) NOT NULL DEFAULT '',
  `password` varchar(60) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'alexandra.randall@irmantasm.lt','Alexandra','Randall','customer'),(2,'evan.mackenzie@irmantasm.lt','Evan','Mackenzie','customer'),(3,'zoe.metcalfe@irmantasm.lt','Zoe','Metcalfe','customer'),(4,'austin.glover@irmantasm.lt','Austin','Glover','customer'),(5,'steven.campbell@irmantasm.lt','Steven','Campbell','customer'),(6,'karen.watson@irmantasm.lt','Karen','Watson','customer'),(7,'lily.baker@irmantasm.lt','Lily','Baker','customer'),(8,'adrian.lewis@irmantasm.lt','Adrian','Lewis','customer'),(9,'sophie.knox@irmantasm.lt','Sophie','Knox','customer'),(10,'alexander.baker@irmantasm.lt','Alexander','Baker','customer'),(11,'alan.turner@irmantasm.lt','Alan','Turner','customer'),(12,'elizabeth.bond@irmantasm.lt','Elizabeth','Bond','customer'),(13,'robert.gray@irmantasm.lt','Robert','Gray','customer'),(14,'leonard.tucker@irmantasm.lt','Leonard','Tucker','customer'),(15,'phil.gibson@irmantasm.lt','Phil','Gibson','customer'),(16,'anna.slater@irmantasm.lt','Anna','Slater','customer'),(17,'dan.nolan@irmantasm.lt','Dan','Nolan','customer'),(18,'carol.skinner@irmantasm.lt','Carol','Skinner','customer'),(19,'rebecca.gill@irmantasm.lt','Rebecca','Gill','customer'),(20,'carol.peters@irmantasm.lt','Carol','Peters','customer'),(41,'irmantas.maciulis@gmail.com','Irmantas','Mačiulis','customer');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `specialists`
--

DROP TABLE IF EXISTS `specialists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `specialists` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(40) NOT NULL,
  `first_name` varchar(40) NOT NULL,
  `last_name` varchar(40) NOT NULL,
  `password` varchar(60) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `specialists`
--

LOCK TABLES `specialists` WRITE;
/*!40000 ALTER TABLE `specialists` DISABLE KEYS */;
INSERT INTO `specialists` VALUES (1,'neil.butler@irmantasm.lt','Neil','Butler','Neil123'),(2,'zoe.white@irmantasm.lt','Zoe','White','Zoe123'),(3,'james.short@irmantasm.lt','James','Short','James123'),(4,'warren.underwood@irmantasm.lt','Warren','Underwood','Warren123'),(5,'oliver.rampling@irmantasm.lt','Oliver','Rampling','Oliver123'),(6,'julian.powell@irmantasm.lt','Julian','Powell','Julian123'),(7,'christopher.hamilton@irmantasm.lt','Christopher','Hamilton','Christopher123'),(8,'lily.walker@irmantasm.lt','Lily','Walker','Lily123'),(9,'stephanie.metcalfe@irmantasm.lt','Stephanie','Metcalfe','Stephanie123'),(10,'lauren.sanderson@irmantasm.lt','Lauren','Sanderson','Lauren123'),(11,'adam.baker@irmantasm.lt','Adam','Baker','Adam123'),(12,'edward.newman@irmantasm.lt','Edward','Newman','Edward123'),(13,'caroline.parr@irmantasm.lt','Caroline','Parr','Caroline123'),(14,'nicola.arnold@irmantasm.lt','Nicola','Arnold','Nicola123'),(15,'caroline.reid@irmantasm.lt','Caroline','Reid','Caroline123'),(16,'sean.wallace@irmantasm.lt','Sean','Wallace','Sean123'),(17,'nicola.baker@irmantasm.lt','Nicola','Baker','Nicola123'),(18,'alexandra.springer@irmantasm.lt','Alexandra','Springer','Alexandra123'),(19,'virginia.hunter@irmantasm.lt','Virginia','Hunter','Virginia123'),(20,'jack.bailey@irmantasm.lt','Jack','Bailey','Jack123');
/*!40000 ALTER TABLE `specialists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `visits`
--

DROP TABLE IF EXISTS `visits`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `visits` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `specialist_id` bigint(20) NOT NULL,
  `customer_id` bigint(20) NOT NULL,
  `visit_time` bigint(13) NOT NULL,
  `visit_duration` int(11) NOT NULL,
  `serial` varchar(6) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `visit_time` (`visit_time`)
) ENGINE=InnoDB AUTO_INCREMENT=147 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `visits`
--

LOCK TABLES `visits` WRITE;
/*!40000 ALTER TABLE `visits` DISABLE KEYS */;
INSERT INTO `visits` VALUES (43,2,16,1617806568014,15,'0002'),(44,4,11,1617805668014,15,'0004'),(45,2,14,1617805668014,15,'0001'),(46,3,17,1617805668014,15,'0003'),(47,6,2,1617805668014,15,'0005'),(48,6,17,1617806568014,15,'0006'),(49,9,9,1617805668014,15,'0008'),(50,10,17,1617805668014,15,'0009'),(51,10,11,1617806568014,15,'0010'),(52,8,8,1617805668014,15,'0007'),(53,11,4,1617805668014,15,'0011'),(54,12,8,1617805668014,15,'0012'),(55,15,9,1617805668014,15,'0013'),(56,16,18,1617805668014,15,'0014'),(57,16,17,1617806568014,15,'0015'),(58,16,1,1617807468014,15,'0016'),(59,17,14,1617805668014,15,'0017'),(60,18,11,1617805668014,15,'0018'),(61,18,6,1617806568014,15,'0019'),(62,19,14,1617805668014,15,'0020'),(63,2,41,1617807468014,15,'0021'),(64,10,41,1617807468014,15,'0022'),(65,4,5,1617806803926,15,'0003'),(66,1,13,1617806803926,15,'0001'),(67,3,12,1617806803926,15,'0002'),(68,4,1,1617807703926,15,'0004'),(69,5,15,1617806803926,15,'0005'),(70,6,3,1617806803926,15,'0006'),(71,7,2,1617806803926,15,'0007'),(72,11,2,1617806803926,15,'0008'),(73,12,14,1617806803926,15,'0009'),(74,13,19,1617807703926,15,'0011'),(75,13,2,1617858900000,15,'0001'),(76,14,19,1617806803926,15,'0012'),(77,13,18,1617806803926,15,'0010'),(78,15,15,1617806803926,15,'0013'),(79,17,16,1617806803926,15,'0015'),(80,16,6,1617806803926,15,'0014'),(81,18,10,1617806803926,15,'0016'),(82,18,19,1617807703926,15,'0017'),(83,18,8,1617858900000,15,'0002'),(84,19,4,1617806803926,15,'0018'),(85,1,9,1617808136290,15,'0003'),(86,1,2,1617807236290,15,'0002'),(87,16,1,1617807236290,15,'0001'),(88,17,11,1617807236290,15,'0004'),(89,17,2,1617808136290,15,'0005'),(90,19,12,1617807236290,15,'0006'),(91,19,4,1617808136290,15,'0007'),(92,4,16,1617808136290,15,'0009'),(93,4,13,1617807236290,15,'0008'),(94,5,10,1617807236290,15,'0010'),(95,6,8,1617807236290,15,'0011'),(96,7,18,1617807236290,15,'0012'),(97,7,6,1617808136290,15,'0013'),(98,7,10,1617858900000,15,'0001'),(99,12,14,1617807236290,15,'0014'),(100,13,19,1617807236290,15,'0015'),(101,15,2,1617807236290,15,'0016'),(102,15,7,1617808136290,15,'0017'),(103,15,8,1617858900000,15,'0002'),(104,15,15,1617859800000,15,'0003'),(105,4,2,1617807726669,15,'0005'),(106,3,1,1617807726669,15,'0003'),(107,3,19,1617808626669,15,'0004'),(108,1,15,1617807726669,15,'0001'),(109,2,16,1617807726669,15,'0002'),(110,5,15,1617807726669,15,'0006'),(111,5,9,1617808626669,15,'0007'),(112,5,16,1617809526669,15,'0008'),(113,9,3,1617807726669,15,'0009'),(114,11,4,1617807726669,15,'0010'),(115,11,13,1617808626669,15,'0011'),(116,12,4,1617807726669,15,'0012'),(117,14,10,1617807726669,15,'0013'),(118,14,4,1617808626669,15,'0014'),(119,15,7,1617807726669,15,'0015'),(120,17,15,1617807726669,15,'0016'),(121,18,12,1617807726669,15,'0018'),(122,17,4,1617808626669,15,'0017'),(123,19,1,1617807726669,15,'0020'),(124,18,15,1617808626669,15,'0019'),(125,4,7,1617808510794,15,'0002'),(126,2,2,1617808510794,15,'0001'),(127,8,1,1617808510794,15,'0006'),(128,11,11,1617809410794,15,'0010'),(129,14,2,1617809410794,15,'0014'),(130,19,12,1617808510794,15,'0018'),(131,7,10,1617808510794,15,'0005'),(132,11,4,1617808510794,15,'0009'),(133,14,17,1617808510794,15,'0013'),(134,18,10,1617808510794,15,'0017'),(135,5,13,1617808510794,15,'0003'),(136,9,1,1617808510794,15,'0007'),(137,12,15,1617808510794,15,'0011'),(138,6,5,1617808510794,15,'0004'),(139,9,11,1617809410794,15,'0008'),(140,13,15,1617808510794,15,'0012'),(141,15,13,1617808510794,15,'0015'),(142,19,9,1617809410794,15,'0019'),(143,16,2,1617808510794,15,'0016'),(144,19,19,1617810310794,15,'0020'),(145,5,41,1617809801189,15,'0021'),(146,5,41,1617810701189,15,'0022');
/*!40000 ALTER TABLE `visits` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-04-07 18:27:33
