@echo off

set ENV=%1

if "%ENV%"==""(
    echo "You didn't specify the environment to which to deploy."
    exit /b 1
)

sbt -Dconfig.file=conf\%ENV%.conf -Dhttp.port=disabled -Dhttps.port=9000 run