-- New fields for Specimen table to support verbatim data capture and external verbatim capture loops.
-- Changes not present in version 1.1.0
-- Added in 1.2.0-SNAPSHOT
-- Changes present in version 1.2.0 
alter table SPECIMEN add verbatimCollector varchar2(2000 char) default '';
alter table SPECIMEN add verbatimCollection varchar2(2000 char) default '';
alter table SPECIMEN add verbatimNumbers varchar2(2000 char) default '';
alter table SPECIMEN add verbatimUnclassifiedText varchar2(4000 char) default ''; 