# About 

This is the desktop application client for DataShot.  It is intended to 
support an object-precapture-image-data workflow for capture of natural 
science collections data.  

[![DOI](https://zenodo.org/badge/DOI/10.5281/zenodo.1040876.svg)](https://doi.org/10.5281/zenodo.1040876)


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

# User Documentation

See: https://github.com/MCZbase/DataShot_DesktopApp/wiki

# Building

Use maven to build (ant is optionally invoked by maven to build executable jar files).  

You will need to do some preparation work in order to build.

(1) The Oracle JDBC driver isn't available in a public Maven repository.  
You will need to download the jar from Oracle and add it locally (to support the build
even if you aren't using Oracle): 

Download the Oracle 10g release 2 (10.2.0.5) JDBC driver ojdbc14.jar 
from oracle.  

Add it to your local .m2 

    mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc14 \
      -Dversion=10.2.0.5.0 -Dpackaging=jar -Dfile=ojdbc14.jar -DgeneratePom=true

(2) Create a test database (and grant your user all permssions on it).  
A dump of the schema of a working test database (as of version 1.2.2) is in docs_manual/sql/mysql_ver1.2.2.sql
Check /docs_manual/sql/for updates and apply those as well.
The expected name, user and location of this database are in 
src/main/java/hibernate.cfg.xml (you will need to create a database lepidoptera).  

    MariaDB [mysql]> create database lepidoptera; 
    MariaDB [mysql]> grant all privileges on lepidoptera.* to '{your username}'@'localhost'; 

(The default name of the database is lepidoptera, but this can be changed, and one database 
can be configured for testing and another for production use).  
Populate the test database from the relevant DDL files in /docs_manual/sql/ from the shell:

    mysql lepidoptera -p < docs_manual/sql/mysql_ver1.2.2.sql
    mysql lepidoptera -p < docs_manual/sql/mysql_1.2.4_to_1.3.0_changes.sql
    mysql lepidoptera -p < docs_manual/sql/mysql_1.3.3_to_1.3.4_changes.sql

Once this database has been created, you'll need to create a user that the
application will use to connect to the database, that is (in the default configuration) a user LEPIDOPTERA 
with select/insert/update/delete privileges on the lepidoptera schema on localhost.

    grant select, insert, update, delete on lepidoptera.* to 'LEPIDOPTERA'@'localhost' identified by 'password';     

And then insert a row for a DataShot administrator into the LEPIDPTERA.Users table. 

    insert into Users (username,fullname,role,hash,description) values
      ('useremail','full name','Administrator',sha1('password'),'the users role in the project');

Once you have inserted the first administrator user, you should enter any additional users
through the user interface in the application (Configuration/Users on the main menu).

(3) Create a not_vcs directory in the project root, copy the file
src/main/java/hibernate.cfg.xml into that directory and edit it to supply 
connection parameters for your production database (likewise the log4j configuration file if you want to change the logging from the production jar), 
You should not put a password inside src/main/java/hibernate.cfg.xml.

     $ mkdir not_vcs
     $ cp src/main/java/hibernate.cfg.xml not_vcs/
     $ cp src/main/java/log4j.properties not_vcs/

Do not include the password in the hibernate.cfc.xml file in src/main/java/, but edit it to point to your test database, then on logging in when running from eclipse or another IDE, enter the password for the LEPIDOPTERA user into the advanced section of the login interface (likewise if a login is prompted when running tests).

then build with:

    mvn package -P production 

An executable jar file will be found in the build/ directory (and in the target/ directory).

If you are working with an IDE (such as eclipse), you will probably want to use the following somewhat
bizare incantation to create the executable jar file including the not_vcs/ configuration files, 
and then to clean out the target/ directory so that your IDE will use the configuration files 
from src/main/java rather than not_vcs (in target/classes) (letting your build with the IDE use 
the default hibernate and log4j configurations, rather than the production ones, which get placed 
where the IDE will used them by _mvn clean install -P production_).  

    mvn clean install clean compile -P production

You can also run integration tests once you have your local database and a user set up using the integrationTests profile:

    mvn package -P integrationTests

This will present you with a login dialog to run the tests, populated from the values in your src/main/java/hibernate.cfg.xml file.

The resulting executable jar file will be in build/Datashot{version}-jar-with-dependencies.jar,
you can run it, for example, with:

    java -jar DataShot-1.2.4-jar-with-dependencies.jar 


Builds were previously done with a mix of maven and ant to build the executable jar.  These are still available with
the profile ant (which will leave executable jars in the build/ directory: 

    mvn package -P ant

Note: If using maven 2, and you get a build error in the form of dependency problem about jai-image-io-core: 

    [ERROR] BUILD ERROR
    [INFO] ------------------------------------------------------------------------
    [INFO] Failed to resolve artifact.
    Unable to get dependency information: Unable to read the metadata file for artifact 'com.github.jai-imageio:jai-imageio-core:jar': Invalid JDK version in profile 'java8-and-higher': Unbounded range: [1.8, for project com.github.jai-imageio:jai-imageio-core
  com.github.jai-imageio:jai-imageio-core:jar:1.3.1

See http://stackoverflow.com/questions/42155692/why-isnt-zxing-playing-nicely-with-ant-java8-and-the-pom-xml for notes on how 
to fix a syntax error in the pom in your local repository. You will need to edit ~/.m2/repository/com/github/jai-imageio/jai-imageio-core/1.3.1/jai-imageio-core-1.3.1.pom to change <jdk>[1.8,</jdk> to <jdk>[1.8,)</jdk>.  
 
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

# Setup 

Setup involves configuring the application, building one or more carriers, 
setting up an imaging station, producing test images, and creating template 
records (using the template record editor in this application 
(Configuration/Edit Templates on the main menu) from those test images.

## Design assumptions

Some of the key design assumptions of DataShot are: 

Images conform to a small set of pre-defined templates.  

Each image is of a single cataloged item.  

Each image contains a barcode containing the catalog number of the specimen in the image.  

Catalog numbers barcodes follow a known pattern (e.g. [MCZ-ENT00061419](http://mczbase.mcz.harvard.edu/MediaSet.cfm?media_id=38974).

Each image contains a barcode containing the current identification as structured data in a known configuration produced by the PreCapture application.

Images are stored on a file server, at an unchanging location below some base point in the directory path.  

The mapping of the base location of the directory path for images will vary amongst workstations and any instance of the DataShot web application.

The images are produced with a camera on a light stand above one or a small set of jigs, each of which conforms to a particular defined template.

There is an expectation that images will be placed on the fileserver in batches corresponding to work done in a day, and that these batches will be placed with each batch of work in a identifiable directory (e.g. path name includes the date imaged) somewhere below the base point in the directory path.  

## Configuration

To configure the application, see Configuration/Properties on the main menu
or edit the imagecapture.properties file created when the application first runs.  
On the first run for a deployment, default values will be used, these almost certainly 
are not the values you want for that deployment, so carefuly examine and edit the 
configuration parameters when first running each deployed instance.  The recommended 
method for editing the configuration parameters is through Configuration/Properties 
on the main menu (as some characters need to be escaped in the properties file, 
the configuration editor handles this automatically, manual edits are more prone
to errors).  

The following configuration parameters are critical for setup:

    configuration.collection=MCZ-ENT
    images.thumbnailheight=120
    images.thumbnailwidth=80
    images.filenameregex=^IMG_[0-9]{6}\\.JPG$
    images.basedirectory=/home/mole/stuff/MCZ/mcz/insects/testImages/base/
    images.basedirectoryurimap=http\://mczbase.mcz.harvard.edu/specimen_images/
    images.regexdrawernumber=[0-9]{3}\\Q.\\E[0-9]+
    images.metadatacontainsbarcode=true
    default.preparation=pinned

configuration.collection must be one of MCZ-ENT or ETHZ-ENT, it configures what catalog number barcodes are expected (and, later, other behavior).

images.thumbnailheight and images.thumbnailwidth determine the size of thumbnail images that will be generated when images are preprocessed.

images.filenameregex only files with a filename that matches this regular expression pattern will be preprocessed.  

The following configuration parameter is optional, and may be changed as different parts of a collection are processed: 

    configuration.specificcollection=

If no value is provided for configuration.specificcollection, then default values will be used appropriate for the value
given for configuraion.collection as the value for Specimen.LocationInCollection.   If a value is provided, it will be 
used as the value for Specimen.LocationInCollection when new records are processed (except if family=Formicidae), for example: 

    configuration.specificcollection=Type Collection

Will set the value LocationInCollection of all newly created Specimen records to 'Type Collection' (except ants,
any records with Family=Formicidae, which get the special case value 'General Ant Collection').

**There is a key assumption that all images will be stored on shared storage and that every instance of the application 
will have the images available at a known mount point**, where part of the path to the images is held as local configuration
 and part is stored in the database.  The path below this mount point cannot change without all of the records in the 
database having to be updated.  The local path to this mount point can vary amongst client installations. 

For example, an image may have an Image.path value of: ent-lepidoptera\images\2009_04_29\IMG_000657.JPG

A windows user of the deskop application might have the local image root mounted at Z:\, configured as 
such in the desktop application, which then knows to put the local path by configuration together with 
the path from the database to retrieve the image at: 
Z:\ent-lepidoptera\images\2009_04_29\IMG_000657.JPG

For this user, images.basedirectory=Z:\

A user running the application on a Linux workstation might have a local image root = /mount/insectimages/
which is a mount point for directory on a shared storage device, and their desktop application would find
the same image file at /mount/insectimages/ent-lepidoptera/images/2009_04_29/IMG_000657.JPG

For this user, images.basedirectory=/mount/insectimages/

images.metadatacontainsbarcode, if true, the application expects that the exif comment will contain the same information as the catalog number barcode.

default.preparation this is the value that will be used by default for the specimen preparation value.


The following configuration parameters control how trying harder to detect barcodes works when simply checking the first time doesn't find a barcode:

    images.barcoderescalesize=400,600sharpen,600brighter,600dimmer,400sharpenbrighter
    images.zxingalsotryharder=true

images.barcodescscalesize is a comma separated list of pixel widths to resize cropped templated image areas that could contain a barcode to before rechecking for the barcode with zxing, with optional image processing transformations sharpen, brighter, and dimmer.  This describes a sequence of operations that will be performed to prepare a cropped area that might contain a barcode if an initial detection of a barcode in that area fails, and a subsequent rescaling of the cropped area to 800 pixels in width fails.  Each operation is carried out until one succeeds.  Adding more operations can increase the time taken in preprocessing images, but can reduce the number of cases where preprocessing fails.  The numbers are the pixel widths to which the cropped area (templated area for catalog number barcode or taxon name unit tray label barcode) are rescaled.  The optional transformations sharpen (sharpens the cropped area before trying to detect a barcode), brighter (brightens the image by an approximation of 1 f stop, suitable if lighting of the barcode is dimmer than the rest of the image and it appears grey), and dimmer (dims the image by an approximation of 1 f stop, sutiable if lighting of the barcode is brighter than the rest of the image and the barcode appears washed out in the image) can also be applied.  If both brighter and dimmer are specified, only dimmer will be applied, but brighter and dimmer can be combined with sharpen.  If all the configured operations fail, a number of additional hard coded operations are also tried (including further brightening and dimming and shifting the crop frame by a few pixels in each direction).  Setting the debug level to trac (see the comments in log4j.properties) will leave behind copies of cropped images from each barcode reading attempt, examining these and the log file can help in adjusting this configuration.

images.zxingalsotryharder takes the values true or false.  If false, each barcode reading attempt is performed only once, with the zxing barcode reading code configured normally.  If true, each barcode reading attempt that fails is repeated with zxing configured to try harder (thus xzing also try harder).

If the catalog number barcode is present in the image exif metadata (scanned into the exif comment), then the default values will probably be fine and should result in around a 1% rate where a template is not detected  and a specimen record is created in state OCR, and a smaller rate where preprocessing failed to create a specimen record.  If the catalog number is only present in the barcode in the image, then testing and tuning further will be of significant benefit, as failure cases where a specimen record mean more handling steps for the problem images (a recommendation for these is to first review them manually for proper placement of barcode labels and proper lighting adjustment (feeding back quality information to the personnel doing the specimen handling and imaging), if those are satisfactory, then entering the catalog number barcode into the image exif comment field, otherwise locating the specimen and taking the image again.  If you are seeing many failure conditions, then the template(s) may not match label placement well, the barcodes may not be well lit, or the barcodes may be printed with too low a resolution printer.  

The following configuration parameters control the behavior of the user interface:

    details.scroll=none
    browse.enabled=false
    picklist.filterlength=3
    numbertypes.showall=false
    template.default=Default template
    login.showadvanced=false

The following configuration parameters are for setting up tesseract as an OCR failover and ImageMagick for
creating thumbnails (and transforming images for OCR failover):

    images.barcoderescalesize=400
    program.convert=/usr/bin/convert
    program.tesseract=tesseract 
    convert.parameters=\ -depth 8 -compress None -type Grayscale 
    program.mogrify=mogrify 

The following properties just store recent activity, they aren't involved in configuration.

    fileload.lastpath=~/workspace/butterflies_sf/docs_manual/example_files/loadtest.csv
    scanonebarcode.lastpath=~/testImages/base/problem_2016Oct12/IMG_000057.JPG

**Note well: If you do not configure each deployed instance, it will not behave as you expect since 
default values for these configuration parameters will be used.**  

## Templates

Example images showing templates used at the MCZ are:

MCZ Butterfly Carrier (catalog number barcode in upper right): 
http://mczbase.mcz.harvard.edu/MediaSet.cfm?media_id=1075684

MCZ Large Butterfly Carrier (catalog number barcode top center):
http://mczbase.mcz.harvard.edu/MediaSet.cfm?media_id=232908

MCZ Ant Carrier (catalog number barcode left center): 
http://mczbase.mcz.harvard.edu/MediaSet.cfm?media_id=227294

You should set up a template for each image carrier you have built using the template editor in the desktop application.
Access this via Configuration/Edit Templates on the main menu.  In the template editor, load an image from which you 
wish to create a new template (File/Create New Template from Image), then fill in the template properties (Template ID= 
a unique name for the template, Name= a longer human readable description) and select regions.  Height and width for
of the entire template (Image Size) will be set from the image and can't be edited from the template editor. The 
template editor is quite crude, you get popup dialogs to enter pixel locations for each part of an image, current 
regions are shown with colored lines overlain on the image (but only update when you save values for a region), yes it is crude.
Once you have selected a region for the Barcode (=catalog number barcode), you can test detection of that barcode in the 
current image with the **Check for Barcode** button.  Once you have selected a region for the Taxon Name Barcode (machine 
readable portion of the label created by the PreCapture application, containing the current identification), you can check
the detection of that barcode in the current image with **Check Taxon Barcode**.  When satisfied, click **Save Template**.  
You can edit existing templates (except for the templates you see initially Default Template, Small Template 1,  and Whole Image Only, 
which  are hard coded in the application). When editing an existing template, File/Load Image will allow you to load an image
to display in the template editor.


Records from the template table for a couple of templates in use at the MCZ are below: 

    Insert into Template (TEMPLATEID,TEMPLATE_NAME,IMAGESIZEX,IMAGESIZEY,BARCODEPOSITIONX,BARCODEPOSITIONY,BARCODESIZEX,BARCODESIZEY,SPECIMENPOSITIONX,SPECIMENPOSITIONY,SPECIMENSIZEX,SPECIMENSIZEY,TEXTPOSITIONX,TEXTPOSITIONY,TEXTSIZEX,TEXTSIZEY,LABELPOSITIONX,LABELPOSITIONY,LABELSIZEX,LABELSIZEY,UTLABELPOSITIONX,UTLABELPOSITIONY,UTLABELSIZEX,UTLABELSIZEY,EDITABLE,REFERENCEIMAGE,UTBARCODEPOSITIONX,UTBARCODEPOSITIONY,UTBARCODESIZEX,UTBARCODESIZEY) values ('EOS600ButterflySmall','EOS600 Butterfly Small Carriage',3456,5184,3022,109,303,303,0,2548,3456,2609,133,127,2366,858,1578,849,1820,1587,0,1031,1820,1409,'1',null,1456,127,1200,1200);
    Insert into Template (TEMPLATEID,TEMPLATE_NAME,IMAGESIZEX,IMAGESIZEY,BARCODEPOSITIONX,BARCODEPOSITIONY,BARCODESIZEX,BARCODESIZEY,SPECIMENPOSITIONX,SPECIMENPOSITIONY,SPECIMENSIZEX,SPECIMENSIZEY,TEXTPOSITIONX,TEXTPOSITIONY,TEXTSIZEX,TEXTSIZEY,LABELPOSITIONX,LABELPOSITIONY,LABELSIZEX,LABELSIZEY,UTLABELPOSITIONX,UTLABELPOSITIONY,UTLABELSIZEX,UTLABELSIZEY,EDITABLE,REFERENCEIMAGE,UTBARCODEPOSITIONX,UTBARCODEPOSITIONY,UTBARCODESIZEX,UTBARCODESIZEY) values ('EOS600ButterflyLarge ','EOS600 Butterfly Large Carrier',3456,5184,1720,40,160,160,0,2000,3456,3184,30,30,1300,600,1500,300,1956,1700,0,650,1500,1361,'1',null,850,80,485,485);

Regions are defined for each template, which matches the physical layout of a carrier
(with the template recording x and y pixel coordinates and x and y pixel offsets for each region).
The regions identify where in the image can be found: the barcode (barcode containing the catalog number of the pin), the specimen, 
the human readable portion of the unit tray label (produced by the PreCapture application), the labels removed 
from the pin, the labels found in the unit tray, and the machine readable portion of the unit tray label.

## Users

The first Administrator user for the application must be inserted directly into the database (see above).

Enter additional users by logging in to the application as that administrator using the user dialog accessed
through Configuration/Users off of the main menu.  

# Maintinance 

## Schema
As of version 1.3.0, DataShot checks for an allowed_version table in the
database schema at login during startup, and will not start if this table
is not present, and it does not contain a value of version compatible with
the current DataShot software version.  Intent is that a change in the 
database schema will be accompanied by either a major or minor version 
change, but that patch version changes will not involve schema changes,
thus any 1.3. version will work with the same database schema, and a 
schema change will involve a change from version 1.3 to version 1.4.  
An exception is x.x.0-SNAPSHOT versions (snapshots of the first patch 
version of a new minor version), these are expected to incrementally 
add schema changes. 

See docs_manual/sql/ for DDL for the databse schema.

# External data paths

As of version 1.1.0, there is experimental support for transcription of 
verbatim information in other external applications and load of that data back into
the datashot staging database.  

## Transcription of (pin) label data without any interpretation.

If you export the barcode number for a specimen and an image file with pin 
label data for that specimen, values for verbatimUnclassifiedText and 
questions can be loaded back into the database if the Specimen record has not 
been processed beyond TaxonEntered, and if no values are present in the 
verbatimUnclassifiedText field.  To do this, construct a csv file
containing the data to be ingested with the columns below, and select 
Action/Load Data from the main menu. Column names are case sensitive.  Exactly 
these three columns must be present (no more, no fewer), but column order does not matter. 

    "barcode","verbatimUnclassifiedText","questions"

### Load Policy ###

Allowed to change a record when: Record is in state OCR, Taxon Entered, or Verbatim Entered.

Barcode policy: Barcode must exist and must be unique.

Overwrite policy: Will overwrite an existing value of verbatimUnclassifiedText.  
Will remove any existing value of verbatimClusterIdentifier.  Will not modify
other verbatim or other fields (except questions).

Questions policy: Any value provided in questions will be appended to the existing value for questions.

Status when complete policy:  Record is in state Verbatim Transcribed.

You can include a cluster identifier (if you have externally clustered the verbatim text values): 

    "barcode","verbatimUnclassifiedText","verbatimClusterIdentifier","questions"
    
Allowed to change a record when: Record is in state OCR, Taxon Entered, or Verbatim Entered.

Barcode policy: Barcode must exist and must be unique.

Overwrite policy: Will overwrite an existing value of verbatimUnclassifiedText.  
Will overwrite any existing value of verbatimClusterIdentifier.   Will not modify
other verbatim or other fields (except questions).

Questions policy: Any value provided in questions will be appended to the existing value for questions.

Status when complete policy:  Record is in state Verbatim Transcribed.    
    
## Transcription of (pin) label data with minimal interpretation into verbatim fields.

If you export the barcode number for a specimen and an image file with pin 
label data for that specimen, values for verbatimLocality, verbatimDate, and 
questions can be loaded back into the database if the Specimen record has not 
been processed beyond TaxonEntered, and if no values are present in the 
verbatimLocality or DateNOS fields.  To do this, construct a csv file
containing the data to be ingested with the columns below, and select 
Action/Load Data from the main menu.  Column names are case sensitive. Exactly 
these columns must be present (no more, no fewer), but column order does not matter. 

    "barcode","verbatimLocality","verbatimDate","verbatimCollector","verbatimCollection","verbatimNumbers","verbatimUnclassifiedText","questions"

For this load, use verbatimDate, this will be mapped internally onto the DateNOS field.

### Load Policy ###

Allowed to change a record when: Record is in state OCR or Taxon Entered.

Barcode policy: Barcode must exist and must be unique.

Overwrite policy: Does not overwrite any existing values.  Will not make any updates to a field if any of the 
verbatim fields contain data.

Questions policy: Any value provided in questions will be appended to the existing value for questions.

Status when complete policy:  Record is in state Verbatim Entered.

## Transcription of verbatim information with classification and metadata ##

Not Yet Implemented.

* verbatimClusterIdentifier
* externalWorkflowProcess 
* externalWorkflowDate 

## Transcription of (pin) label data with interpretation. ##

If you export the barcode number for a specimen and an image file with pin 
label data for that specimen, values for any of the fields listed below can be
loaded back into the database in a csv file that contains a column "barcode", 
and optionally a column "questions".  To do this, construct a csv file
containing the data to be ingested barcode and any the columns below, and select 
Action/Load Data from the main menu.  Column names are case sensitive. 
The barcode column must be present along with any of the columns from the list
below.  Column order does not matter.  Additional columns that are to be skipped 
may be included if the column name is prefixed with an underscore character "_". 

Example of a header: 

    "barcode","Higher_Geography","SpecificLocality","questions","_myExternalId"

Another example of a header for just verbatim fields, but with a verbatim cluster identifier.

    "barcode","verbatimLocality","verbatimDate","verbatimCollector","verbatimCollection","verbatimNumbers","verbatimUnclassifiedText","verbatimClusterIdentifier","questions"
    
Fields that can be included: 

* TypeStatus
* TypeNumber
* CitedInPublication
* Features
* HigherGeography
* SpecificLocality
* VerbatimLocality
* VerbatimCollector
* VerbatimCollection
* VerbatimNumbers
* VerbatimUnclassifiedText
* MinimumElevationSt  (for Minimum_Elevation)
* MaximumElevationSt  (for Maximum_Elevation)
* Elev_Units
* CollectingMethod
* ISODate (in the form 1998-03-05  or the form 1998-03-01/1998-04-03)
* DateNOS (use instead of vebatimDate when loading an arbitrary set of columns).
* DateEmerged
* DateEmergedIndicator
* DateCollected
* DateCollectedIndicator
* Collection
* SpecimenNotes
* LifeStage
* Sex
* PreparationType
* Habitat
* Microhabitat
* AssociatedTaxon
* Questions (value provided will be appended to any existing value in Questions).
* Inferences
* LocationInCollection
* ValidDistributionFlag

For this load, use the actual field name DateNOS instead of verbatimDate for verbatim date values.

In addition, two additional fields that contain structured lists of values can be added (all lower case field names): 

* collectors

The collectors field can contain a single collector name or a pipe '|' delimited list of collector names.  e.g. "R.A.Eastwood|N. Mattoni"

* numbers

The numbers field can contain a pipe '|' delimited list of numbers and types, separated by a colon ':', e.g. "1:unknown|52:Species Number"

The numbers field can contain colon separated other_number values in the form of "number:number type" pairs (e.g. "1:Collection Number|5:Unknown", optionally a list of multiple number:number type pairs separated by a pipe character.  

Order is important.  The element before a colon will be treated as the number, and the element after the colon will be treated as the number type.  
For example, "351:Species Number|2562:Collection Number" will create two other number records in the expected form, but
"351:Species Number|Collection Number:2562" will produce one other number record in the expected form (number=351,numberType=Species Number), but 
will produce an incorrect other number record in the form (number=Collection Number,numberType=2562).  The ingest code does not check whether or not
you have these in the correct order and will produce bad data if you do not.   If a pipe delimited numbers element does not contain a colon, then it 
will be treated as an other number of type unknown (e.g. "3:Species Number|15" will produce two other number records number=3,numberType=Species Number and number=15,numberType=Unknown, likewise "12" will produce one other number record, number=12, numberType=Unkown).

Expected values for number type are (case sensitive, arbitrary values are allowed, but these are preferred): 
* Unknown
* Species Number
* Collector Number
* Collection Number
* Genitalia Preparation
* DNA Sample Number
* Drawer Number
* Lycaenidae Morphology Ref.
* MCZ Slide Number
* MCZ Butterfly Exhibit, 2000

Also three fields containing metadata about external processing and classification of the data can be included:

* verbatimClusterIdentifier
* externalWorkflowProcess
* externalWorkflowDate

To load a csv file that contains additional columns that aren't to be loaded, prefix the columns to be 
skipped with an underscore for it to be skipped (e.g. _someExternalId will be skipped).

Load of data into fields other than those listed above (in particular part/preparation/part attribute (e.g. caste)) are not supported.

### Load Policy ###

#### If any non-verbatim, non-metadata field is included: ####

Allowed to change a record when: Record is in state OCR, Taxon Entered, Verbatim Entered, or Verbatim Classified.

Barcode policy: Barcode must exist and must be unique.

Overwrite policy: Will overwrite any existing value in the Verbatim fields or 
metadata (verbatimClusterIdentifier, externalWorkflowProcess,externalWorkflowDate) fields, 
but will not overwrite any existing value in any non-Verbatim field.

Questions policy: Any value provided in questions will be appended to the existing value for questions.

Status when complete policy:  Workflow status Verbatim Classified..

#### If only verbatim and metadata fields are included: ####

Allowed to change a record when: Record is in state OCR, Taxon Entered, or Verbatim Entered.

Barcode policy: Barcode must exist and must be unique.

Overwrite policy: Will overwrite any existing value in the Verbatim fields or 
metadata (verbatimClusterIdentifier, externalWorkflowProcess,externalWorkflowDate) fields, 
but will not overwrite any existing value in any non-Verbatim field.

Questions policy: Any value provided in questions will be appended to the existing value for questions.

Status when complete policy: Workflow status Verbatim Entered.

