#!/usr/bin/env bash
set -e

APP_NAME=languagedetector_ui
APP_VERSION=1.0
APP_DIR=~/$APP_NAME

echo "creating package for $APP_NAME $APP_VERSION..."
cd $APP_DIR
sbt universal:packageZipTarball
cd -

if ls /opt/$APP_NAME-* 1> /dev/null 2>&1; then
    echo "Archiving the existing version to /opt/archive..."
    rm -rf /opt/archive/$APP_NAME-*
    mv /opt/$APP_NAME-* /opt/archive
fi

echo "Unpacking the package to /opt..."
tar xvf $APP_DIR/target/universal/$APP_NAME-$APP_VERSION.tgz -C /opt

echo "Done."
