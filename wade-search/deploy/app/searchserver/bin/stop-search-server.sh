#!/bin/sh

PROCESS_NAME="com.ailk.search.server.SearchServer"
PROCESS_PARM="search-server"

CUR_USER=`whoami`
ps -ef | grep $PROCESS_NAME | grep $CUR_USER | grep $PROCESS_PARM | grep java | grep -v grep | awk '{print $2}' | while read PID
do
    kill -9 $PID 2>&1 >/dev/null
    echo "进程名称:$PROCESS_NAME，参数:$PROCESS_PARM，PID:$PID 成功停止!"
done
