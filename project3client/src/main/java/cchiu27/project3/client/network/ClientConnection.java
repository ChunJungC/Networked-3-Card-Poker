package cchiu27.project3.client.network;

import cchiu27.project3.server.model.PokerInfo;

import java.io.*;
import java.net.Socket;

public class ClientConnection {

    private final String host;
    private final int port;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private Thread listenerThread;
    private volatile PokerInfoListener listener;

    public ClientConnection(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setListener(PokerInfoListener listener) {
        this.listener = listener;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());

        listenerThread = new Thread(this::listenLoop, "ClientListener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void listenLoop() {
        try {
            while (!socket.isClosed()) {
                Object obj = in.readObject();
                if (obj instanceof PokerInfo info) {
                    PokerInfoListener l = listener;
                    if (l != null) {
                        l.onPokerInfo(info);
                    }
                }
            }
        } catch (EOFException eof) {
            notifyClosed("Server closed the connection.");
        } catch (IOException | ClassNotFoundException e) {
            notifyClosed("Connection error: " + e.getMessage());
        }
    }

    private void notifyClosed(String reason) {
        PokerInfoListener l = listener;
        if (l != null) {
            l.onConnectionClosed(reason);
        }
    }

    public void send(PokerInfo info) throws IOException {
        out.writeObject(info);
        out.flush();
    }

    public void sendConnect(String playerName) throws IOException {
        PokerInfo info = new PokerInfo();
        info.setCommand(PokerInfo.Command.CONNECT);
        info.setPlayerName(playerName);
        send(info);
    }

    public void sendPlaceBets(int ante, int pairPlus) throws IOException {
        PokerInfo info = new PokerInfo();
        info.setCommand(PokerInfo.Command.PLACE_BETS);
        info.setAnteBet(ante);
        info.setPairPlusBet(pairPlus);
        send(info);
    }

    public void sendPlay() throws IOException {
        PokerInfo info = new PokerInfo();
        info.setCommand(PokerInfo.Command.PLAY);
        send(info);
    }

    public void sendFold() throws IOException {
        PokerInfo info = new PokerInfo();
        info.setCommand(PokerInfo.Command.FOLD);
        send(info);
    }

    public void sendNewGame() throws IOException {
        PokerInfo info = new PokerInfo();
        info.setCommand(PokerInfo.Command.NEW_GAME);
        send(info);
    }

    public void sendDisconnect() {
        try {
            PokerInfo info = new PokerInfo();
            info.setCommand(PokerInfo.Command.DISCONNECT);
            send(info);
        } catch (IOException ignored) {}
    }

    public void close() {
        sendDisconnect();
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {}
    }
}
