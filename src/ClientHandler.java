import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;


public class ClientHandler{
    private final Socket socket;
    private final List<ClientHandler> clients;
    private final String clientName;
    private PrintWriter writer;
    private Scanner reader;
    private Random rnd;

    public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException {
        this.socket = socket;
        this.clients = clients;
        this.rnd = new Random();
        this.clientName = "User_" + rnd.nextInt(1, 100);
    }

    private static boolean isQuitMsg(String msg) {
        return "bye".equalsIgnoreCase(msg);
    }

    private static boolean isEmptyMsg(String msg) {
        return msg == null || msg.isBlank();
    }

    private static void sendResponse(String response, Writer writer) {
        try {
            writer.write(response);
            writer.write(System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    private void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (sender == null || !client.equals(sender)) {
                client.sendResponse(message, client.writer);
            }
        }
    }

    //перегрузка метода
    private void broadcastMessage(String message) {
        broadcastMessage(message, null);
    }
}
