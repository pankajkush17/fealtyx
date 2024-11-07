import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class StudentAPI {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        StudentService studentService = new StudentService();  // Create StudentService instance
        StudentHandler studentHandler = new StudentHandler(studentService);  // Pass it to StudentHandler

        server.createContext("/students", studentHandler); // Attach handler
        server.setExecutor(null); // Creates a default executor
        server.start();

        System.out.println("Server started on port 8080");
    }
}
