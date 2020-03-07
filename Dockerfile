FROM lolgab/scala-native-docker
RUN apk add zip
WORKDIR /opt/sbt
COPY build.sbt .
COPY project/plugins.sbt project/
COPY project/build.properties project/
RUN sbt update
COPY . .
RUN sbt examplesNative/nativeLink
RUN cp target/scala-2.11/scala-native-open-whisk-out exec
RUN zip action.zip exec
CMD [ "cat", "action.zip" ]
