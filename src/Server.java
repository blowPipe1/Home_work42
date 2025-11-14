import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;;

    private final ExecutorService pool = Executors.newCachedThreadPool();

    public Server(int port) {
        this.port = port;
    }

    public static Server bindToServer(int port) {
        return new Server(port);
    }

    public void run() {
        try(ServerSocket server = new ServerSocket(port)){
            while(!server.isClosed()){
                Socket socket = server.accept();
                pool.submit(() -> handle(socket));
            }
        } catch (IOException e) {
            System.out.format("Вероятнее всего порт %s занят%n", port);
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        System.out.format("Connected client: %s%n", socket);

        try(
                socket;
                Scanner reader = getReader(socket);
                PrintWriter writer = getWriter(socket);
                ) {
            sendResponse("hello" + socket, writer);
            while (true){
                String message = reader.nextLine().trim();
                if(isEmptyMsg(message) || isQuitMsg(message)){
                    break;
                }
                sendResponse(message.toUpperCase(), writer);
                System.out.format("Got message: %s%n", message);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Client dropped connection");
        } catch (IOException e){
            e.printStackTrace();
        }
        System.out.println("Client disconnected");
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        return new PrintWriter(socket.getOutputStream());
    }

    private Scanner getReader(Socket socket) throws IOException {
        return new Scanner(new InputStreamReader(socket.getInputStream()));
    }

    private static boolean isQuitMsg(String msg) {
        return "bye".equalsIgnoreCase(msg);
    }

    private static boolean isEmptyMsg(String msg) {
        return msg == null || msg.isBlank();
    }

    private static void sendResponse(String response, Writer writer) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }
}
