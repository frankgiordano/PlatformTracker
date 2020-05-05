package us.com.plattrk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import us.com.plattrk.api.model.RCA;
import us.com.plattrk.api.model.RCAVO;
import us.com.plattrk.repository.RCARepository;
import us.com.plattrk.util.PageWrapper;

@Service(value = "RCAService")
public class RCAServiceImpl implements RCAService {
    
    @Autowired
    private RCARepository rcaRepository;

    @Override
    public List<RCAVO> getRCAs() {
        return rcaRepository.getRCAs();
    }

    @Override
    @Transactional
    public PageWrapper<RCA> search(String searchTerm, Long pageIndex) {
        return rcaRepository.getRCAsByCriteria(searchTerm, pageIndex);
    }

    @Override
    @Transactional
    public RCA deleteRCA(Long id) {
        return rcaRepository.deleteRCA(id);
    }

    @Override
    @Transactional
    public RCA saveRCA(RCA rca) {
        return rcaRepository.saveRCA(rca);
    }

    @Override
    public RCA getRCA(Long id) {
        return rcaRepository.getRCA(id);
    }

}
