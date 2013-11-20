#!/bin/zsh
# vim: sw=4 sts=4 ts=8 noet ai
#
#  Migrate Rodin core platform sources from Subversion to Git
#
# The intent of this script is to be sourced, rather than run:
#
#   source ./migrate-core-from-svn.sh
#

# Path to archive of final Rodin Subversion repository
: ${SVN_REPO_ARCHIVE:=/tmp/svn}

# Path to svn export tool
: ${SVN_FAST_EXPORT:=$(pwd)/svn-all-fast-export}

# Path to authors file
: ${AUTHORS_FILE:=$(pwd)/Authors.txt}

# Path to rules file
: ${RULES_FILE:=$(pwd)/RodinCore.rules}

# Working directory
: ${WORK:=$(pwd)}

# List all paths that ever existed in the Subversion repository
list-svn-paths() {
    svneverever --flatten --tags --branches --no-dots \
	$SVN_REPO_ARCHIVE > svn-paths.txt
}

# Create the Git repository from the Subversion one
make-git-repo() {
    print "Importing into Git repository"
    cd $WORK
    $SVN_FAST_EXPORT \
	--identity-map $AUTHORS_FILE \
	--rules $RULES_FILE \
	--add-metadata $SVN_REPO_ARCHIVE
}

rename-tag() {
    local old=$1
    local new=$2
    git tag $new $old
    git tag -d $old
}

# Finalize the Git repository: Archive old branches as tags
finalize-git-branches() {
    cd $WORK/RodinCore

    # Archive old branches
    for t in $(git tag -l 'backups/*'); do
	rename-tag $t ${t//backups/archive}
    done

    # Archive Simplifier branch
    git tag archive/Simplifier Simplifier
    git update-ref -d refs/heads/Simplifier
}

# Finalize the Git repository: Connect tags to master.
finalize-git-tags() {
    cd $WORK/RodinCore

    # Map from platform release to Subversion revision
    local -A revision
    revision=(
	1.3     r8725    # officially r8719, but change only in tests
	2.0RC1  r9848
	2.0     r9964
	2.5     r14636   # Mais problème casse de fichier.
	2.7RC1  r15200   # Idem
	2.7     r15250   # Idem
    )
    for rel in ${(k)revision}; do
	local rev=${revision[$rel]//r/}
	local parent=$(git log --grep=revision=$rev --format=%H) 
	local commit=$(git show-ref -s refs/tags/RodinCore/$rel)
	print $commit $parent >> info/grafts
    done
}

do-migrate() {
    make-git-repo
    finalize-git-branches
    finalize-git-tags
}
