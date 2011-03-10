@echo off

rem
rem Copyright (C) 2007 Funambol, Inc.  All rights reserved.
rem
setlocal
set CURRENT_DIR=%cd%
set SENDER_DRIVER_HOME=%CURRENT_DIR%
if exist "%SENDER_DRIVER_HOME%\lib" goto okHome
cd ..
set SENDER_DRIVER_HOME=%cd%

:okHome
cd %SENDER_DRIVER_HOME%\lib
set CLASSPATH=%SENDER_DRIVER_HOME%\lib
for %%i in (*.jar) do call :append %%i
goto okClasspath
:append
set CLASSPATH=%CLASSPATH%;%SENDER_DRIVER_HOME%\lib\%*
goto :eof

:okClasspath
set CLASSPATH=%CLASSPATH%;%SENDER_DRIVER_HOME%\config\
cd %CURRENT_DIR%

java com.funambol.ctp.sender.driver.MainMenu
endlocal