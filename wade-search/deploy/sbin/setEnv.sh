#!/bin/sh
# 
# @auth: zhoulin2@asiainfo-linkage.com
# @date: 2013-5-2
# @desc: 设置环境变量
#

LIB_HOME=$HOME/lib
ETC_HOME=$HOME/etc

CP=".:$ETC_HOME"
for file in $LIB_HOME/*; do 
    CP=${CP}:$file;
done

CLASSPATH=$CP
JAVA_OPTIONS="-XX:+HeapDumpOnOutOfMemoryError -Djava.net.preferIPv4Stack=true -Dsun.net.inetaddr.ttl=30 -Djava.security.egd=file:/dev/zero -Dfile.encoding=utf8 "
MEM_ARGS="-Xms256m -Xmx256m"
