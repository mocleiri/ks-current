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

	echo "USAGE: <ks-api branch> <aggregate name> <modules> <in_branches:0 or 1>  <source branches> <commit message> <source revision> <recreate branches:0 or 1> <apply aggregate svn:externals: 0 or 1> [<source prefix> <target prefix: like sandbox> ]"
	echo "<ks-api source branch>: either trunk or branches/some_branch"
	echo "<aggregate name>: in branches mode this is the name of the branch for each module.  i.e. ks-enroll/brances/aggregate_name"
	echo "<modules>: non-api based modules to apply, none if only api"
	echo "<in_branches>:1 if $module/branches/aggregate_name is the target module location. 0 if $module is the target location. use 0 for sandbox based aggregates."
	echo "<source branches>: trunk or branches/source-branch or space separated list of module:path elements."
	echo "<commit message>: commit message to use"
	echo "<source revision>: 0 will find out the current revision. >0 will use the indicated revision."
	echo "<recreate branches>:1 will deleted any existing feature branch and replace it with the source branch"
	echo "<apply aggregate svn:externals> 0 will not apply; 1 will apply svn:externals for the modules in the aggregate."
	echo "<source_prefix>: optional. enrollment or sandbox or contrib/CM"
	echo "<target_prefix>: optional. enrollment or sandbox or contrib/CM"
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

SVNMUCC_CMD=svnmucc

REPOSITORY="https://svn.kuali.org/repos/student"

API_SOURCE=$1

if test -z "$API_SOURCE"
then
	usage "Missing Api Source"
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

SOURCE_BRANCH=$5

if test -z "$SOURCE_BRANCH"
then
	usage "Missing Source Branch"
fi

COMMIT_MESSAGE=$6

if test -z "$COMMIT_MESSAGE"
then
	usage "Missing Commit Message"
fi

SOURCE_REVISION=$7

if test -z "$SOURCE_REVISION"
then
	usage "Missing Source Revision"
fi

REPLACE_EXISTING_BRANCHES=$8

if test -z "$REPLACE_EXISTING_BRANCHES"
then
	usage "Missing Replace Existing Branches"
fi

APPLY_AGGREGATE_EXTERNALS=$9

if test -z "$APPLY_AGGREGATE_EXTERNALS"
then
	usage "Missing Apply Aggregate Externals Setting"
fi



SOURCE_PREFIX=${10}

SOURCE_PATH=""

if test -z "$SOURCE_PREFIX"
then
	SOURCE_PATH="$REPOSITORY/enrollment"
else
	SOURCE_PATH="$REPOSITORY/$SOURCE_PREFIX"
fi

TARGET_PREFIX=${11}

TARGET_PATH=""

if test -z "$TARGET_PREFIX"
then
	TARGET_PATH="$REPOSITORY/enrollment"
else
	TARGET_PATH="$REPOSITORY/$TARGET_PREFIX"
fi

SOURCE_REV=""

if test 0 -eq "$SOURCE_REVISION"
then
	SOURCE_REV=$(svn info https://svn.kuali.org/repos/student | grep Revision: | cut -d: -f 2 | sed 's/^\ //')
else
	SOURCE_REV=$SOURCE_REVISION
fi

# echo $SOURCE_REV
# echo "TARGET_PATH=$TARGET_PATH"

CMD_FILE=/tmp/$RANDOM.dat

SVN_EXTERNALS_FILE=/tmp/externals-$RANDOM.dat

cat <<EOF > $SVN_EXTERNALS_FILE
#
# The svn.externals file (indirectly) controls what local directories appear
# in the checkout, and what actual location inside Subversion they represent
# (by tying in with the svn:externals property).
# 
# This enables development streams to be independently managed and released 
# while also providing a single spot to perform an aggregated Kuali Student checkout
# and build.
#
# The SVN command for using this file to create SVN external definitions is
# 
# svn propset svn:externals -F svn.externals .
#
# *** IMPORTANT ***
# You must rerun the svn propset command (shown above) and commit after editing any of
# the external paths for the changes to take effect.
#
EOF

#echo "CMD_FILE=$CMD_FILE"

printf "$SVNMUCC_CMD " > $CMD_FILE

if test 1 -eq $REPLACE_EXISTING_BRANCHES
then
	printf "rm " >> $CMD_FILE

	if test $IN_BRANCH == "1"
	then
		printf "    $TARGET_PATH/ks-api/branches/$AGGREGATE_NAME " >> $CMD_FILE

	else
		printf "    $TARGET_PATH/$AGGREGATE_NAME/ks-api  " >> $CMD_FILE
	fi

	for M in $MODULES
	do
		if test "$M" == "none"
		then
			continue
		fi

		printf "rm " >> $CMD_FILE

		if test $IN_BRANCH == "1"
		then
			printf "    $TARGET_PATH/$M/branches/$AGGREGATE_NAME " >> $CMD_FILE

		else
			printf "    $TARGET_PATH/$AGGREGATE_NAME/$M " >> $CMD_FILE
		fi

	done

fi

printf "cp $SOURCE_REV $SOURCE_PATH/ks-api/$API_SOURCE " >> $CMD_FILE

printf "ks-api " >> $SVN_EXTERNALS_FILE

if test $IN_BRANCH == "1"
then
	printf "    $TARGET_PATH/ks-api/branches/$AGGREGATE_NAME " >> $CMD_FILE
	printf " $TARGET_PATH/ks-api/branches/$AGGREGATE_NAME\n" >> $SVN_EXTERNALS_FILE

else
	printf "    $TARGET_PATH/$AGGREGATE_NAME/ks-api  " >> $CMD_FILE
	printf " $TARGET_PATH/$AGGREGATE_NAME/ks-api\n" >> $SVN_EXTERNALS_FILE
fi

for M in $MODULES
do
	if test "$M" == "none"
	then
		continue
	fi

	if test "$M" != "aggregate"
	then

		printf "$M " >> $SVN_EXTERNALS_FILE
	fi

	# printf "module = $M"

	SOURCE_MOD_BRANCH=$(extractModulePath "$SOURCE_BRANCH" $M)

	if test -z "$SOURCE_MOD_BRANCH"
	then
		echo "ERROR: no source branch for module: $M"
		exit 1;
	fi

	printf "cp $SOURCE_REV $SOURCE_PATH/$M/$SOURCE_MOD_BRANCH " >> $CMD_FILE

	if test $IN_BRANCH == "1"
	then
		printf "    $TARGET_PATH/$M/branches/$AGGREGATE_NAME " >> $CMD_FILE
		if test "$M" != "aggregate"
		then
			printf " $TARGET_PATH/$M/branches/$AGGREGATE_NAME\n" >> $SVN_EXTERNALS_FILE
		fi

	else
		printf "    $TARGET_PATH/$AGGREGATE_NAME/$M " >> $CMD_FILE
		
		if test "$M" != "aggregate"
		then
			printf " $TARGET_PATH/$AGGREGATE_NAME/$M\n" >> $SVN_EXTERNALS_FILE
		fi
	fi

done

printf " -m \"$COMMIT_MESSAGE\"" >> $CMD_FILE

printf "\nSVN COMMAND:\n"
cat $CMD_FILE

if test 1 -eq $APPLY_AGGREGATE_EXTERNALS
then

	printf "\nSVN EXTERNALS\n"
	cat $SVN_EXTERNALS_FILE
fi

printf "\nApply Change (y/n)"

read command

if test $command == "y"
then
	bash $CMD_FILE

	R=$?


	if test 0 -eq $R
	then
	
		if test 1 -eq $APPLY_AGGREGATE_EXTERNALS
		then

			TARGET_AGGREGATE=""

			if test $IN_BRANCH == "1"
			then
				TARGET_AGGREGATE="$TARGET_PATH/aggregate/branches/$AGGREGATE_NAME"

			else
				TARGET_AGGREGATE="$TARGET_PATH/$AGGREGATE_NAME/aggregate"
			fi

			WORKING_COPY="/tmp/$RANDOM-aggregate-working-copy"

			rm -rf $WORKING_COPY

			svn co --depth immediates $TARGET_AGGREGATE $WORKING_COPY

			bash -c "cd $WORKING_COPY && cp $SVN_EXTERNALS_FILE svn.externals && svn propset svn:externals -F svn.externals . && svn commit -m\"$COMMIT_MESSAGE\""

		fi

		echo "applied"

		echo "REMEMBER to CHANGE the POM Version for the new Aggregate"
		echo "REMEMBER to CHANGE the SCM path to the aggregate branch.  Use the branch name not the pom version."
		echo "(1) mvn externals:validatepoms -N"
		echo "(2) mvn validate -Pscm -N"
		
	else
		echo "failed"

	fi
fi

# now if that worked we should be able to set the svn externals

