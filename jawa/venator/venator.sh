#!/usr/bin/env bash

SERVICE_NAME="venator"
PATH_BASE="$UNIVERSE_HOME/jawa/venator/target"
PATH_TO_JAR="$PATH_BASE/venator.jar"
PATH_TO_JAVA="/usr/bin/java"
PID_PATH_NAME="$PATH_BASE/venator.pid"
LOG_PATH_NAME="$PATH_BASE/venator.log"

# colors
if command -v tput &>/dev/null && [ -t 1 ] && [ -z "${NO_COLOR:-}" ]; then
    RED=$(tput setaf 1)
    GREEN=$(tput setaf 2)
    RESET=$(tput sgr0)
else
    RED=""
    GREEN=""
    RESET=""
fi

# utils
is_running() {
    local pid="$1"
    if [ -z "$pid" ]; then
        return 1
    fi
    if kill -0 "$pid" 2>/dev/null; then
        ps -p "$pid" -o args= | grep -q "$PATH_TO_JAR"
        return $?
    fi
    return 1
}

cleanup_stale() {
    if [ -f "$PID_PATH_NAME" ]; then
        pid=$(cat "$PID_PATH_NAME")
        if ! is_running "$pid"; then
            echo "${RED}Found stale PID file, removing${RESET}"
            rm -f "$PID_PATH_NAME"
        fi
    fi
}

start_service() {
    cleanup_stale

    if [ -f "$PID_PATH_NAME" ]; then
        pid=$(cat "$PID_PATH_NAME")
        if is_running "$pid"; then
            echo "${RED}$SERVICE_NAME is already running (PID: $pid)${RESET}"
            exit 1
        fi
    fi

    echo "${GREEN}Starting $SERVICE_NAME...${RESET}"

    nohup "$PATH_TO_JAVA" -jar "$PATH_TO_JAR" /tmp >>"$LOG_PATH_NAME" 2>&1 &
    echo $! > "$PID_PATH_NAME"

    sleep 1
    pid=$(cat "$PID_PATH_NAME")
    if is_running "$pid"; then
        echo "${GREEN}$SERVICE_NAME started (PID: $pid)${RESET}"
    else
        echo "${RED}Failed to start $SERVICE_NAME${RESET}"
        rm -f "$PID_PATH_NAME"
        exit 1
    fi
}

stop_service() {
    if [ ! -f "$PID_PATH_NAME" ]; then
        echo "${RED}$SERVICE_NAME is not running${RESET}"
        return
    fi

    pid=$(cat "$PID_PATH_NAME")

    if ! is_running "$pid"; then
        echo "${RED}Process not running, removing stale PID file${RESET}"
        rm -f "$PID_PATH_NAME"
        return
    fi

    echo "${GREEN}Stopping $SERVICE_NAME (PID: $pid)...${RESET}"
    kill "$pid"

    for _ in {1..5}; do
        if ! is_running "$pid"; then
            rm -f "$PID_PATH_NAME"
            echo "${GREEN}$SERVICE_NAME stopped${RESET}"
            return
        fi
        sleep 1
    done

    echo "${RED}Process did not stop, sending SIGKILL${RESET}"
    kill -9 "$pid"
    rm -f "$PID_PATH_NAME"
    echo "${GREEN}$SERVICE_NAME killed${RESET}"
}

status_service() {
    if [ ! -f "$PID_PATH_NAME" ]; then
        echo "${RED}$SERVICE_NAME is not running${RESET}"
        return
    fi

    pid=$(cat "$PID_PATH_NAME")
    if is_running "$pid"; then
        echo "${GREEN}$SERVICE_NAME is running (PID: $pid)${RESET}"
    else
        echo "${RED}PID file exists but no process, removing stale PID file${RESET}"
        rm -f "$PID_PATH_NAME"
    fi
}

restart_service() {
    stop_service
    start_service
}

# prerequisites
if [ ! -x "$PATH_TO_JAVA" ]; then
    echo "${RED}java not found at $PATH_TO_JAVA${RESET}"
    exit 1
fi

if [ ! -f "$PATH_TO_JAR" ]; then
    echo "${RED}jar not found at $PATH_TO_JAR${RESET}"
    exit 1
fi

# main
case "$1" in
    start)   start_service ;;
    stop)    stop_service ;;
    restart) restart_service ;;
    status)  status_service ;;
    *)
        echo "${RED}usage: $0 [status | start | stop | restart]${RESET}"
        exit 1
    ;;
esac
