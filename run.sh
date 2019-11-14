#!/bin/bash
# docker run -it --rm samlet/langprocs:0.1 test
docker run -it --rm -p 10052:10052 -p 2333:2333 samlet/langprocs:0.1 nlp
