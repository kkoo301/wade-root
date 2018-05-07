#!/bin/sh
#
# @desc: auto created by scripts, don't modify!
#

RES_BASE=/home/web/deploy/personserv
CONTEXT_PATH=/personserv

. ${HOME}/sbin/setEnv.sh

PORT=9101
SERVER_NAME=web-node01-personserv01
MEM_OPTS="-Xms1024m -Xmx1024m -XX:MaxPermSize=256m"

echo "MEM_OPTS=$MEM_OPTS"
echo "JAVA_OPTS=$JAVA_OPTS"
echo ""
$JAVA_HOME/bin/java -server -cp $CP $MEM_OPTS $JAVA_OPTS -Dwade.container.port=$PORT -Dwade.server.name=$SERVER_NAME com.wade.container.start.Main 2>&1 | \
${HOME}/sbin/cronolog -k 2 ../logs/$SERVER_NAME-%m%d.log >/dev/null &
