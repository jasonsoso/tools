#!/bin/bash
# Tools Server 启动脚本

PROJECT_DIR=/opt/project/tools-server
JAR_FILE=$PROJECT_DIR/tools-server-0.0.1-SNAPSHOT.jar
LOG_FILE=$PROJECT_DIR/tools.log

# 先停掉旧进程
OLD_PID=$(ps -ef | grep "$JAR_FILE" | grep -v grep | awk '{print $2}')
if [ -n "$OLD_PID" ]; then
  echo "正在停止旧进程 PID=$OLD_PID ..."
  kill $OLD_PID
  sleep 2
fi

# 启动
echo "正在启动 tools-server ..."
nohup java -server \
  -XX:+HeapDumpOnOutOfMemoryError \
  -Xms512m -Xmx512m \
  -jar $JAR_FILE \
  --logging.level.root=debug \
  --server.port=8081 \
  --spring.profiles.active=prod \
  --spring.config.additional-location=file:$PROJECT_DIR/ \
  >> $LOG_FILE 2>&1 &

NEW_PID=$!
echo "已启动 PID=$NEW_PID，日志: $LOG_FILE"

# 等待启动完成
sleep 3
if ps -p $NEW_PID > /dev/null 2>&1; then
  echo "✅ 启动成功"
  echo ""
  echo "查看日志: tail -f $LOG_FILE"
  echo "停止服务: kill $NEW_PID"
else
  echo "❌ 启动失败，查看日志: tail -20 $LOG_FILE"
fi
