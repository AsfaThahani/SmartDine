-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: smartdine
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Current Database: `smartdine`
--

/*!40000 DROP DATABASE IF EXISTS `smartdine`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `smartdine` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `smartdine`;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `OrderItemID` int NOT NULL AUTO_INCREMENT,
  `OrderID` int NOT NULL,
  `ProductID` int NOT NULL,
  `Quantity` int NOT NULL,
  `UnitPrice` decimal(10,2) NOT NULL,
  PRIMARY KEY (`OrderItemID`),
  KEY `OrderID` (`OrderID`),
  KEY `ProductID` (`ProductID`),
  CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`OrderID`) REFERENCES `orders` (`OrderID`),
  CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`ProductID`) REFERENCES `products` (`ProductID`)
) ENGINE=InnoDB AUTO_INCREMENT=188 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,1,1,1,5.99),(2,1,2,1,5.99),(3,1,3,1,5.99),(4,1,6,1,5.99),(5,2,1,1,5.99),(6,2,2,1,5.99),(7,3,3,1,5.99),(8,3,6,1,5.99),(9,3,9,1,5.99),(10,4,6,1,5.99),(11,5,2,1,5.99),(12,5,3,1,5.99),(13,5,6,1,5.99),(14,6,2,1,5.99),(15,7,2,1,5.99),(16,7,3,1,5.99),(17,8,1,1,5.99),(18,8,2,1,5.99),(19,8,3,1,5.99),(20,8,6,1,5.99),(21,9,1,1,5.99),(22,10,2,1,5.99),(23,10,3,1,5.99),(24,10,6,1,5.99),(25,11,1,1,5.99),(26,12,5,1,5.99),(27,12,6,1,5.99),(28,13,1,1,5.99),(29,13,2,1,5.99),(30,13,3,1,5.99),(31,13,4,1,5.99),(32,13,5,1,5.99),(33,13,6,1,5.99),(34,14,1,1,5.99),(35,14,2,1,5.99),(36,14,3,1,5.99),(37,14,4,1,5.99),(38,14,5,1,5.99),(39,15,1,1,5.99),(40,15,2,1,5.99),(41,16,1,1,5.99),(42,16,2,1,5.99),(43,16,3,1,5.99),(44,17,1,1,5.99),(45,17,2,1,5.99),(46,17,3,1,5.99),(47,17,4,1,5.99),(48,17,5,1,2000.00),(49,17,6,1,2200.00),(50,18,2,1,5.99),(51,18,3,1,5.99),(52,18,4,1,5.99),(53,18,5,1,2000.00),(54,18,6,1,2200.00),(55,19,2,1,1900.00),(56,19,5,1,2000.00),(57,19,6,1,2200.00),(58,19,9,1,1700.00),(59,20,1,1,1700.00),(60,20,2,1,1900.00),(61,20,3,1,1500.00),(62,20,5,1,2000.00),(63,20,6,1,2200.00),(64,21,2,2,1900.00),(65,21,3,1,1500.00),(66,21,6,1,2200.00),(67,22,1,1,1700.00),(68,22,2,1,1900.00),(69,22,3,1,1500.00),(70,23,3,1,1500.00),(71,24,3,1,1500.00),(72,24,6,1,2200.00),(73,25,1,1,1700.00),(74,26,2,1,1900.00),(75,27,2,1,1900.00),(76,27,4,1,2900.00),(77,28,1,1,1700.00),(78,28,4,1,2900.00),(79,29,2,1,1900.00),(80,29,3,1,1500.00),(81,29,6,1,2200.00),(82,30,6,1,2200.00),(83,31,1,1,1700.00),(84,31,3,1,1500.00),(85,32,2,1,1900.00),(86,33,3,1,1500.00),(87,34,3,1,1500.00),(88,34,4,1,2900.00),(89,34,5,1,2000.00),(90,34,6,1,2200.00),(91,34,8,1,2300.00),(92,35,1,1,1700.00),(93,35,2,1,1900.00),(94,35,3,1,1500.00),(95,35,5,1,2000.00),(96,35,6,2,2200.00),(97,36,1,1,1700.00),(98,36,3,1,1500.00),(99,36,5,1,2000.00),(100,36,6,1,2200.00),(101,37,2,1,1900.00),(102,37,3,1,1500.00),(103,37,4,1,2900.00),(104,37,5,1,2000.00),(105,37,6,1,2200.00),(106,38,3,1,1500.00),(107,38,4,4,2900.00),(108,38,5,1,2000.00),(109,38,6,1,2200.00),(110,39,1,3,1700.00),(111,39,3,1,1500.00),(112,39,4,1,2900.00),(113,39,5,1,2000.00),(114,40,1,3,1700.00),(115,40,3,1,1500.00),(116,40,4,1,2900.00),(117,40,5,1,2000.00),(118,41,6,5,2200.00),(119,42,16,1,450.00),(120,42,2,1,1900.00),(121,42,3,1,1500.00),(122,42,26,1,1200.00),(123,42,28,1,1100.00),(124,42,29,1,1150.00),(125,42,13,1,500.00),(126,43,1,1,1600.00),(127,43,2,2,1900.00),(128,43,3,1,1500.00),(129,44,2,1,1900.00),(130,45,3,1,1500.00),(131,46,3,1,1500.00),(132,47,3,1,1500.00),(133,47,6,1,2200.00),(134,48,3,1,1500.00),(135,48,6,1,2200.00),(136,49,25,1,1100.00),(137,49,26,1,1200.00),(138,49,27,1,1150.00),(139,49,28,1,1100.00),(140,49,29,1,1150.00),(141,50,6,1,2200.00),(142,51,3,1,1500.00),(143,52,3,1,1500.00),(144,53,26,5,1200.00),(145,54,2,3,1900.00),(146,54,3,1,1500.00),(147,55,26,3,1200.00),(148,56,1,1,1600.00),(149,56,26,1,1200.00),(150,57,1,1,1600.00),(151,57,2,1,1900.00),(152,57,3,1,1500.00),(153,57,4,1,2800.00),(154,57,6,1,2200.00),(155,57,7,1,1900.00),(156,57,8,1,2300.00),(157,57,9,1,1700.00),(158,57,10,1,1700.00),(159,57,11,1,2000.00),(160,57,12,1,1200.00),(161,58,16,1,450.00),(162,58,1,4,1600.00),(163,58,25,1,1100.00),(164,59,25,1,1100.00),(165,60,1,1,1600.00),(166,60,3,1,1500.00),(167,61,2,1,1900.00),(168,61,3,1,1500.00),(169,61,4,1,2800.00),(170,61,5,1,2000.00),(171,61,6,1,2200.00),(172,62,16,1,450.00),(173,62,2,1,1900.00),(174,62,25,1,1100.00),(175,63,2,1,1900.00),(176,63,3,1,1500.00),(177,63,6,1,2200.00),(178,63,29,1,1150.00),(179,64,16,1,450.00),(180,64,20,1,850.00),(181,64,5,1,2000.00),(182,64,24,1,1000.00),(183,65,17,1,1000.00),(184,65,2,2,1900.00),(185,65,25,1,1100.00),(186,65,26,1,1200.00),(187,65,11,1,2000.00);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `OrderID` int NOT NULL AUTO_INCREMENT,
  `TotalAmount` decimal(10,2) NOT NULL,
  `PaymentType` varchar(10) NOT NULL,
  `OrderDate` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`OrderID`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,23.96,'CASH','2025-12-20 16:01:18'),(2,11.98,'CASH','2025-12-20 16:01:18'),(3,17.97,'CASH','2025-12-20 16:01:18'),(4,5.99,'CASH','2025-12-20 16:01:18'),(5,17.97,'CASH','2025-12-20 16:01:18'),(6,5.99,'CASH','2025-12-20 16:01:18'),(7,11.98,'CASH','2025-12-20 16:01:18'),(8,23.96,'CASH','2025-12-20 16:01:18'),(9,5.99,'CASH','2025-12-20 16:01:18'),(10,17.97,'CASH','2025-12-20 16:01:18'),(11,5.99,'CASH','2025-12-20 16:01:18'),(12,11.98,'CASH','2025-12-20 16:01:18'),(13,35.94,'CASH','2025-12-20 16:01:18'),(14,29.95,'CASH','2025-12-20 16:01:18'),(15,11.98,'CASH','2025-12-20 16:01:18'),(16,17.97,'CASH','2025-12-20 16:01:18'),(17,4223.96,'CASH','2025-12-20 16:01:18'),(18,4217.97,'CASH','2025-12-20 16:01:18'),(19,7800.00,'CASH','2025-12-20 16:01:18'),(20,9300.00,'CARD','2025-12-20 16:01:18'),(21,7500.00,'CASH','2025-12-20 16:01:18'),(22,5100.00,'CASH','2025-12-20 16:01:18'),(23,1500.00,'CARD','2025-12-20 16:01:18'),(24,3700.00,'CARD','2025-12-20 16:01:18'),(25,1700.00,'CARD','2025-12-20 16:01:18'),(26,1900.00,'CASH','2025-12-20 16:01:18'),(27,4800.00,'CASH','2025-12-20 16:01:18'),(28,4600.00,'CARD','2025-12-20 16:01:18'),(29,5600.00,'CASH','2025-12-20 16:01:18'),(30,2200.00,'CARD','2025-12-20 16:01:18'),(31,3200.00,'CARD','2025-12-20 16:01:18'),(32,1900.00,'CASH','2025-12-20 16:01:18'),(33,1680.00,'CASH','2025-12-20 16:01:18'),(34,12208.00,'CASH','2025-12-20 16:01:18'),(35,12880.00,'CARD','2025-12-20 16:01:18'),(36,8288.00,'CARD','2025-12-20 16:01:18'),(37,11760.00,'CARD','2025-12-20 16:01:18'),(38,19376.00,'CASH','2025-12-20 16:01:18'),(39,12880.00,'CARD','2025-12-20 16:01:18'),(40,12880.00,'CASH','2025-12-20 16:01:18'),(41,12320.00,'CARD','2025-12-20 16:01:18'),(42,8736.00,'CARD','2025-12-21 08:36:45'),(43,7728.00,'CASH','2025-12-21 19:31:02'),(44,2128.00,'CASH','2025-12-21 19:31:39'),(45,1680.00,'CASH','2025-12-21 19:31:55'),(46,1680.00,'CARD','2025-12-21 19:34:05'),(47,4144.00,'CARD','2025-12-21 19:38:45'),(48,4144.00,'CASH','2025-12-21 19:42:00'),(49,6384.00,'CASH','2025-12-21 19:44:03'),(50,2464.00,'CASH','2025-12-21 19:46:53'),(51,1680.00,'CASH','2025-12-21 19:49:01'),(52,1680.00,'CASH','2025-12-21 19:52:19'),(53,6720.00,'CARD','2025-12-21 20:14:03'),(54,8064.00,'CASH','2025-12-21 22:14:59'),(55,4032.00,'CASH','2025-12-22 01:50:59'),(56,3136.00,'CASH','2025-12-22 03:23:48'),(57,23296.00,'CASH','2025-12-22 03:28:35'),(58,8904.00,'CARD','2025-12-22 03:57:28'),(59,1232.00,'CARD','2025-12-22 03:57:57'),(60,3472.00,'CASH','2025-12-29 05:04:09'),(61,11648.00,'CARD','2025-12-30 09:50:26'),(62,3864.00,'CASH','2026-01-01 04:07:50'),(63,7560.00,'CARD','2026-01-04 11:04:53'),(64,4816.00,'CARD','2026-10-16 19:10:54'),(65,10192.00,'CARD','2026-10-16 19:13:08');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `ProductID` int NOT NULL AUTO_INCREMENT,
  `ProductName` varchar(100) NOT NULL,
  `Category` varchar(50) DEFAULT NULL,
  `Availability_Status` varchar(20) DEFAULT 'Available',
  `UnitPrice` decimal(10,2) NOT NULL,
  `ImagePath` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ProductID`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'Classic Cheese Burger','Burgers','Available',1600.00,'resources/ClassicCheeseBurger.png'),(2,'Double Cheese Burger','Burgers','Available',1900.00,'resources/DoubleCheeseBurger.png'),(3,'Chicken Burger','Burgers','Sold Out',1500.00,'resources/ChickenBurger.png'),(4,'Tower Burger','Burgers','Available',2800.00,'resources/TowerBurger.png'),(5,'Bacon Burger','Burgers','Available',2000.00,'resources/BaconBurger.png'),(6,'Prawn Burger','Burgers','Available',2200.00,'resources/PrawnBurger.png'),(7,'Black Burger','Burgers','Available',1900.00,'resources/BlackBurger.png'),(8,'Turkey Burger','Burgers','Available',2300.00,'resources/TurkeyBurger.png'),(9,'Mushroom Burger','Burgers','Available',1700.00,'resources/MushroomBurger.png'),(10,'Fish Burger','Burgers','Available',1700.00,'resources/FishBurger.png'),(11,'Chicken Teriyaki Burger','Burgers','Available',2000.00,'resources/ChickenTeriyakiBurger.png'),(12,'Veggie Burger','Burgers','Available',1200.00,'resources/VegBurger.png'),(13,'Chocolate Cake','Desserts','Available',500.00,'resources/desserts/chocCake.png'),(16,'Fudge Brownie','Desserts','Available',450.00,'resources/desserts/brownie.png'),(17,'Lemon Cheesecake','Desserts','Available',1000.00,'resources/desserts/lemCheeseCake.png'),(18,'Strawberry Cheesecake','Desserts','Available',1100.00,'resources/desserts/strawberryCheeseCake.png'),(19,'Crepe','Desserts','Available',800.00,'resources/desserts/crepe.png'),(20,'Strawberry Pancake','Desserts','Available',850.00,'resources/desserts/pancake.png'),(21,'Apple Pie','Desserts','Available',1000.00,'resources/desserts/applepie.png'),(22,'Blueberry Cheesecake','Desserts','Available',1500.00,'resources/desserts/blueberrycheesecake.png'),(23,'Chocolate Brownie Sundae','Desserts','Available',1200.00,'resources/desserts/chocsundae.png'),(24,'Strawberry Milkshake','Drinks','Available',1000.00,'resources/drinks/strawberryshake.png'),(25,'Chocolate Milkshake','Drinks','Available',1100.00,'resources/drinks/chocolateshake.png'),(26,'Blueberry Milkshake','Drinks','Available',1200.00,'resources/drinks/blueberryshake.png'),(27,'Strawberry boba','Drinks','Available',1150.00,'resources/drinks/strawberryboba.png'),(28,'Milktea boba','Drinks','Available',1100.00,'resources/drinks/milkteaboba.png'),(29,'Blueberry boba','Drinks','Available',1150.00,'resources/drinks/blueberryboba.png'),(30,'Matcha boba','Drinks','Available',1000.00,'resources/drinks/matchaboba.png'),(31,'Fruit Mojito','Drinks','Available',900.00,'resources/drinks/fruitmojito.png'),(32,'Orange Juice','Drinks','Available',650.00,'resources/drinks/orangejuice.png'),(39,'Classic Cheese Burger','Desserts','Available',1600.00,'C:\\Users\\Umer Farook\\Desktop\\resources\\ChickenBurger.png'),(42,'Vegan Burger','Burgers','Available',980.00,'C:\\Users\\Umer Farook\\Pictures\\Vegan Burger.png');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `shop_details`
--

DROP TABLE IF EXISTS `shop_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop_details` (
  `ShopID` int NOT NULL,
  `ShopName` varchar(100) DEFAULT NULL,
  `Address` varchar(255) DEFAULT NULL,
  `Phone` varchar(20) DEFAULT NULL,
  `FooterMessage` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ShopID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `shop_details`
--

LOCK TABLES `shop_details` WRITE;
/*!40000 ALTER TABLE `shop_details` DISABLE KEYS */;
INSERT INTO `shop_details` VALUES (1,'STACK\'D','123 Grand Street','011-2345678','Thank you! Come Again.');
/*!40000 ALTER TABLE `shop_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `UserID` int NOT NULL AUTO_INCREMENT,
  `Username` varchar(50) NOT NULL,
  `FullName` varchar(100) NOT NULL,
  `PasswordHash` varchar(255) NOT NULL,
  `Role` varchar(20) NOT NULL DEFAULT 'Cashier ',
  PRIMARY KEY (`UserID`),
  UNIQUE KEY `username` (`Username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin1','Thahani','$2a$10$HmOy3LwgCm40ekqWXLoPPufaUe3ivSqX7Y7f2gy/x/b522OvSs3/u','Admin'),(4,'cashier1','Asfa','$2a$12$k6m1vylUkrg9/4w6ATWWLO78eQpM7zE2LbAQ1kBV8zCI/LbAKQbYi','Cashier');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'smartdine'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-22  3:43:17
