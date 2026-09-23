import java.io.*;
import java.net.*;

public class Client {

    public static void main(String[] args) throws IOException {
        // ---- CONFIGURA ESTOS 3 VALORES ----
        String ipServidor = "127.0.0.1";           // IP de la máquina donde corre Server.java
        int puerto = 5000;                          // Debe ser el mismo puerto del servidor
        String rutaArchivo = "archivos_a_enviar/ejemplo.txt"; // Archivo que quieres enviar
        // ------------------------------------

        try (Socket socket = new Socket(ipServidor, puerto)) {
            System.out.println("Conectado al servidor " + ipServidor + ":" + puerto);
            enviarArchivo(socket, rutaArchivo);
        } catch (ConnectException e) {
            System.out.println("No se pudo conectar. ¿El servidor está corriendo y el puerto/IP son correctos?");
        }
    }

    private static void enviarArchivo(Socket socket, String rutaArchivo) throws IOException {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            System.out.println("El archivo no existe: " + archivo.getAbsolutePath());
            return;
        }

        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        // 1) Enviar el nombre del archivo
        dos.writeUTF(archivo.getName());

        // 2) Enviar el tamaño en bytes (el servidor lo necesita para saber cuándo parar)
        dos.writeLong(archivo.length());

        // 3) Enviar el contenido en bloques
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(archivo))) {
            byte[] buffer = new byte[4096];
            int leidos;
            long enviados = 0;

            while ((leidos = bis.read(buffer)) != -1) {
                dos.write(buffer, 0, leidos);
                enviados += leidos;
            }
            dos.flush();

            System.out.println("Archivo enviado: " + archivo.getName() + " (" + enviados + " bytes)");
        }
    }
}
