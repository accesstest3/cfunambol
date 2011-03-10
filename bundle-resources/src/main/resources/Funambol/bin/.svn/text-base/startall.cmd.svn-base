@echo off

setlocal
echo Starting Funambol...

set FUNAMBOL_HOME=%~dp0..

set JAVA_HOME=%FUNAMBOL_HOME%\tools\jre-1.6.0\jre
set J2EE_HOME=%FUNAMBOL_HOME%\tools\tomcat

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
call "%FUNAMBOL_HOME%\bin\ctp-server.cmd" start

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
