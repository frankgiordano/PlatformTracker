package us.com.plattrk.service;
import java.util.List;
import java.util.Set;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;

public interface RCAService {

	public List<RCAVO> getRCAs();
	
	public boolean deleteRCA(Long id);
	
	public boolean saveRCA(RCA rca);
	
	public RCA getRCA(Long id);
	
}