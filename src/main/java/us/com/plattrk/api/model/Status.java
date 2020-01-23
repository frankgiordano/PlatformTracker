package us.com.plattrk.api.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.Table;

import us.com.plattrk.util.CopyUtil;

@Entity
@Table(name = "Status")
public class Status {
	
	private Long id;
	private String name;
	
	public Status() {
	}
	
	public Status(String name) {
		this.name = name;
	}
	
	
	
	public Status(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	
	@Id
	@Column(name = "status_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(name = "name", columnDefinition="VARCHAR(50)", nullable = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	// ====== Cloneable Override =========

	@Override
	public Status clone() {
		Status instance= new Status(name);
		CopyUtil.copyPropertyies(instance, this);
		return instance;		
	}

}
