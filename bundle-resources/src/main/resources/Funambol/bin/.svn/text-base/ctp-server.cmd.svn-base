@echo off

setlocal

REM
REM  Copyright (C) 2007 Funambol, Inc.  All rights reserved.
REM
REM  Environment Variable prerequisites
REM
REM    JAVA_HOME   (Optional) May point at your jdk/jre installation. If not set,
REM                or not correctly set, the jre embedded in the bundle is used
REM
REM    JAVA_OPTS   (Optional) Java runtime options
REM
REM    MEM_OPTS    (Optional) Memory options, for instance -Xmx256M. It is used only
REM                with start command. Default value -Xmx256M.
REM
REM  Set the JMX_PORT variable below to a different value if the one specified is
REM  already in use.
REM

set JMX_PORT=7101

set SCRIPT_NAME=%~nx0
set FUNAMBOL_HOME=%~dp0..
set CTP_SERVER_HOME=%FUNAMBOL_HOME%\ctp-server

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

REM setting classpath
REM ------------------
cd %CTP_SERVER_HOME%\lib
set CLASSPATH=%CTP_SERVER_HOME%\lib
for %%i in (*.jar) do call :append %%i
goto okClasspath
:append
set CLASSPATH=%CLASSPATH%;%CTP_SERVER_HOME%\lib\%*
goto :eof

:okClasspath
cd ..

IF "%MEM_OPTS%" == "" (
    set MEM_OPTS=-Xmx256M
)

set JAVA_OPTS=%JAVA_OPTS% %MEM_OPTS%
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set JAVA_OPTS=%JAVA_OPTS% -Dfunambol.home="%FUNAMBOL_HOME%"
set JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true

if ""%1"" == ""start""    goto doStart
if ""%1"" == ""stop""     goto doStop
if ""%1"" == ""license""  goto doLicense

goto printUsage

:doStart

if ""%2"" == """" (
    set EXEC=start "CTPServer" "%JAVA_HOME%\bin\javaw.exe"
) else (
    if ""%2"" == ""-debug"" (
      if ""%3"" == """" (
          set EXEC="%JAVA_HOME%\bin\java.exe"
      ) else (
          goto printUsage
      )
    ) else (
        goto printUsage
    )
)

set CLASS_NAME=com.funambol.ctp.server.CTPServer

set JAVA_OPTS=%JAVA_OPTS% -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=%JMX_PORT% -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false
set JAVA_OPTS=%JAVA_OPTS% -Djava.rmi.server.hostname=%COMPUTERNAME%

rem Uncomment the following line to enable remote debug
rem set JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,address=7777,server=y,suspend=n

goto CONT1

:doStop
if not ""%2"" == """" (
    goto printUsage
)
set EXEC="%JAVA_HOME%\bin\java.exe"
set CLASS_NAME=com.funambol.ctp.server.management.ManagementTools
set ARGS=localhost %JMX_PORT% stop
goto CONT1

:doLicense
type "%FUNAMBOL_HOME%"\LICENSE.txt | more
goto END

:printUsage
echo Usage: %SCRIPT_NAME% command
echo command:
echo   start           Start CTPServer
echo   start -debug    Start CTPServer with output console
echo   stop            Stop  CTPServer
echo   license         Show the license
goto END

:CONT1
echo CTP_SERVER_HOME: "%CTP_SERVER_HOME%"
echo JAVA_OPTS: %JAVA_OPTS%

%EXEC% %JAVA_OPTS% %CLASS_NAME% %ARGS%

:END

endlocal
