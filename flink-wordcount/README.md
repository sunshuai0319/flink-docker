# Flink WordCount 示例项目

这是一个简单的Apache Flink WordCount示例项目，演示了如何使用Flink处理文本数据并计算单词频率。

## 项目结构

```
flink-wordcount/
├── pom.xml            - Maven配置文件
└── src/
    └── main/
        └── java/
            └── com/
                └── example/
                    └── WordCount.java  - WordCount实现
```

## 功能说明

本项目实现了一个简单的WordCount计算，具有以下特点：

1. 使用Java编写
2. 使用Maven进行依赖管理
3. 使用硬编码的文本集合作为数据源
4. 结果直接输出到Flink作业的日志中

## 如何构建

```bash
cd flink-wordcount
mvn clean package
```

构建成功后，会在`target`目录下生成一个可执行的JAR文件。

## 如何运行

### 本地模式

```bash
mvn exec:java -Dexec.mainClass="com.example.WordCount"
```

或者直接使用Java命令：

```bash
java -jar target/flink-wordcount-1.0-SNAPSHOT.jar
```

### 在Flink集群上运行

```bash
flink run target/flink-wordcount-1.0-SNAPSHOT.jar
```

## 在Docker环境中运行

本项目所在目录已经包含了Docker Compose配置文件，可以用于在Docker环境中运行Flink。

```bash
# 启动Flink集群
docker-compose up -d

# 等待集群启动完成后，提交作业
docker-compose exec jobmanager flink run -c com.example.WordCount /path/to/flink-wordcount-1.0-SNAPSHOT.jar
``` 