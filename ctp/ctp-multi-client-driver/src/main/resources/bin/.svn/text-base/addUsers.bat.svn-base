@echo off

rem
rem Copyright (C) 2007 Funambol, Inc.  All rights reserved.
rem

setlocal
set CURRENT_DIR=%cd%
set MULTI_CLIENT_DRIVER_HOME=%CURRENT_DIR%
if exist "%MULTI_CLIENT_DRIVER_HOME%\lib" goto okHome
cd ..
set MULTI_CLIENT_DRIVER_HOME=%cd%

:okHome
cd %MULTI_CLIENT_DRIVER_HOME%\lib
set CLASSPATH=%MULTI_CLIENT_DRIVER_HOME%\lib
for %%i in (*.jar) do call :append %%i
goto okClasspath
:append
set CLASSPATH=%CLASSPATH%;%MULTI_CLIENT_DRIVER_HOME%\lib\%*
goto :eof

:okClasspath
set CLASSPATH=%CLASSPATH%;%MULTI_CLIENT_DRIVER_HOME%\config
cd %CURRENT_DIR%

java com.funambol.ctp.client.driver.AddUsers %*
endlocal
