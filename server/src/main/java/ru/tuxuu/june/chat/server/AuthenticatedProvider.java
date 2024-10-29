package ru.tuxuu.june.chat.server;

public interface AuthenticatedProvider {
    void initialize();

    boolean authenticate(ClientHandler clientHandler, String login, String password);

    boolean registration(ClientHandler clientHandler, String login, String password, String username);
    public boolean checkToKick(ClientHandler clientHandler, String username);
}
