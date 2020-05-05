package us.com.plattrk.service;

import java.util.List;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;
import us.com.plattrk.util.PageWrapper;

public interface RCAService {

    public List<RCAVO> getRCAs();

    public PageWrapper<RCA> search(String searchTerm, Long pageIndex);
    
    public RCA deleteRCA(Long id);
    
    public RCA saveRCA(RCA rca);
    
    public RCA getRCA(Long id);
    
}