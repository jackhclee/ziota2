# ziota2

# Requirements
1. Scala 2.13
2. ZIO 2.1.x
3. [ZIO Quill](https://github.com/zio/zio-quill)
3. sbt
4. H2SQL

# Installation
run
`brew install sbt`

Download the H2SQL RDBMS jar from [here](https://search.maven.org/remotecontent?filepath=com/h2database/h2/2.3.232/h2-2.3.232.jar) and then start the RDBMS server by running below command in terminal
`java -jar h2*.jar`

Go to http://localhost:8082

create table 
```
create table person (name varchar(255), age int)
``` 

run the main program
`shell> sbt "runMain MainProg"`

# CI/CD
The `.github/workflows/ci.yaml` will be when code is pushed to `main` branch
