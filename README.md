# 🎓 SmartExam – Exam Scheduling System

## 📌 About the Project

SmartExam is an exam scheduling system designed to help colleges and universities organize and manage their examinations efficiently.

Scheduling examinations involves several important constraints. Students should not have overlapping examinations, rooms must have sufficient seating capacity, required equipment must be available, and invigilators must be free during the examination.

SmartExam brings these requirements together to automate examination scheduling while respecting student enrollments, room capacity, accessibility accommodations, equipment requirements, and invigilator availability.

The system also identifies scheduling conflicts and provides reasons when an examination cannot be scheduled.

## 🛠️ Technologies Used

The project is developed using the following technologies:

- ☕ Java 17
- 🍃 Spring Boot
- 🐘 PostgreSQL
- 🔗 Spring Data JPA
- 📦 Maven
- 🐍 Python
- 🧪 Postman
- 🌐 Git and GitHub

## 📁 Project Structure

```text
SmartExam/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   └── test/
│   ├── pom.xml
│   └── mvnw
│
├── data/
│   ├── generated/
│   └── manifest_first.json
│
├── docs/
│   └── data_dictionary.md
│
├── generator/
│   └── generator.py
│
├── prompts/
│   └── AI_USAGE.md
│
├── .gitignore
└── README.md
```

## 🚀 Main Features

- 👨‍🎓 Manage student information.
- 📚 Manage courses and student enrollments.
- 📝 Create and manage examinations.
- 🏫 Manage examination rooms and seating capacity.
- 👨‍🏫 Manage invigilators.
- 🖥️ Track room equipment and availability.
- 📅 Track invigilator availability.
- ♿ Handle student accommodation requirements.
- 🗓️ Automatically assign rooms and invigilators to examinations.
- ⚠️ Detect student, room, and invigilator scheduling conflicts.
- 📋 Identify unscheduled examinations and provide decision reasons.
- 📊 Validate saved examination schedules.
- 📂 Generate reproducible sample datasets for testing.

## 🗄️ Database Setup

First, install PostgreSQL and create a database named `smartexam`.

```sql
CREATE DATABASE smartexam;
```

Configure the database connection in:

`backend/src/main/resources/application.properties`

Use environment variables for database credentials:

```properties
spring.application.name=SmartExam

spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/smartexam}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

⚠️ **Security:** Never commit database passwords, API keys, or other sensitive credentials to GitHub.

## ▶️ How to Run the Backend

**Step 1: Open the backend directory**

```bash
cd backend
```

**Step 2: Build the application**

```bash
mvn clean package
```

**Step 3: Configure database environment variables**

On Windows PowerShell:

```powershell
$env:DB_USERNAME="postgres"
$env:DB_URL="jdbc:postgresql://localhost:5432/smartexam"
$env:DB_PASSWORD="YOUR_DATABASE_PASSWORD"
```

Replace the password with your actual PostgreSQL password.

**Step 4: Start the Spring Boot application**

```bash
mvn spring-boot:run
```

The backend will run at:

`http://localhost:8080`

**Health Check**

```http
GET http://localhost:8080/api/health
```

A successful response confirms that the backend is running.

## 🔌 API Endpoints

The following are the main API endpoints available in the project.

| Module | Endpoint |
|---|---|
| Health Check | `GET /api/health` |
| Students | `/api/students` |
| Courses | `/api/courses` |
| Enrollments | `/api/enrollments` |
| Examinations | `/api/exams` |
| Rooms | `/api/rooms` |
| Invigilators | `/api/invigilators` |
| Exam Requirements | `/api/exam-requirements` |
| Room Equipment | `/api/room-equipment` |
| Room Availability | `/api/room-availability` |
| Invigilator Availability | `/api/invigilator-availability` |
| Schedule Assignments | `GET /api/scheduling/assignments` |
| Schedule Validation | `GET /api/validation/schedule` |

**Schedule an Individual Examination**

```http
POST /api/scheduling/exam/{examId}
```

**Run the Scheduling Process**

```http
POST /api/scheduling/run
```

**Validate the Saved Schedule**

```http
GET /api/validation/schedule
```

The APIs can be tested using Postman.

## 🧪 Sample Data Generation

A Python data generator is included to create reproducible sample datasets.

From the main project directory, run:

```bash
python generator/generator.py
```

The generator uses a fixed random seed (`20261022`) to produce repeatable datasets.

**Generated Dataset Overview**

| Dataset | Records |
|---|---:|
| Students | 5,000 |
| Courses | 50 |
| Enrollments | 15,000 |
| Examinations | 300 |
| Rooms | 100 |
| Invigilators | 200 |
| Exam Requirements | 300 |
| Room Equipment | 147 |
| Room Availability | 1,000 |
| Invigilator Availability | 2,000 |
| Student Accommodations | 100 |
| Candidate Schedule Assignments | 300 |

Generated CSV and JSON files are stored in:

`data/generated/`

A separate ground-truth dataset describes five test scenarios involving:

- Student examination conflicts.
- Room capacity violations.
- Invigilator scheduling conflicts.
- Student accommodation requirements.
- Room availability restrictions.

ℹ️ Candidate assignments generated by the Python script are sample data, not guaranteed conflict-free schedules. Final scheduling decisions are handled by the backend scheduling service.

## ✅ Testing and Validation

The backend was tested using Postman and PostgreSQL.

The following checks were performed:

- Backend application startup and database connectivity.
- REST API record creation and retrieval.
- Examination scheduling and assignment persistence.
- Room capacity validation.
- Student examination conflict validation.
- Invigilator availability and double-booking validation.
- Room availability and equipment requirements.
- Student accommodation requirements.
- Unscheduled examination reasons.
- Reproducibility of generated sample datasets.

**Schedule Validation Example**

```json
{
  "totalAssignments": 3,
  "scheduled": 3,
  "valid": 3,
  "invalid": 0,
  "unscheduled": 0,
  "hardConstraintViolations": 0,
  "overallStatus": "PASSED"
}
```

This is an example of a successful validation result from the tested sample schedule, not a claim that every generated dataset has been validated.

## ⚠️ Current Limitations

- Sample datasets contain synthetic records and do not represent real students.
- Generated candidate assignments are not final approved schedules.
- More automated tests and large-scale performance testing are required.
- Authentication and role-based access control are not yet implemented.
- Additional testing is needed before real-world institutional deployment.

## 🔮 Future Improvements

Planned improvements include:

- 🎨 Develop a complete administrative frontend.
- 📊 Add scheduling reports and dashboards.
- ⚡ Optimize scheduling performance for larger datasets.
- 🧪 Expand automated tests for scheduling constraints.
- 🔐 Implement authentication and role-based permissions.
- 📄 Add downloadable examination schedules and reports.

## 🤖 AI Assistance and Development Notes

AI tools were used as development assistants for:

- Understanding project requirements and planning modules.
- Backend coding suggestions and implementation guidance.
- Scheduling logic design and debugging.
- PostgreSQL and Spring Boot configuration troubleshooting.
- Sample data generation and documentation.

AI-generated suggestions were reviewed and tested during development. The project includes an AI usage disclosure in:

`prompts/AI_USAGE.md`

The developer remains responsible for understanding, reviewing, testing, and explaining the final implementation.

## 👨‍💻 Project Repository

**GitHub:** https://github.com/Disney35/SmartExam

---

⭐ **SmartExam – Making Examination Scheduling Smarter, Simpler, and More Reliable.**