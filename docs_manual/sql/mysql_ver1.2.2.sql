-- MySQL dump 10.13  Distrib 5.5.53, for debian-linux-gnu (i686)
--
-- Host: localhost    Database: lepidoptera
-- ------------------------------------------------------
-- Server version	5.5.53-0+deb7u1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Collector`
--

DROP TABLE IF EXISTS `Collector`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Collector` (
  `CollectorId` bigint(20) NOT NULL AUTO_INCREMENT,
  `CollectorName` varchar(255) DEFAULT NULL,
  `SpecimenId` bigint(20) NOT NULL,
  PRIMARY KEY (`CollectorId`),
  UNIQUE KEY `ucollspec` (`CollectorName`,`SpecimenId`),
  KEY `SpecimenId` (`SpecimenId`),
  KEY `CollectorIDX1` (`CollectorName`),
  CONSTRAINT `Collector_ibfk_1` FOREIGN KEY (`SpecimenId`) REFERENCES `Specimen` (`SpecimenId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Determination`
--

DROP TABLE IF EXISTS `Determination`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Determination` (
  `DeterminationId` bigint(20) NOT NULL AUTO_INCREMENT,
  `SpecimenId` bigint(20) NOT NULL,
  `Genus` varchar(40) DEFAULT NULL,
  `SpecificEpithet` varchar(40) DEFAULT NULL,
  `SubspecificEpithet` varchar(255) DEFAULT NULL,
  `InfraspecificEpithet` varchar(40) DEFAULT NULL,
  `InfraspecificRank` varchar(40) DEFAULT NULL,
  `Authorship` varchar(255) DEFAULT NULL,
  `UnNamedForm` varchar(50) DEFAULT NULL,
  `IdentifiedBy` varchar(255) DEFAULT NULL,
  `TypeStatus` varchar(50) DEFAULT NULL,
  `SpeciesNumber` varchar(50) DEFAULT '',
  `VerbatimText` varchar(255) DEFAULT NULL,
  `Remarks` text,
  `NatureOfId` varchar(255) NOT NULL DEFAULT 'legacy',
  `DateIdentified` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`DeterminationId`),
  KEY `DeterminationIDX1` (`SpecimenId`),
  CONSTRAINT `Determination_ibfk_1` FOREIGN KEY (`SpecimenId`) REFERENCES `Specimen` (`SpecimenId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `HIGHER_TAXON`
--

DROP TABLE IF EXISTS `HIGHER_TAXON`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `HIGHER_TAXON` (
  `family` varchar(255) NOT NULL DEFAULT '',
  `subfamily` varchar(255) DEFAULT NULL,
  `tribe` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hasCastes` int(11) DEFAULT '0',
  `usesdateemerged` int(11) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Image`
--

DROP TABLE IF EXISTS `Image`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Image` (
  `ImageId` bigint(20) NOT NULL AUTO_INCREMENT,
  `RawBarcode` varchar(255) DEFAULT NULL,
  `Filename` varchar(255) DEFAULT NULL,
  `RawOCR` text,
  `Path` varchar(900) DEFAULT NULL,
  `URI` varchar(255) DEFAULT NULL,
  `SpecimenId` bigint(20) DEFAULT NULL,
  `RawExifBarcode` varchar(255) DEFAULT NULL,
  `TemplateId` varchar(255) DEFAULT NULL,
  `DRAWERNUMBER` varchar(10) DEFAULT NULL,
  `MD5SUM` varchar(900) DEFAULT NULL,
  PRIMARY KEY (`ImageId`),
  KEY `ImageIDX1` (`RawBarcode`),
  KEY `ImageIDX2` (`SpecimenId`),
  CONSTRAINT `Image_ibfk_1` FOREIGN KEY (`SpecimenId`) REFERENCES `Specimen` (`SpecimenId`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LAT_LONG`
--

DROP TABLE IF EXISTS `LAT_LONG`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LAT_LONG` (
  `LAT_LONG_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `specimenid` bigint(20) NOT NULL,
  `LAT_DEG` int(11) DEFAULT NULL,
  `DEC_LAT_MIN` decimal(8,6) DEFAULT NULL,
  `LAT_MIN` int(11) DEFAULT NULL,
  `LAT_SEC` decimal(8,6) DEFAULT NULL,
  `LAT_DIR` varchar(1) DEFAULT NULL,
  `LONG_DEG` int(11) DEFAULT NULL,
  `DEC_LONG_MIN` decimal(10,8) DEFAULT NULL,
  `LONG_MIN` int(11) DEFAULT NULL,
  `LONG_SEC` decimal(8,6) DEFAULT NULL,
  `LONG_DIR` varchar(1) DEFAULT NULL,
  `DEC_LAT` decimal(12,10) DEFAULT NULL,
  `DEC_LONG` decimal(13,10) DEFAULT NULL,
  `DATUM` varchar(55) NOT NULL,
  `UTM_ZONE` varchar(3) DEFAULT NULL,
  `UTM_EW` int(11) DEFAULT NULL,
  `UTM_NS` int(11) DEFAULT NULL,
  `ORIG_LAT_LONG_UNITS` varchar(20) NOT NULL,
  `DETERMINED_BY_AGENT` varchar(255) DEFAULT NULL,
  `DETERMINED_DATE` date NOT NULL,
  `LAT_LONG_REF_SOURCE` varchar(500) NOT NULL,
  `LAT_LONG_REMARKS` varchar(4000) DEFAULT NULL,
  `MAX_ERROR_DISTANCE` int(11) DEFAULT NULL,
  `MAX_ERROR_UNITS` varchar(2) DEFAULT NULL,
  `NEAREST_NAMED_PLACE` varchar(255) DEFAULT NULL,
  `LAT_LONG_FOR_NNP_FG` int(11) DEFAULT NULL,
  `FIELD_VERIFIED_FG` int(11) DEFAULT NULL,
  `ACCEPTED_LAT_LONG_FG` int(11) NOT NULL,
  `EXTENT` decimal(12,5) DEFAULT NULL,
  `GPSACCURACY` decimal(8,3) DEFAULT NULL,
  `GEOREFMETHOD` varchar(255) NOT NULL,
  `VERIFICATIONSTATUS` varchar(40) NOT NULL,
  PRIMARY KEY (`LAT_LONG_ID`),
  UNIQUE KEY `FK_specimen_ID` (`specimenid`),
  KEY `LAT_LONG_DEC_ALL` (`DEC_LAT`,`DEC_LONG`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Label`
--

DROP TABLE IF EXISTS `Label`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Label` (
  `labelid` bigint(20) NOT NULL AUTO_INCREMENT,
  `imageid` bigint(20) NOT NULL,
  `offsettop` int(11) DEFAULT NULL,
  `offsetleft` int(11) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `verbatimtext` varchar(255) DEFAULT NULL,
  `interpretation` text,
  PRIMARY KEY (`labelid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `LabelTag`
--

DROP TABLE IF EXISTS `LabelTag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `LabelTag` (
  `labeltagid` bigint(20) NOT NULL AUTO_INCREMENT,
  `labelid` bigint(20) NOT NULL,
  `value` varchar(255) NOT NULL,
  `tagname` varchar(255) NOT NULL,
  PRIMARY KEY (`labeltagid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MCZBASE_AUTH_AGENT_NAME`
--

DROP TABLE IF EXISTS `MCZBASE_AUTH_AGENT_NAME`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MCZBASE_AUTH_AGENT_NAME` (
  `AGENT_NAME_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `AGENT_ID` bigint(20) DEFAULT NULL,
  `AGENT_NAME_TYPE` varchar(18) DEFAULT NULL,
  `DONOR_CARD_PRESENT_FG` int(11) DEFAULT NULL,
  `AGENT_NAME` varchar(184) DEFAULT NULL,
  PRIMARY KEY (`AGENT_NAME_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MCZBASE_GEOG_AUTH_REC`
--

DROP TABLE IF EXISTS `MCZBASE_GEOG_AUTH_REC`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `MCZBASE_GEOG_AUTH_REC` (
  `GEOG_AUTH_REC_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `CONTINENT_OCEAN` varchar(70) DEFAULT NULL,
  `COUNTRY` varchar(70) DEFAULT NULL,
  `STATE_PROV` varchar(75) DEFAULT NULL,
  `COUNTY` varchar(50) DEFAULT NULL,
  `QUAD` varchar(30) DEFAULT NULL,
  `FEATURE` varchar(50) DEFAULT NULL,
  `ISLAND` varchar(50) DEFAULT NULL,
  `ISLAND_GROUP` varchar(50) DEFAULT NULL,
  `SEA` varchar(50) DEFAULT NULL,
  `VALID_CATALOG_TERM_FG` int(11) NOT NULL,
  `SOURCE_AUTHORITY` varchar(45) NOT NULL,
  `HIGHER_GEOG` varchar(255) DEFAULT NULL,
  `OCEAN_REGION` varchar(50) DEFAULT NULL,
  `OCEAN_SUBREGION` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`GEOG_AUTH_REC_ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `OTHER_NUMBERS`
--

DROP TABLE IF EXISTS `OTHER_NUMBERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OTHER_NUMBERS` (
  `NumberId` bigint(20) NOT NULL AUTO_INCREMENT,
  `OTHER_NUMBER` varchar(50) DEFAULT NULL,
  `NumberType` varchar(50) DEFAULT NULL,
  `SpecimenId` bigint(20) NOT NULL,
  PRIMARY KEY (`NumberId`),
  KEY `SpecimenId` (`SpecimenId`),
  CONSTRAINT `OTHER_NUMBERS_ibfk_1` FOREIGN KEY (`SpecimenId`) REFERENCES `Specimen` (`SpecimenId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Specimen`
--

DROP TABLE IF EXISTS `Specimen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Specimen` (
  `SpecimenId` bigint(20) NOT NULL AUTO_INCREMENT,
  `Barcode` varchar(20) NOT NULL DEFAULT '',
  `DrawerNumber` varchar(10) DEFAULT NULL,
  `TypeStatus` varchar(50) NOT NULL DEFAULT 'Not a Type',
  `TypeNumber` bigint(20) DEFAULT NULL,
  `CitedInPublication` varchar(900) DEFAULT NULL,
  `Features` varchar(50) DEFAULT NULL,
  `Family` varchar(40) DEFAULT NULL,
  `Subfamily` varchar(40) DEFAULT NULL,
  `Tribe` varchar(40) DEFAULT NULL,
  `Genus` varchar(40) DEFAULT NULL,
  `SpecificEpithet` varchar(40) DEFAULT NULL,
  `SubspecificEpithet` varchar(255) DEFAULT NULL,
  `InfraspecificEpithet` varchar(40) DEFAULT NULL,
  `InfraspecificRank` varchar(40) DEFAULT NULL,
  `Authorship` varchar(255) DEFAULT NULL,
  `UnNamedForm` varchar(50) DEFAULT NULL,
  `IdentifiedBy` varchar(255) DEFAULT NULL,
  `Country` varchar(255) DEFAULT NULL,
  `PrimaryDivison` varchar(255) DEFAULT NULL,
  `SpecificLocality` text,
  `verbatimLocality` text,
  `CollectingMethod` varchar(255) DEFAULT NULL,
  `DateNOS` varchar(32) DEFAULT NULL,
  `DateEmerged` varchar(32) DEFAULT NULL,
  `DateEmergedIndicator` varchar(50) DEFAULT NULL,
  `DateCollected` varchar(32) DEFAULT NULL,
  `DateCollectedIndicator` varchar(50) DEFAULT NULL,
  `Collection` varchar(255) DEFAULT NULL,
  `SpecimenNotes` text,
  `LifeStage` varchar(50) DEFAULT NULL,
  `Habitat` varchar(900) DEFAULT NULL,
  `AssociatedTaxon` varchar(900) DEFAULT NULL,
  `Questions` varchar(900) DEFAULT NULL,
  `Inferences` varchar(900) DEFAULT NULL,
  `LocationInCollection` varchar(255) DEFAULT 'General Lepidoptera Collection',
  `WorkFlowStatus` varchar(30) DEFAULT 'OCR',
  `CreatedBy` varchar(255) DEFAULT NULL,
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DateLastUpdated` datetime DEFAULT NULL,
  `LastUpdatedBy` varchar(255) DEFAULT NULL,
  `ValidDistributionFlag` tinyint(1) DEFAULT '1',
  `Sex` varchar(50) DEFAULT NULL,
  `ISODate` varchar(32) DEFAULT '',
  `flaginbulkloader` tinyint(1) DEFAULT '0',
  `flaginmczbase` tinyint(1) DEFAULT '0',
  `flagancilaryalsoinmczbase` tinyint(1) DEFAULT '0',
  `higher_geography` varchar(255) DEFAULT NULL,
  `microhabitat` varchar(900) DEFAULT NULL,
  `elev_units` varchar(5) DEFAULT NULL,
  `minimum_elevation` bigint(20) DEFAULT NULL,
  `maximum_elevation` bigint(20) DEFAULT NULL,
  `creatingPath` varchar(900) DEFAULT NULL,
  `creatingFilename` varchar(255) DEFAULT NULL,
  `IdentificationRemarks` text,
  `DateIdentified` varchar(32) DEFAULT NULL,
  `NatureOfId` varchar(255) NOT NULL DEFAULT 'legacy',
  `verbatimCollector` varchar(2000) DEFAULT '',
  `verbatimCollection` varchar(2000) DEFAULT '',
  `verbatimNumbers` varchar(2000) DEFAULT '',
  `verbatimUnclassifiedText` text,
  PRIMARY KEY (`SpecimenId`),
  UNIQUE KEY `Barcode` (`Barcode`),
  UNIQUE KEY `specimen_barcode` (`Barcode`),
  KEY `idxverblocality` (`verbatimLocality`(500)),
  KEY `idxverblocality2` (`verbatimLocality`(767)),
  KEY `idxverbcollector` (`verbatimCollector`(767))
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Specimen_Part`
--

DROP TABLE IF EXISTS `Specimen_Part`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Specimen_Part` (
  `SpecimenPartId` bigint(20) NOT NULL AUTO_INCREMENT,
  `SpecimenId` bigint(20) NOT NULL,
  `Part_name` varchar(255) NOT NULL DEFAULT 'whole animal',
  `Preserve_Method` varchar(60) NOT NULL DEFAULT 'Pinned',
  `Lot_Count` int(11) NOT NULL DEFAULT '1',
  `Lot_Count_Modifier` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`SpecimenPartId`),
  KEY `SpecimenId` (`SpecimenId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Specimen_Part_Attribute`
--

DROP TABLE IF EXISTS `Specimen_Part_Attribute`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Specimen_Part_Attribute` (
  `SpecimenPartAttributeID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SpecimenPartId` bigint(20) NOT NULL,
  `Attribute_Type` varchar(30) DEFAULT NULL,
  `Attribute_Value` varchar(255) DEFAULT NULL,
  `Attribute_Units` varchar(30) DEFAULT NULL,
  `Attribute_Remark` varchar(4000) DEFAULT NULL,
  `Attribute_Determiner` varchar(255) NOT NULL,
  `Attribute_Date` datetime DEFAULT NULL,
  PRIMARY KEY (`SpecimenPartAttributeID`),
  KEY `SpecimenPartId` (`SpecimenPartId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Tag`
--

DROP TABLE IF EXISTS `Tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Tag` (
  `tagid` bigint(20) NOT NULL AUTO_INCREMENT,
  `tagname` varchar(255) NOT NULL,
  PRIMARY KEY (`tagid`),
  UNIQUE KEY `tagtagname` (`tagname`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Template`
--

DROP TABLE IF EXISTS `Template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Template` (
  `TemplateId` varchar(50) NOT NULL,
  `Template_Name` varchar(255) DEFAULT NULL,
  `ImageSizeX` int(11) DEFAULT NULL,
  `ImageSizeY` int(11) DEFAULT NULL,
  `BarcodePositionX` int(11) DEFAULT NULL,
  `BarcodePositionY` int(11) DEFAULT NULL,
  `BarcodeSizeX` int(11) DEFAULT NULL,
  `BarcodeSizeY` int(11) DEFAULT NULL,
  `SpecimenPositionX` int(11) DEFAULT NULL,
  `SpecimenPositionY` int(11) DEFAULT NULL,
  `SpecimenSizeX` int(11) DEFAULT NULL,
  `SpecimenSizeY` int(11) DEFAULT NULL,
  `TextPositionX` int(11) DEFAULT NULL,
  `TextPositionY` int(11) DEFAULT NULL,
  `TextSizeX` int(11) DEFAULT NULL,
  `TextSizeY` int(11) DEFAULT NULL,
  `LabelPositionX` int(11) DEFAULT NULL,
  `LabelPositionY` int(11) DEFAULT NULL,
  `LabelSizeX` int(11) DEFAULT NULL,
  `LabelSizeY` int(11) DEFAULT NULL,
  `UtLabelPositionX` int(11) DEFAULT NULL,
  `UtLabelPositionY` int(11) DEFAULT NULL,
  `UtLabelSizeX` int(11) DEFAULT NULL,
  `UtLabelSizey` int(11) DEFAULT NULL,
  `Editable` tinyint(1) NOT NULL DEFAULT '1',
  `ReferenceImage` bigint(20) DEFAULT NULL,
  `UtBarcodePositionX` int(11) DEFAULT NULL,
  `UtBarcodePositionY` int(11) DEFAULT NULL,
  `UtBarcodeSizeX` int(11) DEFAULT NULL,
  `UtBarcodeSizeY` int(11) DEFAULT NULL,
  PRIMARY KEY (`TemplateId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Tracking`
--

DROP TABLE IF EXISTS `Tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Tracking` (
  `TrackingId` bigint(20) NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(255) DEFAULT NULL,
  `eventType` varchar(40) DEFAULT NULL,
  `eventDateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `SpecimenId` bigint(20) NOT NULL,
  PRIMARY KEY (`TrackingId`),
  KEY `SpecimenId` (`SpecimenId`),
  CONSTRAINT `Tracking_ibfk_1` FOREIGN KEY (`SpecimenId`) REFERENCES `Specimen` (`SpecimenId`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `UNIT_TRAY_LABEL`
--

DROP TABLE IF EXISTS `UNIT_TRAY_LABEL`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UNIT_TRAY_LABEL` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `Family` varchar(40) DEFAULT '',
  `Subfamily` varchar(40) DEFAULT '',
  `Tribe` varchar(40) DEFAULT '',
  `Genus` varchar(40) DEFAULT '',
  `SpecificEpithet` varchar(40) DEFAULT '',
  `SubspecificEpithet` varchar(255) DEFAULT '',
  `InfraspecificEpithet` varchar(40) DEFAULT '',
  `InfraspecificRank` varchar(40) DEFAULT '',
  `Authorship` varchar(255) DEFAULT '',
  `DrawerNumber` varchar(10) DEFAULT '',
  `UnNamedForm` varchar(40) DEFAULT '',
  `Printed` int(11) NOT NULL DEFAULT '0',
  `NumberToPrint` int(11) NOT NULL DEFAULT '1',
  `DateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `DateLastUpdated` date DEFAULT NULL,
  `Collection` varchar(255) DEFAULT NULL,
  `ordinal` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Users`
--

DROP TABLE IF EXISTS `Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Users` (
  `userid` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL DEFAULT '',
  `fullname` varchar(50) NOT NULL DEFAULT '',
  `description` varchar(255) DEFAULT NULL,
  `role` varchar(20) NOT NULL DEFAULT 'Data Entry',
  `hash` varchar(41) DEFAULT NULL,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `usernames` (`username`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-02-01 14:35:15
