package us.com.plattrk.service;

import java.io.IOException;
import java.util.List;

import javax.naming.NamingException;

import us.com.plattrk.api.model.OwnerInfo;

public interface OwnerService {

    public List<OwnerInfo> getOwnerList();

}
