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
#

if ! git rev-parse --is-inside-work-tree > /dev/null; then
    echo "This script must be run from a Git working copy." >&2
    exit 1
fi

MAIN_DIR=$(git rev-parse --show-toplevel)

git_is_dirty() {
    test -n "$(git status --porcelain)"
}

case "$1" in
    RC*) RC="$1"_; shift ;;
esac

GIT_COMMIT=$(git log -1 --format='%h')

if git_is_dirty; then DIRTY="-dirty"; fi

SUFFIX="$RC$GIT_COMMIT$DIRTY"
echo "Building Rodin $SUFFIX"

mvn clean verify -Dversion-suffix="$SUFFIX" "$@" # -DskipTests -fae|-fn
git checkout -- "$MAIN_DIR"/org.rodinp.platform/plugin.properties
