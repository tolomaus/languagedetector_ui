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
# note: in a real world scenario the package would be downloaded from a corporate repository instead of taking it from a local path

echo "Done."