alter table Specimen add column verbatimClusterIdentifier varchar(255);
alter table Specimen add column externalWorkflowProcess varchar(900);
alter table Specimen add column externalWorkflowDate datetime default null;

ALTER TABLE Template ENGINE=InnoDB;
update Template set referenceimage = null;
alter table Template add CONSTRAINT fk_template_refimg FOREIGN KEY (referenceimage) references Image (imageid) ON DELETE SET null;

create table allowed_version ( 
   allowed_version_id bigint not null primary key auto_increment, 
   version varchar(50) not null
);

insert into allowed_version (version) values ('1.3');

create table external_history ( 
   external_history_id bigint not null primary key auto_increment,
   specimenId bigint not null,
   externalWorkflowProcess varchar(900), 
   externalWorkflowDate datetime
);

