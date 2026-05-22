@echo off

set BASE_DIR=%UNIVERSE_HOME%\jawa
set JAR=%BASE_DIR%\uni\target\uni.jar

if not exist "%JAR%" (
    cd /d "%BASE_DIR%"
    mvn -q -pl uni -am -DskipTests package
)

java ^
 -XX:TieredStopAtLevel=1 ^
 --enable-native-access=ALL-UNNAMED ^
 -jar "%JAR%" %*
