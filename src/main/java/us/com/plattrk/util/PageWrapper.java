package us.com.plattrk.util;

import java.util.List;

public class PageWrapper<T> {

    private List<T> results;
    private Long totalCount;

    public PageWrapper(List<T> results, Long totalCount) {
        super();
        this.results = results;
        this.totalCount = totalCount;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

}
