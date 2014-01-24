#!/bin/bash
#
# Merge a build update site into the production update site
#

# Location of Eclipse distribution to use, override in the environment if needed
: ${ECLIPSE_HOME:="$HOME"/progs/eclipse}

SCRIPT_DIR=$(readlink -f $(dirname $0))
CATEGORY_FILE="$SCRIPT_DIR"/../../org.rodinp.platform.repository/category.xml

CoreUpdateSite=http://rodin-b-sharp.sourceforge.net/core-updates

BuildUpdateSite=/tmp/rodin-build/rodin-3.0/update-site
MergeUpdateSite=/tmp/merge-update-site

fetch-index() {
    local src=$1
    local dest=$2

    wget -P $dest $src/artifacts.jar $src/content.jar
}

mirror() {
    local src=$1
    local dest=$2

    # Mirror the metadata
    $ECLIPSE_HOME/eclipse -nosplash -verbose \
	-application org.eclipse.equinox.p2.metadata.repository.mirrorApplication \
	-source $src \
	-destination $dest

    # Mirror the artifacts
    $ECLIPSE_HOME/eclipse -nosplash -verbose \
	-application org.eclipse.equinox.p2.artifact.repository.mirrorApplication \
	-source $src \
	-destination $dest
}

categorize() {
    local dest=$1

    $ECLIPSE_HOME/eclipse -nosplash -verbose \
        -application org.eclipse.equinox.p2.publisher.CategoryPublisher \
	-metadataRepository file:/$dest/ \
	-categoryDefinition file:/$CATEGORY_FILE
}

rm -rf $MergeUpdateSite

fetch-index $CoreUpdateSite $MergeUpdateSite
mirror $BuildUpdateSite $MergeUpdateSite
categorize $MergeUpdateSite
