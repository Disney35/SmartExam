
import csv
import json
import random
import hashlib
from pathlib import Path
from datetime import date, timedelta

SEED = 20261022
random.seed(SEED)

ROOT = Path(__file__).resolve().parent.parent
OUT = ROOT / "data" / "generated"
OUT.mkdir(parents=True, exist_ok=True)

def write_csv(name, rows):
    path = OUT / f"{name}.csv"
    if not rows:
        path.write_text("", encoding="utf-8")
        return
    with path.open("w", newline="", encoding="utf-8") as f:
        writer = csv.DictWriter(f, fieldnames=list(rows[0].keys()))
        writer.writeheader()
        writer.writerows(rows)

def write_json(name, rows):
    (OUT / f"{name}.json").write_text(
        json.dumps(rows, indent=2, default=str),
        encoding="utf-8"
    )

def checksum(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()

# ---------------- MASTER DATA ----------------

departments = [
    "Computer Science", "Information Technology",
    "Electronics", "Mechanical", "Civil"
]

students = []
for i in range(1, 5001):
    students.append({
        "student_id": f"STU-{i:06d}",
        "full_name": f"Student {i:05d}",
        "email": f"student{i}@example.edu",
        "department": random.choice(departments),
        "active": True
    })

courses = []
for i in range(1, 51):
    courses.append({
        "course_id": f"COURSE-{i:06d}",
        "course_name": f"Course {i:03d}",
        "department": random.choice(departments),
        "credits": random.randint(2, 5)
    })

enrollments = []
for i in range(1, 15001):
    student = random.choice(students)
    course = random.choice(courses)
    enrollments.append({
        "enrollment_id": f"ENR-{i:06d}",
        "student_id": student["student_id"],
        "course_id": course["course_id"],
        "enrollment_date": "2026-08-01",
        "status": "ACTIVE"
    })

exams = []
for i in range(1, 301):
    course = courses[(i - 1) % len(courses)]
    exams.append({
        "exam_id": f"EXAM-2026-{i:06d}",
        "course_id": course["course_id"],
        "exam_name": f"{course['course_name']} Examination",
        "exam_date": str(date(2026, 11, 15) + timedelta(days=(i - 1) % 10)),
        "start_time": random.choice(["09:00:00", "13:00:00", "17:00:00"]),
        "duration_minutes": random.choice([90, 120, 150, 180]),
        "status": "PLANNED"
    })

rooms = []
for i in range(1, 101):
    rooms.append({
        "room_id": f"ROOM-{i:06d}",
        "room_name": f"Hall {i:03d}",
        "building": f"Block {(i - 1) // 20 + 1}",
        "capacity": random.choice([30, 40, 50, 60, 80, 100]),
        "room_type": random.choice(["EXAM_HALL", "LAB"]),
        "accessibility_available": i % 4 == 0 or i == 1,
        "active": True
    })

invigilators = []
for i in range(1, 201):
    invigilators.append({
        "invigilator_id": f"INV-{i:06d}",
        "full_name": f"Invigilator {i:03d}",
        "email": f"invigilator{i}@example.edu",
        "department": random.choice(departments),
        "active": True
    })

# ---------------- REQUIREMENTS AND AVAILABILITY ----------------

exam_requirements = []
for i, exam in enumerate(exams, 1):
    exam_requirements.append({
        "requirement_id": f"REQ-{i:06d}",
        "exam_id": exam["exam_id"],
        "required_room_type": "LAB" if i % 5 == 0 else "EXAM_HALL",
        "required_equipment": "PROJECTOR" if i % 5 == 0 else "",
        "minimum_capacity": 30,
        "invigilators_required": 1
    })

room_equipment = []
equipment_id = 1
for room in rooms:
    for equipment in (["PROJECTOR", "SMART_BOARD"] if room["room_type"] == "LAB" else ["PROJECTOR"]):
        room_equipment.append({
            "equipment_id": f"EQ-{equipment_id:06d}",
            "room_id": room["room_id"],
            "equipment_name": equipment,
            "quantity": 1,
            "operational": True
        })
        equipment_id += 1

room_availability = []
availability_id = 1
for room in rooms:
    for day in range(10):
        exam_date = date(2026, 11, 15) + timedelta(days=day)
        room_availability.append({
            "availability_id": f"RAV-{availability_id:06d}",
            "room_id": room["room_id"],
            "availability_date": str(exam_date),
            "start_time": "08:00:00",
            "end_time": "21:00:00",
            "available": True,
            "reason": "Available for examinations"
        })
        availability_id += 1

invigilator_availability = []
availability_id = 1
for inv in invigilators:
    for day in range(10):
        exam_date = date(2026, 11, 15) + timedelta(days=day)
        invigilator_availability.append({
            "availability_id": f"IAV-{availability_id:06d}",
            "invigilator_id": inv["invigilator_id"],
            "availability_date": str(exam_date),
            "start_time": "08:00:00",
            "end_time": "21:00:00",
            "available": True
        })
        availability_id += 1

accommodations = []
for i in range(1, 101):
    student = students[(i - 1) * 10]
    accommodations.append({
        "accommodation_id": f"ACC-{i:06d}",
        "student_id": student["student_id"],
        "accommodation_type": "EXTRA_TIME",
        "extra_time_minutes": 30,
        "accessible_room_required": i % 3 == 0,
        "active": True
    })

# ---------------- SCHEDULE DATA ----------------

assignments = []
assignment_id = 1
for i, exam in enumerate(exams):
    room = rooms[i % len(rooms)]
    invigilator = invigilators[i % len(invigilators)]

    assignments.append({
        "assignment_id": f"ASN-{assignment_id:06d}",
        "exam_id": exam["exam_id"],
        "room_id": room["room_id"],
        "invigilator_id": invigilator["invigilator_id"],
        "assignment_status": "PROPOSED",
        "decision_reason": "Generated candidate assignment; must pass scheduler validation",
        "effective_duration_minutes": exam["duration_minutes"],
        "start_time": exam["start_time"]
    })
    assignment_id += 1

# ---------------- LABELLED EDGE CASES ----------------

ground_truth = [
    {
        "case_id": "EDGE-001",
        "case_type": "STUDENT_OVERLAPPING_EXAMS",
        "description": "One student is enrolled in two exams scheduled in the same time slot.",
        "expected_result": "CONFLICT_DETECTED",
        "severity": "HIGH"
    },
    {
        "case_id": "EDGE-002",
        "case_type": "ROOM_CAPACITY_EXCEEDED",
        "description": "Candidate room capacity is lower than the number of enrolled students.",
        "expected_result": "ROOM_REJECTED",
        "severity": "HIGH"
    },
    {
        "case_id": "EDGE-003",
        "case_type": "INVIGILATOR_DOUBLE_BOOKED",
        "description": "One invigilator is assigned to overlapping examinations.",
        "expected_result": "CONFLICT_DETECTED",
        "severity": "HIGH"
    },
    {
        "case_id": "EDGE-004",
        "case_type": "ACCOMMODATION_REQUIRED",
        "description": "A student requires additional examination time.",
        "expected_result": "EXTRA_TIME_REQUIRED",
        "severity": "HIGH"
    },
    {
        "case_id": "EDGE-005",
        "case_type": "ROOM_UNAVAILABLE",
        "description": "A room is unavailable during the requested examination time.",
        "expected_result": "ROOM_REJECTED",
        "severity": "HIGH"
    }
]

# ---------------- VALIDATION ----------------

checks = {}

def unique(rows, key):
    values = [r[key] for r in rows]
    return len(values) == len(set(values))

checks["unique_student_ids"] = unique(students, "student_id")
checks["unique_course_ids"] = unique(courses, "course_id")
checks["unique_exam_ids"] = unique(exams, "exam_id")
checks["unique_room_ids"] = unique(rooms, "room_id")
checks["unique_invigilator_ids"] = unique(invigilators, "invigilator_id")

student_ids = {s["student_id"] for s in students}
course_ids = {c["course_id"] for c in courses}
exam_ids = {e["exam_id"] for e in exams}
room_ids = {r["room_id"] for r in rooms}
invigilator_ids = {v["invigilator_id"] for v in invigilators}

checks["enrollment_student_foreign_keys"] = all(
    e["student_id"] in student_ids for e in enrollments
)
checks["enrollment_course_foreign_keys"] = all(
    e["course_id"] in course_ids for e in enrollments
)
checks["exam_course_foreign_keys"] = all(
    e["course_id"] in course_ids for e in exams
)
checks["assignment_exam_foreign_keys"] = all(
    a["exam_id"] in exam_ids for a in assignments
)
checks["assignment_room_foreign_keys"] = all(
    a["room_id"] in room_ids for a in assignments
)
checks["assignment_invigilator_foreign_keys"] = all(
    a["invigilator_id"] in invigilator_ids for a in assignments
)
checks["room_equipment_foreign_keys"] = all(
    e["room_id"] in room_ids for e in room_equipment
)
checks["accommodation_student_foreign_keys"] = all(
    a["student_id"] in student_ids for a in accommodations
)

checks["positive_room_capacity"] = all(r["capacity"] > 0 for r in rooms)
checks["positive_exam_duration"] = all(e["duration_minutes"] > 0 for e in exams)
checks["positive_equipment_quantity"] = all(e["quantity"] > 0 for e in room_equipment)

# ---------------- EXPORT ----------------

datasets = {
    "students": students,
    "courses": courses,
    "enrollments": enrollments,
    "exams": exams,
    "rooms": rooms,
    "invigilators": invigilators,
    "exam_requirements": exam_requirements,
    "room_equipment": room_equipment,
    "room_availability": room_availability,
    "invigilator_availability": invigilator_availability,
    "accommodations": accommodations,
    "schedule_assignments": assignments
}

for name, rows in datasets.items():
    write_csv(name, rows)
    write_json(name, rows)

write_json("ground_truth", ground_truth)

summary = {
    "seed": SEED,
    "generated_at": "Deterministic dataset; timestamp intentionally omitted",
    "record_counts": {name: len(rows) for name, rows in datasets.items()},
    "ground_truth_cases": len(ground_truth),
    "validation_checks": checks,
    "all_validation_checks_passed": all(checks.values()),
    "note": "Candidate assignments are proposed data, not proof of a conflict-free accepted schedule."
}

write_json("validation_summary", summary)

manifest = {}
for path in sorted(OUT.iterdir()):
    if path.is_file() and path.name != "source_manifest.json":
        manifest[path.name] = checksum(path)

write_json("source_manifest", manifest)

print("\nSMARTEXAM DATA GENERATION COMPLETE")
print("----------------------------------")
for name, count in summary["record_counts"].items():
    print(f"{name}: {count}")
print(f"Ground truth cases: {len(ground_truth)}")
print(f"Validation passed: {summary['all_validation_checks_passed']}")
print(f"Output folder: {OUT}")
print("----------------------------------")
