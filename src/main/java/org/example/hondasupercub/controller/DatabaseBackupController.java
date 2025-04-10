package org.example.hondasupercub.controller;

import org.example.hondasupercub.service.DatabaseBackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/v1/admin/backup")
public class DatabaseBackupController {

    @Autowired
    private DatabaseBackupService backupService;

    @GetMapping
    public ResponseEntity<ByteArrayResource> backupDatabase() {
        try {
            File backupFile = backupService.createBackup();

            byte[] backupContent = Files.readAllBytes(backupFile.toPath());
            ByteArrayResource resource = new ByteArrayResource(backupContent);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + backupFile.getName()); // Add this line
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            backupFile.delete();

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(backupContent.length)
                    .body(resource);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
