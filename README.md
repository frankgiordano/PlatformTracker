# PlatformTracker
A rudimentary helpdesk type application. 

The following project was developed in 2015. It was my first Spring MVC/AngularJS application that I developed. This was used
as a starter project to build up knowledge with the tech stack for future projects. This was a short lived application used to mostly 
provide some way to track incoming product related issues in a very custom/niche way.  

The goal of this application was to build an Incident management system to track incidents related to a set of products that a company
may have deployed to production. From the incidents, trends can be identifed which may lead to root cause solutions and enhancement projects. 
As such, this application has a very small project management component. 

Overall, this project was more of a POC and tech stack learning exercise. You may find this application useful and you are welcome to use it.
The basic incident management should be generic enough to adpated to any incident tracking you may want to do. 

I am posting this project for educational proposes, and I intend to continue to develop this project in my spare time if any :) I have areas I may
polish. I may rewrite the UI with a new version of angular, and I may rewrite the back end in kotlin. As such, I may evolve the project as a place
to learn and experiment. 

Setup, Installation, and running the application:

1 - Install Java

2 - Install Maven

3 - Install Apache Tomcat

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

5 - Install MySql server

6 - Within MySql Workbench create db schema named 'plattrk'

7 - Update tomcat's context.xml file with MySql username and password defined during MySql installation, see step 4

8 - GIT clone this project to your local directory

9 - Edit the project's pom.xml file and add in your tomcat's directory webapps location within outputDirectory xml tag, for example:

	<outputDirectory>C:\Users\giofr\Downloads\tools\apache-tomcat-7.0.99\webapps</outputDirectory>

10 - Edit the project's persistence.xml file and set the following variable to 'create' mode

	<property name="hibernate.hbm2ddl.auto" value="create" />

11 - At project's root directory, build project's war deliverable by executing the following command:

	mvn clean install

12 - From tomcat's bin directory, execute the following command to deploy the war deliverable within the tomcat server

	startup

13 - From the browser, type in the following url http://localhost:8080/plattrk

14 - To login into the application UI enter in username as 'guest' and password as 'password'
