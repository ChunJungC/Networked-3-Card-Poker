package cchiu27.project3.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Core socket server (no GUI yet).
 * Listens on a port, accepts clients, spawns ClientHandler threads.
 */
public class Server {

    private final int port;
    private volatile boolean running = false;

    private ServerSocket serverSocket;
    private ExecutorService pool;
    private final AtomicInteger nextPlayerId = new AtomicInteger(1);

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        if (running) return;
        running = true;

        serverSocket = new ServerSocket(port);
        pool = Executors.newCachedThreadPool();

        System.out.println("Server started on port " + port);

        // Accept loop runs in its own thread so GUI can call start() later
        Thread acceptThread = new Thread(this::acceptLoop, "AcceptThread");
        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private void acceptLoop() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                int playerId = nextPlayerId.getAndIncrement();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress()
                        + " (playerId=" + playerId + ")");
                ClientHandler handler = new ClientHandler(clientSocket, playerId);
                pool.submit(handler);
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error accepting client: " + e.getMessage());
                }
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}

        if (pool != null) {
            pool.shutdownNow();
        }

        System.out.println("Server stopped.");
    }
}
