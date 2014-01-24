#!/bin/sh
#
#
# show-feature-branch-changes.sh
#
# Show the feature branch changes for each svn:external 

usage () {

	MESSAGE=$1

	if test -n "$MESSAGE"
	then
		echo "$MESSAGE"
	fi

	echo "USAGE: <feature branch name>"
	exit 1
}


BASE_AGGREGATE=https://svn.kuali.org/repos/student/enrollment/aggregate/branches

FEATURE_BRANCH=$1

if test -z "$FEATURE_BRANCH"
then
	usage "Feature Branch Name Missing"
fi

AGGREGATE_BRANCH="$BASE_AGGREGATE/$FEATURE_BRANCH"

echo "Viewing Changes for $AGGREGATE_BRANCH"

echo "Viewing $AGGREGATE_BRANCH/pom.xml changes"

echo "Use arrow keys to move listing"
echo "Use <space> to decend a page at a time"
echo "Type q to quit thus listing"

svn log -v $AGGREGATE_BRANCH/pom.xml | less

svn propget svn:externals $AGGREGATE_BRANCH 2>&1 | while read -r LINE
do
	if test -n "$LINE"
	then

		if test "${LINE:0:1}" != "#"
		then
			MODULE_NAME=$(echo "$LINE" | cut -d' ' -f 1)
			MODULE_PATH=$(echo "$LINE" | cut -d' ' -f 3)

			echo "View Changes on $MODULE_NAME"
			echo "Use arrow keys to move listing"
			echo "Use <space> to decend a page at a time"
			echo "Type q to quit thus listing"

			svn log -v $MODULE_PATH | less
			
		fi

	fi
	
done 
# EOF
