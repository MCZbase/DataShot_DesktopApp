-- ======================================================================
-- ===   Sql Script for Database : Lepidoptera MySQL db
-- ===
-- === Build : 70
-- ======================================================================

-- Note IdentificationQualifier is depreciated and is to be removed.

CREATE TABLE Specimen
  (
    SpecimenId               BIGINT         not null auto_increment,
    Barcode                  VARCHAR(15)    unique not null,
    DrawerNumber             VARCHAR(10),
    TypeStatus               VARCHAR(50)    not null default "Not a Type",
    TypeNumber               BIGINT,
    CitedInPublication       VARCHAR(900),
    Features                 VARCHAR(50),
    Family                   VARCHAR(40),
    Subfamily                VARCHAR(40),
    Tribe                    VARCHAR(40),
    Genus                    VARCHAR(40),
    SpecificEpithet          VARCHAR(40),
    SubspecificEpithet       VARCHAR(255),
    InfraspecificEpithet     VARCHAR(40),
    InfraspecificRank        VARCHAR(40),
    Authorship               VARCHAR(255),
    UnNamedForm              VARCHAR(50),
    IdentificationQualifier  VARCHAR(50),
    IdentifiedBy             VARCHAR(255),
    Country                  VARCHAR(255),
    PrimaryDivison           VARCHAR(255),
    SpecificLocality         TEXT,
    VerbatimLocality         TEXT,
    VerbatimElevation        VARCHAR(255),
    CollectingMethod         VARCHAR(255),
    DateNOS                  VARCHAR(32),
    DateEmerged              VARCHAR(32),
    DateEmergedIndicator     VARCHAR(10),
    DateCollected            VARCHAR(32),
    DateCollectedIndicator   VARCHAR(10),
    Collection               VARCHAR(255),
    SpecimenNotes            TEXT,
    LifeStage                VARCHAR(50),
    Sex                      VARCHAR(50),
--    PreparationType          VARCHAR(50),
    Habitat                  VARCHAR(900),
    AssociatedTaxon          VARCHAR(900),
    Questions                VARCHAR(900),
    Inferences               VARCHAR(900),
    LocationInCollection     VARCHAR(255)   default "General Lepidoptera Collection",
    WorkFlowStatus           VARCHAR(10),
    CreatedBy                VARCHAR(255),
    DateCreated              TIMESTAMP default CURRENT_TIMESTAMP,
    DateLastUpdated          DATETIME,
    LastUpdatedBy            VARCHAR(255),
    ValidDistributionFlag    BOOLEAN        default true,

    primary key(SpecimenId)
  )
;

-- ======================================================================

CREATE TABLE Determination
  (
    DeterminationId          BIGINT         not null auto_increment,
    SpecimenId               BIGINT         not null,
    Genus                    VARCHAR(40),
    SpecificEpithet          VARCHAR(40),
    SubspecificEpithet       VARCHAR(255),
    InfraspecificEpithet     VARCHAR(40),
    InfraspecificRank        VARCHAR(40),
    Authorship               VARCHAR(255),
    UnNamedForm              VARCHAR(50),
    IdentificationQualifier  VARCHAR(50),
    IdentifiedBy             VARCHAR(255),
    TypeStatus               VARCHAR(50),

    primary key(DeterminationId),

    foreign key(SpecimenId) references Specimen(SpecimenId) on update CASCADE on delete CASCADE
  )
;

CREATE INDEX DeterminationIDX1 ON Determination(SpecimenId);

-- ======================================================================

CREATE TABLE Collector
  (
    CollectorId    BIGINT         not null auto_increment,
    CollectorName  VARCHAR(255),
    SpecimenId     BIGINT         not null,

    primary key(CollectorId),

    foreign key(SpecimenId) references Specimen(SpecimenId) on update CASCADE on delete CASCADE
  )
;

CREATE INDEX CollectorIDX1 ON Collector(CollectorName);

-- ======================================================================

CREATE TABLE Tracking
  (
    TrackingId     BIGINT         not null auto_increment,
    user           VARCHAR(255),
    eventType      VARCHAR(40),
    eventDateTime  TIMESTAMP,
    SpecimenId     BIGINT         not null,

    primary key(TrackingId),

    foreign key(SpecimenId) references Specimen(SpecimenId) on update CASCADE
  )
;

-- ======================================================================

CREATE TABLE Number
  (
    NumberId    BIGINT        not null auto_increment,
    Number      VARCHAR(50),
    NumberType  VARCHAR(50),
    SpecimenId  BIGINT        not null,

    primary key(NumberId),

    foreign key(SpecimenId) references Specimen(SpecimenId) on update CASCADE on delete CASCADE
  )
;

-- ======================================================================

CREATE TABLE Image
  (
    ImageId         BIGINT         not null auto_increment,
    RawBarcode      VARCHAR(255),
    RawExifBarcode  VARCHAR(255),
    Filename        VARCHAR(255),
    RawOCR          TEXT,
    Path            VARCHAR(900),
    URI             VARCHAR(255),
    SpecimenId      BIGINT         not null,
    TemplateId      VARCHAR(255),
    DrawerNumber    VARCHAR(10),
    MD5SUM          VARCHAR(900),

    primary key(ImageId),

    foreign key(SpecimenId) references Specimen(SpecimenId)
  )
;

CREATE INDEX ImageIDX1 ON Image(RawBarcode);
CREATE INDEX ImageIDX2 ON Image(SpecimenId);

-- ======================================================================

CREATE TABLE Users
  (
    userid       INT            not null auto_increment,
    username     VARCHAR(50)    unique not null default '',
    fullname     VARCHAR(50)    not null default '',
    description  VARCHAR(255),
    role         VARCHAR(20)    not null default 'Data Entry',

    primary key(userid)
  )
;

-- ======================================================================

CREATE TABLE Template
  (
    TemplateId         VARCHAR(50)    not null,
    Template_Name      VARCHAR(255),
    ImageSizeX         INT,
    ImageSizeY         INT,
    BarcodePositionX   INT,
    BarcodePositionY   INT,
    BarcodeSizeX       INT,
    BarcodeSizeY       INT,
    SpecimenPositionX  INT,
    SpecimenPositionY  INT,
    SpecimenSizeX      INT,
    SpecimenSizeY      INT,
    TextPositionX      INT,
    TextPositionY      INT,
    TextSizeX          INT,
    TextSizeY          INT,
    LabelPositionX     INT,
    LabelPositionY     INT,
    LabelSizeX         INT,
    LabelSizeY         INT,
    UtLabelPositionX   INT,
    UtLabelPositionY   INT,
    UtLabelSizeX       INT,
    UtLabelSizey       INT,
    Enabled            BOOLEAN,
    ReferenceImage     LONG

    primary key(TemplateId)
  )
;

-- ======================================================================


CREATE TABLE `HIGHER_TAXON` (
  `family` varchar(255) NOT NULL default '',
  `subfamily` varchar(255) default NULL,
  `tribe` varchar(255) default NULL,
  `id` int(11) NOT NULL auto_increment,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM ;

CREATE TABLE `UNIT_TRAY_LABEL` (
  `id` int(11) NOT NULL auto_increment,
  `Family` varchar(40) default '',
  `Subfamily` varchar(40) default '',
  `Tribe` varchar(40) default '',
  `Genus` varchar(40) default '',
  `SpecificEpithet` varchar(40) default '',
  `SubspecificEpithet` varchar(255) default '',
  `InfraspecificEpithet` varchar(40) default '',
  `InfraspecificRank` varchar(40) default '',
  `Authorship` varchar(255) default '',
  `DrawerNumber` varchar(10) default '',
  `UnNamedForm` varchar(40) default '',
  `Printed` int(11) NOT NULL default '0',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=UTF8;
