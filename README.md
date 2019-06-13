# verapdf-crawler

## Manual installation

### Pre-requisites
In order to install Logius crawler you'll need
 * Java 8, which can be downloaded [from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html), or for
 Linux users [OpenJDK](http://openjdk.java.net/install/index.html)
 * Heritrix 3 need to be installed, [web page with details](https://webarchive.jira.com/wiki/display/Heritrix)
 * veraPDF need to be installed, [you can download the latest version here](http://downloads.verapdf.org/).
 * PostgreSQL 8.x or greater need to be installed, [you can download the latest version here](https://www.postgresql.org/download/).
 * Python 2.x need to be installed [you can download the latest version here](https://www.python.org/downloads/)
 * PDFWam checker need to be installed [you can download the latest version here](https://gitlab.tingtun.no/eiii_source/pdfwam/)
 
 Note that you should preferably use the latest version of verapdf.

### Configuring Logius application
  
  * set correct properties in [application properties](LogiusWebApp/src/main/resources/application.properties)
  * set correct uuid and email for admin in [sql schema](LogiusWebApp/src/main/resources/sql/schema.sql)
   
  Also, you need to create the directory with configuration files, suppose you name it data/. You need to move files "sample_configuration.cxml" and "sample_report.ods" from "LogiusWebApp/src/main/resources/" to your "data/" directory. You need to configure the file "LogiusWebApp/src/main/resources/application.properties" accordingly.

### Configuring database
   Logius application requires connection to PostgreSQL database to store information about crawl jobs, validation jobs and processed documents. You are supposed to provide connecting parameters (connection string, username, password) in configuration file. You should be execute [database schema script](LogiusWebApp/src/main/resources/sql/schema.sql) and [pdf properties script](LogiusWebApp/src/main/resources/sql/pdf_properties_base_settings.sql)

### Starting Heritrix
You need to run Heritrix with necessary arguments:

	heritrix_directory/bin/heritrix -a your_login:your_password -p 8443

Here your_login and your_password are the credentials you should pass to LogiusWebApp afterwards.

### Installing Logius web application
You need to download and build modules LogiusWebApp and HeritrixExtention with Maven. You should run the following command from the
directory that contains both downloaded modules

	mvn clean install

After that you will get two jar files "your_directory/LogiusWebApp/target/LogiusWebApp-1.0-SNAPSHOT.jar" and "your_directory/HeritrixExtention/target/HeritrixExtention.jar". The file "your_directory/HeritrixExtention/target/HeritrixExtention.jar" should be placed in "lib/" directory in your Heritrix installation directory.

### Running Logius application
You need to run logius app with necessary arguments:

	java -jar "your_directory/LogiusWebApp/target/LogiusWebApp-1.0-SNAPSHOT.jar"
	
Default admin password: testTEST5%, if you will use application in web, dont forget change password
