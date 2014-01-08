#!/bin/bash

#
# NAME: sync-merge-modules.sh
#
# 
# Used to sync a checked out aggregate with a series of upstream modules.
# 

SVN_REPO_BASE="https://svn.kuali.org/repos/student/enrollment"
SVN_MERGE_CMD="svn merge --non-interactive "


usage () {

	MSG=$1

	if test -n "$MSG"
	then
		echo "ERROR: $MSG"
	fi

	echo "USAGE: <modules to merge> <merge source> <merge aggregate pom.xml: 0 or 1>"
	echo "<modules to merge> : space seperated list of module names or all to autodetect."
	echo "<merge source> : space seperated list of module:path tuples."
	echo "<merge aggregate pom.xml>: if 1 then apply a 2 way merge."

	exit 1

}

# Extract the path for the module given.

#
# input: "module:path <space> module:path" target_module
#
# returns the path for the target_module
#
extractModulePath () {

	ARGS=$1
	
	TARGET_MOD=$2

	
	echo "$ARGS" | grep ":" 2>&1 >/dev/null
	R=$?

	
	if test $R == 1
	then
		# no module specified
		echo "$ARGS"
	else
	
		for P in $ARGS; do


			MOD=$(echo "$P" | cut -d: -f 1)
			MOD_PATH=$(echo "$P" | cut -d: -f 2)

			if test "$MOD" == "$TARGET_MOD"
			then 
				echo "$MOD_PATH"
			fi
		done
	fi
}

				
mergeModule () {

	MODULE="$1"

	if test -z "$MODULE"
	then
		usage "Missing Module to Merge"
	fi
	
	MERGE_PATH="$2"

	if test -z "MERGE_PATH"
	then
		usage "Missing Merge Path for Module '$MODULE'"
	fi

	echo "cd $MODULE; $SVN_MERGE_CMD $MERGE_PATH"
	cd $MODULE
	$SVN_MERGE_CMD $MERGE_PATH
	cd ..


}



MODULES_TO_MERGE=$1

if test -z "$MODULES_TO_MERGE"
then
	usage "Missing Modules to Merge"
fi

MODULE_MERGE_PATHS=$2

if test -z "$MODULE_MERGE_PATHS"
then
	usage "Missing Module Merge Paths"
fi

MERGE_AGGREGATE_POM=$3

if test -z "$MERGE_AGGREGATE_POM"
then
	usage "Missing Merge Aggregate Pom Flag"
fi

if test $MERGE_AGGREGATE_POM -eq 1
then

	AGGREGATE_BASE_PATH=$(extractModulePath "$MODULE_MERGE_PATHS" "aggregate")

	if test -z "$AGGREGATE_BASE_PATH"
	then
		echo "ERROR: Can't find Aggregate base path to merge pom with." >&2 
		exit 1
	fi

	echo "$SVN_MERGE_CMD \"$SVN_REPO_BASE/aggregate/$AGGREGATE_BASE_PATH/pom.xml\""
	$SVN_MERGE_CMD "$SVN_REPO_BASE/aggregate/$AGGREGATE_BASE_PATH/pom.xml"
fi


if test "$MODULES_TO_MERGE" == "all"
then

	# detect which svn:externals are here and merge them.

	svn propget svn:externals . 2>&1 | while read -r LINE
	do
		if test -n "$LINE"
		then

			if test "${LINE:0:1}" != "#"
			then
				M=$(echo "$LINE" | cut -d' ' -f 1)

				MODULE_BASE_PATH=$(extractModulePath "$MODULE_MERGE_PATHS" "$M")

				if test -z "$MODULE_BASE_PATH"
				then
					echo "ERROR: Can't find merge path for Module '$M'." >&2 
					exit 1
				fi

				mergeModule "$M" "$SVN_REPO_BASE/$M/$MODULE_BASE_PATH"

			fi

		fi
		
	done 
else
	# merge only the specified modules

	for M in $MODULES_TO_MERGE
	do	
		
		MODULE_BASE_PATH=$(extractModulePath "$MODULE_MERGE_PATHS" "$M")

		if test -z "$MODULE_BASE_PATH"
		then
			echo "ERROR: Can't find merge path for Module '$M'." >&2 
			exit 1
		fi

		mergeModule "$M" "$SVN_REPO_BASE/$M/$MODULE_BASE_PATH"
	done
fi


