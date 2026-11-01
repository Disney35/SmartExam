# SmartExam – Exam Scheduling System

## About the Project

SmartExam is a project that helps colleges and universities schedule their examinations.

When exams are planned, several things need to be considered. Students should not have two exams at the same time. Rooms should have enough seating capacity. Required equipment should be available, and invigilators should be free during the examination.

This project brings these details together and helps make exam scheduling easier.

It also explains why an exam was assigned to a room or why it could not be scheduled.

## Technologies Used

I developed this project using the following technologies:

- Java 17
- Spring Boot
- PostgreSQL
- Spring Data JPA
- Maven
- Python
- Postman
- Git and GitHub

## Project Structure

```text
SmartExam/
│
├── backend/
│   ├── src/
│   └── pom.xml
│
├── generator/
│   └── generator.py
│
├── data/
│   └── generated/
│
├── docs/
│   └── data_dictionary.md
│
├── prompts/
│   └── AI_USAGE.md
│
├── frontend/
│
└── README.md
```

## Main Features

- Manage student information.
- Manage courses and student enrollments.
- Create and manage examinations.
- Maintain examination rooms and their capacity.
- Manage invigilators.
- Record room equipment and availability.
- Record invigilator availability.
- Store student accommodation requirements.
- Assign rooms and invigilators to examinations.
- Identify scheduling conflicts.
- Provide a reason when an examination cannot be assigned.
- Generate sample data for testing.

## Database Setup

First, install PostgreSQL and create a database named `smartexam`.

```sql
CREATE DATABASE smartexam;
```

Update the database connection settings in:

`backend/src/main/resources/application.properties`

Set the database username and password using environment variables. Do not upload database passwords to GitHub.

## How to Run the Backend

Open a terminal inside the `backend` folder.

Run:

```bash
mvn clean package
```

After the build completes, start the application:

```bash
mvn spring-boot:run
```

The backend will run at:

`http://localhost:8080`

You can check whether the application is running using:

`GET http://localhost:8080/api/health`

## API Endpoints

The main API endpoints are:

| Module | Endpoint |
|---|---|
| Students | `/api/students` |
| Courses | `/api/courses` |
| Enrollments | `/api/enrollments` |
| Exams | `/api/exams` |
| Rooms | `/api/rooms` |
| Invigilators | `/api/invigilators` |
| Exam Requirements | `/api/exam-requirements` |
| Room Equipment | `/api/room-equipment` |
| Room Availability | `/api/room-availability` |
| Invigilator Availability | `/api/invigilator-availability` |
| Schedule Assignments | `/api/scheduling/assignments` |

To schedule an individual examination:

```text
POST /api/scheduling/exam/{examId}
```

These endpoints can be tested using Postman.

## Generating Sample Data

I have included a Python script to generate sample data.

From the main project folder, run:

```bash
python generator/generator.py
```

The generator uses a fixed random seed (`20261022`) so that the same input produces repeatable data.

It generates:

- 5,000 students
- 50 courses
- 15,000 enrollments
- 300 examinations
- 100 rooms
- 200 invigilators
- Examination requirements
- Room equipment
- Room availability
- Invigilator availability
- Student accommodations
- Candidate schedule assignments

The generated files are saved in `data/generated/` in CSV and JSON formats.

A separate file contains descriptions of five test scenarios, including student conflicts, room capacity problems, invigilator conflicts, accommodation requirements and room availability issues.

## Testing

I used Postman to test the backend APIs.

I checked the creation and retrieval of records and tested the scheduling endpoint with different room and availability conditions.

I also ran the Python generator successfully and checked its validation results.

The generator creates candidate assignments. These should not be treated as final, conflict-free scheduling results. The actual scheduling decisions are made by the backend scheduling service.

## Current Limitations

- The sample data is generated for testing and does not represent real students.
- The generator creates candidate assignments rather than final approved schedules.
- More automated tests and larger-scale performance testing are still needed.
- Authentication and role-based access can be added in a future version.
- The project needs further testing before being used in a real college environment.

## Future Improvements

Some improvements I would like to make are:

- Build a complete frontend for administrators.
- Add reports showing scheduled and unscheduled examinations.
- Improve scheduling performance for larger datasets.
- Add automated tests for all scheduling conflicts.
- Add user login and role-based permissions.

## Development Notes

AI tools were used during development to help with coding suggestions, debugging and documentation.

I reviewed and tested the implementation through application startup, database operations and Postman requests. The final code and its behaviour should be verified before deployment.

This project is intended as a working demonstration of an examination scheduling system.