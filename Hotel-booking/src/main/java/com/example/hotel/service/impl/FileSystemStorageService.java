package com.example.hotel.service.impl;

import com.example.hotel.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path storagePath;

    public FileSystemStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        String resolvedUploadDir = (uploadDir == null || uploadDir.isBlank()) ? "uploads" : uploadDir;
        Path desiredPath = Paths.get(resolvedUploadDir).toAbsolutePath().normalize();
        this.storagePath = initializeStorage(desiredPath);
    }

    private Path initializeStorage(Path desiredPath) {
        try {
            Files.createDirectories(desiredPath);
            return desiredPath;
        } catch (IOException primaryEx) {
            Path fallback = Paths.get(System.getProperty("java.io.tmpdir"), "hotel-uploads")
                .toAbsolutePath()
                .normalize();
            try {
                Files.createDirectories(fallback);
                return fallback;
            } catch (IOException fallbackEx) {
                fallbackEx.addSuppressed(primaryEx);
                throw new IllegalStateException(
                    "Unable to create upload directory at " + desiredPath + " or fallback " + fallback, fallbackEx);
            }
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString().replace("-", "");
        if (extension != null && !extension.isBlank()) {
            filename = filename + "." + extension.toLowerCase();
        }
        try {
            Path destination = storagePath.resolve(filename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to store file", ex);
        }
    }

    @Override
    public void delete(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }
        try {
            Path toDelete = storagePath.resolve(relativePath).normalize();
            if (toDelete.startsWith(storagePath) && Files.exists(toDelete)) {
                Files.delete(toDelete);
            }
        } catch (IOException ignored) {
            // Silently ignore delete failures to avoid breaking workflows
        }
    }
}
