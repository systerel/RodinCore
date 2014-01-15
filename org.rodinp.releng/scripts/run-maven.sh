#!/bin/sh
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
SCRIPT=`readlink -m -n $0`
SCRIPTS_DIR=`dirname $SCRIPT`
MAIN_DIR=`readlink -m -n $SCRIPTS_DIR/../..`

cd $MAIN_DIR

GIT_COMMIT=`git log -1 --format='%h'`

case $1 in 
    RC* ) RC=$1_
	  shift ;;
esac

SUFFIX=$RC$GIT_COMMIT
echo "Building Rodin $SUFFIX"

mvn clean install -Dversion-suffix=$SUFFIX $@ # -DskipTests -fae|-fn
git checkout -- $MAIN_DIR/org.rodinp.platform/plugin.properties