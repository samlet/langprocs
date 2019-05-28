#!/bin/bash
set -e

bash docker.build
docker build . -f run.dockerfile -t samlet/langprocs:0.1



