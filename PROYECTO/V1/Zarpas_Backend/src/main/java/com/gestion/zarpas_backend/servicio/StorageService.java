// src/main/java/com/gestion/zarpas_backend/services/StorageService.java
package com.gestion.zarpas_backend.servicio; // Asegúrate de que el paquete sea correcto

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID; // Para generar nombres de archivo únicos

@Service
public class StorageService {

    // Directorio base donde se guardarán las imágenes.
    // En desarrollo, puedes dejarlo como "uploads".
    // Para Docker, asegúrate de que esto sea consistente con el volumen mapeado.
    // Para producción, idealmente sería una ruta absoluta fuera del JAR/WAR.
    private final Path rootLocation;

    public StorageService() {
        // Define la ruta base para el almacenamiento de archivos.
        // Si tu JAR de Spring Boot se ejecuta, por ejemplo, en /app dentro del contenedor Docker,
        // esta ruta podría ser "/app/uploads" o simplemente "uploads" si /app es el CWD.
        // Para el ejemplo de Docker Compose que te di, "./zarpas_uploads:/app/uploads",
        // el Spring Boot estaría escribiendo en "/app/uploads" dentro del contenedor,
        // que se mapea a "./zarpas_uploads" en tu host.
        // Puedes dejarlo como "uploads" por ahora, y Spring Boot creará la carpeta
        // relativa al directorio de ejecución de tu aplicación.
        this.rootLocation = Paths.get("uploads");

        try {
            // Asegura que el directorio exista
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            // Lanza una RuntimeException si no se puede inicializar el directorio de almacenamiento
            throw new RuntimeException("No se pudo inicializar la ubicación de almacenamiento para imágenes", e);
        }
    }

    /**
     * Almacena un archivo MultipartFile en el sistema de archivos.
     * Genera un nombre único para el archivo y devuelve la URL relativa para acceder a él.
     *
     * @param file El archivo a almacenar.
     * @return La URL relativa del archivo almacenado (ej. "/uploads/nombre_unico.jpg").
     */
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("No se pudo almacenar un archivo vacío.");
        }
        try {
            // Obtiene la extensión original del archivo
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Genera un nombre de archivo único usando UUID
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

            // Resuelve la ruta completa del archivo de destino
            Path destinationFile = this.rootLocation.resolve(Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();

            // Comprobación de seguridad para evitar "path traversal"
            // Asegura que el archivo se guarde dentro del directorio rootLocation
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new RuntimeException("No se puede almacenar el archivo fuera del directorio configurado.");
            }

            // Copia el contenido del archivo al destino, reemplazando si ya existe
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Retorna la URL relativa para que el frontend pueda acceder a la imagen
            // Esta URL será la que se guarde en la base de datos
            return "/uploads/" + uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Fallo al almacenar el archivo.", e);
        }
    }
}