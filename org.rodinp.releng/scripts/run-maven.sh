#!/bin/bash
###############################################################################
# Copyright (c) 2013, 2014 Systerel and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#     Systerel - initial API and implementation
###############################################################################
#
#  This script must be run from a working copy of the Rodin platform sources.
#  Since Rodin 3.3, Java 8 is required for building.
#

# Check Java version, Maven searches in JAVA_HOME first
if [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo "Found java executable in JAVA_HOME"
    _java="$JAVA_HOME/bin/java"
else
    echo "Using java from PATH"
    _java=java
fi
if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | grep 'version' | sed 's/.*version .*\.\(.*\)\..*/\1/; 1q')
    echo "Java version: $version"
    if [[ "$version" -lt "8" ]]; then
        echo "Java 8 is required"
	exit 1
    fi
fi

# Check Git repository
if ! git rev-parse --is-inside-work-tree > /dev/null; then
    echo "This script must be run from a Git working copy." >&2
    exit 1
fi

MAIN_DIR=$(git rev-parse --show-toplevel)

git_is_dirty() {
    test -n "$(git status --porcelain)"
}

case "$1" in
    RC*) RC="-$1"; shift ;;
esac

GIT_COMMIT=$(git log -1 --format='%h')

if git_is_dirty; then DIRTY="-dirty"; fi

SUFFIX="$RC-$GIT_COMMIT$DIRTY"
echo "Building Rodin $SUFFIX"

cd "$MAIN_DIR"
mvn clean verify -Dversion-suffix="$SUFFIX" "$@" # -DskipTests -fae|-fn
