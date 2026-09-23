import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) throws IOException {
        int puerto = 5000; // Puerto donde el servidor escucha

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado. Esperando conexiones en el puerto " + puerto + "...");

            // Bucle infinito: el servidor sigue atendiendo clientes uno tras otro
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Cliente conectado desde: " + socket.getInetAddress());
                    recibirArchivo(socket);
                } catch (IOException e) {
                    System.out.println("Error atendiendo al cliente: " + e.getMessage());
                }
            }
        }
    }

    private static void recibirArchivo(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

        // 1) Leer el nombre del archivo
        String nombreArchivo = dis.readUTF();

        // 2) Leer el tamaño del archivo en bytes
        long tamano = dis.readLong();

        // Carpeta donde se guardan los archivos recibidos
        File carpetaDestino = new File("archivos_recibidos");
        if (!carpetaDestino.exists()) {
            carpetaDestino.mkdir();
        }

        File archivoDestino = new File(carpetaDestino, nombreArchivo);

        // 3) Leer el contenido y escribirlo en disco
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(archivoDestino))) {
            byte[] buffer = new byte[4096];
            long totalRecibido = 0;
            int leidos;

            while (totalRecibido < tamano) {
                int porLeer = (int) Math.min(buffer.length, tamano - totalRecibido);
                leidos = dis.read(buffer, 0, porLeer);
                if (leidos == -1) break; // conexión cortada antes de tiempo

                bos.write(buffer, 0, leidos);
                totalRecibido += leidos;
            }
            bos.flush();

            System.out.println("Archivo recibido correctamente: " + nombreArchivo
                    + " (" + totalRecibido + "/" + tamano + " bytes)");
        }
    }
}
