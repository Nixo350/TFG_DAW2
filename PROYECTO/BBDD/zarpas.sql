CREATE DATABASE  IF NOT EXISTS `zarpas` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `zarpas`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: zarpas
-- ------------------------------------------------------
-- Server version	9.3.0

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
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `id_categoria` bigint NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(100) DEFAULT NULL,
  `nombre` varchar(50) NOT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `UK35t4wyxqrevf09uwx9e9p6o75` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat`
--

DROP TABLE IF EXISTS `chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat` (
  `id_chat` bigint NOT NULL AUTO_INCREMENT,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `fecha_modificacion` datetime(6) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_chat`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comentario`
--

DROP TABLE IF EXISTS `comentario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comentario` (
  `id_comentario` bigint NOT NULL AUTO_INCREMENT,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `fecha_modificacion` datetime(6) DEFAULT NULL,
  `texto` text NOT NULL,
  `id_publicacion` bigint NOT NULL,
  `id_usuario` bigint NOT NULL,
  PRIMARY KEY (`id_comentario`),
  KEY `FKtgkcgp4s2b2ctokpolnkygk3a` (`id_publicacion`),
  KEY `FK9619kv3mim3a4yl0m5mdhhbh1` (`id_usuario`),
  CONSTRAINT `FK9619kv3mim3a4yl0m5mdhhbh1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `FKtgkcgp4s2b2ctokpolnkygk3a` FOREIGN KEY (`id_publicacion`) REFERENCES `publicacion` (`id_publicacion`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `comentario_reaccion`
--

DROP TABLE IF EXISTS `comentario_reaccion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comentario_reaccion` (
  `id_comentario` bigint NOT NULL,
  `id_usuario` bigint NOT NULL,
  `fecha_reaccion` datetime(6) DEFAULT NULL,
  `tipo_reaccion` enum('dislike','like') DEFAULT NULL,
  PRIMARY KEY (`id_comentario`,`id_usuario`),
  KEY `FKbxp6vlh2g737oef6hd0v3wcd` (`id_usuario`),
  CONSTRAINT `FKbxp6vlh2g737oef6hd0v3wcd` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `FKf5otj0rjxy00w7qwamn45en2b` FOREIGN KEY (`id_comentario`) REFERENCES `comentario` (`id_comentario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mensaje`
--

DROP TABLE IF EXISTS `mensaje`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mensaje` (
  `id_mensaje` bigint NOT NULL AUTO_INCREMENT,
  `contenido` text NOT NULL,
  `fecha_envio` datetime(6) DEFAULT NULL,
  `leido` bit(1) DEFAULT NULL,
  `id_chat` bigint NOT NULL,
  `id_emisor` bigint NOT NULL,
  PRIMARY KEY (`id_mensaje`),
  KEY `FKe19chdnxesv3itlf0v128hmrp` (`id_chat`),
  KEY `FKq6a804vtw1dadx5rjj63x86vx` (`id_emisor`),
  CONSTRAINT `FKe19chdnxesv3itlf0v128hmrp` FOREIGN KEY (`id_chat`) REFERENCES `chat` (`id_chat`),
  CONSTRAINT `FKq6a804vtw1dadx5rjj63x86vx` FOREIGN KEY (`id_emisor`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `publicacion`
--

DROP TABLE IF EXISTS `publicacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publicacion` (
  `id_publicacion` bigint NOT NULL AUTO_INCREMENT,
  `contenido` text,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `fecha_modificacion` datetime(6) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `id_usuario` bigint NOT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `id_categoria` bigint NOT NULL,
  PRIMARY KEY (`id_publicacion`),
  KEY `FK9kh5k6pnpbv4xeqrdwv4585b7` (`id_usuario`),
  KEY `FKqntaum2m5sf8q1os940iysx8j` (`id_categoria`),
  CONSTRAINT `FK9kh5k6pnpbv4xeqrdwv4585b7` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `FKqntaum2m5sf8q1os940iysx8j` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `publicacion_categoria`
--

DROP TABLE IF EXISTS `publicacion_categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publicacion_categoria` (
  `id_publicacion` bigint NOT NULL,
  `id_categoria` bigint NOT NULL,
  PRIMARY KEY (`id_publicacion`,`id_categoria`),
  KEY `FK5w7avyrbrxebx7ck9r381vlh5` (`id_categoria`),
  CONSTRAINT `FK5w7avyrbrxebx7ck9r381vlh5` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`),
  CONSTRAINT `FKb1ek5p9gan2rcpi2bva4afq7c` FOREIGN KEY (`id_publicacion`) REFERENCES `publicacion` (`id_publicacion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `publicacion_guardada`
--

DROP TABLE IF EXISTS `publicacion_guardada`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publicacion_guardada` (
  `id_publicacion` bigint NOT NULL,
  `id_usuario` bigint NOT NULL,
  `fecha_guardado` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id_publicacion`,`id_usuario`),
  KEY `FKl3ideccur6gn40ql5xq9byvd1` (`id_usuario`),
  CONSTRAINT `FKb0a5cmwbrwo8ip0kw7ioxrl9s` FOREIGN KEY (`id_publicacion`) REFERENCES `publicacion` (`id_publicacion`),
  CONSTRAINT `FKl3ideccur6gn40ql5xq9byvd1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `reaccion_publicacion`
--

DROP TABLE IF EXISTS `reaccion_publicacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reaccion_publicacion` (
  `fecha_reaccion` datetime(6) NOT NULL,
  `tipo_reaccion` enum('dislike','like') NOT NULL,
  `id_publicacion` bigint NOT NULL,
  `id_usuario` bigint NOT NULL,
  PRIMARY KEY (`id_publicacion`,`id_usuario`),
  KEY `FKq75nhai9t9uqle4pnl7bgjxg8` (`id_usuario`),
  CONSTRAINT `FKl8fa9y38t4hcvy1f0y9t7nyub` FOREIGN KEY (`id_publicacion`) REFERENCES `publicacion` (`id_publicacion`),
  CONSTRAINT `FKq75nhai9t9uqle4pnl7bgjxg8` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id_rol` bigint NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` bigint NOT NULL AUTO_INCREMENT,
  `contrasena` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `fecha_registro` datetime(6) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `ultimo_login` datetime(6) DEFAULT NULL,
  `foto_perfil` varchar(255) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `UK5171l57faosmj8myawaucatdw` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario_chat`
--

DROP TABLE IF EXISTS `usuario_chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_chat` (
  `id_chat` bigint NOT NULL,
  `id_usuario` bigint NOT NULL,
  `fecha_union` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id_chat`,`id_usuario`),
  KEY `FKa8ti2vtna4k6ufvxbivr1353r` (`id_usuario`),
  CONSTRAINT `FKa8ti2vtna4k6ufvxbivr1353r` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `FKq5satoahtn801tk9o5d0y40ai` FOREIGN KEY (`id_chat`) REFERENCES `chat` (`id_chat`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario_rol`
--

DROP TABLE IF EXISTS `usuario_rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_rol` (
  `usuario_rol_id` bigint NOT NULL AUTO_INCREMENT,
  `id_rol` bigint DEFAULT NULL,
  `id_usuario` bigint DEFAULT NULL,
  PRIMARY KEY (`usuario_rol_id`),
  KEY `FK3ftpt75ebughsiy5g03b11akt` (`id_usuario`),
  KEY `FKftktpda4tym4fx0na5qjnc6ff` (`id_rol`),
  CONSTRAINT `FK3ftpt75ebughsiy5g03b11akt` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `FKftktpda4tym4fx0na5qjnc6ff` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-09 14:55:22
