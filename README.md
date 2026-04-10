# Taskboard

## Projenin Amacı

Bu proje, Agile geliştirme süreçleri için bir görev panosu (taskboard) uygulaması sağlar. Kullanıcılar görevleri oluşturabilir, güncelleyebilir, silebilir ve görev durumlarını takip edebilir. Proje, backend ve frontend bileşenleri ile gerçek zamanlı iletişim, mesaj kuyruğu ve arama özelliklerini bir arada sunar.

## Genel Mimari

- `backend`: Spring Boot tabanlı Java uygulaması.
- `frontend`: Vite ve React tabanlı modern web arayüzü.
- `docker-compose.yml`: PostgreSQL, Redis, Kafka ve Elasticsearch servislerini yönetir.

## Kullanılan Teknolojiler

### Backend

- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring WebSocket
- Spring Data JPA
- Spring Data Redis
- Spring for Apache Kafka
- PostgreSQL
- Jackson Databind
- Lombok
- Maven Wrapper (`./mvnw`)

### Frontend

- React 19
- Vite 8
- TypeScript
- STOMP WebSocket istemcisi (`@stomp/stompjs`)
- ESLint

### Container ve Altyapı

- Docker Compose
- PostgreSQL 15 (veritabanı)
- Redis 7 (önbellek / cache)
- Kafka 3.7 (event streaming)
- Elasticsearch 8.17 (arama ve indeksleme)

### Geliştirme Araçları

- Git
- Maven
- Node.js / npm
- Docker
- VS Code veya benzeri IDE

## Projenin Yapısı

```
/README.md
/docker-compose.yml
/pom.xml
/frontend/
  package.json
  tsconfig.json
  vite.config.ts
  src/
/src/main/java/com/agileboard/taskboard/
  TaskboardApplication.java
  config/
  controller/
  entity/
  kafka/
  repository/
  search/
  service/
/src/main/resources/application.properties
```

## Çalıştırma Adımları

### 1) Gerekli servisleri başlatma

```bash
docker-compose up -d
```

Bu komut Docker Compose ile PostgreSQL, Redis, Kafka ve Elasticsearch servislerini arka planda çalıştırır.

### 2) Backend'i çalıştırma

```bash
./mvnw spring-boot:run
```

### 3) Frontend'i çalıştırma

```bash
cd frontend
npm install
npm run dev
```

Ardından tarayıcıda `http://localhost:5173` adresini açın.

## Proje Akışı

1. Docker Compose ile altyapı servisleri başlatılır.
2. Spring Boot backend, PostgreSQL veritabanına bağlanır ve API uç noktalarını sunar.
3. Frontend Vite ile canlı geliştirme sunucusunda çalışır.
4. WebSocket veya REST çağrıları üzerinden görev verisi yönetilir.

## Notlar

- `target/` klasörü `.gitignore` içinde dışlanmıştır.
- `application.properties` içinde PostgreSQL bağlantı bilgileri yer alır.
- Elasticsearch ve Kafka entegrasyonu, docker-compose ile kolayca sağlanır.

## GitHub'e Ekleme

Bu proje klasöründe Git henüz başlatılmamıştı. Aşağıdaki adımlarla repo oluşturulup commit yapılabilir:

```bash
git init
git add .
git commit -m "Initial commit with detailed README"
```

Eğer GitHub uzaktan bağlantısı eklemek isterseniz:

```bash
git remote add origin https://github.com/kullanici-adi/repo-adi.git
git push -u origin main
```

---

# Taskboard

## Project Purpose

This project provides a taskboard application for Agile development workflows. Users can create, update, delete, and track task statuses. The project combines backend and frontend components with real-time communication, message queueing, and search capabilities.

## Architecture Overview

- `backend`: Spring Boot-based Java application.
- `frontend`: Modern web interface built with Vite and React.
- `docker-compose.yml`: Manages PostgreSQL, Redis, Kafka, and Elasticsearch services.

## Key Technologies

### Backend

- Java 21
- Spring Boot 4.0.5
- Spring Web MVC
- Spring WebSocket
- Spring Data JPA
- Spring Data Redis
- Spring for Apache Kafka
- PostgreSQL
- Jackson Databind
- Lombok
- Maven Wrapper (`./mvnw`)

### Frontend

- React 19
- Vite 8
- TypeScript
- STOMP WebSocket client (`@stomp/stompjs`)
- ESLint

### Container and Infrastructure

- Docker Compose
- PostgreSQL 15 (database)
- Redis 7 (cache / messaging)
- Kafka 3.7 (event streaming)
- Elasticsearch 8.17 (search and indexing)

### Development Tools

- Git
- Maven
- Node.js / npm
- Docker
- VS Code or similar IDE

## Project Structure

```
/README.md
/docker-compose.yml
/pom.xml
/frontend/
  package.json
  tsconfig.json
  vite.config.ts
  src/
/src/main/java/com/agileboard/taskboard/
  TaskboardApplication.java
  config/
  controller/
  entity/
  kafka/
  repository/
  search/
  service/
/src/main/resources/application.properties
```

## How to Run

### 1) Start required services

```bash
docker-compose up -d
```

This command starts PostgreSQL, Redis, Kafka, and Elasticsearch services in the background.

### 2) Run backend

```bash
./mvnw spring-boot:run
```

### 3) Run frontend

```bash
cd frontend
npm install
npm run dev
```

Then open `http://localhost:5173` in your browser.

## Workflow

1. Infrastructure services are started with Docker Compose.
2. Spring Boot backend connects to PostgreSQL and serves API endpoints.
3. Frontend runs in a live Vite development server.
4. Task data is managed through WebSocket or REST interactions.

## Notes

- The `target/` folder is excluded via `.gitignore`.
- PostgreSQL connection settings are configured in `application.properties`.
- Elasticsearch and Kafka are included in the project and can be started via Docker Compose.

## Add to GitHub

This project directory was not initialized with Git yet. Use these commands to initialize and commit the repository:

```bash
git init
git add .
git commit -m "Initial commit with detailed README"
```

If you want to add a GitHub remote:

```bash
git remote add origin https://github.com/your-username/your-repo.git
git push -u origin main
```
