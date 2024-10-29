package ru.tuxuu.june.chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private AuthenticatedProvider authenticatedProvider;

    public Server(int port) {
        this.port = port;
        clients = new ArrayList<>();
        authenticatedProvider = new InMemoryAuthenticationProvider(this);
        authenticatedProvider.initialize();
    }

    public AuthenticatedProvider getAuthenticatedProvider() {
        return authenticatedProvider;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public boolean isUsernameBusy(String username) {
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void messageForOne(String message, ClientHandler client) {
        String[] result = message.split(" ", 3);
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(result[1])) {
                c.sendMessage(client.getUsername() + " for you: " + result[2]);
                client.sendMessage("Message for " + result[1] + ": " + result[2]);
                return;
            }
        }
        client.sendMessage("Пользователь " + result[1] + " не найден");
    }
    public synchronized void kickUser(String userToKick){
        for(ClientHandler c : clients){
            if(c.getUsername().equals(userToKick)){
                c.sendMessage("Вы были отключены от чата");
                clients.remove(c);
                broadcastMessage("Пользователь "+userToKick+" был отключен администратором");
                break;
            }
        }
    }
}
