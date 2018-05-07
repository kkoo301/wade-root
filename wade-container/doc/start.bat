@echo off
SETLOCAL ENABLEDELAYEDEXPANSION 
set PORT=7002
set SERVER_NAME=web-10.143.186.51
rem set RES_BASE=E:\Work\Workspace\J2EEcrm\tjin\apps\secserv\html
rem set RES_BASE=E:\Work\Workspace\wade3.5_merge\apps\quickstart\web
rem set RES_BASE=E:\Work\Workspace\J2EEcrm\hnan\apps\quickstart\html
set RES_BASE=E:\Work\Workspace\J2EEcrm\yunn\quickstart\CustomerCentre\html
set CONTEXT_PATH=/CustomerCentre
set CONFIG_HOME=%RES_BASE%\WEB-INF\classes
set LIB_HOME=%RES_BASE%\WEB-INF\lib

set MEM_OPTS=-Xms128m -Xmx512m -XX:MaxPermSize=128m
set CP=%CONFIG_HOME%;%LIB_HOME%\wade-container.jar

set JAVA_OPTS=-XX:+HeapDumpOnOutOfMemoryError -Djava.net.preferIPv4Stack=true
rem set JAVA_OPTS=%JAVA_OPTS% -Dfile.encoding=UTF-8
set JAVA_OPTS=%JAVA_OPTS% -Dwade.container.resourceBase=%RES_BASE% -Dwade.container.contextPath=%CONTEXT_PATH% -Dwade.container.nio=true -Dwade.container.ssl=true
set JAVA_OPTS=%JAVA_OPTS% -Dwade.container.sslKeyStorePath=C:\Users\Shieh\www.wadecn.com.jks -Dwade.container.sslKeyStorePassword=OBF:18jj18xr19bz19q719q719bz18xr18jj -Dwade.container.sslKeyManagerPassword=OBF:18jj18xr19bz19q719q719bz18xr18jj
set JAVA_OPTS=%JAVA_OPTS% -Dwade.container.minThreads=50 -Dwade.container.maxThreads=100 -Dwade.container.acceptors=2 -Dwade.container.acceptQueueSize=50
set JAVA_OPTS=%JAVA_OPTS% -Duser.dir=D:\temp -Djava.io.tmpdir=D:\temp
set JAVA_OPTS=%JAVA_OPTS% -Dcom.wade.container.util.log.class=com.wade.container.util.log.StdErrLog -D{classref}.LEVEL=INFO

rem for /R "%LIB_HOME%" %%s in (/f *.jar) do (
  rem for /f "delims=" %%i in ('dir %LIB_HOME%\*.jar /b') do (
  rem call :cpappend %%i
rem    call :cpappend %%s
rem )

rem set CP=!CP!

rem echo CP=!CP!
echo JAVA_OPTS=%JAVA_OPTS%
echo MEM_OPTS=%MEM_OPTS%
java -server -cp %CP% %MEM_OPTS% %JAVA_OPTS% -Dwade.container.port=%PORT% -Dwade.server.name=%SERVER_NAME% com.wade.container.start.Main
pause

:cpappend
set CP=!CP!;%1
goto :eof








