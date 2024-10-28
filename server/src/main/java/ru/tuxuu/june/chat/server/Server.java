package ru.tuxuu.june.chat.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
    private List<ClientHandler> clients;
    private int port;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                subscribe(new ClientHandler(this, socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("В чат зашёл: " + clientHandler.getUserName());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел: " + clientHandler.getUserName());
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }
    public void messageForOne(String message, String username) {
        String[] result = message.split(" ");
        String mes = "";
        for (int i = 2; i < result.length; i++) {
            mes = mes.concat(result[i] + " ");
        }
        for (ClientHandler c : clients) {
            if (c.getUserName().equals(result[1])) {
                c.sendMessage(username + " for you: " + mes);
                break;
            }
        }
        for (ClientHandler c : clients) {
            if (c.getUserName().equals(username)) {
                c.sendMessage("Message for " + result[1] + ": " + mes);
                return;
            }
        }
    }
}