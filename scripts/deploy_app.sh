#!/usr/bin/env bash
set -e

. $(dirname $0)/../settings.sh

ENV=$1

if [ -z "$ENV" ]; then
    echo "You didn't specify the environment to which to deploy."
    exit 1
fi

APP_VERSION=$2

if [ -z "$APP_VERSION" ]; then
    echo "You didn't specify the version number to be deployed."
    exit 1
fi

echo "Deploying $APP_NAME $APP_VERSION to $APP_ENV_DIR..."
APP_ENV_DIR=$APP_DIR/$ENV
mkdir -p ${APP_ENV_DIR}
tar xvf $SOURCE_DIR/target/universal/$APP_NAME-$APP_VERSION.tgz -C ${APP_ENV_DIR}

if [ -f ${APP_ENV_DIR}/$APP_NAME ]; then
    DATE=$(date +"%Y%m%d%H%M")
    mv ${APP_ENV_DIR}/$APP_NAME ${APP_ENV_DIR}/${APP_NAME}_${DATE}
fi

mv ${APP_ENV_DIR}/$APP_NAME-$APP_VERSION ${APP_ENV_DIR}/$APP_NAME
# note: in a real world scenario the package would be downloaded from a corporate repository instead of taking it from a local path

echo "Done."