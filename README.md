# language detector UI

This repo contains a Play application that can detect the language of a text that is entered by the user, sentence by sentence. It can also show the log files of the Apache Spark application from [its companion repo](https://github.com/tolomaus/languagedetector.git)

todo
load parquet file


## quick start
### setup
0] make sure you have jdk 1.8, sbt and git installed

1] clone this repo
```bash
git clone https://github.com/tolomaus/languagedetector_ui.git
cd languagedetector_ui
```

2] (optional) modify the environment variables from [settings.sh](https://github.com/tolomaus/languagedetector_ui/tree/master/settings.sh) to your needs
```bash
# mac/linux
nano settings.sh

# windows
start notepad settings.bat
```

3] modify the environment specific test.conf file to your needs:
```bash
# mac/linux
nano conf/test.conf

# windows
start notepad conf/test.conf # change ${HOME} to ${HOMEPATH}
```

If you haven't changed the settings in the two previous steps all files (binaries, data and logs) will be created under ~/my_workspace and may be deleted when you're done. 


### usage
From source:
```bash
# mac/linux
scripts/sbt_run.sh test # this script uses the settings from conf/test.conf

# windows
scripts/sbt_run.bat test # this script uses the settings from conf/test.conf
```

From a deployed package:
```bash
# mac/linux
scripts/package_app.sh # the version is currently set to 1.0 in the build.sbt
scripts/deploy_app.sh test 1.0 # deploy version 1.0 to the test environment
scripts/languagedetector_ui_test start

# windows
scripts/package_app.bat # the version is currently set to 1.0 in the build.sbt
scripts/deploy_app.bat test 1.0 # deploy version 1.0 to the test environment
```

Now navigate to [https://localhost:9000](https://localhost:9000)