#!/bin/sh

TRACE=:
#TRACE=trace
if [ $TRACE == trace ]; then
    exec >> /tmp/out
    trace() {
       echo "[`date +%H:%M:%S`] $@"
    }
fi

signaled() {
    $TRACE received signal
    kill -TERM $pid
    sleep .1
    if ps $pid > /dev/null; then
        $TRACE sending KILL
        kill -KILL $pid
		exit 137
    fi
}

$TRACE start "$@"
"$@" &
pid=$!
trap signaled 15
wait $pid
ret=$?
$TRACE returned $ret
exit $ret
