# ubuntu-test

# startup server
mvn exec:java -Dexec.mainClass="com.cyocum.CYocumServer"
  leave terminal alone once connected

# first time running
for the first time starting server, manually run fetch and pull,
    git fetch
    git pull 
  set permissions for shell with: 
    chmod 755 compiling.sh
    mvn compile
  then: mvn exec:java -Dexec.mainClass="com.cyocum.CYocumServer"

# MySQL table manipulation
open new terminal and enter curl commands

# commands to use
get index
`curl http://3.139.156.162:8080/ # provides a list of all objects`

get by id
`curl http://3.139.156.162:8080/id # to query object based on id`

post
`curl -X POST http://3.139.156.162:8080/ -d NAME`

put
`curl -X PUT http://3.139.156.162:8080/ -d NAME`

delete
`curl -X DELETE http://3.139.156.162:8080/id`
 
