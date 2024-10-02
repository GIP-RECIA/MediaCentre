# Support documentation


## mvn commands

### To add NOTICE

`./mvnw notice:check` Checks that a NOTICE file exists and that its content match what would be generated.

`./mvnw notice:generate` Generates a new NOTICE file, replacing any existing NOTICE file.

### To add licence headers

`./mvnw license:check` verify if some files miss license header.

`./mvnw license:format` add the license header when missing. If a header is existing, it is updated to the new one.

`./mvnw license:remove` remove existing license header.

### To sort dependencies in pom

`./mvnw sortpom:sort` will sort all in pom.

`./mvnw sortpom:verify` will check the order in the pom and provide a bak file to compare.

### To see deprecated code and warnings

`./mvnw compile -Dmaven.compiler.showWarnings=true -Dmaven.compiler.showDeprecation=true`

### To update maven wrapper

see official doc: https://maven.apache.org/wrapper/

### To run tests 

`./mvnw test -P test` will execute all Spring Boot test classes with the test profile.

---

## Setup git hooks

`git config core.hooksPath .githooks`

---

## test requests

### retrieve user's resources to web service :
- ```curl -X POST 'http://***********/mediacentre-ws/api/ressources/' -d '{"isMemberOf":["***********"],"ENTPersonGARIdentifiant":["****************"],"ESCOUAI":["********"],"ENTPersonProfils":["************"]}' -H 'Accept: application/json' -H 'Content-Type: application/json' --compressed -H 'Pragma: no-cache' -H 'Cache-Control: no-cache' -k -v --tlsv1.2```
