package org.verapdf.crawler.logius.core.validation;

import org.verapdf.crawler.logius.validation.ValidationJob;
import org.verapdf.crawler.logius.validation.VeraPDFValidationResult;

import java.io.File;
import java.io.IOException;

public interface PDFValidator {
    void startValidation(File job, boolean isValidationDisabled) throws IOException, ValidationDeadlockException;

    VeraPDFValidationResult getValidationResult(File job, boolean isValidationDisabled) throws IOException, ValidationDeadlockException, InterruptedException;

    void terminateValidation() throws IOException;
}
