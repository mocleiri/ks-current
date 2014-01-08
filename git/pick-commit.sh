#!/bin/bash

	
usage () {
	MESSAGE=$1

	if test -n "$MESSAGE"
	then
		echo "ERROR: $MESSAGE"		
	fi

	echo "USAGE: <SHA-1> [theirs]"
	echo "if theirs then take the full changes without merging"
	exit 1;
}

# assuming everything is off of aggregate trunk

SHA1=$1
MERGE_OPTION=$2

if test -z "$SHA1"
then
	usage "Missing Target SHA1"
fi

git log -n 1 -p $SHA1

echo "Apply? ([M]erge/[T]heirs/[S]kip)"

read RESPONSE

if test "$RESPONSE" == "M"
then
	
	echo "git cherry-pick $SHA1"
	git cherry-pick $SHA1

elif test "$RESPONSE" == "T"
then
	echo "git cherry-pick --strategy=recursive -X theirs $SHA1"
	git cherry-pick --strategy=recursive -X theirs $SHA1

else
	echo "Skipped $SHA1"
fi
