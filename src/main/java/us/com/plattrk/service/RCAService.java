package us.com.plattrk.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;
import us.com.plattrk.util.PageWrapper;

public interface RCAService {

    public List<RCAVO> getRCAs();

    public PageWrapper<RCA> search(Map<String, String> filtersMap);
    
    public RCA deleteRCA(Long id);
    
    public RCA saveRCA(RCA rca);
    
    public Optional<RCA> getRCA(Long id);
    
}