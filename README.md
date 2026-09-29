# EduNode — School Management System

Application Full Stack de gestion scolaire permettant à un administrateur de gérer une liste d'étudiants. 
Ceci est un projet pédagogique propre et simple.

## Technologies

- **Backend** : Java 21, Spring Boot, Spring Data JPA, Hibernate, MySQL, Maven, Jakarta Validation
- **Frontend** : Angular, TypeScript, HTML, CSS
- **Déploiement** : Docker, Docker Compose

## Architecture

L'application suit une architecture en couches classiques :
```
Angular
   ↓ (HTTP REST)
Controller (Reçoit les requêtes)
   ↓ (Appel de méthode)
Service (Logique métier)
   ↓ (Appel de méthode)
Repository (Accès aux données)
   ↓ (SQL)
MySQL (Persistance)
```

## Installation et Démarrage Local

### Prérequis
- Java 21
- Node.js (v18+)
- MySQL (v8+)

### 1. Démarrer MySQL et créer la base
Assurez-vous que MySQL est démarré sur le port 3306.
Créez une base de données nommée `edunode` :
```sql
CREATE DATABASE edunode;
```

### 2. Lancer le Backend (Spring Boot)
Ouvrez un terminal dans le dossier `backend` :
```bash
mvn spring-boot:run
```
L'application Spring Boot démarrera sur `http://localhost:8080`.
(Le mot de passe de la BD configuré par défaut est `root`, modifiez `application.properties` si nécessaire).

### 3. Lancer le Frontend (Angular)
Ouvrez un terminal dans le dossier `frontend` :
```bash
npm install
npm start
```
L'application Angular démarrera sur `http://localhost:4200`.

## Démarrage via Docker (Recommandé pour tester)
À la racine du projet, exécutez simplement :
```bash
docker-compose up --build
```
- Le frontend sera accessible sur `http://localhost:4200`
- Le backend sur `http://localhost:8080`
- La base de données MySQL est encapsulée dans un conteneur et est initialisée automatiquement.

## API Key
Les requêtes mutantes (POST, PUT, DELETE) nécessitent l'en-tête `X-API-KEY`.
Valeur par défaut : `edunode-admin-key`.
Ceci est géré automatiquement par le Frontend via un HttpInterceptor.
