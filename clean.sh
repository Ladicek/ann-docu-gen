#!/bin/bash

ROOT=$(dirname $(readlink -f $0))

echo "clean"
find $ROOT -type d -name target | xargs rm -rf
