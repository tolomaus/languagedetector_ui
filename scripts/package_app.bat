@echo off

call %~dp0\..\settings.bat

echo "Creating package..."
pushd %SOURCE_DIR%
sbt universal:packageBin
popd

rem note: in a real world scenario the package would be uploaded to a corporate repository
echo "Done."
