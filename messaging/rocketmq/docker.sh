docker run -id --name=rocketmq \
-e TZ='Asia/Shanghai' \
-e JAVA_OPT_EXT='-Xms256m -Xmx512m -DautoCreateTopicEnable=true' \
-p 10911:10911 -p 9876:9876 \
king019/rocketmq:4.8.0