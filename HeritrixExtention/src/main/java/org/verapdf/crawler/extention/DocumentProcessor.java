package org.verapdf.crawler.extention;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.archive.modules.CrawlURI;
import org.archive.modules.writer.MirrorWriterProcessor;
import org.verapdf.common.GracefulHttpClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DocumentProcessor extends MirrorWriterProcessor {

    private static final int MAX_RETRIES = 120;
    private static final long RETRY_INTERVAL = 30 * 1000;
    private static SimpleDateFormat loggingDateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS]");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");

    private ObjectMapper mapper;

    private String jobId;

    private String logiusUrl;
    private Map<String, String> supportedContentTypes;

    public DocumentProcessor() {
        log("Initializing new document processor object");
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    protected boolean shouldProcess(CrawlURI crawlURI) {
        try {
            // TODO: add crawlSince parameter, and compare here with Last-Modified value and do now download and process older files

            log("shouldProcess method invocation with uri " + crawlURI.getURI());
            String contentType = crawlURI.getContentType();
            if (supportedContentTypes.keySet().contains(contentType)) {
                return true;
            } else if (contentType != null && contentType.startsWith("text")) {
                return false;
            }

            String extension = FilenameUtils.getExtension(crawlURI.getURI());
            return supportedContentTypes.values().contains(extension);
        } catch (Throwable e) {
            log("Fail to check file type of " + crawlURI.getURI() + ". Exception message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void innerProcess(CrawlURI crawlURI) {
        String uri = crawlURI.getURI();
        try {
            // Create document
            log("Creating document");
            DomainDocument document = new DomainDocument(jobId, uri);

            // Content type
            log("Setting content type");
            String contentType = crawlURI.getContentType();
            if (supportedContentTypes.containsKey(contentType)) {
                document.setContentType(supportedContentTypes.get(contentType));
            } else {
                document.setContentType(FilenameUtils.getExtension(uri));
            }

            // Set last modified
            log("Setting last modified date");
            String lastModifiedHeader = crawlURI.getHttpResponseHeader("Last-Modified");
            if (lastModifiedHeader != null) {
                try {
                    document.setLastModified(dateFormat.parse(lastModifiedHeader));
                } catch (ParseException e) {
                    log("Fail to parse " + lastModifiedHeader + " for " + uri + ", lastModified won't be set for this document.");
                    e.printStackTrace();
                }
            }

            // Send to main application for further processing
            log("Sending to main application for further processing");
            log("Generating POST request");
            HttpPost request = new HttpPost(logiusUrl + "/api/documents");
            String documentString = mapper.writeValueAsString(document);
            StringEntity payload = new StringEntity(documentString, ContentType.APPLICATION_JSON);
            request.setEntity(payload);

            log("Sending request");
            try (CloseableHttpClient httpClient = new GracefulHttpClient(MAX_RETRIES, RETRY_INTERVAL)) {
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    log("Response obtained with status code " + statusCode);
                    if (statusCode != 200) {
                        HttpEntity responseEntity = response.getEntity();
                        log("Fail to POST document " + documentString + "."
                                + " Response " + statusCode + " " + statusLine.getReasonPhrase()
                                + (responseEntity != null ? "\n" + IOUtils.toString(responseEntity.getContent()) : ""));
                    }
                }
            }
        } catch (Throwable e) {
            log("Fail to process " + uri + ". Exception message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void log(String message) {
        System.out.println(loggingDateFormat.format(new Date()) + " org.verapdf.crawler.extension.DocumentProcessor: " + message);
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getLogiusUrl() {
        return logiusUrl;
    }

    public void setLogiusUrl(String logiusUrl) {
        this.logiusUrl = logiusUrl;
    }

    public Map<String, String> getSupportedContentTypes() {
        return supportedContentTypes;
    }

    public void setSupportedContentTypes(Map<String, String> supportedContentTypes) {
        this.supportedContentTypes = supportedContentTypes;
    }
}
