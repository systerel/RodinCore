#!/bin/zsh
# vim: sw=4 sts=4 ts=8 noet ai
#
#  Migrate Rodin core platform sources from CVS to Git
#
# The intent of this script is to be sourced, rather than run:
#
#   source ./migrate-core.sh
#

# Path to archive of final Rodin CVS repository
: ${CVS_REPO_ARCHIVE:=../../Archive/cvs-rodin-b-sharp.tgz}

# Path to authors file
: ${AUTHORS_FILE:=$(pwd)/Authors.txt}

# Name of pseudo-module for cvsps
: ${CVS_ROOT_MODULE:=root}

# Working directory
: ${WORK:=$(pwd)}

# Create a local copy of the CVS repository
unpack-cvs-repo() {
    print "Unpacking CVS repository"
    cd $WORK
    tar zxf "$CVS_REPO_ARCHIVE"
    export CVSROOT="$WORK/cvs-rodin-b-sharp"
    cd "$CVSROOT"

    # Make the repository writable
    chmod -R u+w .

    # Keep only modules related to the core platform
    rm -rf AtelierBProvers B2Latex FeatureComposition
    rm -rf ReqsManagement SharedEventComposition
    rm -rf UML-B _exploratory/cfsnook com.* doc misc
    rm -rf org.animb.* org.eventb.xprover.*
}

# List all tags in the given CVS module
list-cvs-tags() {
    cvs -Q rlog "$@" |
	sed -n '/^symbolic names:$/,/^keyword subst/ p' |
	sed -n '/^	/ { s/^.\([^:]*\):.*$/\1/; p; }' |
	sort -u
}

# Rename plug-in version tags for the given CVS modules
#
#   The first module gives the prefix to use for renaming.
#   All dots and dashes are renamed to slashes which will be converted back to
#   dots by cvsps.
#
rename-cvs-tags() {
    print "Renaming tags for $1"

    local prefix=${1//./\/}
    prefix=${prefix//-/\/}
    list-cvs-tags $* |
	sed -n '/^V/ { s/^.//; p; }' |
	while read version; do
	    local new_tag=$prefix-${version//-/\/}
	    cvs -Q rtag -r V$version $new_tag $*
	    cvs -Q rtag -d V$version $*
	done
}

# Rename CVS tags by plug-in groups
normalize-cvs-tags() {
    # Remove useless tag
    cvs -Q rtag -d Root_UNDO_DEV .

    # Remove erroneous tag
    cvs -Q rtag -d V0-5-1 \
	org.eventb.core.seqprover \
	org.eventb.core.seqprover.tests

    # Normalize tag names
    rename-cvs-tags \
	fr.systerel.explorer \
	fr.systerel.explorer.tests
    rename-cvs-tags \
	org.eventb.core \
	org.eventb.core.tests
    rename-cvs-tags \
	org.eventb.core.ast \
	org.eventb.core.ast.tests
    rename-cvs-tags \
	org.eventb.core.seqprover \
	org.eventb.core.seqprover.tests
    rename-cvs-tags \
	org.eventb.doc.user
    rename-cvs-tags \
	org.eventb.eventBKeyboard \
	org.eventb.eventBKeyboard.tests
    rename-cvs-tags \
	org.eventb.ide
    rename-cvs-tags \
	org.eventb.ide-feature
    rename-cvs-tags \
	org.eventb.pp \
	org.eventb.pp.tests
    rename-cvs-tags \
	org.eventb.pp.ui
    rename-cvs-tags \
	org.eventb.pptrans \
	org.eventb.pptrans.tests
    rename-cvs-tags \
	org.eventb.ui \
	org.eventb.ui.tests
    rename-cvs-tags \
	org.rodinp
    rename-cvs-tags \
	org.rodinp-feature
    rename-cvs-tags \
	org.rodinp.core \
	org.rodinp.core.tests
    rename-cvs-tags \
	org.rodinp.platform
    rename-cvs-tags \
	org.rodinp.platform-feature
    rename-cvs-tags \
	org.rodinp.releng
}

# Group all modules under one umbrella (necessary for cvsps)
make-one-module() {
    mkdir $CVSROOT/$CVS_ROOT_MODULE
    mv $CVSROOT/[_fo]* $CVSROOT/$CVS_ROOT_MODULE
}

# Extract changesets from massaged CVS repository
extract-changesets() {
    cd $WORK
    CHANGESETS=changesets.txt
    # Value 30 found by trial and error until changeset is consistent with time
    cvsps -x -z 30 root > $CHANGESETS 2> cvsps-err.txt
}

# Create the Git repository from the CVS one
make-git-repo() {
    print "Importing into Git repository"

    cd $WORK

    # Remove lines that cause warnings in Git
    sed -i.bak -e '/^Branches:/d' $CHANGESETS

    # Fix missing ancestor branch
    perl -pi -e 's/$/\nAncestor branch: HEAD/
	    if m/^Branch: UNDO_DEV/ && !$seen++;' $CHANGESETS

    # Convert to Git
    git cvsimport -A $AUTHORS_FILE -C RodinCore -o master -s . \
	-P $CHANGESETS \
	-i -k -m -R \
	$CVS_ROOT_MODULE
}

rename-tag() {
    local old=$1
    local new=$2
    git tag $new $old
    git tag -d $old
}

# Finalize the Git repository
#   - rename tags
#   - merge branch UNDO_DEV back in master
finalize-git-repo() {
    cd $WORK/RodinCore

    # Organize plug-in tags in a hierarchy
    for t in $(git tag -l '*-*'); do
	rename-tag $t ${t//-/\/}
    done

    # Store integration tags in RodinCore
    for t in $(git tag -l 'I*'); do
	rename-tag $t RodinCore/$t
    done

    # Merge back UNDO_DEV branch
    echo $(git log -1 --until='2008-10-03 14:01:47 +0000' \
	    --format=format:'%H %P') \
	$(git show-ref -s UNDO_DEV) >> .git/info/grafts
    git branch -d UNDO_DEV    
    git config core.bare true
    git filter-branch -- --all
}

do-migrate() {
    unpack-cvs-repo
    normalize-cvs-tags
    make-one-module
    extract-changesets
    print "Fix commit messages in file $CHANGESETS"
    print "\t3950: Replace formula with \"¬ card(1‥x) > card(x‥3)\""
    print "\t4440: Replace 'R?utilisation de' by 'Reused'"
    print "\t4483: Replace with 'Nettoyage'"
    print "For VI, set fileencoding=utf-8 before saving."
    print "Press CTRL-Z now to edit the file, then type "fg" to resume."
    read response
    make-git-repo
    finalize-git-repo
}
