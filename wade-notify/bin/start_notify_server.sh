#!/bin/sh
#
# @auth: lvchao@asiainfo.com
# @date: 2015-4-13
# @desc: 
#
BASE_HOME="$HOME"

MEM_OPTS="-Xms4096m -Xmx4096m -XX:MaxPermSize=256m"

. $BASE_HOME/bin/setEnv.sh

CLASSPATH="$BASE_HOME/etc:$CLASSPATH"
JAVA_OPTIONS="$JAVA_OPTIONS -Dwade.server.port=29000"


PROCESS_ALIVE_STATUS=`~/bin/monitor_process.sh java NotifyServer`
if [ "$PROCESS_ALIVE_STATUS" = "PROCESS_EXIST" ]; then
    echo "notify is already running!"
    exit 1
else
    echo "CLASSPATH=$CLASSPATH"
    echo ""
    echo "JAVA_OPTIONS=$JAVA_OPTIONS"
    echo ""
    echo "MEM_OPTS=$MEM_OPTS"
    echo ""

    NOW=`date +%Y%m%d`
    java -server $MEM_OPTS -cp $CLASSPATH $JAVA_OPTIONS com.ailk.notify.server.NotifyServer 2>&1 | ${BASE_HOME}/bin/cronolog -k 3 ${BASE_HOME}/logs/notify-%m%d.log >/dev/null 2>&1 & 
    #| tee -a $BASE_HOME/logs/notify-${NOW}.log
    
	find  $BASE_HOME/logs -mtime +2 | xargs rm -rf
fi
