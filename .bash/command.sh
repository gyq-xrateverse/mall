


sudo chown -R 1000:1000 /root/install/mall
sudo chmod -R 770 /root/install/mall

sudo mkdir -p /root/install/mall/rabbitmq/log
sudo chown -R 999:999 /root/install/mall/rabbitmq/log
sudo chmod -R 755 /root/install/mall/rabbitmq/log

sudo chmod 600 /root/install/mall/rabbitmq/data/.erlang.cookie
sudo chown 999:999 /root/install/mall/rabbitmq/data/.erlang.cookie


docker compose -f /root/install/mall/docker-compose-env.yml up -d

docker compose -f /root/install/mall/docker-compose-env.yml down


# MiniIO
## 配置连接
/root/install/mc alias set myminio http://localhost:9090 minioadmin minioadmin

## 创建测试存储桶
/root/install/mc mb /root/install/mall/minio/data/test-mall
## 删除测试存储桶
/root/install/mc rb --force /root/install/mall/minio/data/test-mall

## 设置为只读下载权限（推荐）
/root/install/mc anonymous set download /root/install/mall/minio/data/test-mall
## 设置为public权限（推荐）
/root/install/mc anonymous set public /root/install/mall/minio/data/test-mall
