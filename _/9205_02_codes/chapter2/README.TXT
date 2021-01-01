This directory represents the state of the VAPORware Marketplace example application as 
of the end of the second chapter.  It expands upon the previous chapter to show mapping 
of associated entities and embedded objects, and partial indexing of specific fields.

This codebase is structured as a Maven-based project (http://maven.apache.org).  If your 
IDE of choice offers Maven support, then you may import this project in the appropriate 
manner.  Otherwise, if you have Maven installed on your system, you may build and test 
the application from the command-line by changing to this directory and typing:

mvn clean jetty:run

The Maven "clean" goal removes working files from any previous builds.  The "jetty:run" goal 
builds the application and launches it with an embedded Jetty web server (http://www.eclipse.org/jetty) 
for testing.  You can access the running application by pointing your web browser to:

http://localhost:8080/
