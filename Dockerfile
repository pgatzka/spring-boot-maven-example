FROM eclipse-temurin:25-jre-alpine AS assembler
WORKDIR /assembler

ARG JAR_FILE=target/application.jar

COPY ${JAR_FILE} application.jar

RUN java -Djarmode=tools -jar application.jar extract --layers --destination extracted

FROM eclipse-temurin:25-jre-alpine AS trainer

WORKDIR /trainer

COPY --from=assembler /assembler/extracted/dependencies/ ./
COPY --from=assembler /assembler/extracted/spring-boot-loader/ ./
COPY --from=assembler /assembler/extracted/snapshot-dependencies/ ./
COPY --from=assembler /assembler/extracted/application/ ./

ENV JAVA_TOOL_OPTIONS="-XX:AOTCacheOutput=application.aot -XX:MaxRAMPercentage=70.0 -XX:+ExitOnOutOfMemoryError -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/application"

RUN java -Dspring.context.exit=onRefresh -Dspring.profiles.active=aot -jar application.jar

FROM eclipse-temurin:25-jre-alpine AS runtime
WORKDIR /runtime

RUN addgroup -S spring && adduser -S -G spring spring && chown spring:spring /runtime

COPY --from=assembler --chown=spring:spring /assembler/extracted/dependencies/ ./
COPY --from=assembler --chown=spring:spring /assembler/extracted/spring-boot-loader/ ./
COPY --from=assembler --chown=spring:spring /assembler/extracted/snapshot-dependencies/ ./
COPY --from=assembler --chown=spring:spring /assembler/extracted/application/ ./
COPY --from=trainer --chown=spring:spring /trainer/application.aot ./

USER spring

EXPOSE 8080

ENV JAVA_TOOL_OPTIONS="-XX:AOTCache=application.aot -XX:MaxRAMPercentage=70.0 -XX:+ExitOnOutOfMemoryError -XX:+UseParallelGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/application"

ENTRYPOINT ["java", "-jar", "application.jar"]