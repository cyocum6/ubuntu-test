# for pulling in next set of updates from gitHub to the server

git fetch
git pull
killall java
mvn compile
mvn exec:java -Dexec.mainClass="com.cyocum.CYocumServer"

