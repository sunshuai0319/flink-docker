FROM flink:1.20.1-scala_2.12-java8

# 添加MySQL驱动包到Flink的lib目录
COPY mysql-connector-java-8.0.22.jar $FLINK_HOME/lib/
