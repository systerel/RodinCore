#!/bin/zsh
# vim: sw=4 sts=4 ts=8 noet ai
#
#  Migrate Rodin SMT plug-in sources from Subversion to Git
#
# The intent of this script is to be sourced, rather than run:
#
#   source ./migrate-svn-smt.sh
#

# Path to file containing Subversion authors
: ${SVN_AUTHORS:=$(pwd)/Authors.txt}

# Path to archive of final Rodin Subversion repository
: ${SVN_REPO:=$(pwd)/svn-rodin-b-sharp}

# Earliest interesting commit
: ${SVN_FIRST_COMMIT:=6903}

# Working directory
: ${WORK:=$(pwd)}

# Log from Subversion (got from core migration).
: ${SVN_LOG_FILE:=../svn2git/log-from-${SVN_FIRST_COMMIT}.txt}

# Find interesting paths in Subversion repository
find-interesting-paths() {
    # All paths of Decert and SMT plug-ins
    sed '/^   /!d' $SVN_LOG_FILE |
        grep -iE 'smt|decert' |
	sed 's/^   [ADMR] //' |
	sed 's/(from \/.*:[0-9]*)$//' |
	sort -u 
}

# Pattern for selecting paths to retrieve (derived from an analysis of the
# result of the previous function.

SMT_PATTERN='/trunk/(SMT|_exploratory/(fages|carinepascal))/'

# Create Git repository for Subversion import
create-git-svn-repo() {
    expl_paths="fages|carinepascal"
    trunk_paths="trunk/(?:SMT|_exploratory/(?:$expl_paths))"
    ignore_paths="^(?!$trunk_paths)"
    git svn init \
	-T /trunk \
	--ignore-paths=$ignore_paths \
	--rewrite-root=svn://svn.code.sf.net/p/rodin-b-sharp/svn \
	file://$SVN_REPO \
	SMT.git
    echo git svn fetch -A $SVN_AUTHORS
}

# Repair Git repository after import:
#  - remove empty commits
repair-git-repo() {
    git checkout -f master	# force cleanup
    git filter-branch --prune-empty -- --all
}

# Change path to SMT subdirectories
move-dirs-in-git-repo() {
    git checkout -f master	# force cleanup
    git filter-branch -f \
	--tree-filter 'if [ -d SMT ]; then mv SMT/* . && rmdir SMT; fi' \
	-- --all
}

# Cleanup Git repository, removing link to Subversion
cleanup-git-repo() {
    git init --bare --share=all ../SMT-bare.git
    git push ../SMT-bare.git trunk:master
}
