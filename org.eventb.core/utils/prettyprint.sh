#!/bin/sh
#
# Copyright (c) 2006 ETH Zurich.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
#  Translates one or more event-B files to HTML.
#

# path to the XSLT processor
PROC="xsltproc"

usage() {
    echo "Usage: $0 event_B_file..." >&2
    exit 1
}

[ $# -ne 0 ] || usage

for f; do
    case "$f" in
      *.bum)
	    b=`basename "$f" .bum`
		"$PROC" --stringparam name $b -o $b.html prettyprint.xslt $f
		;;
      *.buc)
	    b=`basename "$f" .buc`
		"$PROC" --stringparam name $b -o $b.html prettyprint.xslt $f
		;;
	esac
done

