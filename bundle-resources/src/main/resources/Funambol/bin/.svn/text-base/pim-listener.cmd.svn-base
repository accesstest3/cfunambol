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
REM  Before running this script:
REM  - copy the JDBC driver in the lib directory. It will be automatically loaded.
REM
REM  Set the JMX_PORT variable below to a different value if the one specified is
REM  already in use.
REM

set JMX_PORT=3101

set SCRIPT_NAME=%~nx0
set FUNAMBOL_HOME=%~dp0..
set PIM_LISTENER_HOME=%FUNAMBOL_HOME%\pim-listener

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
cd %PIM_LISTENER_HOME%\lib
set CLASSPATH=%PIM_LISTENER_HOME%\lib
for %%i in (*.jar) do call :append %%i
goto okClasspath
:append
set CLASSPATH=%CLASSPATH%;%PIM_LISTENER_HOME%\lib\%*
goto :eof

:okClasspath
cd ..

IF "%MEM_OPTS%" == "" (
    set MEM_OPTS=-Xmx256M
)

set JAVA_OPTS=%JAVA_OPTS% %MEM_OPTS%
set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set JAVA_OPTS=%JAVA_OPTS% -Dfunambol.home="%FUNAMBOL_HOME%"
set JAVA_OPTS=%JAVA_OPTS% -Dfunambol.pushlistener.config.bean="com/funambol/pimlistener/PIMListenerConfiguration.xml"
set JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true

set JMX_BEAN=com.funambol.pimlistener:type=PIMListener

if ""%1"" == ""start""    goto doStart
if ""%1"" == ""status""   goto doStatus
if ""%1"" == ""stop""     goto doStop
if ""%1"" == ""console""  goto doConsole
if ""%1"" == ""license""  goto doLicense

goto printUsage

:doStart

if ""%2"" == """" (
    set EXEC=start "Pim Listener" "%JAVA_HOME%\bin\javaw.exe"
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

set CLASS_NAME=com.funambol.pimlistener.service.PimListener
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
set CLASS_NAME=com.funambol.pushlistener.service.ManagementTools
set ARGS=%JMX_BEAN% localhost %JMX_PORT% stop
goto CONT1

:doStatus
if not ""%2"" == """" (
    goto printUsage
)
set EXEC="%JAVA_HOME%\bin\java.exe"
set CLASS_NAME=com.funambol.pushlistener.service.ManagementTools
set ARGS=%JMX_BEAN% localhost %JMX_PORT% status
goto CONT1

:doConsole
if not ""%2"" == """" (
    goto printUsage
)
set EXEC="%JAVA_HOME%\bin\java.exe"
set CLASS_NAME=com.funambol.pimlistener.console.Menu
goto CONT1

:doLicense
type "%FUNAMBOL_HOME%"\LICENSE.txt | more
goto END

:printUsage
echo Usage: %SCRIPT_NAME% command
echo command:
echo   start           Start PimListener
echo   start -debug    Start PimListener with output console
echo   stop            Stop PimListener
echo   console         Start PimListener command line console
echo   status          Show PimListener status
echo   license         Show the license
goto END

:CONT1
echo PIM_LISTENER_HOME: "%PIM_LISTENER_HOME%"
echo JAVA_OPTS: %JAVA_OPTS%

%EXEC% %JAVA_OPTS% %CLASS_NAME% %ARGS%

:END

endlocal
