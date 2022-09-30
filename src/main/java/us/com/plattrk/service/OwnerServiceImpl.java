package us.com.plattrk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import us.com.plattrk.api.model.OwnerInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service(value = "OwnerService")
@Scope("singleton")
@Configuration
@PropertySources(value = { @PropertySource("classpath:/plattrk.properties") })
public class OwnerServiceImpl implements OwnerService {

    @Autowired
    private Environment env;
    
    private final List<OwnerInfo> owners = new ArrayList<>();

    public List<OwnerInfo> getOwnerList() {
        owners.clear();
        owners.add(new OwnerInfo("guest", "guest"));
        owners.add(new OwnerInfo("giofr01", "Frank Giordano"));
        owners.sort(Comparator.comparing(OwnerInfo::getDisplayName, String::compareToIgnoreCase));
        return owners;
    }

}
