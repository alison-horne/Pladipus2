package com.compomics.pladipus.model.persist;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.NamedQueries;
import javax.persistence.Table;

import com.compomics.pladipus.model.persist.User;

@Table(name="user_defaults")  
@Entity(name="user_defaults")
@NamedQueries({
	@NamedQuery(name="Default.listByUser", query="SELECT d FROM user_defaults d WHERE d.user = :user OR d.user IS NULL"),
	@NamedQuery(name="Default.namedAnyUser", query="SELECT d FROM user_defaults d WHERE d.name = :name"),
	@NamedQuery(name="Default.namedForUser", query="SELECT d FROM user_defaults d WHERE d.name = :name AND (d.user = :user OR d.user IS NULL)")
})
public class Default {

	private Long defaultId;
	private User user;
	private String name;
	private String value;
	private String type;
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="default_id")
    public Long getDefaultId() {  
        return defaultId;  
    }  
    public void setDefaultId(Long id) {  
        this.defaultId = id;  
    }  
    
    @ManyToOne
    @JoinColumn(name="user_id")
    public User getUser() {
    	return user;
    }
    public void setUser(User user) {
    	this.user = user;
    }

    @Column(name="default_name", nullable=false, length=80)
    public String getName() {
    	return name;
    }
    public void setName(String defaultName) {
    	this.name = defaultName;
    }

    @Column(name="default_value", nullable=false, length=200)
    public String getValue() {
    	return value;
    }
    public void setValue(String defaultValue) {
    	this.value = defaultValue;
    }
    
    @Column(name="default_type", nullable=true, length=80)
    public String getType() {
    	return type;
    }
    public void setType(String defaultType) {
    	this.type = defaultType;
    }
}
