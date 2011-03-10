@echo OFF
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

rem Setting the J2EE_HOME to the bundle one if not set
IF "%J2EE_HOME%" == "" (
    set J2EE_HOME=%FUNAMBOL_HOME%\tools\tomcat
)

if EXIST "%JAVA_HOME%\bin\java.exe" goto CONT1
echo . ==================================================
echo .
echo . Please, set JAVA_HOME to the path of a valid jdk.
echo .
echo . ==================================================
goto END
:CONT1

set ANT_HOME=

rem the bundle version uses Tomcat 6.0
set APPSRV=tomcat60

cd %DS_SERVER_HOME%
call ant\bin\ant -Ddo.install-modules-only=true -Ddo.install-db=true -buildfile install\install.xml -q install %*

:END
endlocal
