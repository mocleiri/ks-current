#!/bin/bash
#
# NAME: create-aggregate-branch.sh
#
# PURPOSE: To help creating aggregates
# 

usage () {
	MSG=$1

	if test -n "$MSG"
	then
		echo "ERROR: $MSG"
	fi

	echo "USAGE: <ks-api branch> <aggregate name> <modules> <in_branches:0 or 1>  <commit message> [<source prefix>]"
	echo "<ks-api branch>: either trunk or branches/some_branch"
	echo "<aggregate name>: in branches mode this is the name of the branch for each module.  i.e. ks-enroll/brances/aggregate_name"
	echo "<modules>:non-api based modules to apply"
	echo "<in_branches>:1 if $module/branches/aggregate_name is the target module location. 0 if $module is the target location. use 0 for sandbox based aggregates."
	echo "<commit message>: commit message to use"
	echo "<source_prefix>: optional. enrollment or sandbox or contrib/CM"
	exit 1

}

SVNMUCC_CMD=svnmucc

REPOSITORY="https://svn.kuali.org/repos/student"

API_SOURCE=$1

if test -z "$API_SOURCE"
then
	usage "Missing API Source"
fi

AGGREGATE_NAME=$2

if test -z "$AGGREGATE_NAME"
then
	usage "Missing Aggregate Name"
fi

MODULES=$3

if test -z "$MODULES"
then
	usage "Missing modules"
fi

IN_BRANCH=$4

if test -z "$IN_BRANCH"
then
	usage "Missing In branch mode"
fi

COMMIT_MESSAGE=$5

if test -z "$COMMIT_MESSAGE"
then
	usage "Missing Commit Message"
fi


SOURCE_PREFIX=$6

SOURCE_PATH=""

if test -z "$SOURCE_PREFIX"
then
	SOURCE_PATH="$REPOSITORY/enrollment"
else
	SOURCE_PATH="$REPOSITORY/$SOURCE_PREFIX"
fi

CMD_FILE=/tmp/$RANDOM.dat

#echo "CMD_FILE=$CMD_FILE"

printf "$SVNMUCC_CMD " > $CMD_FILE

printf "rm $SOURCE_PATH/ks-api/$API_SOURCE " >> $CMD_FILE

for M in $MODULES
do
	if test $IN_BRANCH == "1"
	then
		printf "rm $SOURCE_PATH/$M/branches/$AGGREGATE_NAME " >> $CMD_FILE

	else
		printf "rm $SOURCE_PATH/$AGGREGATE_NAME/$M " >> $CMD_FILE
	fi

done

printf " -m \"$COMMIT_MESSAGE\"" >> $CMD_FILE

cat $CMD_FILE

printf "\nApply Change (y/n)"

read command

if test $command == "y"
then
	bash -c $CMD_FILE
	echo "applied"
fi
