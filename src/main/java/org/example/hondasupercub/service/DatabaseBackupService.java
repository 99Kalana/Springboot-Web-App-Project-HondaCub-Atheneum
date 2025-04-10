package org.example.hondasupercub.service;

import java.io.File;
import java.io.IOException;

public interface DatabaseBackupService {
    File createBackup() throws IOException, InterruptedException;
}