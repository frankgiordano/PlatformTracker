# Platform Tracker
A rudimentary helpdesk type application. 

![Demo](https://github.com/frankgiordano/PlatformTracker/blob/master/demo/demo2.gif)

The following project was developed in 2015. It was my first of the following tech stack:  
  
	Java   
	Spring MVC  
	JPA  
	AngularJS  
	JavaScript  
	HTML  
	Bootstrap  
  
application that I developed. This was used as a starter project to build up knowledge with the tech stack for future projects. This was a short lived application used to mostly provide some way to track incoming product related issues in a very custom/niche way.  

The goal of this application was to build an Incident management system to track incidents related to a set of products that a company
may have deployed to production. From the incidents, trends can be identifed which may lead to root cause solutions and enhancement projects. As such, this application has a very small project management component. 

You may find this application useful and you are welcome to use it. The basic incident management provided may be generic enough to adapt for any incident tracking needs. 

I posted this project for educational proposes, and I intend to continue to polish and work out all the kinks on my spare time. I may evolve the project as a place to learn and experiment. 

## Setup, Installation, and running the application:

1 - Install Java 8

2 - Install Maven 3.6.3 or higher

3 - Install Apache Tomcat 7.0.99 or higher

4 - Within tomcat's conf directory location for example: C:\Users\giofr\Downloads\tools\apache-tomcat-7.0.99\conf, 
    edit context.xml file and add the following resource xml tag info between context xml tag:

	<Resource name="jdbc/plattrk"
	auth="Container"
	type="javax.sql.DataSource"
	factory="org.apache.tomcat.jdbc.pool.DataSourceFactory"
	username="xxxxxx"
	password="xxxxxx"
	driverClassName="com.mysql.jdbc.Driver"
	url="jdbc:mysql://localhost:3306/plattrk"
	maxActive="15"
	maxIdle="3"/>
    
5 - Install MySql server 8

6 - Within MySql Workbench create db schema named 'plattrk'

7 - Update tomcat's context.xml file with MySql root's username and password defined during MySql installation, see step 4, replace the xxxxxx entries. 

8 - Git clone this project to your local directory

9 - Edit the project's pom.xml file and add in your tomcat's webapps directory location within outputDirectory xml tag, for example:

	<outputDirectory>C:\Users\giofr\Downloads\tools\apache-tomcat-7.0.99\webapps</outputDirectory>

	NOTE: This line may be commented out; it is commented out because the project is currently set  
	to use the "Alernate build and deploy method" described in the next section of this readme.  
	For now, go ahead and uncomment this line and make the change as noted.  

10 - Edit the project's persistence.xml file (src/main/resources/META-INF/persistence.xml) and set the following variable to 'create' mode

	<property name="hibernate.hbm2ddl.auto" value="create" />
	
11 - From the project's root directory, copy the following file mysql-connector-java-8.0.18 to tomcat's lib directory if not already there. 	

12 - At project's root directory, build project's war deliverable by executing the following command:

        mvn clean install
  
        Note: before build you will need to delete the plattrk directory under tomcat's webapps directory if it exists 	

13 - From tomcat's bin directory, execute the following command to deploy the war deliverable within the tomcat server's webapps directory

	startup

14 - From the browser, type in the following url http://localhost:8080/plattrk

15 - To login into the application UI enter in username as 'guest' and password as 'password'

NOTE: You might run into the following issue https://stackoverflow.com/questions/24838298/java-sql-sqlexception-access-denied-for-user-rootlocalhostlocalhost-usin/24838420. If so, use the ALTER command noted in the link. 

## Alernate build and deploy method:

1 - Add the following to tomcat's tomcat-users.txt file located in tomcat's conf directory:

	<?xml version='1.0' encoding='utf-8'?>
	<tomcat-users>
  		<role rolename="manager-script"/>
  		<role rolename="manager-gui"/>
  		<user username="admin" password="admin" roles="manager-gui,manager-script"/>
	</tomcat-users>

2 - Add the following to maven's settings.xml file located in maven's conf directory:

	<server>
		<id>TomcatServer</id>
		<username>admin</username>
		<password>admin</password>
	</server>

3 - Check the project's pom.xml and make sure the following are set in org.apache.tomcat.maven plugin under <configuration> tag:

	<username>admin</username>
	<password>admin</password>

    If not there please add.

4 - From tomcat's bin directory, execute the following command to deploy the war deliverable within the tomcat server

	startup

5 - Now you can automatcially deploy to tomcat without the need to delete the project directory in tomcat, see step 11

	mvn clean install (optional - if you need this check your IDE's .settings and .classpath settings are Java 1.8 specified)
	mvn tomcat7:deploy
	mvn tomcat7:undeploy
