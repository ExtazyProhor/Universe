@echo off
set BASE_DIR=%UNIVERSE_HOME%\c3po\uni
set CP_FILE=%BASE_DIR%\.uni-cache\classpath

if not exist "%CP_FILE%" (
    cd /d "%BASE_DIR%"
    mvn -q -DskipTests compile
    mvn -q dependency:build-classpath -Dmdep.outputFile="%CP_FILE%"
)

set /p CP=<"%CP_FILE%"
set CLASSPATH=%BASE_DIR%\target\classes;%CP%

java ^
 -XX:TieredStopAtLevel=1 ^
 --enable-native-access=ALL-UNNAMED ^
 -cp "%CLASSPATH%" ru.prohor.universe.uni.cli.MainKt %*
