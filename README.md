

or create a new repository on the command line

echo "# ContinousFramework" >> README.md
git init
git add README.md
git commit -m "first commit"
git remote add origin https://github.com/kirtigupta13/ContinousFramework.git
git push -u origin master


Continuing-Education-Development-Evaluation-Framework
=====================================================
Continuing Education Development Evaluation Framework

##Description

This web app provides software engineers with a platform for assessing their own skills and then identifying resources in the areas where they need to improve.


##Instructions
------------
###Database setup
Steps to regenerate project database from the provided SQL script:

1. Setup postgres by following instructions in dbSetup file attached to project [JIRA page](https://jira2.cerner.com/browse/ACADEM-5198).
2. Click on default `postgres` database.
3. Go to `plugins` and open `PSQL Console` in the main menu bar.
4. Run the provided sql scripts by running the given command:
	`\i <SQL script file location>/dbSchema.sql && \i <SQL script file location>/dbData.sql`
5. Refresh Databases section.

###Running the app
Pre-requisites:

Apache-tomcat [download latest version from http://tomcat.apache.org/download-80.cgi]
Eclipse [download eclipse from https://www.eclipse.org/home/index.php]
Maven [Download Maven from http://maven.apache.org/download.cgi]

1. Open Eclipse and import the project using File->Import->Maven->Existing Maven Project
2. Right click on the project in Eclipse and select Run As->Run in Server
3. Select the appropriate Apache Tomcat server and click Finish
