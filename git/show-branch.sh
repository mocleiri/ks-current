#!/bin/bash

	
usage () {
	MESSAGE=$1

	if test -n "$MESSAGE"
	then
		echo "ERROR: $MESSAGE"		
	fi

	echo "USAGE: <created from API branch name> <feature branch name>"
	exit 1;
}

# assuming everything is off of aggregate trunk

API_BRANCH=$1
FEATURE_BRANCH=$2

if test -z "$API_BRANCH"
then
	usage "Missing API Branch Name"
fi

if test -z "$FEATURE_BRANCH"
then
	usage "Missing Feature Branch Name"
fi



show_branch () {

	MODULE=$1
	MERGE_BASE_BRANCH=$2
	BRANCH=$3

	if test -z "$MODULE"
	then
		usage "missing module"
	fi

	if test -z "$MERGE_BASE_BRANCH"
	then
		usage "missing created from branch name"
	fi

	if test -z "$BRANCH"
	then
		usage "missing feature branch name"
	fi

	echo "=== Feature Branch Commits on $MODULE/$BRANCH relative to $MODULE/$MERGE_BASE_BRANCH ==="
	MERGE_BASE_SHA1=$(git --git-dir=$MODULE/.git merge-base -a $MODULE/$MERGE_BASE_BRANCH $MODULE/$BRANCH)
	
	git --git-dir=$MODULE/.git show-branch --sha1-name --topics --sparse $MERGE_BASE_SHA1 $MODULE/$BRANCH | grep "^\ +\ " | sed 's/^.*\[//' | sed 's/\].*$//' | while read SHA; do git --git-dir=$MODULE/.git log --pretty --format="%H:%an:%ad" --date=iso -n 1 $SHA | sed 's/\ +0000$//'; done;

}


show_branch ks-api $API_BRANCH $FEATURE_BRANCH 
show_branch ks-core trunk $FEATURE_BRANCH
show_branch ks-lum trunk $FEATURE_BRANCH
show_branch ks-enroll trunk $FEATURE_BRANCH
show_branch ks-deployments trunk $FEATURE_BRANCH
show_branch ks-parent trunk $FEATURE_BRANCH
