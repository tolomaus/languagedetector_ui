@echo off

. %~dp0\..\settings.sh

echo "Creating package..."
cd %SOURCE_DIR%
sbt universal:packageZipTarball
cd -

rem note: in a real world scenario the package would be uploaded to a corporate repository
echo "Done."
