package org.verapdf.crawler.logius.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.verapdf.crawler.logius.core.heritrix.HeritrixClient;
import org.verapdf.crawler.logius.crawling.CrawlJob;
import org.verapdf.crawler.logius.db.CrawlJobDAO;
import org.verapdf.crawler.logius.db.ValidationJobDAO;
import org.verapdf.crawler.logius.exception.NotFoundException;
import org.verapdf.crawler.logius.monitoring.CrawlJobStatus;
import org.verapdf.crawler.logius.monitoring.HeritrixCrawlJobStatus;
import org.verapdf.crawler.logius.monitoring.ValidationQueueStatus;
import org.verapdf.crawler.logius.tools.DomainUtils;
import org.verapdf.crawler.logius.validation.ValidationJob;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class CrawlJobService {
    private static final Logger logger = LoggerFactory.getLogger(CrawlJobService.class);
    private static final int GET_STATUS_MAX_DOCUMENT_COUNT = 10;
    private final CrawlJobDAO crawlJobDAO;
    private final ValidationJobDAO validationJobDAO;
    private final HeritrixClient heritrixClient;

    public CrawlJobService(CrawlJobDAO crawlJobDAO, ValidationJobDAO validationJobDAO, HeritrixClient heritrixClient) {
        this.crawlJobDAO = crawlJobDAO;
        this.validationJobDAO = validationJobDAO;
        this.heritrixClient = heritrixClient;
    }

    @Transactional
    public CrawlJob getNewBingJob() {
        List<CrawlJob> newJob = crawlJobDAO.findByStatus(CrawlJob.Status.NEW, CrawlJob.CrawlService.BING, null, 1);
        if (newJob != null && !newJob.isEmpty()) {
            CrawlJob crawlJob = newJob.get(0);
            crawlJob.setStatus(CrawlJob.Status.RUNNING);
            return crawlJob;
        }
        return null;
    }

    @Transactional
    public List<CrawlJob> findNotFinishedJobs(String domainFilter, int start, int limit) {
        return crawlJobDAO.findNotFinishedJobs(domainFilter, start, limit);
    }

    @Transactional
    public long count(String domainFilter, boolean isFinished) {
        return crawlJobDAO.count(domainFilter, isFinished);
    }

    @Transactional
    public CrawlJob update(CrawlJob update, UUID id) throws IOException, XPathExpressionException,
            SAXException, ParserConfigurationException {
        CrawlJob crawlJob = getCrawlJob(update.getDomain(), id);

        String heritrixJobId = crawlJob.getHeritrixJobId();
        CrawlJob.CrawlService service = crawlJob.getCrawlService();
        if (crawlJob.getStatus() == CrawlJob.Status.RUNNING && update.getStatus() == CrawlJob.Status.PAUSED) {
            if (service == CrawlJob.CrawlService.HERITRIX && !heritrixClient.isJobFinished(heritrixJobId)) {
                heritrixClient.pauseJob(heritrixJobId);
            }
            validationJobDAO.pause(crawlJob.getId());
            crawlJob.setStatus(CrawlJob.Status.PAUSED);
        }
        if (crawlJob.getStatus() == CrawlJob.Status.PAUSED && update.getStatus() == CrawlJob.Status.RUNNING) {
            if (service == CrawlJob.CrawlService.HERITRIX && !heritrixClient.isJobFinished(heritrixJobId)) {
                heritrixClient.unpauseJob(heritrixJobId);
            }
            validationJobDAO.unpause(crawlJob.getId());
            crawlJob.setStatus(CrawlJob.Status.RUNNING);
        }

        return crawlJob;
    }

    @Transactional
    public CrawlJob getCrawlJob(String domain, UUID userId) {
        domain = DomainUtils.trimUrl(domain);
        CrawlJob job = crawlJobDAO.findByDomainAndUserId(domain, userId);
        if (job == null) {
            throw new NotFoundException(String.format("crawl job with domain %s not found", domain));
        }
        return job;
    }

    @Transactional
    public long count(String domainFilter, UUID id, Boolean finished) {
        return crawlJobDAO.count(domainFilter, id, finished);
    }

    @Transactional
    public List<CrawlJob> find(String domainFilter, UUID id, Boolean finished, int startParam, int limitParam) {
        return crawlJobDAO.find(domainFilter, id, finished, startParam, limitParam);
    }

    @Transactional
    public CrawlJob unlinkCrawlRequests(String domain, UUID id, String email) {
        CrawlJob crawlJob = getCrawlJob(domain, id);
        crawlJob.getCrawlRequests().removeIf(request -> email.equals(request.getEmailAddress()));
        return crawlJob;
    }

    @Transactional
    public CrawlJobStatus getFullJobStatus(String domain, UUID id) {
        CrawlJob crawlJob = getCrawlJob(domain, id);
        HeritrixCrawlJobStatus heritrixStatus = null;
        CrawlJob.CrawlService crawlService = crawlJob.getCrawlService();

        switch (crawlService) {
            case HERITRIX:
                try {
                    heritrixStatus = heritrixClient.getHeritrixStatus(crawlJob.getHeritrixJobId());
                } catch (Throwable e) {
                    logger.error("Error during obtaining heritrix status", e);
                    heritrixStatus = new HeritrixCrawlJobStatus("Unavailable: " + e.getMessage(), null, null);
                }
                break;
            case BING:
                //TODO: fix this
                heritrixStatus = null;
                break;
            default:
                throw new IllegalStateException("CrawlJob service can't be null");
        }

        UUID crawlJobId = crawlJob.getId();
        Long count = validationJobDAO.count(crawlJobId);
        List<ValidationJob> topDocuments = validationJobDAO.getDocuments(crawlJobId, GET_STATUS_MAX_DOCUMENT_COUNT);
        crawlJob.getCrawlRequests().forEach(crawlRequest -> crawlRequest.getCrawlJobs().size());

        return new CrawlJobStatus(crawlJob, heritrixStatus, new ValidationQueueStatus(count, topDocuments));
    }
}
