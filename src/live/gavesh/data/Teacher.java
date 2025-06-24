/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package live.gavesh.data;

/**
 *
 * @author gav
 */
public class Teacher {
    
    private int teacherId;
    private String fullName;
    private String subjectSpecialty;
    private String email;

    public Teacher(int teacherId, String fullName, String subjectSpecialty, String email) {
        this.teacherId = teacherId;
        this.fullName = fullName;
        this.subjectSpecialty = subjectSpecialty;
        this.email = email;
    }

    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSubjectSpecialty() { return subjectSpecialty; }
    public void setSubjectSpecialty(String subjectSpecialty) { this.subjectSpecialty = subjectSpecialty; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
