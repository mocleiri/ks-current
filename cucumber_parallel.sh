#!/bin/bash -xe

TEST_SITE=$1
PROFILE1=$2
PROFILE2=$3
DISTRIBUTED=${4-true}
FIREFOX_VERSION=${5-firefox14}
THREADS=${6-2}

# show the bundler version and invoke it to guarantee gems are installed as dictated by Gemfile.lock
bundle --version; bundle install

# Point cucumber at the correct Firefox version and show the firefox version
FIREFOX_PATH=/usr/bin/$FIREFOX_VERSION; $FIREFOX_PATH -version

# Cleanup test processes
$WORKSPACE/cleanup_test_processes.sh

# common args
ARGS="TEST_SITE=$TEST_SITE FIREFOX_PATH=$FIREFOX_PATH DISTRIBUTED_ENV=$DISTRIBUTED --format pretty --format json"

# Invoke Cucumber to run tests for the first profile
cucumber $ARGS --profile $PROFILE1 --out $WORKSPACE/cucumber1.json || true

# setup args for the parallel_test call
TEST_OPTIONS="$ARGS --profile $PROFILE2 --out $WORKSPACE/cucumber2.json"

# Invoke cucumber via parallel_test to execute the 2nd profile
parallel_test -n $THREADS -t cucumber --test-options "$TEST_OPTIONS"  $WORKSPACE/features