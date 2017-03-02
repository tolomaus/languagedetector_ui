# language detector UI

This repo contains a Play application that can detect the language of a text that is entered by the user, sentence by sentence. It can also show the log files of the Apache Spark application from [its companion repo](https://github.com/tolomaus/languagedetector.git)

todo
load parquet file


## quick start
### installation and configuration
0] make sure you have jdk 1.8, sbt and git installed

1] clone this repo
```shell
git clone https://github.com/tolomaus/languagedetector_ui.git
cd languagedetector_ui
```

2] (optional) modify the environment variables from [settings.sh](https://github.com/tolomaus/languagedetector_ui/tree/master/settings.sh) to your needs
```shell
nano settings.sh
```

3] (optional) modify the environment specific test.conf file to your needs:
```shell
nano conf/test.conf
```

If you haven't changed the settings in the two previous steps all files (binaries, data and logs) will be created under ~/my_workspace and may be deleted when you're done. 

4] run the application:

From source:
```shell
scripts/sbt_run.sh test # this script uses the settings from conf/test.conf
```
From a deployed package:
```shell
scripts/package_app.sh # the version is currently set to 1.0 in the build.sbt
scripts/deploy_app.sh test 1.0 # deploy version 1.0 to the test environment
scripts/languagedetector_ui_test start
```