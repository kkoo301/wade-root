#!/bin/sh
#
# @desc: auto created by scripts, don't modify!
#

. ${HOME}/sbin/setEnv.sh

PORT=10001
SERVER_NAME=app-node01-srv01
MEM_OPTS="-Xms4096m -Xmx4096m -XX:MaxPermSize=256m"

echo "JAVA_OPTS=$JAVA_OPTS"
echo "MEM_OPTS=$MEM_OPTS"
echo ""
$JAVA_HOME/bin/java -server -cp $CP $MEM_OPTS $JAVA_OPTS -Dwade.container.port=$PORT -Dwade.server.name=$SERVER_NAME com.wade.container.start.Main 2>&1 | \
${HOME}/sbin/cronolog -k 2 ../logs/$SERVER_NAME-%m%d.log >/dev/null &