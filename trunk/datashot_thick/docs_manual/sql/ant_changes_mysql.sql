create table Specimen_Part (
  SpecimenPartId bigint not null primary key auto_increment, 
  SpecimenId bigint not null,
  Part_name varchar(255) not null default 'whole animal',
  Preserve_Method varchar(60) not null default 'Pinned',
  Lot_Count int not null default 1, 
  Lot_Count_Modifier varchar(50), 
  
  foreign key(SpecimenId) references Specimen(SpecimenId) on update CASCADE on delete CASCADE
);

create table Specimen_Part_Attribute (
  SpecimenPartAttributeID bigint not null primary key auto_increment,
  SpecimenPartId bigint not null,
  Attribute_Type varchar(30),
  Attribute_Value varchar(255),
  Attribute_Units varchar(30),
  Attribute_Remark varchar(4000),
  Attribute_Determiner varchar(255) not null,
  Attribute_Date datetime,

  foreign key (SpecimenPartId) references Specimen_Part(SpecimenPartID) on update CASCADE on delete CASCADE
);

insert into Specimen_Part (SpecimenId, Part_name, Preserve_Method, Lot_Count)
     select specimenid, 'whole animal', lcase(PreparationType), 1 from Specimen;

alter table Specimen drop column PreparationType;
alter table Specimen change column ISODate ISODate varchar(32) default '';

CREATE TABLE MCZBASE_GEOG_AUTH_REC (
    GEOG_AUTH_REC_ID bigint NOT NULL primary key auto_increment,  
    CONTINENT_OCEAN VARCHAR(70), 
    COUNTRY VARCHAR(70), 
    STATE_PROV VARCHAR(75), 
    COUNTY VARCHAR(50), 
    QUAD VARCHAR(30), 
    FEATURE VARCHAR(50), 
    ISLAND VARCHAR(50), 
    ISLAND_GROUP VARCHAR(50), 
    SEA VARCHAR(50), 
    VALID_CATALOG_TERM_FG int NOT NULL, 
    SOURCE_AUTHORITY VARCHAR(45) NOT NULL, 
    HIGHER_GEOG VARCHAR(255), 
    OCEAN_REGION VARCHAR(50), 
    OCEAN_SUBREGION VARCHAR(50)
);

alter table Specimen add column higher_geography varchar(255); 
alter table Specimen add column creatingPath varchar(900); 
alter table Specimen add column creatingFilename varchar(255); 
