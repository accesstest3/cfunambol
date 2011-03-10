@echo off

setlocal
echo Restarting Funambol...

set FUNAMBOL_HOME=%~dp0..

set JAVA_HOME=%FUNAMBOL_HOME%\tools\jre-1.6.0\jre
set J2EE_HOME=%FUNAMBOL_HOME%\tools\tomcat

if ""%1"" == ""license""  goto doLicense

REM
REM Shutdown Inbox listener
REM
call "%FUNAMBOL_HOME%\bin\inbox-listener.cmd" stop

REM
REM Shutdown PIM listener
REM
call "%FUNAMBOL_HOME%\bin\pim-listener.cmd" stop

REM
REM Shutdown CTP Server
REM
call "%FUNAMBOL_HOME%\bin\ctp-server.cmd" stop

rem
rem Shutdown Tomcat
rem
call "%FUNAMBOL_HOME%\bin\funambol-server.cmd" stop

rem
rem Shutdown Hypersonic
rem
call "%FUNAMBOL_HOME%\bin\hypersonic.cmd" stop

REM
REM Start all
REM

REM
REM Run Hypersonic
REM
call "%FUNAMBOL_HOME%\bin\hypersonic.cmd" start

REM
REM Run DS Server
REM
call "%FUNAMBOL_HOME%\bin\funambol-server.cmd" start
cd "%FUNAMBOL_HOME%\bin"
start fnblstatus.exe "%FUNAMBOL_HOME%"

REM
REM Run CTP Server
REM
call "%FUNAMBOL_HOME%\bin\ctp-server.cmd"  start

REM
REM Run Inbox listener
REM
call "%FUNAMBOL_HOME%\bin\inbox-listener.cmd" start

REM
REM Run PIM listener
REM
call "%FUNAMBOL_HOME%\bin\pim-listener.cmd" start

goto END

:doLicense
type "%BUNDLE_HOME%"\LICENSE.txt | more
goto END

:END
endlocal
