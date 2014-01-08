#!/bin/bash
#
# NAME: archive-feature-branch.sh
#
# PURPOSE: To help creating aggregates
# 

usage () {
	MSG=$1

	if test -n "$MSG"
	then
		echo "ERROR: $MSG"
	fi

	echo "USAGE: <aggregate name> <commit message> "
	echo "<aggregate name>: in branches mode this is the name of the branch for each module.  i.e. ks-enroll/brances/aggregate_name"
	echo "<commit message>: commit message to use"
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

AGGREGATE_NAME=$1

if test -z "$AGGREGATE_NAME"
then
	usage "Missing Aggregate Name"
fi

COMMIT_MESSAGE=$2

if test -z "$COMMIT_MESSAGE"
then
	usage "Missing Commit Message"
fi

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

AGGREGATE_BRANCH="$REPOSITORY/enrollment/aggregate/branches/$AGGREGATE_NAME"
ARCHIVED_AGGREGATE_BRANCH="$REPOSITORY/enrollment/aggregate/branches/inactive/$AGGREGATE_NAME"

printf "$SVNMUCC_CMD " > $CMD_FILE

printf "mv $AGGREGATE_BRANCH $ARCHIVED_AGGREGATE_BRANCH " >> $CMD_FILE

svn propget svn:externals $AGGREGATE_BRANCH 2>1 | while read -r LINE
do
	if test -n "$LINE"
	then

		if test "${LINE:0:1}" != "#"
		then
			M=$(echo "$LINE" | cut -d' ' -f 1)

			MODULE_BRANCH="$REPOSITORY/enrollment/$M/branches/$AGGREGATE_NAME"
			ARCHIVED_MODULE_BRANCH="$REPOSITORY/enrollment/$M/branches/inactive/$AGGREGATE_NAME"
	
			printf "mv $MODULE_BRANCH $ARCHIVED_MODULE_BRANCH " >> $CMD_FILE

			printf "$M $ARCHIVED_MODULE_BRANCH\n" >> $SVN_EXTERNALS_FILE
			
		fi

	fi
	
done 


printf " -m \"$COMMIT_MESSAGE\"" >> $CMD_FILE

printf "\nSVN COMMAND:\n"
cat $CMD_FILE

printf "\nSVN EXTERNALS\n"
cat $SVN_EXTERNALS_FILE

printf "\nApply Change (y/n)"

read command

if test $command == "y"
then
	bash $CMD_FILE

	R=$?


	if test 0 -eq $R
	then
	

		WORKING_COPY="/tmp/$RANDOM-aggregate-working-copy"

		rm -rf $WORKING_COPY

		svn co --depth immediates $ARCHIVED_AGGREGATE_BRANCH $WORKING_COPY

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

# now if that worked we should be able to set the svn externals

