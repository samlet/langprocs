#!/bin/bash
## start com.samlet.langprocs.actors.JacksonExampleTest
## ⊕ [1. Introduction • Akka HTTP](https://doc.akka.io/docs/akka-http/10.1.5/introduction.html#using-akka-http)

echo "create ..."
curl -H "Content-Type: application/json" -X POST -d '{"items":[{"name":"hhgtg","id":42}]}' http://localhost:8080/create-order
printf "\nquery ...\n"
curl http://localhost:8080/item/42
printf "\ndone.\n"
