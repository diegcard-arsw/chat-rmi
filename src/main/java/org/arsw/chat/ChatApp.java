package org.arsw.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

/**
 * Aplicación principal de chat RMI que actúa como servidor y cliente simultáneamente.
 * Permite conectarse a otros clientes de chat remotos y recibir conexiones entrantes.
 */
public class ChatApp {
    
    private ChatServiceImpl localChatService;
    private ChatService remoteChatService;
    private String userName;
    private int localPort;
    private String remoteHost;
    private int remotePort;
    private Registry localRegistry;
    private boolean isConnected = false;

    /**
     * Punto de entrada principal de la aplicación.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        try {
            ChatApp chatApp = new ChatApp();
            chatApp.start();
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación de chat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inicia la aplicación de chat.
     */
    public void start() {
        try {
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("=== CHAT RMI ===");
            System.out.println();
            
            // Solicitar nombre de usuario
            System.out.print("Ingrese su nombre de usuario: ");
            userName = scanner.nextLine().trim();
            
            while (userName.isEmpty()) {
                System.out.print("El nombre de usuario no puede estar vacío. Ingrese su nombre: ");
                userName = scanner.nextLine().trim();
            }
            
            // Solicitar puerto local
            System.out.print("Ingrese el puerto local para publicar su servicio: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Puerto inválido. Ingrese un número: ");
                scanner.next();
            }
            localPort = scanner.nextInt();
            scanner.nextLine(); // consumir newline
            
            // Inicializar servicio local
            setupLocalService();
            
            System.out.println("Servicio local iniciado en puerto " + localPort);
            System.out.println("Su servicio está disponible en: rmi://localhost:" + localPort + "/ChatService");
            System.out.println();
            
            // Solicitar conexión remota
            System.out.print("¿Desea conectarse a un chat remoto? (s/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            
            if (response.equals("s") || response.equals("si") || response.equals("yes")) {
                connectToRemoteChat(scanner);
            }
            
            // Iniciar chat
            startChatSession(scanner);
            
        } catch (Exception e) {
            System.err.println("Error durante la ejecución: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Configura el servicio local RMI.
     */
    private void setupLocalService() throws Exception {
        // Crear el servicio de chat local
        localChatService = new ChatServiceImpl(userName);
        
        try {
            // Intentar crear un nuevo registro
            localRegistry = LocateRegistry.createRegistry(localPort);
        } catch (RemoteException e) {
            // Si falla, intentar usar un registro existente
            localRegistry = LocateRegistry.getRegistry(localPort);
        }
        
        // Registrar el servicio
        localRegistry.rebind("ChatService", localChatService);
    }

    /**
     * Se conecta a un chat remoto.
     */
    private void connectToRemoteChat(Scanner scanner) {
        try {
            System.out.print("Ingrese la dirección IP del servidor remoto: ");
            remoteHost = scanner.nextLine().trim();
            
            if (remoteHost.isEmpty()) {
                remoteHost = "localhost";
            }
            
            System.out.print("Ingrese el puerto del servidor remoto: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Puerto inválido. Ingrese un número: ");
                scanner.next();
            }
            remotePort = scanner.nextInt();
            scanner.nextLine(); // consumir newline
            
            // Conectar al servicio remoto
            String remoteUrl = "rmi://" + remoteHost + ":" + remotePort + "/ChatService";
            System.out.println("Conectando a: " + remoteUrl);
            
            remoteChatService = (ChatService) Naming.lookup(remoteUrl);
            
            // Verificar conexión
            if (remoteChatService.isAlive()) {
                String remoteUserName = remoteChatService.getUserName();
                System.out.println("Conectado exitosamente al chat de: " + remoteUserName);
                
                // Notificar al usuario remoto sobre nuestra conexión
                remoteChatService.userConnected(userName);
                
                // Agregar el usuario remoto a nuestra lista
                localChatService.addConnectedUser(remoteUserName);
                
                isConnected = true;
            }
            
        } catch (Exception e) {
            System.err.println("Error al conectar con el servidor remoto: " + e.getMessage());
            System.out.println("Continuando en modo local...");
            remoteChatService = null;
            isConnected = false;
        }
    }

    /**
     * Inicia la sesión de chat.
     */
    private void startChatSession(Scanner scanner) {
        System.out.println();
        System.out.println("=== CHAT INICIADO ===");
        System.out.println("Comandos especiales:");
        System.out.println("  /quit - Salir del chat");
        System.out.println("  /users - Ver usuarios conectados");
        System.out.println("  /help - Mostrar ayuda");
        System.out.println();
        
        if (isConnected) {
            System.out.println("Está conectado a un chat remoto. ¡Comience a escribir!");
        } else {
            System.out.println("Está en modo local. Otros usuarios pueden conectarse a su servicio.");
        }
        
        System.out.println("Escriba sus mensajes y presione Enter para enviar.");
        System.out.println();

        // Leer mensajes en un hilo separado para no bloquear la entrada
        CompletableFuture.runAsync(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String message;
                while ((message = readUserInput(reader)) != null) {
                    if (message.trim().isEmpty()) {
                        continue;
                    }
                    
                    // Procesar comandos especiales
                    if (message.startsWith("/")) {
                        handleCommand(message.trim());
                        continue;
                    }
                    
                    // Enviar mensaje al chat remoto si está conectado
                    if (isConnected && remoteChatService != null) {
                        try {
                            remoteChatService.receiveMessage(message, userName);
                        } catch (RemoteException e) {
                            System.err.println("Error al enviar mensaje: " + e.getMessage());
                            isConnected = false;
                            remoteChatService = null;
                        }
                    } else {
                        System.out.println("(Sin conexión remota - mensaje local): " + message);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error en la sesión de chat: " + e.getMessage());
            }
        }).join();
    }

    /**
     * Lee la entrada del usuario de manera síncrona.
     */
    private String readUserInput(BufferedReader reader) {
        try {
            System.out.print(userName + " > ");
            return reader.readLine();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Maneja comandos especiales del usuario.
     */
    private void handleCommand(String command) {
        switch (command.toLowerCase()) {
            case "/quit":
            case "/exit":
                handleQuit();
                break;
            case "/users":
                handleUsers();
                break;
            case "/help":
                handleHelp();
                break;
            default:
                System.out.println("Comando desconocido: " + command + ". Use /help para ver los comandos disponibles.");
        }
    }

    /**
     * Maneja el comando de salir.
     */
    private void handleQuit() {
        try {
            if (isConnected && remoteChatService != null) {
                remoteChatService.userDisconnected(userName);
            }
            System.out.println("Desconectando del chat...");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("Error al desconectar: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Maneja el comando de mostrar usuarios.
     */
    private void handleUsers() {
        try {
            System.out.println("Usuarios conectados:");
            System.out.println("  - " + userName + " (usted)");
            
            if (isConnected && remoteChatService != null) {
                String remoteUser = remoteChatService.getUserName();
                System.out.println("  - " + remoteUser + " (remoto)");
            }
            
            if (!isConnected) {
                System.out.println("(No hay conexiones remotas activas)");
            }
        } catch (Exception e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
    }

    /**
     * Maneja el comando de ayuda.
     */
    private void handleHelp() {
        System.out.println();
        System.out.println("=== AYUDA ===");
        System.out.println("Comandos disponibles:");
        System.out.println("  /quit, /exit - Salir del chat");
        System.out.println("  /users - Ver usuarios conectados");
        System.out.println("  /help - Mostrar esta ayuda");
        System.out.println();
        System.out.println("Para enviar un mensaje, simplemente escriba y presione Enter.");
        System.out.println();
    }
}