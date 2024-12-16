import org.glassfish.tyrus.server.Server;

public class WebSocketServerMain {
    public static void main(String[] args) {
        Server server = new Server("localhost", 8080, "/", null, WebSocketServer.class);

        try {
            server.start();
            System.out.println("WebSocket server started at ws://localhost:8080/ws");
            System.in.read(); // Ожидание ввода для завершения
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
