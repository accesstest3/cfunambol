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
REM                with start command. Default value -Xmx256M.
REM

set SCRIPT_NAME=%~nx0
set FUNAMBOL_HOME=%~dp0..

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

cd ..

set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set CLASSPATH=%FUNAMBOL_HOME%\tools\hypersonic\lib\hsqldb.jar

if ""%1"" == ""start""    goto doStart
if ""%1"" == ""manager""  goto doManager
if ""%1"" == ""stop""     goto doStop

goto printUsage

:doStart
if ""%2"" == """" (
    set EXEC=start "Hypersonic" "%JAVA_HOME%\bin\javaw.exe"
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

set CLASS_NAME=org.hsqldb.Server
set ARGS=-database.0 "%FUNAMBOL_HOME%\tools\hypersonic\data\funambol" -dbname.0 funambol

IF "%MEM_OPTS%" == "" (
    set MEM_OPTS=-Xmx256M
)

set JAVA_OPTS=%JAVA_OPTS% %MEM_OPTS%

rem Uncomment the following line to enable remote debug
rem set JAVA_OPTS=%JAVA_OPTS% -Xdebug -Xrunjdwp:transport=dt_socket,address=7777,server=y,suspend=n

goto CONT1

:doManager
if not ""%2"" == """" (
    goto printUsage
)
set EXEC=start "Hypersonic Database Manager" "%JAVA_HOME%\bin\javaw.exe"
set CLASS_NAME=org.hsqldb.util.DatabaseManager
set ARGS=--user sa --url jdbc:hsqldb:hsql://localhost/funambol
goto CONT1

:doStop
if not ""%2"" == """" (
    goto printUsage
)
set EXEC="%JAVA_HOME%\bin\java.exe"
set CLASS_NAME=org.hsqldb.util.ShutdownServer
set ARGS=-port -user sa -url jdbc:hsqldb:hsql://localhost/funambol -shutdownarg IMMEDIATELY
goto CONT1

:printUsage
echo Usage: %SCRIPT_NAME% command
echo command:
echo   start           Start Hypersonic
echo   start -debug    Start Hypersonic with output console
echo   manager         Start Hypersonic database manager
echo   stop            Stop  Hypersonic
goto END

:CONT1
echo FUNAMBOL_HOME: "%FUNAMBOL_HOME%"
echo JAVA_OPTS: %JAVA_OPTS%

%EXEC% %JAVA_OPTS% -classpath "%CLASSPATH%" %CLASS_NAME% %ARGS%

:END

endlocal
