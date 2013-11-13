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
If libraries are stored outside the main JAR file one should run it via: 'java -cp "KLab.jar:KLab-assembly-0.1.4-deps.jar" klab.klab'

Useful SBT compiler commands
----------------------------
 - 'compile' compiles the code
 - 'assembly' generate KLab.jar runnable file
 - 'assemblyPackageDependency' generates separate .jar file for the libraries used
 - 'gen-idea' generates library bindings with intellij IDEA
 - 'doc' generate scaladocs
 - 'clean' helps to remove sbt compilation artifacts

Requirements
------------
 - Scala 2.10
 - SBT 0.13
 - Recommended to have BLAS or LAPACK installed

Authors
-------
Karolis Misiunas
km558@cam.ac.uk
k.misiunas@gmail.com