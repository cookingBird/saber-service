#!/bin/bash

APP_HOME=`pwd`
dirname $0|grep "^/" >/dev/null
if [ $? -eq 0 ];then
  APP_HOME=`dirname $0`
else
  dirname $0|grep "^\." >/dev/null
  retval=$?
  if [ $retval -eq 0 ];then
    APP_HOME=`dirname $0|sed "s#^.#$APP_HOME#"`
  else
    APP_HOME=`dirname $0|sed "s#^#$APP_HOME/#"`
  fi
fi

APP_NAME="gostream"
APP_BASE_NAME="gostream"

#trim
PORT=`echo $PORT |tr -d '\r'`
PORT=`echo $PORT |tr -d ' '`
PORT=`echo $PORT |tr -d ','`
# PORT=${PORT%% }
# PORT=${PORT## }

LOG_PATH="$APP_HOME/log"
GO_STD_OUT_PATH="$LOG_PATH/std.log"


JAR_FILE=$APP_HOME/$APP_NAME
pid=0
start(){
  #启动才判断存在与否
  if [ ! -f "$JAR_FILE" ];then
  echo "$JAR_FILE 不存在"
    exit 1
  fi

  # #创建log目录
  # if [ ! -d "$LOG_PATH" ];then
  #   mkdir -p $LOG_PATH
  # fi

  checkpid
  if [ ! -n "$pid" ]; then
    nohup $JAR_FILE > $GO_STD_OUT_PATH 2>&1 &
    # #创建最新链接
    # rm -rf $CURRENT_LOG_PATH
    # ln -sf $LOG_PATH $CURRENT_LOG_PATH
    # rm -rf $CURRENT_GC_LOG_PATH
    # ln -sf $GC_LOG_PATH $CURRENT_GC_LOG_PATH
    #sleep 1s
    #tail -f $LOG_PATH
    sleep 1
    checkpid
    if [ -n "$pid" ]; then
      echo "$APP_NAME 启动成功"
      #tail -f $APP_LOG_PATH
    else
      echo "启动失败"
    fi
  else
    echo "$APP_NAME 运行中 PID: $pid"
  fi

}


status(){
  checkpid
  if [ ! -n "$pid" ]; then
    echo "$APP_NAME 未启动"
  else
    echo "$APP_NAME 运行中 PID: $pid"
  fi
}

checkpid(){
  pid=`ps -ef |grep $JAR_FILE |grep -v 'grep' |grep -v $0 |grep -v 'tail -f' |awk '{print $2}'`
}

stop(){
  checkpid
  if [ ! -n "$pid" ]; then
    echo "$APP_NAME 未启动"
  else
    echo "$APP_NAME 停止中..."
    for i in {1..30}
    do
      checkpid
      kill $pid
      sleep 2
      checkpid
      if [ ! -n "$pid" ]; then
        echo "$APP_NAME 停止成功"
        break
      else
        echo "$APP_NAME 停止失败, 重试中 $i"
      fi
    done
  fi

  checkpid
  if [ -n "$pid" ]; then
    kill -9 $pid
    checkpid
    if [ ! -n "$pid" ]; then
      echo "$APP_NAME 强制停止成功"
    else
      echo "$APP_NAME 强制停止失败"
    fi
  fi
}

restart(){
  stop
  sleep 1s
  start
}

case $1 in
start) start;;
stop)  stop;;
restart)  restart;;
status)  status;;
*)  echo "无效的操作指令(start|stop|restart|status)"; exit 1  ;;
esac

