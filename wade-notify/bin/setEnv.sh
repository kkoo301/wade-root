#!/bin/sh
# 
# @auth: lvchao@asiainfo.com
# @date: 2015-4-13
# @desc: 设置环境变量
#

LIB_HOME=$HOME/lib
ETC_HOME=$HOME/etc

CP=".:$ETC_HOME"
for file in $LIB_HOME/*; do 
    CP=${CP}:$file;
done

CLASSPATH=$CP
JAVA_OPTIONS="-Dwade.server.name=notify -XX:+HeapDumpOnOutOfMemoryError -Djava.net.preferIPv4Stack=true -Dsun.net.inetaddr.ttl=30 -Djava.security.egd=file:/dev/zero -Dfile.encoding=utf8 "
MEM_ARGS="-Xms4096m -Xmx4096m"