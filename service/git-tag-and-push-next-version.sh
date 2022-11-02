#!/bin/bash -e

NEXT_VERSION=$(cat .newversion)
git tag ${NEXT_VERSION}
git push --tags
