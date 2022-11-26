rm -rf green-final.tar
docker build -t green-final-java .

docker save green-final-java -o green-final.tar
