# SmartExam – Data Dictionary

This document explains the main data used in the SmartExam project.

The purpose is to make it easy to understand what information is stored and how the different records are connected.

## 1. Students

Stores basic information about students.

| Field | Description |
|---|---|
| student_id | Unique ID assigned to each student |
| full_name | Student's name |
| email | Student's email address |
| department | Department to which the student belongs |
| active | Whether the student record is active |

Example ID: `STU-000001`

## 2. Courses

Stores information about available courses.

| Field | Description |
|---|---|
| course_id | Unique course ID |
| course_name | Name of the course |
| department | Department offering the course |
| credits | Number of course credits |

Example ID: `COURSE-000001`

## 3. Enrollments

Connects students with the courses they have enrolled in.

| Field | Description |
|---|---|
| enrollment_id | Unique enrollment ID |
| student_id | Student associated with the enrollment |
| course_id | Course associated with the enrollment |
| enrollment_date | Date of enrollment |
| status | Current enrollment status |

Example ID: `ENR-000001`

## 4. Examinations

Stores the details of examinations.

| Field | Description |
|---|---|
| exam_id | Unique examination ID |
| course_id | Course for which the exam is conducted |
| exam_name | Name of the examination |
| exam_date | Planned examination date |
| start_time | Planned start time |
| duration_minutes | Normal examination duration |
| status | Current examination status |

Example ID: `EXAM-2026-000001`

## 5. Rooms

Stores information about examination rooms.

| Field | Description |
|---|---|
| room_id | Unique room ID |
| room_name | Room name |
| building | Building where the room is located |
| capacity | Maximum number of students the room can accommodate |
| room_type | Type of room |
| accessibility_available | Whether accessibility facilities are available |

Example ID: `ROOM-000001`

## 6. Invigilators

Stores information about examination invigilators.

| Field | Description |
|---|---|
| invigilator_id | Unique invigilator ID |
| full_name | Invigilator's name |
| email | Invigilator's email |
| department | Department |
| active | Whether the invigilator is active |

Example ID: `INV-000001`

## 7. Exam Requirements

Stores additional requirements for each examination.

| Field | Description |
|---|---|
| requirement_id | Unique requirement ID |
| exam_id | Related examination |
| required_room_type | Type of room needed |
| required_equipment | Equipment needed |
| minimum_capacity | Minimum room capacity |
| invigilators_required | Number of invigilators needed |

Example: An examination may require a laboratory with a projector.

## 8. Room Equipment

Stores the equipment available in each room.

| Field | Description |
|---|---|
| equipment_id | Unique equipment record ID |
| room_id | Room containing the equipment |
| equipment_name | Name of the equipment |
| quantity | Available quantity |
| operational | Whether the equipment is working |

Examples include projectors and smart boards.

## 9. Room Availability

Stores the dates and times when rooms can be used.

| Field | Description |
|---|---|
| availability_id | Unique availability record ID |
| room_id | Related room |
| availability_date | Date of availability |
| start_time | Available from |
| end_time | Available until |
| available | Whether the room is available |
| reason | Explanation of availability |

## 10. Invigilator Availability

Stores the dates and times when invigilators are available.

| Field | Description |
|---|---|
| availability_id | Unique availability record ID |
| invigilator_id | Related invigilator |
| availability_date | Date of availability |
| start_time | Available from |
| end_time | Available until |
| available | Whether the invigilator is available |

## 11. Student Accommodations

Stores additional requirements for students during examinations.

| Field | Description |
|---|---|
| accommodation_id | Unique accommodation ID |
| student_id | Related student |
| accommodation_type | Type of accommodation |
| extra_time_minutes | Additional examination time |
| accessible_room_required | Whether an accessible room is needed |
| active | Whether the accommodation is active |

## 12. Schedule Assignments

Stores the results of examination scheduling.

| Field | Description |
|---|---|
| assignment_id | Unique assignment ID |
| exam_id | Related examination |
| room_id | Assigned room |
| invigilator_id | Assigned invigilator |
| assignment_status | Assignment status |
| decision_reason | Explanation of the scheduling decision |
| effective_duration_minutes | Duration after applying extra time |
| start_time | Examination start time |

An examination that cannot be assigned should have a clear reason explaining the problem.

## 13. Ground Truth

This is a separate dataset containing descriptions of known test scenarios.

It includes examples of:

- Two examinations that conflict for the same student.
- A room that does not have enough capacity.
- An invigilator assigned to overlapping examinations.
- A student who needs extra examination time.
- A room that is unavailable during the requested time.

These records help explain which situations should be identified during testing.

## Relationships Between Records

- An enrollment connects one student to one course.
- An examination belongs to a course.
- An exam requirement belongs to an examination.
- Room equipment belongs to a room.
- Room availability belongs to a room.
- Invigilator availability belongs to an invigilator.
- An accommodation belongs to a student.
- A schedule assignment connects an examination, a room and an invigilator.

All sample records generated for this project are synthetic.