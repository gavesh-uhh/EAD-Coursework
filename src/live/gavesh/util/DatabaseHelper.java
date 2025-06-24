/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package live.gavesh.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import live.gavesh.data.Attendance;
import live.gavesh.data.Mark;
import live.gavesh.data.Student;
import live.gavesh.data.Subject;
import live.gavesh.data.Teacher;
import live.gavesh.data.User;

/**
 *
 * @author gav
 */
public class DatabaseHelper {

    private static final String URI = "jdbc:mysql://localhost:3306/ead_coursework";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    /**
     * Opens a new database connection.
     *
     * @return A Connection object if successful, null otherwise.
     */
    public static Connection openConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URI, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            return null;
        } catch (SQLException e) {
            System.err.println("Error opening database connection: " + e.getMessage());
            return null;
        }
    }

    /**
     * Closes a database connection.
     *
     * @param conn The Connection object to close.
     */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    /**
     * Authenticates a user against the database.
     *
     * @param username The username to authenticate.
     * @param password The password hash to authenticate.
     * @return true if authentication is successful, false otherwise.
     */
    public static boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return false;
            }
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Authentication error: " + e.getMessage());
            return false;
        }
    }

    public static boolean insertUser(String username, String password, String email) {
        String query = "INSERT INTO marks (username, password_hash, role, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return false;
            }
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, "Staff");
            stmt.setString(4, email);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to insert student: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all students from the database.
     *
     * @return An ArrayList of Student objects.
     */
    public static ArrayList<Student> getAllStudents() {
        ArrayList<Student> students = new ArrayList<>();
        String query = "SELECT * FROM students";
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (conn == null) {
                return students;
            }
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("student_id"));
                String name = rs.getString("full_name");
                String grade = rs.getString("grade_level");
                String contact = rs.getString("contact_number");
                String email = rs.getString("email");
                String address = rs.getString("address");
                Student s = new Student(id, name, grade, contact, email, address);
                students.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving students: " + e.getMessage());
        }
        return students;
    }

    /**
     * Retrieves all teachers from the database.
     *
     * @return An ArrayList of Teacher objects.
     */
    public static ArrayList<Teacher> getAllTeachers() {
        ArrayList<Teacher> teachers = new ArrayList<>();
        String query = "SELECT * FROM teachers";
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (conn == null) {
                return teachers;
            }
            while (rs.next()) {
                int id = rs.getInt("teacher_id");
                String name = rs.getString("full_name");
                String subject = rs.getString("subject_specialty");
                String email = rs.getString("email");
                Teacher t = new Teacher(id, name, subject, email);
                teachers.add(t);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving teachers: " + e.getMessage());
        }
        return teachers;
    }

    /**
     * Retrieves all subjects from the database.
     *
     * @return An ArrayList of Subject objects.
     */
    public static ArrayList<Subject> getAllSubjects() {
        ArrayList<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM subjects";
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (conn == null) {
                return subjects;
            }
            while (rs.next()) {
                int id = rs.getInt("subject_id");
                String name = rs.getString("subject_name");
                int teacherId = rs.getInt("teacher_id");
                Subject s = new Subject(id, name, teacherId);
                subjects.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving subjects: " + e.getMessage());
        }
        return subjects;
    }

    /**
     * Retrieves the subject ID by its name.
     *
     * @param subjectName The name of the subject.
     * @return The subject ID if found, -1 otherwise.
     */
    public static int getSubjectIdByName(String subjectName) {
        String query = "SELECT subject_id FROM subjects WHERE subject_name = ?";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return -1;
            }
            stmt.setString(1, subjectName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("subject_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding subject ID: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves the subject ID by its name.
     *
     * @param teacherName The name of the subject.
     * @return The subject ID if found, -1 otherwise.
     */
    public static int getTeacherIDByName(String teacherName) {
        String query = "SELECT teacher_id FROM teachers WHERE full_name = ?";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return -1;
            }
            stmt.setString(1, teacherName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("teacher_id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher ID: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves all marks for a specific student.
     *
     * @param studentId The ID of the student.
     * @return An ArrayList of Mark objects.
     */
    public static ArrayList<Mark> getMarksByStudentId(int studentId) {
        ArrayList<Mark> marks = new ArrayList<>();
        String query = "SELECT * FROM marks WHERE student_id = ?";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return marks;
            }
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("mark_id");
                    int subjectId = rs.getInt("subject_id");
                    String examType = rs.getString("exam_type");
                    double obtained = rs.getDouble("marks_obtained");
                    double max = rs.getDouble("max_marks");
                    java.sql.Date date = rs.getDate("exam_date");
                    Mark m = new Mark(id, studentId, subjectId, examType, obtained, max, date);
                    marks.add(m);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving marks for student ID " + studentId + ": " + e.getMessage());
        }
        return marks;
    }

    /**
     * Inserts an attendance record into the database.
     *
     * @param studentId The ID of the student.
     * @param subjectId The ID of the subject.
     * @param attendanceDate The date of the attendance.
     * @param status The attendance status (e.g., "Present", "Absent").
     * @return true if insertion is successful, false otherwise.
     */
    public static boolean insertAttendance(int studentId, int subjectId, java.util.Date attendanceDate, String status) { // Renamed examDate to attendanceDate and added status parameter
        String query = "INSERT INTO attendance (student_id, subject_id, date, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return false;
            }
            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            stmt.setDate(3, new java.sql.Date(attendanceDate.getTime()));
            stmt.setString(4, status); // Use the status parameter
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to insert attendance: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a mark record into the database.
     *
     * @param studentId The ID of the student.
     * @param subjectId The ID of the subject.
     * @param examType The type of exam.
     * @param examDate The date of the exam.
     * @param marksObtained The marks obtained by the student.
     * @param maxMarks The maximum possible marks.
     * @return true if insertion is successful, false otherwise.
     */
    public static boolean insertMark(int studentId, int subjectId, String examType, java.util.Date examDate, double marksObtained) {
        String query = "INSERT INTO marks (student_id, subject_id, exam_type, exam_date, marks_obtained, max_marks) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return false;
            }
            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            stmt.setString(3, examType);
            stmt.setDate(4, new java.sql.Date(examDate.getTime()));
            stmt.setDouble(5, marksObtained);
            stmt.setDouble(6, 100);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Failed to insert mark: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a teacher and associated subjects from the database. This
     * operation is performed within a transaction to ensure data integrity.
     *
     * @param teacherId The ID of the teacher to delete.
     * @return true if the teacher and all related data were successfully
     * deleted, false otherwise.
     */
    public static boolean deleteTeacher(int teacherId) {
        Connection conn = null;
        PreparedStatement stmtDeleteSubjects = null;
        PreparedStatement stmtDeleteTeacher = null;
        try {
            conn = openConnection();
            if (conn == null) {
                System.err.println("Failed to open database connection for teacher deletion.");
                return false;
            }
            conn.setAutoCommit(false); // Start transaction

            // Delete subjects taught by this teacher
            String sqlDeleteSubjects = "DELETE FROM subjects WHERE teacher_id = ?";
            stmtDeleteSubjects = conn.prepareStatement(sqlDeleteSubjects);
            stmtDeleteSubjects.setInt(1, teacherId);
            stmtDeleteSubjects.executeUpdate();
            System.out.println("Deleted subjects taught by teacher ID: " + teacherId);

            // Delete the teacher
            String sqlDeleteTeacher = "DELETE FROM teachers WHERE teacher_id = ?";
            stmtDeleteTeacher = conn.prepareStatement(sqlDeleteTeacher);
            stmtDeleteTeacher.setInt(1, teacherId);
            int rowsAffected = stmtDeleteTeacher.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback(); // Rollback if no teacher found
                System.out.println("No teacher found with ID " + teacherId + ". Deletion rolled back.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting teacher and related data: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                    System.err.println("Transaction rolled back for teacher deletion.");
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            return false;
        } finally {
            // Close statements and connection
            try {
                if (stmtDeleteSubjects != null) {
                    stmtDeleteSubjects.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing stmtDeleteSubjects: " + e.getMessage());
            }
            try {
                if (stmtDeleteTeacher != null) {
                    stmtDeleteTeacher.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing stmtDeleteTeacher: " + e.getMessage());
            }
            closeConnection(conn);
        }
    }

    /**
     * Deletes a subject from the database.
     *
     * @param subjectId The ID of the subject to delete.
     * @return true if the subject was successfully deleted, false otherwise.
     */
    public static boolean deleteSubject(int subjectId) {
        Connection conn = null;
        PreparedStatement stmtDeleteMarks = null;
        PreparedStatement stmtDeleteAttendance = null;
        PreparedStatement stmtDeleteSubject = null;
        try {
            conn = openConnection();
            if (conn == null) {
                System.err.println("Failed to open database connection for subject deletion.");
                return false;
            }
            conn.setAutoCommit(false); // Start transaction

            // Delete marks associated with this subject
            String sqlDeleteMarks = "DELETE FROM marks WHERE subject_id = ?";
            stmtDeleteMarks = conn.prepareStatement(sqlDeleteMarks);
            stmtDeleteMarks.setInt(1, subjectId);
            stmtDeleteMarks.executeUpdate();
            System.out.println("Deleted marks for subject ID: " + subjectId);

            // Delete attendance records associated with this subject
            String sqlDeleteAttendance = "DELETE FROM attendance WHERE subject_id = ?";
            stmtDeleteAttendance = conn.prepareStatement(sqlDeleteAttendance);
            stmtDeleteAttendance.setInt(1, subjectId);
            stmtDeleteAttendance.executeUpdate();
            System.out.println("Deleted attendance records for subject ID: " + subjectId);

            // Delete the subject itself
            String sqlDeleteSubject = "DELETE FROM subjects WHERE subject_id = ?";
            stmtDeleteSubject = conn.prepareStatement(sqlDeleteSubject);
            stmtDeleteSubject.setInt(1, subjectId);
            int rowsAffected = stmtDeleteSubject.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit(); // Commit transaction
                return true;
            } else {
                conn.rollback(); // Rollback if no subject found
                System.out.println("No subject found with ID " + subjectId + ". Deletion rolled back.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error deleting subject and related data: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                    System.err.println("Transaction rolled back for subject deletion.");
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            return false;
        } finally {
            // Close statements and connection
            try {
                if (stmtDeleteMarks != null) {
                    stmtDeleteMarks.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing stmtDeleteMarks: " + e.getMessage());
            }
            try {
                if (stmtDeleteAttendance != null) {
                    stmtDeleteAttendance.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing stmtDeleteAttendance: " + e.getMessage());
            }
            try {
                if (stmtDeleteSubject != null) {
                    stmtDeleteSubject.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing stmtDeleteSubject: " + e.getMessage());
            }
            closeConnection(conn);
        }
    }

    /**
     * Deletes a specific mark record from the database.
     *
     * @param studentId The ID of the student.
     * @param subjectId The ID of the subject.
     * @param examType The type of exam.
     * @param examDate The date of the exam.
     * @return true if deletion is successful, false otherwise.
     */
    public static boolean deleteMark(int studentId, int subjectId, String examType, Date examDate) {
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM marks WHERE student_id = ? AND subject_id = ? AND exam_type = ? AND exam_date = ?")) {
            if (conn == null) {
                return false;
            }
            stmt.setInt(1, studentId);
            stmt.setInt(2, subjectId);
            stmt.setString(3, examType);
            stmt.setDate(4, examDate);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting mark: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all marks from the database.
     *
     * @return An ArrayList of all Mark objects.
     */
    public static ArrayList<Mark> getAllMarks() {
        ArrayList<Mark> marks = new ArrayList<>();
        String query = "SELECT * FROM marks";
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (conn == null) {
                return marks;
            }
            while (rs.next()) {
                int id = rs.getInt("mark_id");
                int studentId = rs.getInt("student_id");
                int subjectId = rs.getInt("subject_id");
                String examType = rs.getString("exam_type");
                double obtained = rs.getDouble("marks_obtained");
                double max = rs.getDouble("max_marks");
                java.sql.Date date = rs.getDate("exam_date");
                Mark m = new Mark(id, studentId, subjectId, examType, obtained, max, date);
                marks.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving all marks: " + e.getMessage());
        }
        return marks;
    }

    /**
     * Retrieves all attendance records from the database.
     *
     * @return An ArrayList of Attendance objects.
     */
    public static ArrayList<Attendance> getAllAttendance() {
        ArrayList<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance";
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (conn == null) {
                return attendanceList;
            }
            while (rs.next()) {
                int id = rs.getInt("attendance_id");
                int studentId = rs.getInt("student_id");
                int subjectId = rs.getInt("subject_id");
                java.sql.Date date = rs.getDate("date");
                String status = rs.getString("status");
                Attendance a = new Attendance(id, studentId, subjectId, date, status);
                attendanceList.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving attendance records: " + e.getMessage());
        }
        return attendanceList;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return An ArrayList of User objects.
     */
    public static ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection conn = openConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (conn == null) {
                return users;
            }
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String role = rs.getString("role");
                String email = rs.getString("email");
                User u = new User(id, username, passwordHash, role, email);
                users.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving users: " + e.getMessage());
        }
        return users;
    }

    /**
     * Retrieves a student by their ID.
     *
     * @param studentId The ID of the student.
     * @return A Student object if found, null otherwise.
     */
    public static Student getStudentById(int studentId) {
        String query = "SELECT * FROM students WHERE student_id = ?";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return null;
            }
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            String.valueOf(rs.getInt("student_id")),
                            rs.getString("full_name"),
                            rs.getString("grade_level"),
                            rs.getString("contact_number"),
                            rs.getString("email"),
                            rs.getString("address")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student by ID " + studentId + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves a student by their full name.
     *
     * @param studentName The full name of the student.
     * @return A Student object if found, null otherwise.
     */
    public static Student getStudentByName(String studentName) {
        String query = "SELECT * FROM students WHERE full_name = ?";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return null;
            }
            stmt.setString(1, studentName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            String.valueOf(rs.getInt("student_id")),
                            rs.getString("full_name"),
                            rs.getString("grade_level"),
                            rs.getString("contact_number"),
                            rs.getString("email"),
                            rs.getString("address")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving student by name '" + studentName + "': " + e.getMessage());
        }
        return null;
    }

    /**
     * Inserts a new subject into the database.
     *
     * @param s The Subject object to insert.
     * @return true if insertion is successful, false otherwise.
     */
    public static boolean insertSubject(Subject s) {
        String query = "INSERT INTO subjects (subject_name, teacher_id) VALUES (?, ?)";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return false;
            }
            stmt.setString(1, s.getSubjectName());
            stmt.setInt(2, s.getTeacherId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting subject: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new teacher into the database.
     *
     * @param t The Teacher object to insert.
     * @return true if insertion is successful, false otherwise.
     */
    public static boolean insertTeacher(Teacher t) {
        String query = "INSERT INTO teachers (full_name, subject_specialty, email) VALUES (?, ?, ?)";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return false;
            }
            stmt.setString(1, t.getFullName());
            stmt.setString(2, t.getSubjectSpecialty());
            stmt.setString(3, t.getEmail());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting teacher: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserts a new student into the database.
     *
     * @param s The Student object to insert.
     * @return true if insertion is successful, false otherwise.
     */
    public static boolean insertStudent(Student s) {
        String query = "INSERT INTO students (full_name, grade_level, contact_number, email, address) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return false;
            }
            stmt.setString(1, s.getFullName());
            stmt.setString(2, s.getGradeLevel());
            stmt.setString(3, s.getContactNumber());
            stmt.setString(4, s.getEmail());
            stmt.setString(5, s.getAddress());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting student: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a student and their associated attendance records from the
     * database. This operation is performed within a transaction to ensure data
     * integrity.
     *
     * @param studentId The ID of the student to delete.
     * @return true if the student and all related data were successfully
     * deleted, false otherwise.
     */
    public static boolean deleteStudent(int studentId) {
        Connection conn = null;
        PreparedStatement stmtDeleteAttendance = null;
        PreparedStatement stmtDeleteStudent = null;
        PreparedStatement stmtDeleteGrades = null;

        try {

            conn = openConnection();
            if (conn == null) {
                System.err.println("Failed to open database connection for deletion.");
                return false;
            }

            conn.setAutoCommit(false);

            String sqlDeleteAttendance = "DELETE FROM attendance WHERE student_id = ?";
            stmtDeleteAttendance = conn.prepareStatement(sqlDeleteAttendance);
            stmtDeleteAttendance.setInt(1, studentId);
            stmtDeleteAttendance.executeUpdate();
            System.out.println("Deleted attendance records for student ID: " + studentId);

            String sqlDeleteGrades = "DELETE FROM grades WHERE student_id = ?";
            stmtDeleteGrades = conn.prepareStatement(sqlDeleteGrades);
            stmtDeleteGrades.setInt(1, studentId);
            stmtDeleteGrades.executeUpdate();
            System.out.println("Deleted attendance records for student ID: " + studentId);

            String sqlDeleteStudent = "DELETE FROM students WHERE student_id = ?";
            stmtDeleteStudent = conn.prepareStatement(sqlDeleteStudent);
            stmtDeleteStudent.setInt(1, studentId);
            int rowsAffected = stmtDeleteStudent.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                System.out.println("No student found with ID " + studentId + ". Deletion rolled back.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error deleting student and related data: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Transaction rolled back for student deletion.");
                } catch (SQLException rbEx) {
                    System.err.println("Error during rollback: " + rbEx.getMessage());
                }
            }
            return false;
        } finally {
            try {
                if (stmtDeleteAttendance != null) {
                    stmtDeleteAttendance.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing stmtDeleteAttendance: " + e.getMessage());
            }
            try {
                if (stmtDeleteStudent != null) {
                    stmtDeleteStudent.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing stmtDeleteStudent: " + e.getMessage());
            }
            closeConnection(conn);
        }
    }

    /**
     * Retrieves attendance records for a specific student.
     *
     * @param studentId The ID of the student.
     * @return An ArrayList of Attendance objects for the specified student.
     */
    public static ArrayList<Attendance> getAttendanceByStudentId(int studentId) {
        ArrayList<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT * FROM attendance WHERE student_id = ?";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return attendanceList;
            }
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attendanceList.add(new Attendance(
                            rs.getInt("attendance_id"),
                            rs.getInt("student_id"),
                            rs.getInt("subject_id"),
                            rs.getDate("date"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching attendance for student ID " + studentId + ": " + e.getMessage());
        }
        return attendanceList;
    }

    /**
     * Retrieves subjects taught by a specific teacher.
     *
     * @param teacherId The ID of the teacher.
     * @return An ArrayList of Subject objects taught by the teacher.
     */
    public static ArrayList<Subject> getSubjectsByTeacher(int teacherId) {
        ArrayList<Subject> subjects = new ArrayList<>();
        String query = "SELECT * FROM subjects WHERE teacher_id = ?";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return subjects;
            }
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subjects.add(new Subject(
                            rs.getInt("subject_id"),
                            rs.getString("subject_name"),
                            rs.getInt("teacher_id")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching subjects for teacher ID " + teacherId + ": " + e.getMessage());
        }
        return subjects;
    }

    /**
     * Retrieves a subject by its ID.
     *
     * @param subId The ID of the subject.
     * @return A Subject object if found, null otherwise.
     */
    public static Subject getSubjectById(int subId) {
        String query = "SELECT * FROM subjects WHERE subject_id = ?";
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return null;
            }
            stmt.setInt(1, subId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Subject(
                            subId,
                            rs.getString("subject_name"),
                            rs.getInt("teacher_id")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving subject by ID " + subId + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves students with an average performance below a given threshold.
     *
     * @param threshold The percentage threshold for low performance (e.g., 60.0
     * for below 60%).
     * @return An ArrayList of Student objects who are low performers.
     */
    public static ArrayList<Student> getStudentsWithLowPerformance(double threshold) {
        ArrayList<Student> lowPerformers = new ArrayList<>();
        // Note: It's good practice to ensure `max_marks` is not zero in the division
        String query = """
        SELECT s.* FROM students s
        JOIN marks m ON s.student_id = m.student_id
        WHERE m.max_marks > 0 -- Avoid division by zero
        GROUP BY s.student_id
        HAVING AVG(m.marks_obtained / m.max_marks * 100) < ?
        """;
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return lowPerformers;
            }
            stmt.setDouble(1, threshold);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lowPerformers.add(new Student(
                            rs.getString("student_id"),
                            rs.getString("full_name"),
                            rs.getString("grade_level"),
                            rs.getString("contact_number"),
                            rs.getString("email"),
                            rs.getString("address")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting low performance students: " + e.getMessage());
        }
        return lowPerformers;
    }

    /**
     * Calculates the attendance percentage for a specific student.
     *
     * @param studentId The ID of the student.
     * @return The attendance percentage (0-100), or 0 if no attendance records
     * exist.
     */
    public static double getStudentAttendancePercentage(int studentId) {
        String query = """
        SELECT
            SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS present_days,
            COUNT(*) AS total_days
        FROM attendance
        WHERE student_id = ?
        """;
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return 0.0;
            }
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int present = rs.getInt("present_days");
                    int total = rs.getInt("total_days");
                    return total > 0 ? (present * 100.0) / total : 0.0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating attendance for student ID " + studentId + ": " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Calculates the overall attendance percentage for all students.
     *
     * @return The overall attendance percentage (0-100), or 0 if no attendance
     * records exist.
     */
    public static double getOverallAttendancePercentage() {
        String query = """
        SELECT
            SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS total_present,
            COUNT(*) AS total_records
        FROM attendance
        """;
        try (Connection conn = openConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            if (conn == null) {
                return 0.0;
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int present = rs.getInt("total_present");
                    int total = rs.getInt("total_records");
                    return total > 0 ? (present * 100.0) / total : 0.0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating overall attendance: " + e.getMessage());
        }
        return 0.0;
    }
}
