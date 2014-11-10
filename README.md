EstimationPoker
===============
Swing application for an online estimation of bid entries in lokal network. It uses hazelcast for data sharing.

Installation
===============
This is a maven project. Building is as easy as "mvn clean install"

Usage
===============
Maven builds a executable jar. You can start the application with double click or "java -jar EstimationPoker-XYZ.jar". Every team member should start a new instance on his own comp. The team members are displayed with their user names. Every member can insert one or more bid items and announce his estimation. In order to prevent anchoring, the estimation of other members on one entry remains invisible until every member posted his estimation.  

Open issues
===============
Exceptions on leaving member. As per default, hazelcast data is clustered between the members. I.e. on member exit data loss could appear. To avoid this, we probably should use data caching.
