package org.verapdf.crawler.domain.crawling;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
public class CrawlRequest {
    private List<String> domains;
    private String id;
    private boolean isFinished;
    private String emailAddress;
    private Date crawlSinceTime;

    public CrawlRequest() {
    }

    public CrawlRequest(String id, String emailAddress, Date crawlSinceTime) {
        this.domains = new ArrayList<>();
        this.id = id;
        this.emailAddress = emailAddress;
        this.isFinished = false;
        this.crawlSinceTime = crawlSinceTime;
    }

    @JsonProperty
    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date getCrawlSinceTime() {
        return crawlSinceTime;
    }

    @JsonProperty
    public String getId() { return  id; }

    @JsonProperty
    public void setId(String id) { this.id = id; }

    @JsonProperty
    public List<String> getDomains() { return domains; }

    @JsonProperty
    public boolean isFinished() {
        return isFinished;
    }

    @JsonProperty
    public void setFinished() {
        isFinished = true;
    }

    @JsonProperty
    public String getEmailAddress() { return emailAddress; }

    @JsonProperty
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @JsonProperty
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public void setCrawlSinceTime(Date crawlSinceTime) {
        this.crawlSinceTime = crawlSinceTime;
    }

    @JsonProperty
    public void setDomains(List<String> domains) {
        this.domains = domains == null ? Collections.emptyList() : new ArrayList<>(domains);
    }
}