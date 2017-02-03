#!/usr/bin/env bash
set -e

. $(dirname $0)/../settings.sh

APP_VERSION=$1

if [ -z "$APP_VERSION" ]; then
    echo "You didn't specify the version number to be deployed."
    exit 1
fi

echo "Deploying $APP_NAME $APP_VERSION to $APP_DIR..."
mkdir -p ${APP_DIR}
tar xvf $SOURCE_DIR/target/universal/$APP_NAME-$APP_VERSION.tgz -C ${APP_DIR}

echo "Done."