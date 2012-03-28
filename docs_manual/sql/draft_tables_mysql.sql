-- ======================================================================
-- ===   Sql Script for Database : MySQL db
-- ===
-- === Build : 47
-- ======================================================================

CREATE TABLE Specimen
  (
    SpecimenId               BIGINT         not null auto_increment,
    Barcode                  VARCHAR(20)    unique not null default '',
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
    PreparationType          VARCHAR(50),
    Habitat                  VARCHAR(900),
    AssociatedTaxon          VARCHAR(900),
    Questions                VARCHAR(900),
    Inferences               VARCHAR(900),
    LocationInCollection     VARCHAR(255)   default "General Lepidoptera Collection",
    WorkFlowStatus           VARCHAR(10),
    CreatedBy                VARCHAR(255),
    DateCreated              TIMESTAMP,
    DateLastUpdated          DATETIME,
    LastUpdatedBy            VARCHAR(255),
    ValidDistributionFlag    BOOLEAN        default true,

    primary key(SpecimenId)
  )
 TYPE = InnoDB;

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

    primary key(DeterminationId),

    foreign key(SpecimenId) references Specimen(SpecimenId) on update CASCADE on delete CASCADE
  )
 TYPE = InnoDB;

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
 TYPE = InnoDB;

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
 TYPE = InnoDB;

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
 TYPE = InnoDB;

-- ======================================================================

CREATE TABLE Image
  (
    ImageId     BIGINT         not null auto_increment,
    RawBarcode  VARCHAR(255),
    RawExifBarcode VARCHAR(255),
    Filename    VARCHAR(255),
    RawOCR      TEXT,
    Path        VARCHAR(900),
    URI         VARCHAR(255),
    SpecimenId  BIGINT         not null,

    primary key(ImageId),

    foreign key(SpecimenId) references Specimen(SpecimenId)
  )
 TYPE = InnoDB;

CREATE INDEX ImageIDX1 ON Image(RawBarcode);
CREATE INDEX ImageIDX2 ON Image(SpecimenId);

-- ======================================================================

