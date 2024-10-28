package ru.tuxuu.june.chat.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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

    public synchronized void messageForOne(String message, ClientHandler client) {
        String[] result = message.split(" ", 3);
        for (ClientHandler c : clients) {
            if (c.getUserName().equals(result[1])) {
                c.sendMessage(client.getUserName() + " for you: " + result[2]);
                client.sendMessage("Message for " + result[1] + ": " + result[2]);
                return;
            }
        }
        client.sendMessage("Пользователь " + result[1] + " не найден");
    }
}