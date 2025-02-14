#!/bin/sh
# 获取当前环境
NAME_SPACE=${MY_POD_NAMESPACE}
NAME=${HOSTNAME%-*-*}
echo namespace is $NAME_SPACE
echo hostname is $HOSTNAME

#预设java参数，自定义java参数用容器启动时配置环境变量JAVA_OPTS来实现，相同参数后者会覆盖前者
JAVA_DEFAULT_OPTS="
     -Xms1g
     -Xmx2g
     -XX:+UseG1GC
     -XX:MaxGCPauseMillis=200
     -XX:+PrintTenuringDistribution
     -XX:+PrintGCDetails
     -XX:+PrintHeapAtGC
     -XX:+PrintGCDateStamps
     -XX:+PrintGCTimeStamps
     -XX:+UseContainerSupport
     -XX:+HeapDumpOnOutOfMemoryError
     -XX:+ExitOnOutOfMemoryError
     -Xloggc:gc-%t.log
     -Duser.timezone=GMT+08"

#启动服务
exec java $JAVA_DEFAULT_OPTS $JAVA_OPTS -jar app.jar $JAVA_ARGS
