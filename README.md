# 创建README文件
cat > README.md << EOF
# Flink Docker配置

本仓库包含用于构建Flink Docker镜像的配置文件。

## 镜像版本

- \`Dockerfile\`: 基础Flink镜像，仅添加MySQL驱动支持
- \`Dockerfile-pyflink\`: Flink镜像，添加Python支持和MySQL驱动

## 使用方法

### 构建镜像

\`\`\`bash
# 构建基础镜像
docker build -t flink:mysql -f Dockerfile .

# 构建带Python支持的镜像
docker build -t flink:mysql-pyflink -f Dockerfile-pyflink .
\`\`\`

### 运行镜像

\`\`\`bash
# 启动Flink集群
docker run -d -p 8081:8081 flink:mysql-pyflink ./bin/start-cluster.sh
\`\`\`
EOF