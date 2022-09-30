package us.com.plattrk.api.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Error_Condition")
@Cacheable(true)
@NamedQueries({
        @NamedQuery(name = ErrorCondition.FIND_ALL_ERROR_CONDITIONS,
                query = "Select e from ErrorCondition e order by e.name",
                hints = {@QueryHint(name = "org.hibernate.cacheable", value = "true")})
})
public class ErrorCondition implements Serializable {

    public static final String FIND_ALL_ERROR_CONDITIONS = "findAllErrorConditions";

    private Long id;
    private String name;

    public ErrorCondition() {
    }

    public ErrorCondition(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Id
    @Column(name = "error_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name", columnDefinition = "VARCHAR(50)", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
