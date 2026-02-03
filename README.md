# 🎲 PESEL Number Generator

A web application for generating random PESEL numbers with a user authentication system.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Installation](#installation)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Security](#security)
- [Author](#author)

---

## ✨ Features

### 🌐 Public Access (No Login Required)

- **PESEL Generator** – generate a single PESEL number based on:
    - Date of birth
    - Gender (female/male)

### 🔐 Authenticated Access (After Login)

- **Bulk Generator** – generate multiple PESEL numbers at once
- **File Download** – download generated numbers as a `.txt` file
- **Email Sending** – send generated PESEL numbers as a `.txt` attachment to the user’s email
- **Account Management** – log out of the application

### 👤 Authorization System

- User registration
- Secure login authentication
- Secure password storage (BCrypt)
- User sessions

---

## 🛠️ Technology Stack

| Component | Version / Details |
|---------|------------------|
| **Java** | 21 |
| **Spring Boot** | 3.2.3 |
| **Spring Web** | REST & MVC |
| **Spring Security** | Authentication & Authorization |
| **Spring Data JPA** | ORM & database access |
| **Hibernate** | JPA implementation + SQLite dialect |
| **SQLite** | 3.45.1.0 |
| **Thymeleaf** | Server-side templates |
| **Thymeleaf Extras** | Spring Security integration |
| **Spring Mail** | Email sending (Jakarta Mail) |
| **Validation** | Jakarta Bean Validation |
| **Lombok** | Boilerplate code reduction |
| **Maven** | Build & dependency management |

---

## 📦 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/Kostya20079/peselgenerator.git
cd peselgenerator
```

### 2. Install Dependencies
```bash
mvn clean install
```

## 🔧 Configuration

### 1. ```application.properties``` Configuration

```properties
# Server
server.port=8080

# Database (SQLite)
spring.datasource.url=jdbc:sqlite:peselgenerator.db

# Thymeleaf
spring.thymeleaf.cache=false

# Logging
logging.level.com.peselgenerator=DEBUG
```

### 2. Gmail Configuration (Required for Email Sending)

To enable email sending:

1. Enable 2FA on your Gmail account
2. Generate an App Password (Google Account Settings)
3. Set environment variables

Or add directly to ```application.properties```:
```properties
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

## 📊 Project Structure

```text
peselgenerator/
├── src/main/java/com/peselgenerator/
│   ├── controller/          # Controllers (HTTP endpoints)
│   ├── service/             # Business logic
│   ├── repository/          # Database access
│   ├── entity/              # Entities (User, GeneratedPesel)
│   ├── dto/                 # Data Transfer Objects
│   ├── config/              # Spring Security configuration
│   └── PeselgeneratorApplication.java
├── src/main/resources/
│   ├── templates/           # Thymeleaf templates
│   ├── static/css/          # CSS styling
│   └── application.properties
├── pom.xml                  # Maven dependencies
└── README.md
```

## 🔒 Security

- ✅ Passwords hashed with BCrypt (salt strength 12)
- ✅ Spring Security authorization
- ✅ CSRF protection
- ✅ User session management
- ✅ Input data validation

## 👨‍💻 Author

This project was created for educational purposes.