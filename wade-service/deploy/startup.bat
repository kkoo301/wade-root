@echo off

set LIB_HOME=.\lib
set ETC_HOME=.\classes

FOR %%F IN (%LIB_HOME%\*.jar) DO call :addcp %%F
goto extlib
:addcp
SET CP=%CP%;%1
goto :eof
:extlib	

set CLASSPATH=%CLASSPATH%;%ETC_HOME%;%CP%
echo %CLASSPATH%

set JAVA_OPTS=%JAVA_OPTS% -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=utf8
set MEM_OPTS=-Xms256m -Xmx512m -XX:MaxNewSize=128m -XX:MaxPermSize=128m 

java -server %MEM_OPTS% -cp %CLASSPATH% %JAVA_OPTIONS% com.ailk.service.server.socket.SocketServer localhost 8008 
