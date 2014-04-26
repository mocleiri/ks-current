#!/bin/bash

# show the bundler version, invoke bundler to install Gems as dictated by Gemfile.lock
bundle --version; bundle install

# Point cucumber at the correct Firefox  version.  Show the firefox version
FIREFOX_PATH=/usr/bin/firefox$FIREFOX_VERSION; $FIREFOX_PATH -version

# Cleanup test processes
$WORKSPACE/cleanup_test_processes.sh

# common args
ARGS="TEST_SITE=$TEST_SITE FIREFOX_PATH=$FIREFOX_PATH DISTRIBUTED_ENV=$DISTRIBUTED --format pretty --format json"

# Invoke Cucumber to run the tests
cucumber $ARGS --profile $PROFILE1 --out $WORKSPACE/cucumber1.json
cucumber $ARGS --profile $PROFILE2 --out $WORKSPACE/cucumber2.json

