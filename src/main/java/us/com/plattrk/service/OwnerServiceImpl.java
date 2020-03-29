package us.com.plattrk.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import us.com.plattrk.api.model.OwnerInfo;

@Service(value = "OwnerService")
@Scope("singleton")
@Configuration
@PropertySources(value = { @PropertySource("classpath:/plattrk.properties") })
public class OwnerServiceImpl implements OwnerService {

    private static Logger logger = LoggerFactory.getLogger(OwnerServiceImpl.class);

    @Autowired
    private Environment env;
    private List<OwnerInfo> owners = new ArrayList<OwnerInfo>();

    public List<OwnerInfo> getOwnerList() throws NamingException, IOException {

        owners.clear();
        OwnerInfo owner = new OwnerInfo("guest", "guest");
        owners.add(owner);

        Collections.sort(owners, OwnerInfo.OwnerComparator);
        return owners;
    }
}
