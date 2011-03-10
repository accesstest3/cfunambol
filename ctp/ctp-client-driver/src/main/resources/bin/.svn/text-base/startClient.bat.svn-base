@echo off

rem
rem Copyright (C) 2007 Funambol, Inc.  All rights reserved.
rem

setlocal
set CURRENT_DIR=%cd%
set CLIENT_DRIVER_HOME=%CURRENT_DIR%
if exist "%CLIENT_DRIVER_HOME%\lib" goto okHome
cd ..
set CLIENT_DRIVER_HOME=%cd%

:okHome
cd %CLIENT_DRIVER_HOME%\lib
set CLASSPATH=%CLIENT_DRIVER_HOME%\lib
for %%i in (*.jar) do call :append %%i
goto okClasspath
:append
set CLASSPATH=%CLASSPATH%;%CLIENT_DRIVER_HOME%\lib\%*
goto :eof

:okClasspath
set CLASSPATH=%CLASSPATH%;%CLIENT_DRIVER_HOME%\config
cd %CURRENT_DIR%

java com.funambol.ctp.client.driver.MainMenu %*
endlocal
