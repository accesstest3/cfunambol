@echo off

setlocal

REM
REM  Copyright (C) 2008 Funambol, Inc.  All rights reserved.
REM
REM  Environment Variable prerequisites
REM
REM    JAVA_HOME   (Optional) May point at your jdk/jre installation. If not set,
REM                or not correctly set, the jre embedded in the bundle is used
REM
REM    JAVA_OPTS   (Optional) Java runtime options
REM
REM    MEM_OPTS    (Optional) Memory options, for instance -Xmx256M. It is used only
REM                with start command. Default value -Xmx512M.
REM
REM   Set the JMX_PORT variable below to a different value if the one specified is
REM   already in use.
REM

set JMX_PORT=8101

REM
REM Unset Tomcat environment variables, 
REM so that Funambol Server can overwrite them with the right configuration. 
REM
set CATALINA_BASE=
set CATALINA_HOME=
set CATALINA_TMPDIR=

set SCRIPT_NAME=%~nx0
set FUNAMBOL_HOME=%~dp0..
set DS_SERVER_HOME=%FUNAMBOL_HOME%\ds-server
set J2EE_HOME=%FUNAMBOL_HOME%\tools\tomcat

rem Setting the JAVA_HOME to the JRE in the bundle if not set or if not correctly set
IF "%JAVA_HOME%" == "" (
    set JAVA_HOME=%FUNAMBOL_HOME%\tools\jre-1.6.0\jre
) ELSE (
    IF NOT EXIST "%JAVA_HOME%\bin\java.exe" (
        set JAVA_HOME=%FUNAMBOL_HOME%\tools\jre-1.6.0\jre
    )
)

if not "%JAVA_HOME%" == "" goto verifyJavaHome
echo ERROR: Please, set JAVA_HOME before running this script.
goto END

:verifyJavaHome
if EXIST "%JAVA_HOME%\bin\java.exe" goto okJavaHome
echo ERROR: Set JAVA_HOME to the path of a valid jre.
goto END

:okJavaHome

if ""%1"" == ""start""    goto doStart
if ""%1"" == ""stop""     goto doStop
if ""%1"" == ""license""  goto doLicense

goto printUsage

:doStart

IF "%MEM_OPTS%" == "" (
    set MEM_OPTS=-Xmx512M
)

set JAVA_OPTS=%JAVA_OPTS% %MEM_OPTS%

set JAVA_OPTS=%JAVA_OPTS% -Dfunambol.debug=false
set JAVA_OPTS=%JAVA_OPTS% -Dfunambol.home="%FUNAMBOL_HOME%"
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set JAVA_OPTS=%JAVA_OPTS% -Djava.library.path="%DS_SERVER_HOME%\lib\win32"
set JAVA_OPTS=%JAVA_OPTS% -Djavax.net.ssl.trustStore="%DS_SERVER_HOME%\lib\security\cacerts"
set JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=%JMX_PORT% -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false
set JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true
set JAVA_OPTS=%JAVA_OPTS% -Djava.awt.headless=true
set JAVA_OPTS=%JAVA_OPTS% -Djava.rmi.server.hostname=%COMPUTERNAME%

set JPDA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n

cd /D "%J2EE_HOME%"\bin

REM
REM Run Tomcat for normal use
REM
call catalina.bat start

REM
REM Run Tomcat for debug
REM
rem call catalina.bat jpda start

goto END

:doStop
cd /D "%J2EE_HOME%\bin"

rem
rem Shutdown Tomcat
rem
call shutdown.bat

goto END

:doLicense
type "%FUNAMBOL_HOME%"\LICENSE.txt | more
goto END

:printUsage
echo Usage: %SCRIPT_NAME% command
echo command:
echo   start           Start the server
echo   stop            Stop the server
echo   license         Show the license
goto END

:END

endlocal
