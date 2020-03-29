package us.com.plattrk.repository;

import java.util.List;
import java.util.Set;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;

public interface RCARepository {
    
    public List<RCAVO> getRCAs();

    public RCA deleteRCA(Long id);

    public RCA saveRCA(RCA rca);

    public RCA getRCA(Long id);

}
