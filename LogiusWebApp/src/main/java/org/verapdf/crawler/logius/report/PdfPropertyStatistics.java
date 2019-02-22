package org.verapdf.crawler.logius.report;

import java.util.List;

public class PdfPropertyStatistics {
    public static final String VERSION_PROPERTY_NAME = "pdfVersion";
    public static final String PRODUCER_PROPERTY_NAME = "producer";

    public static final int TOP_PRODUCERS_COUNT = 10;
    private Long totalPdfDocumentsCount;
    private Long openPdfDocumentsCount;
    private Long notOpenPdfDocumentsCount;
    private List<ValueCount> flavourStatistics;
    private List<ValueCount> versionStatistics;
    private List<ValueCount> topProducerStatistics;

    public Long getTotalPdfDocumentsCount() {
        return totalPdfDocumentsCount;
    }

    public void setTotalPdfDocumentsCount(Long totalPdfDocumentsCount) {
        this.totalPdfDocumentsCount = totalPdfDocumentsCount;
    }

    public Long getOpenPdfDocumentsCount() {
        return openPdfDocumentsCount;
    }

    public void setOpenPdfDocumentsCount(Long openPdfDocumentsCount) {
        this.openPdfDocumentsCount = openPdfDocumentsCount;
    }

    public Long getNotOpenPdfDocumentsCount() {
        return notOpenPdfDocumentsCount;
    }

    public void setNotOpenPdfDocumentsCount(Long notOpenPdfDocumentsCount) {
        this.notOpenPdfDocumentsCount = notOpenPdfDocumentsCount;
    }

    public List<ValueCount> getFlavourStatistics() {
        return flavourStatistics;
    }

    public void setFlavourStatistics(List<ValueCount> flavourStatistics) {
        this.flavourStatistics = flavourStatistics;
    }

    public List<ValueCount> getVersionStatistics() {
        return versionStatistics;
    }

    public void setVersionStatistics(List<ValueCount> versionStatistics) {
        this.versionStatistics = versionStatistics;
    }

    public List<ValueCount> getTopProducerStatistics() {
        return topProducerStatistics;
    }

    public void setTopProducerStatistics(List<ValueCount> topProducerStatistics) {
        this.topProducerStatistics = topProducerStatistics;
    }

    public static class ValueCount {
        private String value;
        private Long count;

        public ValueCount() {
        }

        public ValueCount(String value, Long count) {
            this.value = value;
            this.count = count;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}
