package us.com.plattrk.api.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reference_data")
@Cacheable(true) 
public class ReferenceData implements Serializable{
	
	public static final String FIND_REFERENCES_BY_GROUP_ID = "findReferencesByGroupId";
	private String description;
	private String displayName;
	private Long groupId;
	private Long id;
	
	public ReferenceData() {
	}
	
	public ReferenceData(Long id, String displayName, Long groupId, String description) {
		this.id = id;
		this.displayName = displayName;
		this.description = description;
		this.groupId = groupId;
	}

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return id;
	}

	@Column(name = "description", columnDefinition="VARCHAR(100)", nullable = true)
	public String getDescription() {
		return description;
	}

	@Column(name = "display_name", columnDefinition="VARCHAR(50)", nullable = true)
	public String getDisplayName() {
		return displayName;
	}

	@Column(name = "group_id",  nullable = true)
	public Long getGroupId() {
		return groupId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public void setId(Long id) {
		this.id = id;
	}
}