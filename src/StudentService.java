import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class StudentService {
    private final Map<Integer, Student> studentMap = new ConcurrentHashMap<>();
    private int nextId = 1;

    public Collection<Student> getAllStudents() {
        return studentMap.values();
    }

    public Student getStudentById(int id) {
        return studentMap.get(id);
    }

    public synchronized Student createStudent(String name, int age, String email) {
        int id = nextId++;
        Student student = new Student(id, name, age, email);
        studentMap.put(id, student);
        return student;
    }

    public synchronized Student updateStudent(int id, String name, int age, String email) {
        Student student = studentMap.get(id);
        if (student != null) {
            student.setName(name);
            student.setAge(age);
            student.setEmail(email);
        }
        return student;
    }

    public synchronized boolean deleteStudent(int id) {
        return studentMap.remove(id) != null;
    }
}
