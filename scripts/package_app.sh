#!/usr/bin/env bash
set -e

. $(dirname $0)/../settings.sh

echo "Creating package..."
cd $SOURCE_DIR
sbt universal:packageZipTarball
cd -
echo "Done."
