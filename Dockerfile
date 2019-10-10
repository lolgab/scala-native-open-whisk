FROM lolgab/scala-native-build:2.11.12_0.3.9 as scalabuilder
WORKDIR /opt/sbt
RUN mkdir project
COPY project/plugins.sbt project
COPY project/build.properties project
COPY src src
COPY build.sbt build.sbt
RUN sbt nativeLink

FROM scala-native-runtime:latest
COPY --from=openwhisk/actionloop-v2:latest /bin/proxy /bin/proxy
RUN mkdir -p /proxy/bin /proxy/lib /proxy/action
COPY --from=scalabuilder /opt/sbt/target/scala-2.11/scala-native-open-whisk-out /proxy/bin/exec
WORKDIR /proxy
CMD ["/bin/proxy"]
