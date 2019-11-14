#!/bin/bash
set -e

bash docker.build
rm -rf /pi/hanlp/target/appassembler
cp -r target/appassembler /pi/hanlp/target/appassembler

cd /pi/hanlp
docker build . -f run.dockerfile -t samlet/langprocs:0.1



