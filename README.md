# Platform Tracker
A rudimentary helpdesk type application. 

![Demo](https://github.com/frankgiordano/PlatformTracker/blob/master/demo/demo2.gif)

The following project was initially developed in 2015. It was my first full stack project with AngularJS/Java.

Current tech stack utilized for this project:   
  
	Java   
	Spring MVC  
	Hibernate/JPA  
	AngularJS  
	JavaScript  
	HTML  
	Bootstrap
	Tomcat  
  
This was used as a starter project to build up knowledge with the tech stack for future projects. This was a short-lived application used to mostly provide some way to track an incoming product related issues in a very custom/niche way.  

The goal of this application was to build an Incident management system to track incidents related to a set of products that a company may have deployed to production. From the incidents, trends can be identified which may lead to root cause solutions and enhancement projects. As such, this application has a very small project management component. 

You may find this application useful, and you are welcome to use it. The basic incident management provided may be generic enough to adapt for any incident tracking needs. 

I posted this project for educational proposes, and I intend to continue to polish and work out all the kinks on my spare time. I may evolve the project as a place to learn and experiment. 

## For Developers

The development code work for this project is in the following locations:

    src/main/java/us/com/plattrk  
    src/webapp/WEB-INF/resources/html  
    src/webapp/WEB-INF/resources/js/custom  
    src/webapp/WEB-INF/resources/css/custom  
    src/webapp/WEB-INF/resources/doctemplates  

## Incident Email Notification System/Outline

The application will trigger an email notification to be sent to the following distributions:

      RegularDist  
      DeskTopOps    
      EscalatedDist  
      Outages (not currently used)
      WeeklyProd  
    
These distributions are set within the plattrk.properties file.   
  
An email notification is sent by the application whenever the following actions occur: 
  
      Opening an Incident.  
      Closing an Incident.    
      Adding a Chronology entry to an Incident.   
      
Each of the above notification is sent to a distribution set within the Incident's Email RCPT dropdown field.
         
An email notification can be sent when no particular actions occur. This occurs when an Incident in OPEN status is left untouched within a timeframe.
   
For the following timeframes, an email notification will be sent on any OPEN Incident EVERY:
  
      55 minutes starting since start date time or until last Chronology update. (DeskTopOps)  
      1 hour starting since start date time or until last Chronology update. (From Email RCPT dropdown in Incident Detail screen)   
      2 hours starting since start date time or until last Chronology update. (EscalatedDist)   

These notifications are meant to notify a front line team about OPEN incidents that have not been updated for a while.   
  
These notifications are not sent during off hours or the weekend.    
        
For instance, the every 55 minute notification is an early warning notification to the front line team indicating the need for an update. If no update occurs, then additional team members will be notified at the 1 hour mark for a need of an update. At this point, at the 2 hour mark if no update has occurred an escalated team will be notified. 
      
Setup the distributions accordingly. 

A daily email notification is sent summarizing any new Incidents created the previous day. 
  
A weekly email notification is sent containing a weekly report. This is controlled by a setting set within the UI Report Settings. (WeeklyProd)
     
## Setup, Installation, and running the application:

1 - Install Java 8 or 11

2 - Install Maven 3.6.3 or higher

3 - Install Apache Tomcat 8.5.57 or higher

4 - Within tomcat's conf directory location for example: C:\Users\giofr\Downloads\tools\apache-tomcat-8.5.57\conf, 
    edit context.xml file and add the following resource xml tag info between context xml tag:
    
        <Resource name="jdbc/plattrk"
                auth="Container"
                type="com.mchange.v2.c3p0.ComboPooledDataSource"
                factory="org.apache.naming.factory.BeanFactory"
                user="xxxxxx"
                password="xxxxxx"
                driverClass="com.mysql.cj.jdbc.Driver"
                jdbcUrl="jdbc:mysql://localhost:3306/plattrk"
                minPoolSize="1"
                maxPoolSize="25"
                acquireIncrement="1"
                maxStatements="15"
                idleConnectionTestPeriod="3000"
                loginTimeout="300"
                preferredTestQuery="SELECT 1"
                privilegeSpawnedThreads="true"
                contextClassLoaderSource="library"	
                />
    
5 - Install MySql server 8

6 - Within MySql Workbench create db schema named 'plattrk'

7 - Update tomcat's context.xml file with MySql root's username and password defined during MySql installation, see step 4, replace the xxxxxx entries. 

8 - Git clone this project to your local directory

9 - Edit the project's pom.xml file and add in your tomcat's webapps directory location within outputDirectory xml tag, for example:

	    <outputDirectory>C:\Users\giofr\Downloads\tools\apache-tomcat-8.5.57\webapps</outputDirectory>

	    NOTE: This line may be commented out; it is commented out because the project is currently set  
	    to use the "Alernate build and deploy method" described in the next section of this readme.  
	    For now, go ahead and uncomment this line and make the change as noted.  

10 - Edit the project's persistence.xml file (src/main/resources/META-INF/persistence.xml) and set the following variable to 'create' mode

	    <property name="hibernate.hbm2ddl.auto" value="create" />
	
11 - From the project's root directory, copy the following file mysql-connector-java-8.0.18 to tomcat's lib directory if not already there. 	

12 - At project's root directory, build project's war deliverable by executing the following command:

        mvn clean install
  
        Note: before build you may need to delete the plattrk directory under tomcat's webapps directory if it exists 	

13 - From tomcat's bin directory, execute the following command to deploy the war deliverable within the tomcat server's webapps directory

	    startup

14 - From the browser, type in the following url http://localhost:8080/plattrk

15 - If you have a dedicated IP address or domain name, you may replace localhost with it within the pom.xml file. Search for all localhost instances. 

16 - To login into the application UI enter in username as 'guest' and password as 'password' when requested. 

NOTE: You might run into the following issue https://stackoverflow.com/questions/24838298/java-sql-sqlexception-access-denied-for-user-rootlocalhostlocalhost-usin/24838420. If so, use the ALTER command noted in the link. 

## Alernate build and deploy method:

1 - Add the following to tomcat's tomcat-users.txt file located in tomcat's conf directory between tomcat-users xml tag:

        <role rolename="manager-script"/>
        <role rolename="manager-gui"/>
        <user username="admin" password="admin" roles="manager-gui,manager-script"/>
  		
  	    or replace entire file with this content:
  	    
  	    <?xml version='1.0' encoding='utf-8'?>
        <tomcat-users>
          	<role rolename="manager-script"/>
          	<role rolename="manager-gui"/>
          	<user username="admin" password="admin" roles="manager-gui,manager-script"/>
        </tomcat-users>

2 - Add the following to maven's settings.xml file located in maven's conf directory or on Linux under its root directory between servers xml tag:

	    <server>
		    <id>TomcatServer</id>
		    <username>admin</username>
		    <password>admin</password>
	    </server>

3 - Check the project's pom.xml and make sure the following are set in org.apache.tomcat.maven plugin under configuration xml tag:

	    <username>admin</username>
	    <password>admin</password>

        If not there please add.

4 - From tomcat's bin directory, execute the following command to deploy the war deliverable within the tomcat server:

	    startup
	
5 - Revert step 9 in above section. 

6 - Now you can automatically deploy to tomcat without the need to delete the project war file in tomcat's webapps directory, see step 12 in above section.

	    mvn clean install (optional - if you need this check your IDE's .settings and .classpath settings are Java 1.8 specified)
	    mvn tomcat8:deploy
	    mvn tomcat8:undeploy
