#!/bin/bash

# 提取文件
file1="./backendcloud/backend/target/backend-0.0.1-SNAPSHOT.jar"
file2="./backendcloud/botrunningsystem/target/botrunningsystem-0.0.1-SNAPSHOT.jar"
file3="./backendcloud/matchingsystem/target/matchingsystem-0.0.1-SNAPSHOT.jar"

# 使用scp传输文件到名叫springboot远程服务器
remote_server="aldocker"

scp "$file1" "$remote_server":/home/sheng/kob/backendcloud
scp "$file2" "$remote_server":/home/sheng/kob/backendcloud
scp "$file3" "$remote_server":/home/sheng/kob/backendcloud