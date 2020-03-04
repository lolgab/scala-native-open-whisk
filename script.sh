#!/bin/sh

docker build -t action .
docker run -v "$PWD":/opt/mount --rm --entrypoint cp action action.zip /opt/mount/action.zip
ibmcloud wsk action update ciao action.zip --docker openwhisk/actionloop-base:nightly
