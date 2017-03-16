alter table Specimen add column verbatimClusterIdentifier varchar(255);
alter table Specimen add column externalWorkflowProcess varchar(900);
alter table Specimen add column externalWorkflowDate datetime default null;

ALTER TABLE Template ENGINE=InnoDB;
update Template set referenceimage = null;
alter table Template add CONSTRAINT fk_template_refimg FOREIGN KEY (referenceimage) references Image (imageid) ON DELETE SET null;
