#!/bin/bash

# Can't run "test" because it exceeds the time limit for a Travis job
# Also get intermittent 503 errors when running via Travis

if [ "${TRAVIS_SECURE_ENV_VARS}" = "true" ] ; then
  ./gradlew clean compiletestjava
fi
