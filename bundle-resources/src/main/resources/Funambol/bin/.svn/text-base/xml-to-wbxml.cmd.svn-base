@echo off
setlocal

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

if EXIST "%JAVA_HOME%\bin\java.exe" goto CONT0
echo . ==================================================
echo .
echo . Please, set JAVA_HOME to the path of a valid jdk.
echo .
echo . ==================================================
goto END
:CONT0

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


"%JAVA_HOME%\bin\java.exe" com.funambol.tools.test.WBXMLConverter -xml %*

:END
endlocal