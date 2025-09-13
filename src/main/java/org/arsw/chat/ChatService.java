package org.arsw.chat;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Interfaz remota que define los métodos del servicio de chat RMI.
 * Esta interfaz permite enviar mensajes y notificar a otros clientes
 * en un chat distribuido.
 */
public interface ChatService extends Remote {

    /**
     * Envía un mensaje a este cliente de chat.
     *
     * @param message el mensaje a enviar
     * @param senderName el nombre del remitente
     * @throws RemoteException si ocurre un error de comunicación RMI
     */
    void receiveMessage(String message, String senderName) throws RemoteException;

    /**
     * Obtiene el nombre de usuario de este cliente.
     *
     * @return el nombre de usuario
     * @throws RemoteException si ocurre un error de comunicación RMI
     */
    String getUserName() throws RemoteException;

    /**
     * Verifica si el servicio está activo.
     *
     * @return true si el servicio está activo
     * @throws RemoteException si ocurre un error de comunicación RMI
     */
    boolean isAlive() throws RemoteException;

    /**
     * Obtiene la lista de usuarios conectados (si está disponible).
     *
     * @return lista de nombres de usuarios conectados
     * @throws RemoteException si ocurre un error de comunicación RMI
     */
    List<String> getConnectedUsers() throws RemoteException;

    /**
     * Notifica que un usuario se ha conectado al chat.
     *
     * @param userName el nombre del usuario que se conectó
     * @throws RemoteException si ocurre un error de comunicación RMI
     */
    void userConnected(String userName) throws RemoteException;

    /**
     * Notifica que un usuario se ha desconectado del chat.
     *
     * @param userName el nombre del usuario que se desconectó
     * @throws RemoteException si ocurre un error de comunicación RMI
     */
    void userDisconnected(String userName) throws RemoteException;
}