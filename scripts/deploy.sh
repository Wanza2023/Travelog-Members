#cd /root/01_DockerCompose
#sudo docker compose down
#sudo docker rmi gustjdw/travelog-members:latest
#sudo docker pull gustjdw/travelog-members:latest
#sudo yq eval ‘.services.members.image = “gustjdw’travelog-members:latest”’ docker-compose.yml --inplace
#sudo docker compose up -d
