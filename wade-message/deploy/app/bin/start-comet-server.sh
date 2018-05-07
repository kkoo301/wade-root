#!/bin/sh
#
# @auth: zhoulin2@asiainfo-linkage.com
# @date: 2013-5-3
# @desc: 
#

# 判断进程是否重复启动
PROCESS_ALIVE_STATUS=`./monitor-comet-server.sh`
if [ "$PROCESS_ALIVE_STATUS" = "PROCESS_EXIST" ];
then
    echo "此进程已经启动，不能重复启动！"
    exit 0;
fi

BASE_HOME="$HOME"
APP_HOME="$BASE_HOME/app"

MEM_OPTS="-Xms512m -Xmx512m"

. $BASE_HOME/sbin/setEnv.sh

CLASSPATH="$APP_HOME/etc:$CLASSPATH"
#-XX:SurvivorRatio=1 -XX:-UseAdaptiveSizePolicy
JAVA_OPTIONS="$JAVA_OPTIONS -XX:+UseParallelGC"

echo "CLASSPATH=$CLASSPATH"
echo ""
echo "JAVA_OPTIONS=$JAVA_OPTIONS"
echo "MEM_OPTS=$MEM_OPTS"
echo ""

java -server -Dwade.server.name=crm-comet-node01-srv01 $MEM_OPTS -cp $CLASSPATH $JAVA_OPTIONS com.wade.message.comet.server.Main 8000 2>&1 | \
$BASE_HOME/sbin/cronolog -k 3 ../log/comet-server-%Y%m%d.log & 

echo "启动完成，请查看日志！"
