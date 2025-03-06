FROM flink:1.20.1-scala_2.12-java8

# 添加MySQL驱动包到Flink的lib目录
COPY mysql-connector-java-8.0.22.jar $FLINK_HOME/lib/

# 设置工作目录
WORKDIR /opt/flink

# 创建简洁的启动脚本
RUN echo '#!/bin/bash\necho "Flink with MySQL driver ready."\nexec "$@"' > /docker-entrypoint.sh && \
    chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["bash"]