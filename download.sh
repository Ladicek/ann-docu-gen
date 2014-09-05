#!/bin/bash

ROOT=$(dirname $(readlink -f $0))

mkdir -p $ROOT/lib

curl http://www.antlr.org/download/antlr-4.4-complete.jar > $ROOT/lib/antlr-4.4-complete.jar
curl http://www.antlr.org/download/antlr-runtime-4.4.jar > $ROOT/lib/antlr-runtime-4.4.jar
