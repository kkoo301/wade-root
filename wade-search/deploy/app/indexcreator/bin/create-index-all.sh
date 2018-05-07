#!/bin/sh
#
# @auth: zhoulin2@asiainfo-linkage.com
# @date: 2013-5-3
# @desc: 
#

BASE_HOME="$HOME"
APP_HOME="$BASE_HOME/app/indexcreator"

MEM_OPTS="-Xms1024m -Xmx1024m"

. $BASE_HOME/sbin/setEnv.sh

CLASSPATH="$APP_HOME/etc:$CLASSPATH"

echo "CLASSPATH=$CLASSPATH"
echo ""
echo "JAVA_OPTIONS=$JAVA_OPTIONS"
echo ""
echo "MEM_OPTS=$MEM_OPTS"
echo ""


NOW=`date +%Y%m%d`
java -server $MEM_OPTS -cp $CLASSPATH $JAVA_OPTIONS com.ailk.search.server.index.IndexCreator ALL | tee -a $APP_HOME/log/create-index-all-${NOW}.log
find $APP_HOME/log -mtime +2 | xargs rm -rf
