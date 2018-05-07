#!/bin/sh
#
# @auth: zhoulin2@asiainfo-linkage.com
# @date: 2013-5-3
# @desc: 
#

# 判断进程是否重复启动
PROCESS_ALIVE_STATUS=`./monitor-search-server.sh`
if [ "$PROCESS_ALIVE_STATUS" = "PROCESS_EXIST" ];
then
    echo "此进程已经启动，不能重复启动！"
    exit 0;
fi

BASE_HOME="$HOME"
APP_HOME="$BASE_HOME/app/searchserver"

MEM_OPTS="-Xms2048m -Xmx2048m"

. $BASE_HOME/sbin/setEnv.sh

CLASSPATH="$APP_HOME/etc:$CLASSPATH"

echo "CLASSPATH=$CLASSPATH"
echo ""
echo "JAVA_OPTIONS=$JAVA_OPTIONS"
echo "MEM_OPTS=$MEM_OPTS"
echo ""

java -server -Dwade.server.name=search-server $MEM_OPTS -cp $CLASSPATH $JAVA_OPTIONS com.ailk.search.server.SearchServer 10.154.63.121 11000 2>&1 | \
$BASE_HOME/sbin/cronolog -k 3 ../log/search-server-%Y%m%d.log & 

echo "启动完成，请查看日志！"
