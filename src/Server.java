import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();
    private final List<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public static Server bindToServer(int port) {
        return new Server(port);
    }

    public void run() {
        try(ServerSocket server = new ServerSocket(port)){
            System.out.println("Server started on port: " + port);
            while(!server.isClosed()){
                Socket socket = server.accept();
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clients.add(clientHandler);
                pool.submit(clientHandler);
            }
        } catch (IOException e) {
            System.out.format("Вероятнее всего порт %s занят%n", port);
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
