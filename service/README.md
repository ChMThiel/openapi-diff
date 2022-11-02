# openapi-diff-service Project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .


| BOM | Config | Routing | SCT | SMT | Simulation | TNT | KeyCloak |
| :-: | :----: | :-----: | :-: | :-: | :--------: | :-: | :------: |
|     |        |         |     |     |            |     |          |


You can run your application in dev mode that enables live coding using:

```
mvn quarkus:dev
```


The application is packageable using `mvn package`.
It produces the executable `openapi-diff-service-1.0-SNAPSHOT-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/openapi-diff-service-1.0-SNAPSHOT-runner.jar`.


You can create a native executable using: `mvn package -Pnative`.

Or you can use Docker to build the native executable using: `mvn package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your binary: `./target/openapi-diff-service-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .


There are 2 different test types:

- unit tests
- integration tests

Unit tests are performed by maven-surefire-plugin. Integration tests AND system tests are performed by maven-failsafe-plugin.

The naming of the class is as follows:

- unit test: `*Test.java`
- integration test: `*IT.java`


There are some properties for configure database and
server during integrationtests:

- `integrationtest.db.name` (Default: iotos)
- `integrationtest.db.user` (Default: iotos)
- `integrationtest.db.pwd` (Default: iotos_pwd)
- `integrationtest.db.host` (Default: localhost)
- `integrationtest.db.port` (Default: 5432)
- `integrationtest.server.port` (Default: 8080)

If database port for example colides with a running database another port can set with
`mvn -Dintegrationtest.db.port=<anotherPort> clean verify`

Unit tests can skipped with `-Dskip.tests`
Integration tests can skipped with `-Dskip.it`
Quarkus packaging can skipped with `-Dskip.quarkus`


- Run only integrationtests
  `mvn -Dskip.tests clean verify`
- Run unit and integrationtests
  `mvn clean verify`
- Run only integrationtests
  `mvn -Dskip.tests clean verify`
- Start empty database in docker container for integrationtest (database will stay open)
  `mvn -Pprepare-integrationtest integration-test`

