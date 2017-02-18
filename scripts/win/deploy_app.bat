@echo off

. %~dp0\..\settings.sh

set ENV=%1

if "%ENV%"==""(
    echo "You didn't specify the environment to which to deploy."
    exit /b 1
)

set APP_VERSION=%2

if "%APP_VERSION%"==""(
    echo "You didn't specify the version number to be deployed."
    exit /b 1
)

echo "Deploying $APP_NAME $APP_VERSION to $APP_ENV_DIR..."
set APP_ENV_DIR=%APP_DIR%/%ENV%
if not exist %APP_ENV_DIR% mkdir %APP_ENV_DIR%
jar xvf %SOURCE_DIR%/target/universal/%APP_NAME%-%APP_VERSION%.tgz -C %APP_ENV_DIR%

if exist %APP_ENV_DIR%/%APP_NAME%(
    set DATE=%DATE:~4,2%%DATE:~7,2%%DATE:~12,2%
    move %APP_ENV_DIR%/%APP_NAME% %APP_ENV_DIR%/%APP_NAME%_%DATE%
)

move %APP_ENV_DIR%/%APP_NAME%-%APP_VERSION% %APP_ENV_DIR%/%APP_NAME%
rem note: in a real world scenario the package would be downloaded from a corporate repository instead of taking it from a local path

echo "Done."