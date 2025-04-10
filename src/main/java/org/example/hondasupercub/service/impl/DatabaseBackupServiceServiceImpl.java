package org.example.hondasupercub.service.impl;

import org.example.hondasupercub.service.DatabaseBackupService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class DatabaseBackupServiceServiceImpl implements DatabaseBackupService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Override
    public File createBackup() throws IOException, InterruptedException {
        try {
            // Extract database name from URL
            String dbName = extractDatabaseName(dbUrl);
            if (dbName == null) {
                throw new IOException("Could not extract database name from URL: " + dbUrl);
            }

            // Generate filename with timestamp
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String timestamp = LocalDateTime.now().format(formatter);
            String backupFileName = "backup_" + dbName + "_" + timestamp + ".sql";
            Path backupPath = Paths.get(System.getProperty("java.io.tmpdir"), backupFileName);

            // Build the mysqldump command
            String[] command = {
                    "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe", // Add the .exe here
                    "-u" + dbUsername,
                    "-p" + dbPassword,
                    dbName,
                    "--routines",
                    "--events",
                    "--result-file=" + backupPath.toString()
            };

            // Execute the command
            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return backupPath.toFile();
            } else {
                throw new IOException("Backup process failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw e;
        }
    }

    private String extractDatabaseName(String dbUrl) {
        // Assuming URL format: jdbc:mysql://host:port/databaseName?...
        String[] parts = dbUrl.split("/");
        if (parts.length > 3) {
            String dbPart = parts[3];
            // Remove any query parameters
            if (dbPart.contains("?")) {
                return dbPart.substring(0, dbPart.indexOf("?"));
            }
            return dbPart;
        }
        return null;
    }
}