-- New fields for Specimen table to support verbatim data capture and external verbatim capture loops.
-- Proposed changes, not added yet as of version 1.1.0
-- Added in 1.2.0-SNAPSHOT
alter table Specimen add column verbatimCollector varchar(2000) not null default "";
alter table Specimen add column verbatimCollection varchar(2000) not null default "";
alter table Specimen add column verbatimNumbers varchar(2000) not null default "";
alter table Specimen add column verbatimUnclassifiedText text not null default "";