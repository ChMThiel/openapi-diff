#!/bin/bash

# $1: application name
APPLICATION_NAME=$1
BUILD_CONFIG_NAME=$(echo "${APPLICATION_NAME}-release")
NEXT_VERSION=$(cat .newversion)
REPOSITORY_NAME=iotos

if [[ -z "$NEXT_VERSION" ]] || [[ "$NEXT_VERSION" == "DEVEL" ]]
then
  NEXT_VERSION="latest"
fi

if [[ -n $2 ]] && [[ "$2" != "null" ]]
then
  REPOSITORY_NAME=$2
fi

oc patch bc ${BUILD_CONFIG_NAME} --patch '{"spec":{"output":{"to":{"name":"registry.iotos.io/'${REPOSITORY_NAME}'/'${APPLICATION_NAME}:${NEXT_VERSION}'"}}}}'
oc start-build ${BUILD_CONFIG_NAME} --follow