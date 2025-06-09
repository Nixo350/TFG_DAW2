package com.gestion.zarpas_backend.servicio;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    private final Path rootLocation;

    public StorageService() {
        this.rootLocation = Paths.get("uploads");

        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la ubicación de almacenamiento para imágenes", e);
        }
    }

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