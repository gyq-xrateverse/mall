


sudo chown -R 1000:1000 /root/install/mall
sudo chmod -R 770 /root/install/mall

sudo mkdir -p /root/install/mall/rabbitmq/log
sudo chown -R 999:999 /root/install/mall/rabbitmq/log
sudo chmod -R 755 /root/install/mall/rabbitmq/log

sudo chmod 600 /root/install/mall/rabbitmq/data/.erlang.cookie
sudo chown 999:999 /root/install/mall/rabbitmq/data/.erlang.cookie


docker compose -f /root/install/mall/docker-compose-env.yml up -d

docker compose -f /root/install/mall/docker-compose-env.yml down



docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/mall-admin-web:latest

docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/beilv-agent-web:latest

docker compose -f /root/install/mall/docker-compose-frontend.yml down

docker compose -f /root/install/mall/docker-compose-frontend.yml up -d



docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/mall-admin:latest

docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/mall-portal:latest

docker compose -f /root/install/mall/docker-compose-app.yml down

docker compose -f /root/install/mall/docker-compose-app.yml up -d



docker-compose -f /root/install/mall/docker-compose-env.yml stop minio-init
docker-compose -f /root/install/mall/docker-compose-env.yml rm -f minio-init
docker-compose -f /root/install/mall/docker-compose-env.yml up -d minio-init
docker logs beilv_agent_minio_init -f

docker-compose -f /root/install/mall/docker-compose-env.yml stop new-api
docker-compose -f /root/install/mall/docker-compose-env.yml rm -f new-api
docker-compose -f /root/install/mall/docker-compose-env.yml up -d new-api
docker logs beilv_agent_new_api -f


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




# 立即刷新缓存解决URL端口问题：
curl -X POST http://localhost:8085/api/cache/case/refresh/all
