package us.com.plattrk.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module.Feature;

public class HibernateAwareObjectMapper extends ObjectMapper {

    public HibernateAwareObjectMapper() {
    	Hibernate4Module hm = new Hibernate4Module();
        hm.disable(Feature.USE_TRANSIENT_ANNOTATION);
        registerModule(hm);
    }
}