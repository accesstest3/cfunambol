@echo off

REM
REM  Copyright (C) 2007 Funambol, Inc.  All rights reserved.
REM

REM
REM Set JDBC_JAR to the absolute path of your jdbc drive jar file(s).
REM That/those files will be added to the CLASSPATH
REM

setlocal

set JDBC_JAR=

set FUNAMBOL_HOME=%~dp0..
set DS_SERVER_HOME=%FUNAMBOL_HOME%\ds-server

rem Setting the JAVA_HOME to the bundle one if not set or if not correctly set
IF "%JAVA_HOME%" == "" (
    set JAVA_HOME=%FUNAMBOL_HOME%\tools\jre-1.6.0\jre
) ELSE (
    IF NOT EXIST "%JAVA_HOME%\bin\java.exe" (
        set JAVA_HOME=%FUNAMBOL_HOME%\tools\jre-1.6.0\jre
    )
)

:verifyJavaHome
if EXIST "%JAVA_HOME%\bin\java.exe" goto okJavaHome
echo . ==================================================
echo .
echo . Please, set JAVA_HOME to the path of a valid jdk.
echo .
echo . ==================================================
goto END

:okJavaHome

REM setting classpath
REM ---------
cd %DS_SERVER_HOME%\default\lib
set CLASSPATH=%DS_SERVER_HOME%\default\lib
for %%i in (*.jar) do call :append %%i
goto okClasspath
:append
set CLASSPATH=%CLASSPATH%;%DS_SERVER_HOME%\default\lib\%*
goto :eof

:okClasspath
cd ..\..

set CLASSPATH="%JDBC_JAR%;%FUNAMBOL_HOME%\bin\log4j.properties;%FUNAMBOL_HOME%\bin;%CLASSPATH%"

set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set JAVA_OPTS=%JAVA_OPTS% -Dfunambol.home="%FUNAMBOL_HOME%"
set JAVA_OPTS=%JAVA_OPTS% -Djava.library.path="%DS_SERVER_HOME%\lib\win32"

"%JAVA_HOME%\bin\java.exe" %JAVA_OPTS% com.funambol.server.util.PasswordSetter admin

:END
endlocal
