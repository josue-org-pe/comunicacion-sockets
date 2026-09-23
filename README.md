# Transferencia de archivos con Sockets en Java

## Estructura del proyecto

```
socket-file-transfer/
├── src/
│   ├── Server.java     # Recibe archivos
│   └── Client.java     # Envía archivos
├── archivos_a_enviar/
│   └── ejemplo.txt     # Archivo de prueba
├── archivos_recibidos/ # Se crea automáticamente al recibir algo
└── README.md
```

## Cómo probarlo en la MISMA computadora

1. Abre una terminal en la carpeta `src`.
2. Compila ambos archivos:
   ```
   javac Server.java Client.java
   ```
3. En una terminal, ejecuta el servidor:
   ```
   java Server
   ```
   Verás: `Servidor iniciado. Esperando conexiones en el puerto 5000...`
4. En OTRA terminal (misma carpeta), ejecuta el cliente:
   ```
   java Client
   ```
5. El servidor mostrará que recibió el archivo y lo guardará en `archivos_recibidos/`.

## Cómo probarlo entre DOS computadoras distintas

1. Copia la carpeta `socket-file-transfer` a ambas máquinas (o solo `Server.java` a una y `Client.java` a la otra).
2. En la máquina que hará de **servidor**, averigua su IP local:
   - Windows: `ipconfig` (busca "Dirección IPv4")
   - Linux/Mac: `ifconfig` o `ip a`
3. Asegúrate de que **ambas máquinas estén en la misma red** (mismo WiFi/LAN) y que el firewall no bloquee el puerto 5000.
4. En `Client.java`, cambia:
   ```java
   String ipServidor = "IP_DEL_SERVIDOR_AQUI"; // ej: "192.168.1.15"
   ```
5. Ejecuta primero `Server` en la máquina servidor, luego `Client` en la otra máquina.

## Notas importantes

- El servidor solo atiende **un cliente a la vez** en este ejemplo básico (bucle secuencial). Para atender varios clientes simultáneamente, cada `socket.accept()` debería lanzarse en un hilo nuevo (`new Thread(() -> recibirArchivo(socket)).start();`).
- El protocolo usado (nombre + tamaño + bytes) es la forma más simple y confiable de mandar archivos por TCP, porque el flujo de datos no tiene "fin" natural: hay que decirle al receptor exactamente cuántos bytes esperar.
- Si quieres comunicación en ambos sentidos (que el servidor también pueda enviar archivos al cliente), se necesita repetir la misma lógica pero invirtiendo los roles de lectura/escritura, o usar hilos separados para leer y escribir al mismo tiempo.
- Puertos por debajo de 1024 suelen requerir permisos de administrador; por eso se usa 5000 en el ejemplo.
