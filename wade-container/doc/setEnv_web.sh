#!/bin/sh
#
# @desc: auto created by scripts, don't modify!
#
JAVA_HOME=${HOME}/support/jdk1.6.0_45
CONFIG_HOME=${RES_BASE}/WEB-INF/classes
LIB_HOME=${RES_BASE}/WEB-INF/lib

MEM_OPTS="-Xms4096m -Xmx4096m -XX:MaxPermSize=256m "
#-Dcom.wade.container.org.eclipse.jetty.util.URI.charset=GBK

CP=${CONFIG_HOME}:${LIB_HOME}/wade-container.jar

JAVA_OPTS="-XX:+HeapDumpOnOutOfMemoryError -Djava.net.preferIPv4Stack=true -Dsun.net.inetaddr.ttl=30 -Dsun.net.inetaddr.negative.tt=30 -Djava.security.egd=file:/dev/zero -Dfile.encoding=utf8"
JAVA_OPTS="$JAVA_OPTS -Dwade.container.minThreads=200 -Dwade.container.maxThreads=200 -Dwade.container.acceptors=2 -Dwade.container.acceptQueueSize=0"
JAVA_OPTS="$JAVA_OPTS -Dwade.container.resourceBase=${RES_BASE} -Dwade.container.contextPath=${CONTEXT_PATH} -Dwade.container.nio=true -Djava.io.tmpdir=${HOME}/tmp -Duser.dir=${HOME}/tmp"

#export LD_LIBRARY_PATH=~/support/TimesTen/tt70/lib


