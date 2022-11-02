#!/bin/bash

# $1: build type
# $2: commit id

BUILD_TYPE=$(echo "$1" | tr '[:lower:]' '[:upper:]')
COMMIT_ID=$2


log() {
  echo "[set-next-version] $@"
}

if [[ -z "$BUILD_TYPE" ]] || [[ ! $BUILD_TYPE =~ (MAJOR|MINOR|BUGFIX|DEVEL)$ ]]
then
  log "Error: Invalid BUILD_TYPE $BUILD_TYPE, must be one of MAJOR MINOR BUGFIX DEVEL"
  exit 1
fi


if [[ -z $COMMIT_ID ]]
then
  log "Error: COMMIT_ID must not be empty!"
  exit 2
fi

BUILD_DATE=$(date +"%Y%m%d%H%M" -u)


# $1: BUILD_TYPE
# $2: COMMIT_ID
get_next_version() {
local BUILD_TYPE=$1
local COMMIT_ID=$2
  NEW_VERSION="DEVEL"

  if [[ "$BUILD_TYPE" != "DEVEL" ]]
  then
    if ! git branch --contains $COMMIT_ID 2>/dev/null >/dev/null
    then
      log "Error: COMMIT_ID $COMMIT_ID was not found in repository"
      exit 3
    fi

    log "Checking out $COMMIT_ID"
    git checkout $COMMIT_ID 2>/dev/null >/dev/null

    CURRENT_TAG_ON_COMMIT=$(git tag -l --points-at ${COMMIT_ID})

    if [[ -n ${CURRENT_TAG_ON_COMMIT} ]]
    then
      echo "Revision ${COMMIT_ID} is already tagged (${CURRENT_TAG_ON_COMMIT}), aborting..."
      exit 4
    fi

    LATEST_TAG=$(git describe --abbrev=0 --tags $COMMIT_ID 2>/dev/null)

    if [[ -z "$LATEST_TAG" ]]
    then
      NEW_VERSION="1.0.0"
      LATEST_TAG="(empty/no tag)"
    else
      MAJOR=$(echo $LATEST_TAG | cut -d. -f1)
      MINOR=$(echo $LATEST_TAG | cut -d. -f2)
      BUGFIX=$(echo $LATEST_TAG | cut -d. -f3)
      case $BUILD_TYPE in
        MAJOR) MAJOR=$(echo $[$MAJOR+1]); MINOR=0; BUGFIX=0 ;;
        MINOR) MINOR=$(echo $[$MINOR+1])  BUGFIX=0 ;;
        BUGFIX) BUGFIX=$(echo $[$BUGFIX+1]) ;;
      esac
      NEW_VERSION=$(echo $MAJOR.$MINOR.$BUGFIX)
    fi

    log "Latest tag was $LATEST_TAG, updating to $NEW_VERSION (due to $BUILD_TYPE release; build date: $BUILD_DATE)"
  fi

  # export new version to parent process
  rm .newversion 2>/dev/null >/dev/null
  echo $NEW_VERSION > .newversion
}

prepare_version_in_repo() {
  JAVA_VERSION_CLASS=$(find . -iname "Version.java" | head -n 1)

  if [[ -f "$JAVA_VERSION_CLASS" ]]
  then
    # Java Version class found -> replace version and buildDate
    sed -i -e "s/VERSION.*=.*$/VERSION = \"$NEW_VERSION\";/g" $JAVA_VERSION_CLASS
    sed -i -e "s/BUILDDATE.*=.*$/BUILDDATE = \"$BUILD_DATE\";/g" $JAVA_VERSION_CLASS
  else
    # Assume Nodejs build
    mkdir -p public
    echo "{" > public/version.json
    echo "  \"version\": \"$NEW_VERSION\"," >> public/version.json
    echo "  \"buildDate\": \"$BUILD_DATE\"" >> public/version.json
    echo "}" >> public/version.json
  fi

  if [[ "$BUILD_TYPE" != "DEVEL" ]]
  then
    # replace 1st occurrence of "1.0-SNAPSHOT" with the actual version in all pom.xml
    find . -iname "pom.xml" | xargs sed -ie "s/1.0-SNAPSHOT/$NEW_VERSION/1" || true
  fi
}

get_next_version $BUILD_TYPE $COMMIT_ID
prepare_version_in_repo
