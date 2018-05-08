#!/bin/sh

[ $# -lt 2 ] && echo "必须传入2个参数，第一个参数是进程名称，第二个参数是进程参数" && exit 0;

PROCESS_NAME=$1
PROCESS_PARM=$2

PROCESS_COUNT=`ps -ef | grep $PROCESS_NAME | grep $PROCESS_PARM | grep -v $0 | grep -v /bin/sh | grep -v grep | wc -l`

if [ "$PROCESS_COUNT" -gt "0" ]; then
    echo "PROCESS_EXIST"
else
    echo "PROCESS_NOT_EXIST"
fi