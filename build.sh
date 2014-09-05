#!/bin/bash

ROOT=$(dirname $(readlink -f $0))

$ROOT/clean.sh

echo "compile 'framework'"
cd $ROOT/framework
mkdir target
javac -d target src/cz/ladicek/annDocuGen/api/*.java

echo "compile 'annotation-processor'"
cd $ROOT/annotation-processor
mkdir target
$ROOT/build-antlr.sh
javac -d target -cp $ROOT/lib/antlr-runtime-4.4.jar:$ROOT/framework/target/ src/cz/ladicek/annDocuGen/annotationProcessor/*.java src/cz/ladicek/annDocuGen/annotationProcessor/javaParser/*.java
cp -a resources/* target

echo "compile 'example'"
cd $ROOT/example
mkdir target
javac -d target -cp $ROOT/lib/antlr-runtime-4.4.jar:$ROOT/framework/target/:$ROOT/annotation-processor/target/ src/cz/ladicek/annDocuGen/example/*.java
