#!/bin/sh

PROCESS_NAME="com.wade.message.comet.server.Main"
PROCESS_PARM="8000"

CUR_USER=`whoami`
ps -ef | grep $PROCESS_NAME | grep $CUR_USER | grep $PROCESS_PARM | grep java | grep -v grep | awk '{print $2}' | while read PID
do
    kill -9 $PID 2>&1 >/dev/null
    echo "进程名称:$PROCESS_NAME，参数:$PROCESS_PARM，PID:$PID 成功停止!"
done
