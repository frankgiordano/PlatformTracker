package us.com.plattrk.service;
import java.util.List;
import java.util.Set;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;

public interface RCAService {

	List<RCAVO> getRCAs();
	
	Set<RCA> getRCAs(RCA rca);
	
	boolean deleteRCA(Long id);
	
	boolean saveRCA(RCA rca);
	
	RCA getRCA(Long id);
	
}