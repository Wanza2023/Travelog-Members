./gradlew clean
./gradlew build
./gradlew bootJar

if [ -e ${gustjdw/travelog-members:latest} ] ; then
  echo "기존 이미지 삭제"
  docker rmi gustjdw/travelog-members:latest
fi

docker build --platform linux/amd64 -t gustjdw/travelog-members:latest .
docker push gustjdw/travelog-members:latest
