package us.com.plattrk.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;
import us.com.plattrk.repository.RCARepository;
import us.com.plattrk.repository.ReferenceDataRepository;

@Service(value = "RCAService")
public class RCAServiceImpl implements RCAService {
	
	@Autowired
	private RCARepository rcaRepository;

	@Override
	@Transactional
	public RCA deleteRCA(Long id) {
		return rcaRepository.deleteRCA(id);
	}

	@Override
	public RCA getRCA(Long id) {
		return rcaRepository.getRCA(id);
	}

	@Override
	public List<RCAVO> getRCAs() {
		return rcaRepository.getRCAs();
	}

	@Override
	@Transactional
	public RCA saveRCA(RCA rca) {
		return rcaRepository.saveRCA(rca);
	}

}
