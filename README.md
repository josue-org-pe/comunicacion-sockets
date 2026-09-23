# Transferencia de archivos con Sockets en Java — Guía completa

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

---

## Paso 0: Instalar el JDK (si no lo tienes)

Necesitas el **JDK** (Java Development Kit), no solo el JRE, porque incluye `javac` (el compilador).

### Windows 11 (desde cmd)

1. Abre **cmd como administrador**.
2. Verifica que tengas `winget`:
   ```
   winget --version
   ```
3. Instala el JDK:
   ```
   winget install Microsoft.OpenJDK.21
   ```
4. **Cierra completamente la ventana de cmd** (el PATH no se actualiza en la misma ventana).
5. Abre un cmd nuevo y confirma:
   ```
   javac -version
   java -version
   ```
   Ambos deben responder con un número de versión. Si sigue sin funcionar, reinicia la PC.

### Mac
```
brew install openjdk
```

### Linux (Ubuntu/Debian)
```
sudo apt update
sudo apt install default-jdk
```

---

## Paso 1: Preparar la red entre las dos computadoras

Esto aplica solo si vas a probar el Server y el Client en **máquinas distintas**. Si vas a probar todo en la misma PC, salta al Paso 2.

1. **Misma red**: ambas computadoras deben estar conectadas al mismo WiFi/router.
2. **Obtén la IP del servidor**: en la máquina que va a ejecutar `Server.java`:
   - Windows: `ipconfig` → busca "Dirección IPv4" (ej. `192.168.1.15`)
   - Linux/Mac: `ifconfig` o `ip a`
3. **Verifica conectividad**: desde la máquina cliente, ejecuta:
   ```
   ping 192.168.1.15
   ```
   (usando la IP real del servidor). Debe responder con tiempos, no con "tiempo de espera agotado".
4. **Abre el puerto en el firewall del servidor** (puerto 5000 por defecto):
   - Windows: Firewall de Windows Defender → Configuración avanzada → Reglas de entrada → nueva regla permitiendo TCP 5000.
   - Linux: `sudo ufw allow 5000/tcp`

---

## Paso 2: Compilar Server.java y Client.java

1. Abre una terminal y ubícate en la carpeta que creaste
2. 
3. Compila ambos archivos:
   ```
   javac Server.java Client.java
   ```
   Si no hay errores, no imprime nada y genera `Server.class` y `Client.class` en la misma carpeta.

---

## Paso 3: Configurar la IP en Client.java (solo si son 2 PCs distintas)

1. Abre `Client.java` en un editor de texto.
2. Cambia esta línea:
   ```java
   String ipServidor = "127.0.0.1";
   ```
   por la IP real del servidor obtenida en el Paso 1:
   ```java
   String ipServidor = "192.168.1.15";
   ```
3. Guarda y **vuelve a compilar**:
   ```
   javac Client.java
   ```

Si estás probando todo en la misma máquina, deja `127.0.0.1` tal cual.

---

## Paso 4: Ejecutar el Servidor

En la máquina servidor, dentro de la carpeta `src`:
```
java Server
```
Debe quedar mostrando:
```
Servidor iniciado. Esperando conexiones en el puerto 5000...
```
Esta ventana se queda abierta esperando conexiones. No la cierres.

---

## Paso 5: Ejecutar el Cliente

En la otra máquina (o en otra terminal si es la misma PC), dentro de su carpeta `src`:
```
java Client
```
Debería conectarse de inmediato y mostrar:
```
Conectado al servidor 192.168.1.15:5000
Archivo enviado: ejemplo.txt (NN bytes)
```

> Nota: la ruta `archivos_a_enviar/ejemplo.txt` dentro de `Client.java` es relativa a la carpeta desde donde ejecutas `java Client`. Si ejecutas desde `src`, asegúrate de que la carpeta `archivos_a_enviar` esté un nivel arriba, o copia el archivo dentro de `src`.

---

## Paso 6: Confirmar la recepción

Vuelve a la ventana del servidor. Debe mostrar:
```
Archivo recibido correctamente: ejemplo.txt (NN/NN bytes)
```
El archivo aparecerá dentro de la carpeta `archivos_recibidos/`, creada automáticamente junto al `Server.java`.

---

## Solución de problemas comunes

| Problema | Causa probable |
|---|---|
| `'javac' no se reconoce como comando` | No tienes el JDK instalado, o instalaste solo el JRE. Ver Paso 0. |
| `Connection refused` / `No se pudo conectar` | El servidor no está corriendo, la IP está mal, o el firewall bloquea el puerto. |
| `ping` no responde entre las dos PCs | Están en redes distintas (ej. WiFi de invitados) o el firewall bloquea ICMP. |
| El cliente compila pero no encuentra el archivo a enviar | Ruta relativa incorrecta — revisa desde qué carpeta ejecutas `java Client`. |

---

## Notas técnicas

- El servidor atiende **un cliente a la vez** en este ejemplo básico. Para atender varios simultáneamente, cada conexión debería manejarse en un hilo (`new Thread(...).start()`).
- El protocolo usado (nombre del archivo + tamaño + bytes) es necesario porque TCP es un flujo continuo de bytes sin "fin" natural: el receptor necesita saber de antemano cuántos bytes esperar.
- Puertos por debajo de 1024 requieren permisos de administrador; por eso se usa el 5000.
