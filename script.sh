#!/bin/sh

docker build -t action .
docker run -v "$PWD":/opt/mount --rm --entrypoint cp action action.zip /opt/mount/action.zip
wsk action update ciao action.zip --docker lolgab/actionloop-scala-native
