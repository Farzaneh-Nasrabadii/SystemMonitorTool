# 📊 System Monitor Tool (Systemüberwachung-Tool)

A lightweight, automated system monitoring utility written in Java. This tool runs continuously in a Linux/Ubuntu background environment, periodically tracks system performance metrics (RAM and Disk usage), archives them in a PostgreSQL database, and sends instant email alerts via JavaMail API if any critical hardware thresholds are breached.

Ein leichtgewichtiges, automatisiertes Systemüberwachungstool, geschrieben in Java. Dieses Tool läuft kontinuierlich im Linux/Ubuntu-Hintergrund, erfasst regelmäßig Systemleistungsdaten (RAM- und Festplattenauslastung), archiviert diese in einer PostgreSQL-Datenbank und sendet sofortige E-Mail-Benachrichtigungen über die JavaMail-API, wenn kritische Hardware-Schwellenwerte überschritten werden.

---

## 🚀 Key Features | Hauptmerkmale

* **Automated Resource Tracking:** Periodically fetches real-time Disk and Memory usage using native Linux commands (`df`, `free`).
* Persistent Database Storage: Saves collected system metrics in a structured PostgreSQL database for history tracking.
* Smart Alerting System: Automatically triggers critical email notifications to the system administrator via secure Gmail SMTP (App Passwords) if resource consumption exceeds specified thresholds.
* Task Scheduling & Daemon Service: Built with ScheduledExecutorService to execute seamlessly as a hands-free background process (`nohup`).
* Production-Ready Configuration: Uses environment variables to securely handle database credentials and sensitive API tokens without hardcoding.

---

## 🛠️ Tech Stack | Technologien

* Language: Java 21
* Database: PostgreSQL
* Build Automation: Maven
* Libraries: JavaMail API (javax.mail), PostgreSQL JDBC Driver
* Environment: Linux / Ubuntu (WSL supported)

---

## 📋 Prerequisites | Voraussetzungen

Before running the application, ensure you have the following installed on your Linux/WSL system:
* Java Development Kit (JDK 21 or higher)
* Apache Maven
* PostgreSQL Database Server

---

## ⚙️ Configuration & Environment Variables

To protect sensitive information, the application reads database credentials and email tokens directly from the operating system's environment variables. Set them in youru background or ~/.profile:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/system_monitor"
export DB_USER="your_db_user"
export DB_PASSWORD="your_db_password"

export EMAIL_USER="sender_email@gmail.com"
export EMAIL_PASS="your_16_character_app_password"
export EMAIL_RECEIVER="admin_email@gmail.com"
After adding the variables, apply the changes:
source ~/.bashrc
📦 Installation & Deployment | Installation & Ausführung
1. Clone the Repository
Clone the project directly from GitHub onto your local machine or server:
git clone [https://github.com/Farzaneh-Nasrabadii/SystemMonitorTool.git](https://github.com/Farzaneh-Nasrabadii/SystemMonitorTool.git)
cd SystemMonitorTool
2. Build the Fat JAR
Run the following Maven command to clean and package all source files and external dependencies into a single executable JAR file:
mvn clean package
3. Run Locally or on a Server
Execute the application directly inside your terminal:
java -jar target/SystemMonitorTool-1.0-SNAPSHOT-jar-with-dependencies.jar
4. Run 24/7 in Linux Background (Production Mode)
To keep the tool running continuously on your Ubuntu server or WSL environment even after closing the terminal session, execute it as a daemon using nohup:
nohup java -jar target/SystemMonitorTool-1.0-SNAPSHOT-jar-with-dependencies.jar > monitor.log 2>&1 &
View Real-Time Logs: tail -f monitor.log
Stop the Process: Find the PID using ps aux | grep java and terminate it using kill <PID>.
🗄️ Database Schema | Datenbankschema
The application automatically logs system statuses into the metrics_history table with the following structure:
Column Name
Data Type
Description
id
SERIAL (Primary Key)
Auto-incremented unique record identifier
disk_usage
DOUBLE PRECISION
Percentage of used disk space
ram_usage
DOUBLE PRECISION
Percentage of used RAM capacity
recorded_at
TIMESTAMP
Exact date and time of the data collection
Developed with ❤️ by Farzaneh_Nasrabadii
