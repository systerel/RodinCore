#!/bin/zsh
# vim: set fileencoding=utf-8
#
# Fix dates in copyright notices in Java sources for Rodin.
#
# Put the right date in copyright notices of Java source files, based on the
# commit date in Rodin.
#
# @author: Laurent Voisin

# Stop at the first error
set -e

# Name of temporary branch
MYBRANCH=fix-copyright-dates

PROG=$(basename $0)

usage() {
    cat <<-"EOF" >&2
	Usage: $PROG base-commit
	where base-commit is the commit to start from (typically master).
	EOF
    exit 1
}

fail() {
    echo "$@" >&2
    exit 1
}

parse_args() {
    [ $# -eq 1 ] || usage
    BASE_COMMIT=$1
}

ensure_clean_workspace() {
    git status
}

process_commit() {
    local commit=$1
    git --no-pager show --oneline --no-patch $commit

    # Copy the commit in the work branch
    git cherry-pick --ff $commit

    # Get the commit date
    local year=$(git --no-pager show --no-patch --pretty=format:%ai $commit |
	    cut -c1-4)

    # Fix all changed files
    git diff-tree --no-commit-id --name-status -r $commit |
	while read m f; do
	    case $m in
		([AM]) fix_file $f $year
	    esac
	done

    # Amend the cherry-picked commit if anything changed
    if ! git diff-files --quiet; then
	git commit --amend --all -C $commit
    fi
}

fix_file() {
    local file=$1 year=$2

    # Ignore non Java files
    [[ $file != *.java ]] && return

    # Create a temporary file
    TMPFILE=${TMPDIR:-/tmp}/fix-date.$$
    awk -v YEAR=$year $AWK_PROGRAM $file > $TMPFILE

    # Update real file only if changed
    if diff --ignore-all-space $file $TMPFILE > /dev/null; then
	/bin/rm $TMPFILE
    else
	print '  Fixed' $file
	/bin/mv $TMPFILE $file
    fi
}

AWK_PROGRAM='
    NR == 2 && /\(c\) [0-9]{4}, [0-9]{4}/ {
	match($0, /[0-9]{4}/)
	prefix = substr($0, 1, RSTART - 1)
	first = substr($0, RSTART, 4)
	second = substr($0, RSTART + 6, 4)
	suffix = substr($0, RSTART + 10)
	if (YEAR < second) {
	    print
	} else {
	    printf "%s%s, %s%s\n", prefix, first, YEAR, suffix
	}
	next
    }
    NR == 2 && /\(c\) [0-9]{4}/ {
	match($0, /[0-9]{4}/)
	prefix = substr($0, 1, RSTART - 1)
	found  = substr($0, RSTART, 4)
	suffix = substr($0, RSTART + 4)
	if (YEAR <= found) {
	    print
	} else {
	    printf "%s%s, %s%s\n", prefix, found, YEAR, suffix
	}
	next
    }
    { print }
'


# MAIN PROGRAM

parse_args "$@"
INIT_HEAD=$(git show-ref --hash --head '')
echo "Initial HEAD to come back in case of error: $INIT_HEAD"
COMMITS=($(git rev-list --no-merges --reverse $BASE_COMMIT..))
if ! git checkout -b $MYBRANCH $BASE_COMMIT; then
    fail "Remove leftover branch '$MYBRANCH' first"
fi
for c in $COMMITS; do
    process_commit $c
done

# vim: sw=4 sts=4 ai
