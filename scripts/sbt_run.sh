#!/usr/bin/env bash

ENV=test

sbt -Dconfig.file=conf/${ENV}.conf -Dhttp.port=disabled -Dhttps.port=9000 run