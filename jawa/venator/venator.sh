#!/bin/sh

SERVICE_NAME=messenger
PATH_TO_JAR=~/universe/jawa/venator/target/venator.jar
PATH_TO_JAVA=/usr/bin/java
PID_PATH_NAME=~/universe/jawa/venator/target/venator.pid

RED=$(tput setaf 1)
GREEN=$(tput setaf 2)
WHITE=$(tput setaf 7)
RESET=$(tput sgr0)

if [ -z "$1" ]; then
    echo "${RED}usage: venator.sh [status | start | stop | restart]${RESET}"
    exit 1
fi


case $1 in
    start)
        echo "starting $SERVICE_NAME ..."
        if [ ! -f $PID_PATH_NAME ]; then
            nohup $PATH_TO_JAVA -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &
            echo $! > $PID_PATH_NAME
            echo "${GREEN}$SERVICE_NAME started${RESET}"
        else
            echo "${RED}$SERVICE_NAME is already running${RESET}"
        fi
    ;;
    stop)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stoping ..."
            kill $PID;
            echo "${GREEN}$SERVICE_NAME stopped${RESET}"
            rm $PID_PATH_NAME
        else
            echo "${RED}$SERVICE_NAME is not running${RESET}"
        fi
    ;;
    restart)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME);
            echo "$SERVICE_NAME stopping ...";
            kill $PID;
            echo "${GREEN}$SERVICE_NAME stopped${RESET}";
            rm $PID_PATH_NAME
            echo "$SERVICE_NAME starting ..."
            nohup $PATH_TO_JAVA -jar $PATH_TO_JAR /tmp 2>> /dev/null >> /dev/null &
            echo $! > $PID_PATH_NAME
            echo "${GREEN}$SERVICE_NAME started${RESET}"
        else
            echo "${RED}$SERVICE_NAME is not running${RESET}"
        fi
    ;;
    status)
        if [ -f $PID_PATH_NAME ]; then
            PID=$(cat $PID_PATH_NAME)
            if ps -p $PID > /dev/null 2>&1; then
                echo "${GREEN}$SERVICE_NAME is running (PID: $PID)${RESET}"
            else
                echo "${RED}$SERVICE_NAME is not running, but PID file exists, removing stale PID file${RESET}"
                rm $PID_PATH_NAME
            fi
        else
            echo "${RED}$SERVICE_NAME is not running${RESET}"
        fi
    ;;
    *)
        echo "${RED}unknown command '$1', use [status | start | stop | restart]${RESET}"
        exit 1
    ;;
esac
