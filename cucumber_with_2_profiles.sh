#!/bin/bash -xe

TEST_SITE=$1
PROFILE1=$2
PROFILE2=$3
DISTRIBUTED=${4-true}
FIREFOX_VERSION=${5-firefox14}

# show the bundler version, invoke bundler to install Gems as dictated by Gemfile.lock
bundle --version; bundle install

# Point cucumber at the correct Firefox  version.  Show the firefox version
FIREFOX_PATH=/usr/bin/$FIREFOX_VERSION; $FIREFOX_PATH -version

# Cleanup test processes
$WORKSPACE/cleanup_test_processes.sh

# common args
ARGS="TEST_SITE=$TEST_SITE FIREFOX_PATH=$FIREFOX_PATH DISTRIBUTED_ENV=$DISTRIBUTED --format pretty --format json"

# Invoke Cucumber to run the tests
cucumber $ARGS --profile $PROFILE1 --out $WORKSPACE/cucumber1.json
cucumber $ARGS --profile $PROFILE2 --out $WORKSPACE/cucumber2.json

