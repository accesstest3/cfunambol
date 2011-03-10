@echo off
REM
REM  Copyright (C) 2010 Funambol, Inc.  All rights reserved.
REM
setlocal

set SCRIPT_NAME=%~nx0
set CURRENT_DIR=%cd%
set FUNAMBOL_HOME=%~dp0..
set DS_HOME=%FUNAMBOL_HOME%\ds-server

rem Setting the JAVA_HOME to the bundle one if not set or if not correctly set
IF "%JAVA_HOME%" == "" (
    set JAVA_HOME=%FUNAMBOL_HOME%\tools\jre-1.6.0\jre
) ELSE (
    IF NOT EXIST "%JAVA_HOME%\bin\java.exe" (
        set JAVA_HOME=%FUNAMBOL_HOME%\tools\jre-1.6.0\jre
    )
)

if EXIST "%JAVA_HOME%\bin\java.exe" goto CONT1
echo . ==================================================
echo .
echo . Please, set JAVA_HOME to the path of a valid jre.
echo .
echo . ==================================================
goto END

:CONT1
if ""%1"" == """" (
    goto printUsage
)
if ""%2"" == """" (
    goto printUsage
)
goto patch

:printUsage
echo Usage: %SCRIPT_NAME% old-file new-file
echo example: %SCRIPT_NAME% core-framework somewhere\core-framework-8.8.2.jar
goto END

:patch
IF NOT EXIST "%2" (
    echo ERROR: File %2 not found
    goto END
)
    
set ANT_HOME=
call %DS_HOME%\ant\bin\ant -buildfile %FUNAMBOL_HOME%\bin\patch.xml -Dbasedir=%CURRENT_DIR% -Ddir=%FUNAMBOL_HOME% -Dold-file=%1 -Dnew-file=%2 %3 %4 -q patch-file

:END
endlocal


