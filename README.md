# CHAT RMI - Java 21 con Maven

Este proyecto implementa una aplicación de chat distribuido utilizando RMI (Remote Method Invocation) en Java 21.

## Características

- **Chat distribuido**: Permite conectar múltiples clientes de chat a través de RMI
- **Servidor y cliente**: Cada instancia actúa como servidor y cliente simultáneamente
- **Interfaz interactiva**: Interfaz de línea de comandos fácil de usar
- **Comandos especiales**: Incluye comandos para ver usuarios conectados, ayuda, etc.
- **Ejemplos incluidos**: Incluye EchoServer y EchoClient como ejemplos de sockets TCP

## Estructura del Proyecto

```
chat-rmi/
├── src/
│   └── main/
│       └── java/
│           └── org/
│               └── arsw/
│                   ├── chat/
│                   │   ├── ChatService.java      # Interfaz RMI
│                   │   ├── ChatServiceImpl.java  # Implementación del servicio
│                   │   └── ChatApp.java          # Aplicación principal
│                   ├── EchoServer.java           # Servidor de eco (ejemplo)
│                   └── EchoClient.java           # Cliente de eco (ejemplo)
├── compile.bat         # Script de compilación
├── run-chat.bat        # Script para ejecutar el chat
├── run-echo-server.bat # Script para ejecutar el servidor de eco
├── run-echo-client.bat # Script para ejecutar el cliente de eco
├── pom.xml            # Configuración de Maven
└── README.md          # Este archivo
```

## Requisitos

- Java 21 o superior
- Maven 3.6 o superior

## Instalación y Compilación

1. Clona o descarga este proyecto
2. Abre una terminal en el directorio del proyecto
3. Ejecuta el script de compilación:

### Windows:
```batch
compile.bat
```

### Linux/Mac:
```bash
mvn clean compile
```

## Uso

### Aplicación de Chat RMI

Para iniciar la aplicación de chat:

### Windows:
```batch
run-chat.bat
```

### Linux/Mac:
```bash
mvn exec:java -Dexec.mainClass="org.arsw.chat.ChatApp"
```

#### Instrucciones de uso:

1. **Configuración inicial**: La aplicación te pedirá:
   - Tu nombre de usuario
   - El puerto local donde publicar tu servicio RMI

2. **Conexión remota** (opcional): Puedes conectarte a otro chat remoto proporcionando:
   - Dirección IP del servidor remoto
   - Puerto del servidor remoto

3. **Comandos disponibles**:
   - `/quit` o `/exit` - Salir del chat
   - `/users` - Ver usuarios conectados
   - `/help` - Mostrar ayuda

### Ejemplo de Uso

**Terminal 1 (Usuario A):**
```
=== CHAT RMI ===

Ingrese su nombre de usuario: Alice
Ingrese el puerto local para publicar su servicio: 1099
Servicio local iniciado en puerto 1099
Su servicio está disponible en: rmi://localhost:1099/ChatService

¿Desea conectarse a un chat remoto? (s/n): n

=== CHAT INICIADO ===
Está en modo local. Otros usuarios pueden conectarse a su servicio.
Alice > 
```

**Terminal 2 (Usuario B):**
```
=== CHAT RMI ===

Ingrese su nombre de usuario: Bob
Ingrese el puerto local para publicar su servicio: 1100
Servicio local iniciado en puerto 1100
Su servicio está disponible en: rmi://localhost:1100/ChatService

¿Desea conectarse a un chat remoto? (s/n): s
Ingrese la dirección IP del servidor remoto: localhost
Ingrese el puerto del servidor remoto: 1099
Conectando a: rmi://localhost:1099/ChatService
Conectado exitosamente al chat de: Alice

=== CHAT INICIADO ===
Está conectado a un chat remoto. ¡Comience a escribir!
Bob > Hola Alice!
```

### Testing

![Pruebas Unitarias](/img/test.png)

## Arquitectura

### Interfaz RMI
- **ChatService**: Define los métodos remotos para enviar mensajes y gestionar usuarios

### Implementación
- **ChatServiceImpl**: Implementa la interfaz RMI y maneja la lógica de mensajería
- **ChatApp**: Aplicación principal que gestiona tanto la funcionalidad de servidor como de cliente

### Funcionamiento

1. Cada instancia de la aplicación crea un registro RMI local
2. Publica su servicio ChatService en el puerto especificado
3. Opcionalmente se conecta a otro servicio ChatService remoto
4. Los mensajes se envían a través de llamadas RMI remotas
5. Los mensajes recibidos se muestran en tiempo real

## Troubleshooting

### Problemas Comunes

1. **Puerto ya en uso**: Si el puerto especificado ya está en uso, intenta con otro puerto
2. **Firewall**: Asegúrate de que el firewall permita conexiones en los puertos especificados
3. **Conectividad**: Verifica que las direcciones IP y puertos sean correctos

### Logs y Debugging

La aplicación muestra mensajes informativos sobre:
- Estado de la conexión
- Mensajes recibidos y enviados
- Usuarios que se conectan/desconectan
- Errores de comunicación

## Autor

Diego Cardenas 