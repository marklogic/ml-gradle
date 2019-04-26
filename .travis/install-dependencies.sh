#!/bin/bash

if [ "${TRAVIS_SECURE_ENV_VARS}" = "true" ] ; then
  ./.travis/travis-install-ml.sh release
  ./.travis/setup-marklogic.sh
fi
