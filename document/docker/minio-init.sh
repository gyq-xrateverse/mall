#!/bin/bash

# MinIO初始化脚本 - 配置CORS策略

# 等待MinIO服务启动
echo "等待MinIO服务启动..."
sleep 15

# 设置MinIO客户端别名 - 使用容器内网络
echo "设置MinIO客户端连接..."
mc alias set minio http://minio:9000 minioadmin minioadmin

# 验证连接
echo "验证MinIO连接..."
if ! mc admin info minio >/dev/null 2>&1; then
    echo "MinIO连接失败，尝试备用地址..."
    mc alias set minio http://beilv_agent_minio:9000 minioadmin minioadmin
fi

# 定义需要创建的bucket列表
BUCKETS=("test-mall" "minio-mall" "minio-agent")

# 为每个bucket进行配置
for BUCKET in "${BUCKETS[@]}"; do
    echo "配置bucket: $BUCKET"

    # 检查并创建bucket
    if ! mc ls minio/$BUCKET >/dev/null 2>&1; then
        echo "创建 $BUCKET bucket..."
        mc mb minio/$BUCKET
    else
        echo "$BUCKET bucket 已存在"
    fi

    # 创建临时CORS配置文件
    CORS_FILE="/tmp/cors-${BUCKET}.json"
    echo "配置 $BUCKET 的CORS策略..."
    cat > $CORS_FILE << 'CORS_EOF'
{
    "CORSRules": [
        {
            "AllowedOrigins": ["*"],
            "AllowedMethods": ["GET", "HEAD", "POST", "PUT", "DELETE"],
            "AllowedHeaders": ["*"],
            "ExposeHeaders": ["ETag", "Content-Length"],
            "MaxAgeSeconds": 3600
        }
    ]
}
CORS_EOF

    # 尝试设置CORS配置
    if mc cors set $CORS_FILE minio/$BUCKET 2>/dev/null; then
        echo "$BUCKET CORS配置成功"
    else
        echo "$BUCKET CORS配置失败(可能是MinIO版本不支持)，使用访问策略替代"
    fi

    # 清理临时文件
    rm -f $CORS_FILE

    # 设置bucket为可公开访问（仅读取）
    echo "设置 $BUCKET 访问策略为 download..."
    mc anonymous set download minio/$BUCKET

    # 验证配置
    echo "验证 $BUCKET 访问策略..."
    mc anonymous get minio/$BUCKET 2>/dev/null || echo "访问策略已设置"

    echo "--------------------------------"
done

echo "MinIO初始化完成!"
echo "如果CORS配置失败，图片仍可通过代理访问"