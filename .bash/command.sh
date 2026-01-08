docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/mall-admin:latest && \
docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/mall-portal:latest && \
docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/beilv-agent:latest && \
docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/mall-admin-web:latest && \
docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/beilv-agent-web:latest

docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/new-api:latest






docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/mall-admin-web:latest
docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/beilv-agent-web:latest
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml up -d


docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/mall-admin:latest
docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/mall-portal:latest
docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml up -d

docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml up -d

docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml up -d

docker compose -f /root/install/beilv-agent-deploy/docker-compose-databse.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml down


docker compose -f /root/install/beilv-agent-deploy/docker-compose-databse.yml up -d
docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml up -d
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml up -d
docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml up -d

docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml down mall-admin
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml down mall-admin-web


docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml stop elasticsearch
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml down mongo
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml stop kibana
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml stop logstash

docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml start elasticsearch
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml create mongo
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml start mongo
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml start kibana
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml start logstash

docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml restart redis

docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/beilv-agent:latest
docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/beilv-agent-web:latest

docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml down beilv-agent
docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml create beilv-agent
docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml start beilv-agent

docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml down beilv-agent-web
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml create beilv-agent-web
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml start beilv-agent-web

# 临时外网
docker pull crpi-4z4v1n5g8hbg9g3x.cn-hangzhou.personal.cr.aliyuncs.com/beilv-agent/beilv-agent-web-foreign:latest
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml down beilv-agent-web-foreign
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml create beilv-agent-web-foreign
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml start beilv-agent-web-foreign


docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml down mysql
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml create mysql
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml start mysql



docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml up -d

docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml down minio
docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml create minio
docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml start minio

docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml down minio-init
docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml create minio-init
docker compose -f /root/install/beilv-agent-deploy/docker-compose-minio.yml start minio-init


docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml up -d
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-frontend.yml up -d
docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml down
docker compose -f /root/install/beilv-agent-deploy/docker-compose-app.yml up -d


docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml stop minio-init
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml rm -f minio-init
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml up -d minio-init
docker logs beilv_agent_minio_init -f


docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml stop nginx
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml rm -f nginx
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml up -d nginx
docker logs beilv_agent_nginx -f


docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml down new-api
docker compose -f /root/install/beilv-agent-deploy/docker-compose-env.yml up -d new-api
docker logs beilv_agent_new_api -f


# MiniIO
## 配置连接
/root/install/mc alias set myminio http://localhost:9090 minioadmin minioadmin

## 创建测试存储桶
/root/install/mc mb /root/install/beilv-agent-deploy/minio/data/test-mall
## 删除测试存储桶
/root/install/mc rb --force /root/install/beilv-agent-deploy/minio/data/test-mall

## 设置为只读下载权限（推荐）
/root/install/mc anonymous set download /root/install/beilv-agent-deploy/minio/data/test-mall
## 设置为public权限（推荐）
/root/install/mc anonymous set public /root/install/beilv-agent-deploy/minio/data/test-mall




# 立即刷新缓存解决URL端口问题：
curl -X POST http://localhost:8085/api/cache/case/refresh/all
