FROM lolgab/scala-native-docker as builder
WORKDIR /opt/sbt
COPY . .
RUN sbt "set nativeLinkingOptions += \"-static\"; nativeLink"
RUN apk add zip
RUN cp target/scala-2.11/scala-native-open-whisk-out exec
RUN zip action.zip exec
