import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class StudentHandler implements HttpHandler {

    // In-memory student storage (database)
    private final StudentService studentService;
    private final Map<Integer, Student> studentDatabase = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StudentHandler(StudentService studentService) {
        this.studentService = studentService;
    } 
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String response = "";
        int statusCode = 200;

        if (path.equals("/students") && method.equals("POST")) {
            // Handle POST request to create a new student
            response = createStudent(exchange);
        } else if (path.startsWith("/students/")) {
            int studentId = getStudentId(path);
            if (studentId == -1) {
                statusCode = 404;
                response = "Invalid student ID";
            } else {
                switch (method) {
                    case "GET" -> response = getStudentById(studentId);
                    case "PUT" -> response = updateStudent(studentId, exchange);
                    case "DELETE" -> response = deleteStudent(studentId);
                    default -> {
                        statusCode = 405; // Method Not Allowed
                        response = "Method Not Allowed";
                    }
                }
            }
        } else {
            statusCode = 405; // Not Found
            response = "Endpoint not found";
        }

        sendResponse(exchange, response, statusCode);
    }

    // Create a new student from the POST request body
    private String createStudent(HttpExchange exchange) throws IOException {
        // Read the incoming request body and convert it to a Student object
        InputStream requestBody = exchange.getRequestBody();
        Student student = objectMapper.readValue(requestBody, Student.class);

        // Store the student in the in-memory database (HashMap)
        studentDatabase.put(student.getId(), student);

        return "Student created successfully";
    }

    // Get a student by ID
    private String getStudentById(int studentId) {
        Student student = studentDatabase.get(studentId);
        if (student != null) {
            return objectMapper.writeValueAsString(student);
        } else {
            return "Student not found";
        }
    }

    // Update a student by ID
    private String updateStudent(int studentId, HttpExchange exchange) throws IOException {
        // Read the incoming request body and convert it to a Student object
        Student updatedStudent = objectMapper.readValue(exchange.getRequestBody(), Student.class);

        if (studentDatabase.containsKey(studentId)) {
            studentDatabase.put(studentId, updatedStudent);
            return "Student updated successfully";
        } else {
            return "Student not found";
        }
    }

    // Delete a student by ID
    private String deleteStudent(int studentId) {
        if (studentDatabase.containsKey(studentId)) {
            studentDatabase.remove(studentId);
            return "Student deleted successfully";
        } else {
            return "Student not found";
        }
    }

    // Helper method to extract student ID from the URL path
    private int getStudentId(String path) {
        try {
            String[] parts = path.split("/");
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (Exception e) {
            return -1;
        }
    }

    // Send the response back to the client
    private void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
