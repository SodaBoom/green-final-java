rm -rf green-final-java.tar
docker build -t green-final-java .

docker save green-final-java -o green-final-java.tar
