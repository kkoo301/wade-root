#!/bin/sh

WADE_SERVER_NAME="app-node01-srv01"
ps -ef | grep `whoami` | grep java | grep ${WADE_SERVER_NAME} | grep -v grep | awk '{print $2}' | while read PID
do
    kill -9 $PID 2>&1 >/dev/null
    [ "$?" == "0" ] && echo "stop  instance ${WADE_SERVER_NAME} successful! pid:${PID}"
done
