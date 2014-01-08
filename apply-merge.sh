#!/bin/bash
#
# apply-merge.sh
#
# Used to cherry-pick a specific revision 

usage() {

	echo "USAGE: $0 <path to pick from> <rev(s) to cherry pick>"
	echo "pass in multiple revs in a quoted string"
	exit 1
}

SVN_PATH=$1
if test -z $SVN_PATH
then
	usage
fi


REVS=$2

if test -z "$REVS" 
then
	usage
fi

for REV in $REVS
do
	echo "svn merge -c $REV $SVN_PATH"
	svn merge -c $REV $SVN_PATH
done


# EOF
