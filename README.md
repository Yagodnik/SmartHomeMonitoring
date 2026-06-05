# Smart Home Monitoring
CLI утилита для мониторинга умного дома яндекс.

<!-- TOC -->
* [Smart Home Monitoring](#smart-home-monitoring)
* [Экспортер](#экспортер)
    * [Доступные плейсхолдеры для csv-экспортера:](#доступные-плейсхолдеры-для-csv-экспортера)
* [Доступные команды](#доступные-команды)
* [Docker](#docker)
* [Использование](#использование)
<!-- TOC -->

# Экспортер
Экспортер - способ сохранить/отправить данные, собранные из умного дома. 
На данный момент доступны два:
1) **CSV**
2) **Prometheus**

Все экспортеры настраиваются через yaml файл, который необходимо указывать через аргумент ```-c```, либо же ```--config```
Пример ```config.yaml```:
```yaml
polling-interval: 60 # Интервал опроса yandex api в секундах (рекомендуется ставить не меньше 15)
port: 9091 # Порт для запуска веб сервера
exporters:
  csv:
    format: "{{ timestamp }};{{ deviceId }};{{ deviceName }};{{ metric }} = {{ value }}"
    outputDir: "/logs"
  prometheus: {}
```

### Доступные плейсхолдеры для csv-экспортера:

| Плейсхолдер | Описание                                               |
|-------------|--------------------------------------------------------|
| timestamp   | дата и время, когда были считаны метрики               |
| deviceId    | идентификатор устройства умного дома                   |
| deviceName  | обозначение устройства умного дома                     |
| value       | Приведенное у числу показание устройства               |
| rawValue    | Исходное представление значение (никак необработанное) |
| metric      | название параметра/метрики                             |

# Доступные команды

```login-yandex, logout-yandex, list-devices```

# Docker
На данный момент рекомендуется устанавливать приложение используя docker.
Установить можно как в связке с Grafana + Prometheus, так и без неё.

<details>
<summary>Grafana + Prometheus + Smart Home Monitoring</summary>

```yaml
services:
  grafana:
    image: grafana/grafana:12.4
    ports:
      - "${GRAFANA_PORT}:3000"
    volumes:
      - ./grafana/provisioning:/etc/grafana/provisioning
      - ./grafana/dashboards:/var/lib/grafana/dashboards
    environment:
      GF_SECURITY_ADMIN_USER: "${GRAFANA_USER}"
      GF_SECURITY_ADMIN_PASSWORD: "${GRAFANA_PASSWORD}"
    depends_on:
      - prometheus

  prometheus:
    image: prom/prometheus:v3.10.0
    volumes:
      - ./prometheus/prometheus.yml:/etc/prometheus/prometheus.yml

  monitoring-app:
    image: smart-home-monitoring-jvm:latest
    ports:
      - "9091:9091"
    environment:
      ACCESS_TOKEN: "${ACCESS_TOKEN}"
      YANDEX_CLIENT_ID: "${YANDEX_CLIENT_ID}"
      CREDENTIALS_DIR: "/credentials"
      MASTER_KEY: "1"
    volumes:
      - ./credentials:/credentials
      - ./config.yaml:/config.yaml
      - ./logs:/logs
    command: ["-c", "/config.yaml"]

```

</details>

<details>
<summary>Smart Home Monitoring</summary>

```yaml
services:
  monitoring-app:
    image: smart-home-monitoring-jvm:latest
    ports:
      - "9091:9091"
    environment:
      ACCESS_TOKEN: "${ACCESS_TOKEN}"
      YANDEX_CLIENT_ID: "${YANDEX_CLIENT_ID}"
      CREDENTIALS_DIR: "/credentials"
      MASTER_KEY: "1"
    volumes:
      - ./credentials:/credentials
      - ./config.yaml:/config.yaml
      - ./logs:/logs
    command: ["-c", "/config.yaml"]

```

</details>

# Использование
К проекту приложен just файл, в нем есть примеры авторизации и использования команд для compose-а из папки docker.

1. Авторизация
Запустите команду: 
```bash
docker compose -f ./docker/docker-compose.yml -p smart-monitoring \
run --rm -q monitoring-app -c /config.yaml                     \
login-yandex
```
Авторизуйтесь используя qr code или ссылку.

2. Запуск
Запустите команду:

```docker compose -p smart-monitoring up -d```