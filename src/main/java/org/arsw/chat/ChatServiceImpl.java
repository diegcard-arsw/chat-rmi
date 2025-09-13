package org.arsw.chat;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementación del servicio de chat RMI.
 * Esta clase maneja la recepción de mensajes y mantiene información
 * sobre el usuario y conexiones activas.
 */
public class ChatServiceImpl extends UnicastRemoteObject implements ChatService {

    private final String userName;
    private final List<String> connectedUsers;
    private final DateTimeFormatter timeFormatter;

    /**
     * Crea una nueva instancia del servicio de chat.
     *
     * @param userName el nombre del usuario para este servicio
     * @throws RemoteException si ocurre un error al exportar el objeto remoto
     */
    public ChatServiceImpl(String userName) throws RemoteException {
        super();
        this.userName = userName;
        this.connectedUsers = new CopyOnWriteArrayList<>();
        this.timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        // Agregar el usuario actual a la lista
        this.connectedUsers.add(userName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void receiveMessage(String message, String senderName) throws RemoteException {
        String timestamp = LocalDateTime.now().format(timeFormatter);
        System.out.println("\n[" + timestamp + "] " + senderName + ": " + message);
        System.out.print(userName + " > ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserName() throws RemoteException {
        return userName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getConnectedUsers() throws RemoteException {
        return new ArrayList<>(connectedUsers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void userConnected(String userName) throws RemoteException {
        if (!connectedUsers.contains(userName)) {
            connectedUsers.add(userName);
            String timestamp = LocalDateTime.now().format(timeFormatter);
            System.out.println("\n[" + timestamp + "] *** " + userName + " se ha conectado al chat ***");
            System.out.print(this.userName + " > ");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void userDisconnected(String userName) throws RemoteException {
        connectedUsers.remove(userName);
        String timestamp = LocalDateTime.now().format(timeFormatter);
        System.out.println("\n[" + timestamp + "] *** " + userName + " se ha desconectado del chat ***");
        System.out.print(this.userName + " > ");
    }

    /**
     * Agrega un usuario a la lista de usuarios conectados.
     *
     * @param userName el nombre del usuario a agregar
     */
    public void addConnectedUser(String userName) {
        if (!connectedUsers.contains(userName)) {
            connectedUsers.add(userName);
        }
    }

    /**
     * Remueve un usuario de la lista de usuarios conectados.
     *
     * @param userName el nombre del usuario a remover
     */
    public void removeConnectedUser(String userName) {
        connectedUsers.remove(userName);
    }
}