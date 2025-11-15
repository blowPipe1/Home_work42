import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;


public class ClientHandler implements Runnable {
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

    @Override
    public void run() {
        System.out.format("Connected client: %s with name %s%n", socket, clientName);

        try {
            this.reader = new Scanner(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream());

            sendResponse("hello " + clientName, writer);
            broadcastMessage("SERVER: " + clientName + " has joined the chat.");

            while (true) {
                String message = reader.nextLine().trim();
                if (isEmptyMsg(message) || isQuitMsg(message)) {
                    break;
                }
                broadcastMessage(String.format("<%s>: %s", clientName, message), this);
                System.out.format("Got message from %s: %s%n", clientName, message);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Client dropped connection: " + clientName);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
