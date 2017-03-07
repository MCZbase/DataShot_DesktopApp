# About 

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

(2) Create a test database.  A dump of the schema of a working 
test database (as of version 1.2.2) is in docs_manual/sql/mysql_ver1.2.2.sql
the expected name, user and location of this database are in 
src/main/java/hibernate.cfg.xml (you will need to create a database lepidoptera).  
(The default name of the database is lepidoptera, but this can be changed, and one database 
can be configured for testing and another for production use).


    mysql lepidoptera -p < docs_manual/sql/mysql_ver1.2.2.sql

Once this database has been created, you'll need to create a user that the
application will use to connect to the database, that is (in the default configuration) a user LEPIDOPTERA 
with select/insert/update/delete privileges on the lepidoptera schema on localhost.

    grant select, insert, update, delete on lepidoptera.* to 'LEPIDOPTERA'@'localhost';     

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

Allowed to change a record when: Record is in state Taxon Entered or Verbatim Entered.

Barcode policy: Barcode must exist and must be unique.

Overwrite policy: Will overwrite an existing value of verbatimUnclassifiedText.

Questions policy: Any value provided in questions will be appended to the existing value for questions.

Status when complete policy:  Record is in state Verbatim Transcribed.
    
This functionality is expected to change in future versions.

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

Allowed to change a record when: Record is in state Taxon Entered or Verbatim Entered.

Barcode policy: Barcode must exist and must be unique.

Overwrite policy: Does not overwrite any existing values.

Questions policy: Any value provided in questions will be appended to the existing value for questions.

Status when complete policy:  Record is in state Verbatim Transcribed.

This functionality is expected to change in future versions.

## Transcription of (pin) label data with interpretation.

If you export the barcode number for a specimen and an image file with pin 
label data for that specimen, values for any of the fields listed below can be
loaded back into the database in a csv file that contains a column "barcode", 
and optionally a column "questions".  To do this, construct a csv file
containing the data to be ingested barcode and any the columns below, and select 
Action/Load Data from the main menu.  Column names are case sensitive. Exactly 
these columns must be present (no more, no fewer), but column order does not matter. 

    "barcode","Higher_Geography","SpecificLocality","questions"

* TypeStatus
* TypeNumber
* CitedInPublication
* Features
* Higher_Geography
* SpecificLocality
* VerbatimLocality
* VerbatimCollector
* VerbatimCollection
* VerbatimNumbers
* VerbatimUnclassifiedText
* Minimum_Elevation
* Maximum_Elevation
* Elev_Units
* CollectingMethod
* ISODate 
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
* Questions
* Inferences
* LocationInCollection
* ValidDistributionFlag

Allowed to change a record when: Record is in state Taxon Entered, Verbatim Entered, (Verbatim Classified??).

Barcode policy: Barcode must exist and must be unique.

Overwrite policy: Will overwrite any existing value in the Verbatim fields, but will not overwrite any existing value in any non-Verbatim field.

Questions policy: Any value provided in questions will be appended to the existing value for questions.

Status when complete policy:  If any non-verbatim field is present, Verbatim Classified, othewise, verbatimEntered.

This functionality is expected to change in future versions.
