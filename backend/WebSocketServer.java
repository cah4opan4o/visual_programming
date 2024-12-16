import com.fasterxml.jackson.databind.ObjectMapper;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws")
public class WebSocketServer {
    private static final String JSON_FILE_PATH = "lte_cell_info.json";
    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if ("REQUEST_DATA".equalsIgnoreCase(message)) {
            sendJsonData(session);
        }
    }

    private void sendJsonData(Session session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(JSON_FILE_PATH);
            if (file.exists()) {
                String jsonData = mapper.writeValueAsString(mapper.readTree(file));
                session.getBasicRemote().sendText(jsonData);
            } else {
                session.getBasicRemote().sendText("{\"error\": \"JSON file not found.\"}");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcastData() {
        sessions.forEach(session -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                File file = new File(JSON_FILE_PATH);
                if (file.exists()) {
                    String jsonData = mapper.writeValueAsString(mapper.readTree(file));
                    session.getBasicRemote().sendText(jsonData);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
