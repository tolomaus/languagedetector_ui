# language detector UI

This repo contains a Play application that can detect the language of a text that is entered by the user, sentence by sentence. It can also show the log files of the Apache Spark application from [its companion repo](https://github.com/tolomaus/languagedetector.git)

todo
load parquet file


## quick start
### installation and configuration
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

4] run the application:

From source:
```shell
scripts/sbt_run.sh # this script uses the settings from conf/test.conf
```
From a package:
```shell
scripts/package_app.sh
scripts/deploy_app.sh 1.0
scripts/languagedetector_ui start # this script uses the settings from conf/test.conf
```