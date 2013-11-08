KLab
====
Project aimed at analysing diffusion experiment data using Scala/Java.

Roadmap
-------
 - Develop import code from external video trackers
 - Create sophisticated track reconstruction algorithm
 - Provide all essential analysis tools

Usage
-----
Start app by typing 'java -jar KLab.jar -t'. Where flag -t indicates that terminal version should be started.
If libraries are stored outside the main JAR file one should run it via: 'todo'

Full documentation to be done.

Useful SBT compiler commands
----------------------------
 - 'compile' compiles code
 - 'assembly' generate KLab.jar runnable file
 - 'assembly-package-dependency' generates separate .jar file for the libraries used
 - 'gen-idea' generates library bindings with intellij IDEA
 - 'doc' generate scaladocs

Authors
-------
Karolis Misiunas
km558@cam.ac.uk
k.misiunas@gmail.com