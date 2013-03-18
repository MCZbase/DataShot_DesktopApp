Use maven to build.  

You will need to do some preparation work in order to build.

(1) The Oracle JDBC driver isn't available in a public Maven repository.  
You will need to download the jar from Oracle and add it locally: 

Download the Oracle 10g release 2 (10.2.0.5) JDBC driver ojdbc14.jar 
from oracle.  

Add it to your local .m2 

mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc14 \ 
 -Dversion=10.2.0.5.0 -Dpackaging=jar -Dfile=ojdbc14.jar -DgeneratePom=true


(2) Create a not_vcs directory in the project root, copy the file
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

If you have a test database conforming to the configuration in
src/main/java/hibernate.cfg.xml, you can include the unit tests, 
which make connections to this database.  

The ant build.xml file is obsolete.  The ant buildAppJar.xml file is invoked 
with maven in the package phase.