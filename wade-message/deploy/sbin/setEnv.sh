#!/bin/sh
LIB_HOME=$HOME/lib
ETC_HOME=$HOME/etc

CP=".:$ETC_HOME"
for file in $LIB_HOME/*; do 
    CP=${CP}:$file;
done

CLASSPATH=$CP
JAVA_OPTIONS="-XX:+HeapDumpOnOutOfMemoryError -Djava.net.preferIPv4Stack=true -Dsun.net.inetaddr.ttl=10 -Djava.security.egd=file:/dev/zero -Dfile.encoding=utf8"
MEM_ARGS="-Xms128m -Xmx512m"
