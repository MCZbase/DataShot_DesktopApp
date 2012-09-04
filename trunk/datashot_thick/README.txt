Use maven to build.  

You probably need some preparation work to build.

At a minimum, create a not_vcs directory in the project root, copy the file
src/main/java/hibernate.cfg.xml into that directory and edit it to supply 
connection parameters for your production database, then build with:

mvn package -DskipTests 

The resulting executable jar file will be in build/ImageCapture.jar,
you can run it with:

java -jar ImageCapture.jar 

You should not redistribute this file outside your organization.

If you have a test database conforming to the configuration in
src/main/java/hibernate.cfg.xml, you can include the unit tests, 
which make connections to this database.  

The ant build.xml file is obsolete.  The ant buildAppJar.xml file is invoked 
with maven in the package phase.
