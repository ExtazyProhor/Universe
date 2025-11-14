#!/usr/bin/env bash

SERVICE_NAME="venator"
MAIN_POM_PATH="$UNIVERSE_HOME/jawa/pom.xml"
TARGET_JAR_PATH="$UNIVERSE_HOME/jawa/venator/target/venator.jar"
PATH_BASE="$UNIVERSE_WORKSPACE/venator"
PATH_TO_JAR="$PATH_BASE/venator.jar"
PATH_TO_JAVA="/usr/bin/java"
PID_PATH_NAME="$PATH_BASE/venator.pid"
LOG_PATH_NAME="$PATH_BASE/venator-$(date +'%Y-%m-%d_%H-%M-%S').log"

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

    pid=$(cat "$PID_PATH_NAME")
    for _ in {1..30}; do
        sleep 0.5
        if curl -fs http://127.0.0.1:7005/actuator/health >/dev/null; then
            echo "${GREEN}$SERVICE_NAME started with PID $pid${RESET}"
            exit 0
        fi

        if ! is_running "$pid"; then
            echo "${RED}$SERVICE_NAME crashed on startup${RESET}"
            echo ""
            tail -n 100 "$LOG_PATH_NAME"
            rm -f "$PID_PATH_NAME"
            exit 1
        fi
    done

    echo "${RED}Health check timed out${RESET}"
    exit 1
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

build_service() {
    echo "${GREEN}Building $SERVICE_NAME...${RESET}"
    mvn -f "$MAIN_POM_PATH" clean package -pl venator -am -DskipTests
    status=$?

    if [ "$status" -ne 0 ]; then
        echo "${RED}Maven build failed. Aborting${RESET}"
        return 1
    fi

    if [ ! -f "$TARGET_JAR_PATH" ]; then
        echo "${RED}Build succeeded but output JAR is missing: $TARGET_JAR_PATH${RESET}"
        return 1
    fi

    if [ -f "$PATH_TO_JAR" ]; then
        rm -f "$PATH_TO_JAR"
    fi
    mv "$TARGET_JAR_PATH" "$PATH_TO_JAR"
    status=$?

    if [ "$status" -ne 0 ]; then
        echo "${RED}Failed to move new JAR into place${RESET}"
        return 1
    fi

    echo "${GREEN}$SERVICE_NAME built and deployed, use ${RESET}venator restart${GREEN} to restart it${RESET}"
}

test_service() {
    echo "${GREEN}Running tests for $SERVICE_NAME...${RESET}"

    mvn -f "$MAIN_POM_PATH" clean test -pl venator -am -amd
    status=$?

    if [ "$status" -ne 0 ]; then
        echo "${RED}Tests failed${RESET}"
        return 1
    fi

    echo "${GREEN}All tests passed successfully${RESET}"
}

# prerequisites
if [ ! -x "$PATH_TO_JAVA" ]; then
    echo "${RED}java not found at $PATH_TO_JAVA${RESET}"
    exit 1
fi

if [ ! -f "$PATH_TO_JAR" ]; then
    echo "${RED}jar not found at $PATH_TO_JAR${RESET}"
fi

# main
case "$1" in
    start)   start_service ;;
    stop)    stop_service ;;
    restart) restart_service ;;
    status)  status_service ;;
    build)  build_service ;;
    test)  test_service ;;
    *)
        echo "${RED}usage: $0 [status | start | stop | restart | build | test]${RESET}"
        exit 1
    ;;
esac
