== About ==

This is the desktop application client for DataShot.  It is intended to 
support an object-precapture-image-data workflow for capture of natural 
science collections data.  

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
src/main/java/hibernate.cfg.xml (you will need to create a database lepidoptera
and a user LEPIDOPTERA with select/insert/update/delete privileges on 
the lepidoptera schema on localhost.

mysql lepidoptera -p < docs_manual/sql/mysql_ver1.0.4.sql

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
