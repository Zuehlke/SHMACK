#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters"
    echo "Usage: `basename $0` <URL>"
    exit 1
fi

echo "Opening URL in browser: $1"
xdg-open "${1}" > /dev/null 2>&1 &
echo "Done."
