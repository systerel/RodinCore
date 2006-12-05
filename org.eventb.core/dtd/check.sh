#!/bin/sh
#
#  Checks an event-B file for validity with respect to its DTD.
#

tmpFile="/tmp/check$$"
trap "rm -f $tmpFile" 0 1 2 3 6 15

verbose=

#validator="xmllint --valid --noout"
validator="rxp -V -s"

check() {
    [ $verbose ] && echo "Checking $1"
    case "$1" in
	(*.buc)  root="contextFile";;
	(*.bum)  root="machineFile";;
	(*.bpr)  root="prFile";;
	(*)      fatal "Unknown file type for $1";;
    esac
    exec 3> "$tmpFile"
    head -1 "$1" >&3
    dtd="$DTD_HOME/$root.dtd"
    echo "<!DOCTYPE org.eventb.core.$root SYSTEM \"${dtd}\">" >&3
    tail -n +2 "$1" >&3
    exec 3>&-

    $validator "$tmpFile" || error "$1 is not valid"
}


findLocation() {
    case "$0" in
	(/*)    DTD_HOME=`dirname "$0"`;;
	(*/*)   DTD_HOME=`pwd`/`dirname "$0"`;;
	(*)
	    #IFS=":" \
	    for d in "$PATH"; do
	        if [ -f "$d/$0" ]; then
		    DTD_HOME="$d"
		    break
		fi
	    done
    esac
}

error() {
    echo "$@" >&2;
}

fatal() {
    error "$@"
    exit 1
}

runList() {
    exec < "$1"
    while read f; do
	check "$f"
    done
    exec <&-
}

usage() {
    fatal "Usage: `basename $0` [-l listFile] file..."
}


findLocation
while [ "$#" -ne 0 ]; do
    case "$1" in
	(-l) shift
	     [ $# -ne 0 ] || fatal "Missing argument for '-l'"
	     runList "$1"
	     ;;
	(-v) verbose=true ;;
	(-*) usage ;;
	(*)  check "$1"
    esac
    shift
done
