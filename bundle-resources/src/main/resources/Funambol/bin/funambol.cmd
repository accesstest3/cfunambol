@echo off

setlocal

set SCRIPT_NAME=%~nx0
set FUNAMBOL_HOME=%~dp0..

set COMED=true

if ""%1"" == ""start""   goto doStart
if ""%1"" == ""stop""    goto doStop
if ""%1"" == ""license"" goto doLicense

goto printUsage

:doStop
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

IF "%COMED%" == "true" (
    rem
    rem Shutdown Hypersonic
    rem
    call "%FUNAMBOL_HOME%\bin\hypersonic.cmd" stop
)

goto END

:doStart
REM
REM Start all
REM

IF "%COMED%" == "true" (
    REM
    REM Run Hypersonic
    REM
    call "%FUNAMBOL_HOME%\bin\hypersonic.cmd" start
)

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
type "%FUNAMBOL_HOME%"\LICENSE.txt | more
goto END

:printUsage
echo Usage: %SCRIPT_NAME% command
echo command:
echo   start    Start Funambol
echo   stop     Stop Funambol
echo   license  Show the license
goto END

:END
endlocal
