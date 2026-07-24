
# 📊 System Monitor Tool (Systemüberwachung-Tool)

A lightweight, production-grade automated system monitoring utility written in **Java 21**. This tool runs continuously in a Linux/Ubuntu background environment, periodically tracks system performance metrics (RAM and Disk usage), archives them in a PostgreSQL database, and sends instant email alerts via JavaMail API if any critical hardware thresholds are breached.

Ein leichtgewichtiges, automatisiertes Systemüberwachungstool in **Java 21**. Dieses Tool läuft kontinuierlich im Linux/Ubuntu-Hintergrund, erfasst regelmäßig Systemleistungsdaten (RAM- und Festplattenauslastung), archiviert diese in einer PostgreSQL-Datenbank und sendet sofortige E-Mail-Benachrichtigungen über die JavaMail-API, wenn kritische Hardware-Schwellenwerte überschritten werden.

---

## 🚀 Key Features | Hauptmerkmale

* **Automated Resource Tracking:** Periodically fetches real-time Disk and Memory usage.
* **Persistent Database Storage:** Saves collected system metrics in a structured PostgreSQL time-series database.
* **Smart Alerting System:** Automatically triggers critical email notifications to the system administrator via secure SMTP (Gmail / Docker Mailhog for local testing).
* **Test-Driven Design (TDD):** Comprehensive unit tests powered by **JUnit 5** to ensure full reliability for metrics, configurations, and alerting rules.
* **Structured Logging:** Unified, production-ready logging implemented with **SLF4J + Logback**.
* **Zero Hardcoded Configs:** Secure environment variable handling using `dotenv-java` and `.env` files.
* **Task Scheduling & Daemon Service:** Built with `ScheduledExecutorService` to execute seamlessly as a hands-free background process (`nohup`).

---

## 🛠️ Tech Stack | Technologien

* **Language:** Java 21
* **Build Automation:** Apache Maven
* **Database:** PostgreSQL
* **Testing Suite:** JUnit 5 (Jupiter Engine)
* **Logging Framework:** SLF4J & Logback
* **Libraries:** `dotenv-java`, `javax.mail`
* **Environment:** Linux / Ubuntu / WSL / Docker

---

## 📋 Prerequisites | Voraussetzungen

Before running the application, ensure you have the following installed:
* Java Development Kit (JDK 21 or higher)
* Apache Maven
* PostgreSQL Database Server
* Docker & Docker Compose *(Optional, for Mailhog local testing)*

---

## ⚙️ Configuration & Environment (.env)

To protect sensitive credentials, the application reads parameters via `dotenv-java`. Create a `.env` file in the project root directory:

```env
DB_URL=jdbc:postgresql://localhost:5432/system_monitor
DB_USER=your_db_user
DB_PASSWORD=your_db_password

EMAIL_USER=sender_email@gmail.com
EMAIL_PASS=your_16_character_app_password
EMAIL_RECEIVER=admin_email@gmail.com

ALERT_RAM_THRESHOLD=80.0
ALERT_DISK_THRESHOLD=90.0

```

---

## 📦 Installation & Deployment | Installation & Ausführung

### 1. Clone the Repository

```bash
git clone [https://github.com/Farzaneh-Nasrabadii/SystemMonitorTool.git](https://github.com/Farzaneh-Nasrabadii/SystemMonitorTool.git)
cd SystemMonitorTool

```

### 2. Run Unit Tests

Verify that all application components pass test specs:

```bash
mvn test

```

### 3. Build the Application

Package the source code and dependencies into a single runnable JAR:

```bash
mvn clean package

```

### 4. Run 24/7 in Linux Background (Production Mode)

To keep the tool running continuously on your Ubuntu server or WSL environment:

```bash
nohup java -jar target/SystemMonitorTool-1.0-SNAPSHOT-jar-with-dependencies.jar > monitor.log 2>&1 &

```

* **View Real-Time Logs:** `tail -f monitor.log`
* **Stop the Process:** Find the PID using `ps aux | grep java` and terminate it using `kill <PID>`.

---

## 🗄️ Database Schema | Datenbankschema

The application automatically logs system statuses into the `metrics_history` table with the following structure:

| Column Name | Data Type | Description |
| --- | --- | --- |
| `id` | SERIAL (PK) | Auto-incremented unique record identifier |
| `disk_usage` | DOUBLE PRECISION | Percentage of used disk space |
| `ram_usage` | DOUBLE PRECISION | Percentage of used RAM capacity |
| `recorded_at` | TIMESTAMP | Exact date and time of the data collection |

---

Developed with ❤️ by **Farzaneh Nasrabadi**
