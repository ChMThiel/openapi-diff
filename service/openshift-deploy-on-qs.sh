#!/bin/bash

if [[ -z "$1" ]]
then
  echo "Error: this script requires an application name!"
  echo "Usage: $0 <application name>"
  exit 1
fi

DEPLOYMENT_CONFIG_NAME=$1
IMAGE_NAME=$DEPLOYMENT_CONFIG_NAME

# if second argument is present, use it as IMAGE_NAME
if [[ -n $2 ]]
then
  IMAGE_NAME=$2
fi

NEXT_VERSION=$(cat .newversion)

if [[ -z "$NEXT_VERSION" ]] || [[ "$NEXT_VERSION" == "DEVEL" ]]
then
  NEXT_VERSION="latest"
fi

oc patch -n qs dc ${DEPLOYMENT_CONFIG_NAME} --patch '{"spec":{"template":{"spec":{"containers":[{"name":"'${DEPLOYMENT_CONFIG_NAME}'","image":"registry.iotos.io/iotos/'$IMAGE_NAME':'$NEXT_VERSION'"}]}}}}'
oc rollout -n qs latest dc/$DEPLOYMENT_CONFIG_NAME
oc patch -n qs dc ${DEPLOYMENT_CONFIG_NAME} --patch '{"spec":{"template":{"spec":{"containers":[{"name":"'${DEPLOYMENT_CONFIG_NAME}'","image":"registry.iotos.io/iotos/'$IMAGE_NAME':latest"}]}}}}'
