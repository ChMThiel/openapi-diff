#!/bin/bash

DUPLICATE_SWAGGER_OPERATION_IDS=$(grep -E "@Operation" -r . | grep -o -E "operationId.?=.?\"[A-Za-z\.]*\"" | cut -d "=" -f 2 | tr -d '"' | sort | uniq -c | sort | awk '$1 >= 2')

if [[ -n "$DUPLICATE_SWAGGER_OPERATION_IDS" ]]
then
  echo "Duplicate Swagger operationIds found, aborting: $DUPLICATE_SWAGGER_OPERATION_IDS"
  exit 1
fi