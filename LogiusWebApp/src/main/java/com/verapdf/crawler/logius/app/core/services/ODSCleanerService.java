package com.verapdf.crawler.logius.app.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.verapdf.crawler.logius.app.tools.AbstractService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * @author Maksim Bezrukov
 */

@Service
public class ODSCleanerService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(HeritrixCleanerService.class);
    private static final long SLEEP_DURATION = 60 * 60 * 1000;
    private static final long FILE_LIFETIME_IN_MILLIS = 7 * 24 * 60 * 60 * 1000;
    private String odsTempFolder;

    public ODSCleanerService(@Value("${reports.odsTempFolder}") String odsTempFolder) {
        super("ODSCleanerService", SLEEP_DURATION);
        this.odsTempFolder = odsTempFolder;
    }

    @Override
    protected void onStart() {
    }

    @Override
    protected boolean onRepeat() {
        File odsTempFolder = new File(this.odsTempFolder);
        if (odsTempFolder.exists() && odsTempFolder.isDirectory()) {
            long currentTimeInMillis = System.currentTimeMillis();
            for (File ods : odsTempFolder.listFiles()) {
                checkFile(ods, currentTimeInMillis);
            }
        }
        return true;
    }

    private void checkFile(File file, long currentTimeInMillis) {
        if (file.exists()) {
            try {
                BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                FileTime fileTime = attributes.creationTime();
                if (currentTimeInMillis - fileTime.toMillis() > FILE_LIFETIME_IN_MILLIS) {
                    file.delete();
                }
            } catch (IOException e) {
                logger.error("Error in removing a file with path " + file.getAbsolutePath(), e);
            }
        }
    }
}
