#!/bin/sh

"$@" &
pid=$!
trap "kill -9 $pid" 1 2 3 15
wait $pid
