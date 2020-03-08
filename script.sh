#!/bin/sh

docker build -t action .
docker run -v "$PWD":/opt/mount --rm --entrypoint cp action action.zip /opt/mount/action.zip
wsk action update hello action.zip --docker lolgab/actionloop-scala-native
