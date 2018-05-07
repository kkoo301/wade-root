#!/bin/sh
#
# @desc: auto created by scripts, don't modify!
#
JAVA_HOME=/opt/JDK6
RES_BASE=${HOME}/deploy/app
CONFIG_HOME=${RES_BASE}/WEB-INF/classes
LIB_HOME=${RES_BASE}/WEB-INF/lib

MEM_OPTS="-Xms2048m -Xmx2048m -XX:MaxPermSize=256m"

CP=${CONFIG_HOME}:${LIB_HOME}/wade-container.jar

JAVA_OPTS="-XX:+HeapDumpOnOutOfMemoryError -Djava.net.preferIPv4Stack=true -Dsun.net.inetaddr.ttl=30 -Dsun.net.inetaddr.negative.tt=30 -Djava.security.egd=file:/dev/zero -Dfile.encoding=utf8"
JAVA_OPTS="$JAVA_OPTS -Dwade.container.minThreads=200 -Dwade.container.maxThreads=200 -Dwade.container.acceptors=2 -Dwade.container.acceptQueueSize=0"
JAVA_OPTS="$JAVA_OPTS -Dwade.container.resourceBase=$RES_BASE -Dwade.container.nio=true -Djava.io.tmpdir=${HOME}/tmp -Duser.dir=${HOME}/tmp "
#JAVA_OPTS="$JAVA_OPTS -Dcom.wade.org.eclipse.jetty.util.URI.charset=GBK"
JAVA_OPTS="$JAVA_OPTS -Dcom.wade.container.util.log.class=com.wade.container.util.log.StdErrLog -D{classref}.LEVEL=INFO"

