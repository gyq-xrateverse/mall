sudo chown -R 1000:1000 /root/install/beilv-agent-deploy
sudo chmod -R 770 /root/install/beilv-agent-deploy

sudo mkdir -p /root/install/beilv-agent-deploy/rabbitmq/log
sudo chown -R 999:999 /root/install/beilv-agent-deploy/rabbitmq/log
sudo chmod -R 755 /root/install/beilv-agent-deploy/rabbitmq/log

sudo chmod 600 /root/install/beilv-agent-deploy/rabbitmq/data/.erlang.cookie
sudo chown 999:999 /root/install/beilv-agent-deploy/rabbitmq/data/.erlang.cookie
