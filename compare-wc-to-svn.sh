#!/bin/bash
#
# NAME: compare-wc-to-svn.sh
#
# PURPOSE: 
#
# for add, add tree conflicts lets compare the local working copy of a file with
# the inbound version.

usage () {
	MESSAGE=$1

	if test -n "$MESSAGE"
	then
		echo "$MESSAGE"
	fi

	echo "USAGE: <svn branch path> <file path>"
	exit 1
}

INBOUND_BRANCH=$1

if test -z "$INBOUND_BRANCH"
then
	usage "Missing svn branch"
fi

FILE_PATH=$2

if test -z "$FILE_PATH"
then
	usage "Missing file path"
fi

REPO_FILE_PATH="$INBOUND_BRANCH/$FILE_PATH"

REPO_PATH=/tmp/$RANDOM.dat

svn cat $REPO_FILE_PATH > $REPO_PATH

echo "diff $FILE_PATH $REPO_PATH"

kdiff3 $FILE_PATH $REPO_PATH

echo "Keep (w)orking copy or Copy (b)ranch version"
read CHOICE

if test "$CHOICE" == "w" 
then
	echo "Keeping the working copy"

elif test "$CHOICE" == "b"
then
	echo "Copying Branch Version into Working Copy"
	cp $REPO_PATH $FILE_PATH

fi


echo "Resolve $FILE_PATH (y/n)?"
read RESOLVE

if test "$RESOLVE" == "y"
then
	echo "svn resolve --accept working $FILE_PATH"
	svn resolve --accept working $FILE_PATH
else
	echo "Not resolving $FILE_PATH"
fi

rm $REPO_PATH

# EOF
