#!/bin/sh
###############################################################################
# Copyright (c) 2013 Systerel and others.
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
echo "Building Rodin with Maven for git commit: $GIT_COMMIT"

mvn clean install -Dgit-commit=$GIT_COMMIT $@ # -DskipTests -fn 
