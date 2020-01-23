package us.com.plattrk.api.model;

import java.util.Comparator;

public class ProductComparator implements Comparator<Product> {
	
    public int compare(Product c1, Product c2) { 
        return c1.getIncidentName().toUpperCase().compareTo(c2.getIncidentName().toUpperCase());
    }
}
