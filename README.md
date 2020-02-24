# Platform Tracker
A rudimentary helpdesk type application. 

![Demo](https://github.com/frankgiordano/PlatformTracker/blob/master/demo/demo2.gif)

The following project was developed in 2015. It was my first Java/Spring MVC/JPA/AngularJS/JavaScript/HTML/Bootstrap application that I developed. This was used as a starter project to build up knowledge with the tech stack for future projects. This was a short lived application used to mostly provide some way to track incoming product related issues in a very custom/niche way.  

The goal of this application was to build an Incident management system to track incidents related to a set of products that a company
may have deployed to production. From the incidents, trends can be identifed which may lead to root cause solutions and enhancement projects. As such, this application has a very small project management component. 

You may find this application useful and you are welcome to use it. The basic incident management provided may be generic enough to adapt for any incident tracking needs. 

I posted this project for educational proposes, and I intend to continue to polish and work out all the kinks on my spare time. I may evolve the project as a place to learn and experiment. 

## Setup, Installation, and running the application:

1 - Install Java 8

2 - Install Maven 3.6.3 or higher

3 - Install Apache Tomcat 7.0.99 or higher

4 - Within tomcat's directory location for example: C:\Users\giofr\Downloads\tools\apache-tomcat-7.0.99\conf, 
    edit context.xml file and add the following reource xml tag info between context xml tag:

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

7 - Update tomcat's context.xml file with MySql root's username and password defined during MySql installation, see step 4

8 - GIT clone this project to your local directory

9 - Edit the project's pom.xml file and add in your tomcat's directory webapps location within outputDirectory xml tag, for example:

	<outputDirectory>C:\Users\giofr\Downloads\tools\apache-tomcat-7.0.99\webapps</outputDirectory>

10 - Edit the project's persistence.xml file and set the following variable to 'create' mode

	<property name="hibernate.hbm2ddl.auto" value="create" />

11 - At project's root directory, build project's war deliverable by executing the following command:

	mvn clean install

     Note: everytime you rebuild you will need to delete the plattrk directory under tomcat's webapps directory before step 12	

12 - From tomcat's bin directory, execute the following command to deploy the war deliverable within the tomcat server

	startup

13 - From the browser, type in the following url http://localhost:8080/plattrk

14 - To login into the application UI enter in username as 'guest' and password as 'password'

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
		<password>password</password>
	</server>

3 - Check the project's pom.xml and make sure the following are set in org.apache.tomcat.maven plugin under <configuration> tag:

	<username>admin</username>
	<password>admin</password>

    If not there please add.

4 - From tomcat's bin directory, execute the following command to deploy the war deliverable within the tomcat server

	startup

5 - Now you can automatcially deploy to tomcat without the need to delete the project directory in tomcat, see step 11

	mvn tomcat7:deploy
	mvn tomcat7:undeploy
