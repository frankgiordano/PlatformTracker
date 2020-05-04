package us.com.plattrk.util;

import java.util.List;

public class PageWrapper<T> {

    private List<T> results;
    private Long total_count;

    public PageWrapper(List<T> results, Long total_count) {
        super();
        this.results = results;
        this.total_count = total_count;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public Long getTotal_count() {
        return total_count;
    }

    public void setTotal_count(Long total_count) {
        this.total_count = total_count;
    }

}
