#!/bin/bash
#
# compare-revs.sh
#
# Note on windows needs to be run within the git mingw window vs the standard
# mingw window.
# 
# BY_JIRA emits in the confluence wiki table markup, suitable for pasting 
# into a jira.


usage() {
	
	echo "USAGE: <mode: SHOW | BY_JIRA> | BY_JIRA_STDIN"
	echo "SHOW: <changes in branch> <since branch> [gitk]"
	echo "gitk: trigger showing of commits in gitk"
	echo "BY_JIRA: <changes in branch> <since branch> <wiki mode: 0 or 1 (jira) or 2(confluence)> <stdin: 0 or 1 > <show_headers: 0 or 1> <show sha: 0 or 1>"
	exit 1
}
checkParam () {

	PARAM=$1
	
	if test -z $PARAM
	then
		usage
	fi
}

computeRev () {

	SHA1=$1

	REV=$(git log $SHA1 -n 1 | grep git-svn-id | awk '{print $2}' | cut -d@ -f 2)

	echo $REV
}

MODE=$1

checkParam $MODE

if test "$MODE" == "SHOW"
then
	
	CHANGES_IN=$2
	SINCE=$3

	COMMAND="git log --first-parent "

	if test "$4" == "gitk"
	then
		COMMAND="gitk --first-parent "
	fi

	checkParam $CHANGES_IN
	checkParam $SINCE

	echo "$COMMAND $SINCE..$CHANGES_IN"
	echo "Press any key to continue"
	read F
	$COMMAND $SINCE..$CHANGES_IN

elif test "$MODE" == "BY_JIRA"
then
	CHANGES_IN=$2
	SINCE=$3
	WIKI_MODE=$4
	USE_STDIN=$5
	SHOW_HEADERS=$6
	SHOW_SHA=$7

	checkParam $CHANGES_IN
	checkParam $SINCE
	checkParam $WIKI_MODE
	checkParam $USE_STDIN
	checkParam $SHOW_HEADERS
	checkParam $SHOW_SHA

	JIRAS=""

	if test $USE_STDIN -eq 0
	then
		JIRAS=$(git log --first-parent $SINCE..$CHANGES_IN --pretty --format="%H?%an?%s?%cD") 

	else
		# Read the sha1's from stdin
		JIRAS=$(while read SHA
		do
			git log -n 1 $SHA --pretty --format="%H?%an?%s?%cD"	
		done)
	fi

	if test $SHOW_HEADERS -eq 1
	then
		if test 0 -eq $WIKI_MODE
		then
			if test 1 -eq $SHOW_SHA
			then
				echo "SHA1::JIRA::DATE::AUTHOR::REV"
			else
				echo "JIRA::DATE::AUTHOR::REV"
			fi
		else
			if test 1 -eq $SHOW_SHA
			then
				echo "||SHA1||JIRA||DATE||AUTHOR||REV||"
			else
				echo "||JIRA||DATE||AUTHOR||REV||"
			fi
		fi
	fi

	printf "%s" "$JIRAS" | while IFS= read -r LINE
	do

		SHA1=$(echo "$LINE" | cut -d'?' -f 1)
		AUTHOR=$(echo "$LINE" | cut -d'?' -f 2)
		JIRA=$(echo "$LINE" | cut -d'?' -f 3 | cut -d' ' -f 1)
		CDATE=$(echo "$LINE" | cut -d'?' -f 4)

		REV=$(computeRev $SHA1)

		if test 0 -eq $WIKI_MODE
		then
			if test 1 -eq $SHOW_SHA
			then
				echo "$SHA1::https://jira.kuali.org/browse/$JIRA::$CDATE::$AUTHOR::https://fisheye.kuali.org/changelog/ks?cs=$REV"
			else
				
				echo "https://jira.kuali.org/browse/$JIRA::$CDATE::$AUTHOR::https://fisheye.kuali.org/changelog/ks?cs=$REV"
			fi
		else
		
			FORMATTED_JIRA=$JIRA
			
			if test 2 -eq $WIKI_MODE
			then

				FORMATTED_JIRA="[$JIRA|https://jira.kuali.org/browse/$JIRA]"	
			fi
			
			if test 1 -eq $SHOW_SHA
			then
				echo "|$SHA1|$FORMATTED_JIRA|$CDATE|$AUTHOR|[$REV|https://fisheye.kuali.org/changelog/ks?cs=$REV]|"
			else
				echo "|$FORMATTED_JIRA|$CDATE|$AUTHOR|[$REV|https://fisheye.kuali.org/changelog/ks?cs=$REV]|"
			fi
		fi
		
	done
	

else
	echo "Invalid Mode: $MODE"
	usage
fi
