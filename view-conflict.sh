#!/bin/bash
#
# view-conflict.sh
#
#
# A helper to launch k3diff for the identified conflict
#

usage () {
	echo "USAGE: <mode: SHOW|VIEW|RESOLVE>"
	echo "SHOW: no args"
	echo "VIEW: no args"
	echo "RESOLVE: [TREE] "
	exit 1
}

MODE=$1

if test -z $MODE
then
	usage
fi

if [ "$MODE" == "VIEW" ]; 
then 

	P=$(svn status | egrep ^C | awk '{print $2}' | head -n 1 | sed 's/\\/\//g')


	if test -z $P
	then
		echo "No File conflict to view"
		exit 2
	fi
	echo "P=$P"
	echo "D=$D"

	#echo "ls -l ${P}*"
	#ls -l "${P}*"

	DIR=$(dirname $P)
	FILE=$(basename $P)

	#LEFT_FILE="$DIR/$FILE"

	#echo "DIR=$DIR"
	#echo "FILE=$FILE"

	K=$(ls ${P}* | grep merge | xargs echo "kdiff3 -m -o $P")

	RVAL=$($K)

	echo "RVAL=$RVAL"

	echo "finished with $P"
	#k3diff -m -o $FILE

elif [ "$MODE" == "SHOW" ];
then

	echo "Merge Conflicts"
	svn status | grep ^C

	echo "Tree Conflicts"
	svn status | grep -A 1 ^!

elif [ "$MODE" == "RESOLVE" ];
then

	TYPE=$2

	if test -z $TYPE
	then
		# normal case

		# assume standard
		P=$(svn status | grep ^C | awk '{print $2}' | head -n 1 | sed 's/\\/\//g')

		$(echo "svn resolve --accept working $P")
		$(svn resolve --accept working $P)

	else
		# make sure the type is TREE

		if [ "$TYPE" == "TREE" ]; then
			# resolve the tree conflict
			P=$(svn status | grep ^! | awk '{print $3}')
			echo $P
		fi

	fi
else
	echo "Unknown mode: $MODE"
	usage
fi

