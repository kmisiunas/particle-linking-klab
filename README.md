KLab
====
Project aimed at analysing diffusion experiment data using Scala/Java.

Roadmap
-------
 - Develop import code from external video trackers
 - Create sophisticated track reconstruction algorithm
 - Provide all essential analysis tools

Starting
--------
Start app by typing 'java -jar KLab.jar -t'. Where flag -t indicates that terminal version should be started.
If libraries are stored outside the main JAR file one should run it via: 'java -cp "KLab.jar:KLab-assembly-0.1.4-deps.jar" klab.klab'

Basic Usage
-----------
The simplest way to get started is with 'Run()' command. It runs an automated analysis script.
If 'Run()' is typed it will display all known scrips - this includes packaged ones with the software and also ones in
'/scripts/' folder. An example is scrip would be 'Run("example_script")'.

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
 - JVM 1.6 or 1.7
 - Recommended to have BLAS or LAPACK installed
 - Scala 2.10 (for compilation)
 - SBT 0.13 (for compilation)

Authors
-------
Karolis Misiunas
km558@cam.ac.uk
k.misiunas@gmail.com