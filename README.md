KLab
====
Project aimed at analysing diffusion experiment data using Scala/Java.

Roadmap
-------
 - [x] Develop import code from external video trackers
 - [ ] Create sophisticated track reconstruction algorithm (urgent!)
 - [x] Full integration with breeze and universality of analysis results
 - [x] JSON as iterator
 - [ ] Automatic buffering Assembly for very large data sets
 - [ ] Redesign Assembly
 - [ ] universal id system
 - [x] JSON save LQPos points
 - [ ] Integration with Mathematica for general analysis - use Mathematica's plotting

Self Comments
-------------


Starting
--------
Start app by  double clicking the 'KLab.jar' file, it will give an option to copy the command that should be pasted into
Console, Terminal or PowerShell. Flag -t indicates that terminal version should be started.
If libraries are stored outside the main JAR file one should run it via: 'java -cp "KLab.jar:KLab-assembly-0.1.4-deps.jar" klab.klab -t'

Basic Usage
-----------
The simplest way to get started is with 'Run()' command. It runs an automated analysis script.
If 'Run()' is typed it will display all known scrips - this includes packaged ones with the software and also ones in
'/scripts/' folder. An example is scrip would be 'Run("example_script")'.

Useful SBT compiler commands
----------------------------
 - 'compile' compiles the code
 - 'assembly' generate KLab.jar runnable file
 - 'pack' a quick way of packing klab, where libraries are left unchanged
 - 'gen-idea' generates library bindings with intellij IDEA
 - 'doc' generate scaladocs
 - 'clean' helps to remove sbt compilation artifacts

Requirements
------------
 - JVM 1.6 or 1.7
 - Recommended to have BLAS or LAPACK installed
 - Scala 2.10.3 (for compilation)
 - SBT 0.13 (for compilation)

Authors
-------
Karolis Misiunas
k.misiunas@gmail.com