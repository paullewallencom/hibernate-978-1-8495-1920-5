This directory represents the state of the VAPORware Marketplace example application as 
of the end of the seventh chapter.  It expands upon the previous chapters to demonstrate 
the application making use of a clustered server environment and index sharding.

This codebase is structured as a Maven-based project (http://maven.apache.org).  If your 
IDE of choice offers Maven support, then you may import this project in the appropriate 
manner.  Otherwise, if you have Maven installed on your system, you may build and test 
the application from the command-line by changing to this directory and typing:

mvn clean compile war:exploded test

The Maven "clean" goal removes working files from any previous builds.  The "compile" and 
"war:exploded" goals build the web application, and the "test" goal executes the 
"com.packtpub.hibernatesearch.domain.test.ClusterTest" class located under "src/test/java".

In contrast to the single-node Maven Jetty plugin used in previous chapters, the "ClusterTest" 
class initializes a two-node cluster of Jetty servers... to demonstrate Hibernate Search 
running with master and slave directory providers and a JMS backend.  You can access the 
either running node by pointing your web browser to:

http://localhost:8080/  ("master" node)
http://localhost:8181/  ("slave" node)
