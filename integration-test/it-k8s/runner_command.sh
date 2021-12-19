#!/bin/sh
opts=""
if [ $JAVA_OPTS ]; then
    opts="$opts $JAVA_OPTS"
fi

if [ $RAM_PERCENTAGE ]; then
echo "添加初始化内存占比: $RAM_PERCENTAGE"
## 这三个参数是JDK8U191为适配Docker容器新增的几个参数
opts="$opts -XX:InitialRAMPercentage=$RAM_PERCENTAGE -XX:MaxRAMPercentage=$RAM_PERCENTAGE -XX:MinRAMPercentage=$RAM_PERCENTAGE"
fi

final_command="java $opts -jar server.jar $*"
echo "最终执行命令为: $final_command"

java $opts -jar server.jar $*