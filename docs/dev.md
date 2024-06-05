# Support documentation

----

### For more information on the resources retrieved by the web service, please consult the GAR documentation: https://gar.education.fr/wp-content/uploads/2024/02/GAR-RTFS_V7.2-Referentiel_technique_FR1.pdf

# mvn commands :

## To add NOTICE

`./mvnw notice:check` Checks that a NOTICE file exists and that its content match what would be generated.

`./mvnw notice:generate` Generates a new NOTICE file, replacing any existing NOTICE file.

## To add licence headers

`./mvnw license:check` verify if some files miss license header.

`./mvnw license:format` add the license header when missing. If a header is existing, it is updated to the new one.

`./mvnw license:remove` remove existing license header.

## To see deprecated code and warnings

`./mvnw compile -Dmaven.compiler.showWarnings=true -Dmaven.compiler.showDeprecation=true`

## To update maven wrapper

see official doc: https://maven.apache.org/wrapper/

# Setup git hooks

`git config core.hooksPath .githooks`

# test requests :

## retrieve user's resources 
```curl -X POST 'http://***********/mediacentre-ws/api/ressources/' -d '{"isMemberOf":["***********"],"ENTPersonGARIdentifiant":["****************"],"ESCOUAI":["********"],"ENTPersonProfils":["************"]}' -H 'Accept: application/json' -H 'Content-Type: application/json' --compressed -H 'Pragma: no-cache' -H 'Cache-Control: no-cache' -k -v --tlsv1.2```