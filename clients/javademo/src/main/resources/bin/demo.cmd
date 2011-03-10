@echo off

setlocal

rem
rem Copyright (C) 2007 Funambol, Inc.  All rights reserved.
rem
set CMD_HOME=%~dp0..

REM
REM BUNDLED_JAVA_HOME is set to the JRE home distributed in the bundle. If this
REM JDK is not found JAVA_HOME will be used.
REM
set BUNDLED_JAVA_HOME=%CMD_HOME%\..\..\tools\jre-1.6.0\jre
if EXIST "%BUNDLED_JAVA_HOME%" set JAVA_HOME=%BUNDLED_JAVA_HOME%

cd %CMD_HOME%\lib
set CLASSPATH=%CMD_HOME%\lib
for %%i in (*.jar) do call :append %%i
goto okClasspath
:append
set CLASSPATH=%CLASSPATH%;%CMD_HOME%\lib\%*
goto :eof

:okClasspath
cd %CMD_HOME%

start launchw %JRE% -J-Dfile.encoding=UTF-8 -J-Dwd="%CMD_HOME%" -cp:p "%CLASSPATH%" -mc com.funambol.syncclient.javagui.Client %*

endlocal
