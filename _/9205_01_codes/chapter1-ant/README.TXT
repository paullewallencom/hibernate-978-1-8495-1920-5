This directory represents the state of the VAPORware Marketplace example application as 
of the end of the first chapter.  It demonstrates a single entity being mapped with JPA-style
annotations, and queried using a Hibernate session-based style.

This is a variant of the main "chapter1" application, structured for Ant (http://ant.apache.org)
and Ivy (http://ant.apache.org/ivy) rather than Maven.  If your IDE of choice offers Ant support, then 
you may import this project in the appropriate manner.  Otherwise, if you have Ant installed on your system, 
you may build and test the application from the command-line by changing to this directory and typing:

ant

The default Ant target will configure Ivy if necessary, use Ivy to fetch all of the application's 
dependencies, compile the application, and then run it in a Jetty test server.  Unfortunately, the 
Jetty plugin for Ant is not quite as sophisticated as the plugin for Maven, so this project has to 
contain a "jetty-lib" subdirectory with pre-existing Jetty binaries (the Maven plugin does not require
Jetty binaries to be bundled with the source code).

You can access the running application by pointing your web browser to:

http://localhost:8080/
