package org.arsw.chat;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para el servicio de chat RMI.
 */
public class ChatServiceTest {

    private ChatServiceImpl chatService;
    private Registry registry;
    private final int testPort = 9999;

    @BeforeEach
    public void setUp() throws Exception {
        // Crear servicio de chat de prueba
        chatService = new ChatServiceImpl("TestUser");
        
        try {
            // Crear registro RMI para pruebas
            registry = LocateRegistry.createRegistry(testPort);
            registry.rebind("TestChatService", chatService);
        } catch (RemoteException e) {
            // Si el puerto ya está en uso, usar el registro existente
            registry = LocateRegistry.getRegistry(testPort);
            registry.rebind("TestChatService", chatService);
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (registry != null && chatService != null) {
            try {
                registry.unbind("TestChatService");
            } catch (Exception e) {
                // Ignorar errores al limpiar
            }
        }
    }

    @Test
    public void testGetUserName() throws RemoteException {
        assertEquals("TestUser", chatService.getUserName());
    }

    @Test
    public void testIsAlive() throws RemoteException {
        assertTrue(chatService.isAlive());
    }

    @Test
    public void testGetConnectedUsers() throws RemoteException {
        List<String> users = chatService.getConnectedUsers();
        assertNotNull(users);
        assertTrue(users.contains("TestUser"));
    }

    @Test
    public void testUserConnected() throws RemoteException {
        chatService.userConnected("NewUser");
        List<String> users = chatService.getConnectedUsers();
        assertTrue(users.contains("NewUser"));
    }

    @Test
    public void testUserDisconnected() throws RemoteException {
        // Primero conectar un usuario
        chatService.userConnected("TempUser");
        assertTrue(chatService.getConnectedUsers().contains("TempUser"));
        
        // Luego desconectarlo
        chatService.userDisconnected("TempUser");
        assertFalse(chatService.getConnectedUsers().contains("TempUser"));
    }

    @Test
    public void testReceiveMessage() throws RemoteException {
        // Esta prueba verifica que el método no lance excepciones
        assertDoesNotThrow(() -> {
            chatService.receiveMessage("Test message", "Sender");
        });
    }

    @Test
    public void testRemoteAccess() throws Exception {
        // Probar acceso remoto al servicio
        ChatService remoteService = (ChatService) registry.lookup("TestChatService");
        
        assertNotNull(remoteService);
        assertEquals("TestUser", remoteService.getUserName());
        assertTrue(remoteService.isAlive());
    }
}