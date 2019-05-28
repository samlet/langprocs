#!/bin/bash
curl -X PUT "http://localhost:8080/auction?bid=22&user=MartinO"
printf "\nresult:\n"
curl http://localhost:8080/auction
printf "\ndone.\n"

