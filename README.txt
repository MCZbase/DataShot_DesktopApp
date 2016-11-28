== About ==

This is the desktop application client for DataShot.  It is intended to 
support an object-precapture-image-data workflow for capture of natural 
science collections data.  

This software was designed to work along side MCZbase and to load data from
the DataShot staging database into MCZbase through a variant of the MCZbase
bulkloader.  It should not be difficult to deploy this software next to an 
Arctos installation.  Work would be required to produce a bulkloading system
from DataShot's staging database to other natural science collections 
database systems.

This software assumes that images of specimens and labels are taken using
standardized carriers, that each specimen has a machine readable barcode 
that contains that specimen's catalog number, and that the position of that
machine readable catalog number barcode on the carrier identifies which 
carrier is in use, and which portion of the image contain the specimen, 
which portion contains a machine readable representation of the current 
identification (produced by the PreCapture application), which portion
of the image contains labels specific to the specimen (pin labels in the 
case of pinned insects), which portion of the image contains labels 
specific to the container (unit tray labels), and which portion of the 
image contains a human readable current identifiction (as a failover if
the machine readable form is not read).  These templates are configurable.

This software suite assumes that images are stored at a known location on a 
mounted networked filesystem, and that each instance of the software is 
able to see the images at a consistent location below some locally configured
mountpoint.  

This software assumes that some user with an editorial role will use this
desktop application to preprocess image files (extracting data from the 
machine readable barcodes) to produce skeletal Specimen records (with a
current identification and a catalog number), that other users will 
transcribe data from the labels and about the specimen in the image to 
database fields, and that a user with an editorial role will approve the
specimen records for bulkload into the database of record.  

== Building ==

Use maven to build (ant is invoked by maven to build executable jar files).  

You will need to do some preparation work in order to build.

(1) The Oracle JDBC driver isn't available in a public Maven repository.  
You will need to download the jar from Oracle and add it locally: 

Download the Oracle 10g release 2 (10.2.0.5) JDBC driver ojdbc14.jar 
from oracle.  

Add it to your local .m2 

mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc14 \
 -Dversion=10.2.0.5.0 -Dpackaging=jar -Dfile=ojdbc14.jar -DgeneratePom=true

(2) Create a test database.  A dump of the schema of a working 
test database (as of version 1.0.4) is in docs_manual/sql/mysql_ver1.0.4.sql
the expected name, user and location of this database are in 
src/main/java/hibernate.cfg.xml (you will need to create a database lepidoptera.

mysql lepidoptera -p < docs_manual/sql/mysql_ver1.0.4.sql
mysql lepidoptera -p < docs_manual/sql/mysql_post_ver1.1.0.changes.sql

Once this database has been created, you'll need to create a user that the
application will use to connect to the database, that is a user LEPIDOPTERA 
with select/insert/update/delete privileges on the lepidoptera schema on localhost.

grant select, insert, update, delete on lepidoptera.* to 'dbuser'@'localhost'; 

And then insert a row for a DataShot administrator into the LEPIDPTERA.Users table. 

insert into Users (username,fullname,role,hash,description) values
('useremail','full name','Administrator',sha1('password'),'the users role in the project');

(3) Create a not_vcs directory in the project root, copy the file
src/main/java/hibernate.cfg.xml into that directory and edit it to supply 
connection parameters for your production database, then build with:

mvn package -DskipTests 

(If you have a test database set up that fits the parameters in the 
hibernate.cfg.xml file, then you can omit the -DskipTests to run the tests, 
which will include authenticating in to the test database.  You should not 
put a password inside src/main/java/hibernate.cfg.xml)

The resulting executable jar file will be in build/ImageCapture.jar,
you can run it with:

java -jar ImageCapture.jar 

You should not redistribute this file outside your organization.

There will also be an executable jar built by maven in 
target/butterflies_sf-{version}-SNAPSHOT-jar-with-dependencies.jar
this jar will include the hibernate configuration for your test 
database src/main/java/hibernate.cfg.xml.

If you have a test database conforming to the configuration in
src/main/java/hibernate.cfg.xml, you can include the unit tests, 
which make connections to this database.  

The ant buildAppJar.xml file is invoked  with maven in the package phase.

(3) After a maven package, you can invoke ant on the build_CIF.xml file in
order to build a CandidateImageFile.jar that can examine an image file or
a directory of image files and produce a list of filenames and barcodes found
in the files.

ant -f build_CIF.xml

The resulting executable jar file will be in build/CandidateImageFile.jar,
you can run it with:

java -jar CandidateImageFile.jar -h

== Setup ==

Setup involves building one or more carriers, setting up an imaging station,
producing test images, and creating template records (using the template
record editor in this application) from those test images.

Example images showing templates used at the MCZ are:

MCZ Butterfly Carrier (catalog number barcode in upper right): 
http://mczbase.mcz.harvard.edu/MediaSet.cfm?media_id=1075684

MCZ Large Butterfly Carrier (catalog number barcode top center):
http://mczbase.mcz.harvard.edu/MediaSet.cfm?media_id=232908

MCZ Ant Carrier (catalog number barcode left center): 
http://mczbase.mcz.harvard.edu/MediaSet.cfm?media_id=227294


== External data paths == 

As of version 1.1.0, there is experimental support for transcription of 
verbatim information in other external applications and load of that data back into
the datashot staging database.  

If you export the barcode number for a specimen and an image file with pin 
label data for that specimen, values for verbatimLocality, verbatimDate, and 
questions can be loaded back into the database if the Specimen record has not 
been processed beyond TaxonEntered, and if no values are present in the 
verbatimLocality or DateNOS fields.  To do this, construct a csv file
containing the data to be ingested with the columns below, and select 
Action/Load Data from the main menu. 

"barcode","verbatimLocality","verbatimDate","questions"

This functionality is expected to change in future versions.