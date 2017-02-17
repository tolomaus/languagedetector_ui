#!/usr/bin/env bash

ENV=$1

if [ -z "$ENV" ]; then
    echo "You didn't specify the environment."
    exit 1
fi

sbt -Dconfig.file=conf/${ENV}.conf -Dhttp.port=disabled -Dhttps.port=9000 run