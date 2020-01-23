package us.com.plattrk.repository;

import java.util.Set;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;

public interface RCARepository {
	
    Set<RCAVO> getRCAs();
    
    Set<RCA> getRCAs(RCA rca);

    boolean deleteRCA(Long id);

    boolean saveRCA(RCA rca);

	RCA getRCA(Long id);

}
