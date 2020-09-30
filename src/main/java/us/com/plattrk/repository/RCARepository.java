package us.com.plattrk.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;
import us.com.plattrk.api.model.PageWrapper;

public interface RCARepository {
    
    public List<RCAVO> getRCAs();

    public PageWrapper<RCA> getRCAsByCriteria(Map<String, String> filtersMap);

    public RCA deleteRCA(Long id);

    public RCA saveRCA(RCA rca);

    public Optional<RCA> getRCA(Long id);

}
