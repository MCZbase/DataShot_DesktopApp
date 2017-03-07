alter table Specimen add column verbatimClusterIdentifier varchar(255);
alter table Specimen add column externalWorkflowProcess varchar(900);
alter table Specimen add column externalWorkflowDate datetime default null;
