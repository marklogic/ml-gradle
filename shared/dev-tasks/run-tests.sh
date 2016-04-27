#!/bin/bash

if [ "${TRAVIS_SECURE_ENV_VARS}" = "true" ] ; then
  cd ${TRAVIS_BUILD_DIR}
  ./gradlew test -i
fi
