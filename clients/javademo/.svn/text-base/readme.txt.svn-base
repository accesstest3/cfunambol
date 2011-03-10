Copyright (C) 2007 Funambol, Inc.

Funambol Java Demo Client readme
================================

Table of contents
------------------

1. Building
2. Creating the NSIS installer

1. Building
-----------

To clean:
  mvn clean

To compile:
  mvn compiler:compile

To create the unix/zip packages:
  mvn package

To create the windows packages (see next section):
  mvn package exec:exec

To install the artifacts:
  mvn install


2. Creating the NSIS installer for Windows
-------------------------------------------

First of all install NSIS and the inetc plugin:

- download NSIS from here http://nsis.sourceforge.net/Main_Page
- download the plugin from here http://nsis.sourceforge.net/Inetc_plug-in
- extract inetc.dll into NSIS\Plugins

Then add to one of the active profiles in %USER_HOME%/.m2/settings.xml the following property:

<properties>
   <nsis.home>... NSIS path ...</nsis.home>
</properties>

where the NSIS path is the directory where makesis.exe is located.
If you prefer, you can set the nsis.home as environment variable with:
  set nsis.home=<NSIS path>
  
The NSIS path can be set also as system property running maven.

To create the setup program run:
  mvn package exec:exec
  
To create the setup program specifying the NSIS path on the command line run:
  mvn package exec:exec -Dnsis.home=NSISPath
