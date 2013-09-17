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

