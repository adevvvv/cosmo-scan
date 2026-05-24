# КосмоСкан — Система автоматической проверки студенческих работ

## Описание проекта

**КосмоСкан** — микросервисная система для автоматизации приёма и первичной технической проверки студенческих контрольных работ. Система сохраняет загруженные файлы, выполняет их валидацию по формату и размеру, генерирует отчёты и визуализирует содержимое в виде облака слов.

---

## Статус выполнения требований

| Требование | Баллы | Статус | Комментарий                   |
|---------|-------|--------|-------------------------------|
| Исходный код | обязательно | ✅ | 3 микросервиса в репозитории  |
| Используется БД | 3 | ✅ | H2 (file_metadata, analysis_reports) |
| Микросервисы (2 бизнес + gateway) | 3 | ✅ | File Service, Analysis Service, API Gateway |
| Обработка ошибок | 2 | ✅ | Выполнено                     |
| Dockerfile + docker-compose | 6 | ✅ | 3 Dockerfile + docker-compose.yml |
| Swagger | 3 | ✅ | Swagger UI                    |
| Чистый код | 3 | ✅ | Модульная структура, DTO, логирование |
| Тесты ≥60% | 2 | ✅ | `./gradlew test jacocoTestReport`         |
| Визуализация (облако слов) | 2 | ✅ | wordcloud.png                 |
| README | 7 | ✅ | Текущий файл                  |
| Скринкаст | 4 | ✅ | Скринкаст. Синхронное межсервисное взаимодействие.mp4                 |

---

## UML Диаграммы

### Диаграмма последовательности

![Диаграмма последовательности](https://github.com/adevvvv/cosmo-scan/blob/main/diagram%20(30).svg)

### Диаграмма компонентов

![Диаграмма компонентов](https://github.com/adevvvv/cosmo-scan/blob/main/diagram%20(31).svg)

### Диаграмма развертывания

![Диаграмма развертывания](https://github.com/adevvvv/cosmo-scan/blob/main/diagram%20(34).svg)

### ERD

![ERD](https://github.com/adevvvv/cosmo-scan/blob/main/diagram%20(39).svg)

## API Endpoints

### API Gateway (:8080)

| Метод | URL | Описание | Ответ |
|-------|-----|----------|-------|
| POST | `/api/v1/works` | Отправить работу | 201: WorkSubmissionResponse |
| GET | `/api/v1/works/{workId}/report` | Получить отчёт | 200: AnalysisResponse |

### File Service (:8081)

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/v1/files` | Загрузить файл |
| GET | `/api/v1/files/{fileId}` | Скачать файл |
| GET | `/api/v1/files/{fileId}/metadata` | Метаданные файла |

### Analysis Service (:8082)

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/v1/analysis` | Запустить проверку |
| GET | `/api/v1/analysis/{reportId}` | Получить отчёт |
| GET | `/api/v1/analysis/{reportId}/wordcloud` | Облако слов (PNG) |
