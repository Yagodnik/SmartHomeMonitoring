build-jvm:
    docker build -t smart-home-monitoring-jvm -f ./docker/Dockerfile.jvm .

compose-up-d:
    docker compose -f ./docker/docker-compose.yml -p smart-monitoring up -d

login-yandex:
    docker compose -f ./docker/docker-compose.yml -p smart-monitoring \
    run --rm -q monitoring-app -c /config.yaml                     \
    login-yandex

logout-yandex:
    docker compose -f ./docker/docker-compose.yml -p smart-monitoring \
    run --rm -q monitoring-app -c /config.yaml                     \
    logout-yandex

list-devices:
    docker compose -f ./docker/docker-compose.yml -p smart-monitoring \
    run --rm -q monitoring-app -c /config.yaml                     \
    list-devices