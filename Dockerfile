FROM lolgab/scala-native-docker
RUN apk add zip
WORKDIR /opt/sbt
COPY . .
RUN sbt nativeLink
RUN cp target/scala-2.11/scala-native-open-whisk-out exec
RUN zip action.zip exec
CMD [ "cat", "action.zip" ]
