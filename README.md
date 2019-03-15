# Прототип системы отслеживания статуса заявок в МФЦ для колл центра

## Управление сетью

В папке `docker` находятся все скрипты для управления сетью

Чтобы собрать все актуальные образы выполните:
```
./build.sh
```

Чтобы запустить сеть выполните:
```
./start.sh
```
Чтобы остановить сеть выполните:

```
./stop.sh
```
Чтобы остановить сеть и удалить все данные по ней выполните:

```
./teardown.sh
```

В корне проекта находится файл `Call Center.postman_collection.json` в котором записана колеция запросов для Postman.