@echo off

setlocal

rem
rem Copyright (C) 2007 Funambol, Inc.  All rights reserved.
rem
rem  Before running this script:
rem  - set the JAVA_HOME
rem

set CURRENT_DIR=%cd%

set CMD_HOME=%~dp0..

cd %CMD_HOME%\lib
set CLASSPATH=%CMD_HOME%\lib
for %%i in (*.jar) do call :append %%i
goto okClasspath
:append
set CLASSPATH=%CLASSPATH%;%CMD_HOME%\lib\%*

goto :eof

:okClasspath
cd %CURRENT_DIR%

if not "%JAVA_HOME%" == "" goto verifyJavaHome
echo ERROR: Please, set JAVA_HOME before running this script.
goto END

:verifyJavaHome
if EXIST "%JAVA_HOME%\bin\java.exe" goto okJavaHome
echo ERROR: Set JAVA_HOME to the path of a valid jre.
goto END

:okJavaHome
cd %CMD_HOME%
%JAVA_HOME%\bin\java com.funambol.syncclient.cl.Main %*

:end
cd %CURRENT_DIR%

endlocal