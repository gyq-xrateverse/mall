


sudo chown -R 1000:1000 /root/install/mall
sudo chmod -R 770 /root/install/mall

sudo mkdir -p /root/install/mall/rabbitmq/log
sudo chown -R 999:999 /root/install/mall/rabbitmq/log
sudo chmod -R 755 /root/install/mall/rabbitmq/log

sudo chmod 600 /root/install/mall/rabbitmq/data/.erlang.cookie
sudo chown 999:999 /root/install/mall/rabbitmq/data/.erlang.cookie


docker compose -f /root/install/mall/docker-compose-env.yml up -d

docker compose -f /root/install/mall/docker-compose-env.yml down


