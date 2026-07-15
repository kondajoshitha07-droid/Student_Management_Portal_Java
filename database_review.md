# Database Layer Review Report

## 1. Java Files Created
The following Java files were created to implement the database layer:
- `Admin.java`
- `Student.java`
- `Mark.java`
- `DatabaseManager.java`
- `AdminDAO.java`
- `StudentDAO.java`
- `MarksDAO.java`

## 2. Package Structure
- **com.studentportal.model**: `Admin.java`, `Student.java`, `Mark.java`
- **com.studentportal.database**: `DatabaseManager.java`
- **com.studentportal.dao**: `AdminDAO.java`, `StudentDAO.java`, `MarksDAO.java`

## 3. Public Methods per Class
### `Admin.java`
- `Admin()`
- `Admin(String username, String passwordHash)`
- `getUsername()`
- `setUsername(String username)`
- `getPasswordHash()`
- `setPasswordHash(String passwordHash)`
- `toString()`

### `Student.java`
- `Student()`
- `Student(String rollNo, String name, String passwordHash, String department, int year, String email, String phone)`
- `getRollNo()`, `setRollNo(String rollNo)`
- `getName()`, `setName(String name)`
- `getPasswordHash()`, `setPasswordHash(String passwordHash)`
- `getDepartment()`, `setDepartment(String department)`
- `getYear()`, `setYear(int year)`
- `getEmail()`, `setEmail(String email)`
- `getPhone()`, `setPhone(String phone)`
- `toString()`

### `Mark.java`
- `Mark()`
- `Mark(int id, String rollNo, String subjectName, int marks)`
- `getId()`, `setId(int id)`
- `getRollNo()`, `setRollNo(String rollNo)`
- `getSubjectName()`, `setSubjectName(String subjectName)`
- `getMarks()`, `setMarks(int marks)`
- `toString()`

### `DatabaseManager.java`
- `getConnection()`

### `AdminDAO.java`
- `validateLogin(String username, String password)`

### `StudentDAO.java`
- `createStudent(Student student)`
- `updateStudent(Student student)`
- `deleteStudent(String rollNo)`
- `getStudentByRollNo(String rollNo)`
- `getAllStudents()`
- `searchStudentsByName(String name)`

### `MarksDAO.java`
- `insertMarks(Mark mark)`
- `updateMarks(Mark mark)`
- `getMarksByRollNo(String rollNo)`
- `deleteMarks(int id)`

## 4. SQL Statements Used
- `CREATE TABLE IF NOT EXISTS admins (username TEXT PRIMARY KEY, password_hash TEXT NOT NULL);`
- `CREATE TABLE IF NOT EXISTS students (roll_no TEXT PRIMARY KEY, name TEXT NOT NULL, password_hash TEXT NOT NULL, department TEXT NOT NULL, year INTEGER NOT NULL, email TEXT, phone TEXT);`
- `CREATE TABLE IF NOT EXISTS marks (id INTEGER PRIMARY KEY AUTOINCREMENT, roll_no TEXT NOT NULL, subject_name TEXT NOT NULL, marks INTEGER NOT NULL, FOREIGN KEY (roll_no) REFERENCES students(roll_no));`
- `PRAGMA foreign_keys = ON;`
- `SELECT COUNT(*) FROM admins WHERE username = ?`
- `INSERT INTO admins (username, password_hash) VALUES (?, ?)`
- `SELECT password_hash FROM admins WHERE username = ?`
- `INSERT INTO students (roll_no, name, password_hash, department, year, email, phone) VALUES (?, ?, ?, ?, ?, ?, ?)`
- `UPDATE students SET name = ?, password_hash = ?, department = ?, year = ?, email = ?, phone = ? WHERE roll_no = ?`
- `DELETE FROM students WHERE roll_no = ?`
- `SELECT * FROM students WHERE roll_no = ?`
- `SELECT * FROM students`
- `SELECT * FROM students WHERE name LIKE ?`
- `INSERT INTO marks (roll_no, subject_name, marks) VALUES (?, ?, ?)`
- `UPDATE marks SET roll_no = ?, subject_name = ?, marks = ? WHERE id = ?`
- `SELECT * FROM marks WHERE roll_no = ?`
- `DELETE FROM marks WHERE id = ?`

## 5. Table Schemas
### `admins`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `username` | TEXT | PRIMARY KEY |
| `password_hash` | TEXT | NOT NULL |

### `students`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `roll_no` | TEXT | PRIMARY KEY |
| `name` | TEXT | NOT NULL |
| `password_hash` | TEXT | NOT NULL |
| `department` | TEXT | NOT NULL |
| `year` | INTEGER | NOT NULL |
| `email` | TEXT | |
| `phone` | TEXT | |

### `marks`
| Column | Type | Constraints |
| :--- | :--- | :--- |
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT |
| `roll_no` | TEXT | NOT NULL, FOREIGN KEY REFERENCES students(roll_no) |
| `subject_name` | TEXT | NOT NULL |
| `marks` | INTEGER | NOT NULL |

## 6. Imports Used
- `java.sql.Connection`
- `java.sql.DriverManager`
- `java.sql.PreparedStatement`
- `java.sql.SQLException`
- `java.sql.Statement`
- `java.sql.ResultSet`
- `java.util.ArrayList`
- `java.util.List`
- `org.mindrot.jbcrypt.BCrypt`
- `com.studentportal.database.DatabaseManager`
- `com.studentportal.model.Student`
- `com.studentportal.model.Mark`

## 7. DatabaseManager Usage
The `DatabaseManager` class is currently only called within the project by the three newly created DAO classes (`AdminDAO`, `StudentDAO`, `MarksDAO`) to establish database connections securely.

## 8. Modified Existing Files
No existing files outside the database layer were modified during this implementation.

## 9. Unfinished Methods / TODOs
There are no TODOs, placeholders, or unfinished methods in any of the newly created files. All required methods have been fully implemented exactly as specified.
