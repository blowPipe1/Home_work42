import java.io.IOException;
import java.net.Socket;
import java.util.Random;


public class ClientHandler{
    private final Socket socket;
    private final String clientName;
    private Random rnd;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.clientName = "User_" + rnd.nextInt(1, 100);
    }
}
