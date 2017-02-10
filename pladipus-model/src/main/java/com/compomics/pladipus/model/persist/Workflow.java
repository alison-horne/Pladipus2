package com.compomics.pladipus.model.persist;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="global" type="{}global" minOccurs="0"/&gt;
 *         &lt;element name="steps" type="{}steps"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "global",
    "steps"
})
@XmlRootElement(name = "template")
@Table(name="workflows")  
@Entity(name="workflows")
@NamedQueries({
	@NamedQuery(name="Workflow.activeForUser", query="SELECT w FROM workflows w WHERE w.user = :user AND w.active = true"),
	@NamedQuery(name="Workflow.namedActiveForUser", query="SELECT w FROM workflows w WHERE w.name = :name AND w.user = :user AND w.active = true")
})
public class Workflow {

    protected Global global;
    @XmlElement(required = true)
    protected Steps steps;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlTransient
    private Long id;
    @XmlTransient
    private String templateXml;
    @XmlTransient
    private Set<Step> templateSteps;
    @XmlTransient
    private Set<WorkflowGlobalParameter> globalParams = new HashSet<WorkflowGlobalParameter>();
    @XmlTransient
    private User user;
    @XmlTransient
    private boolean active = true;
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="workflow_id")
    public Long getId()  
    {  
        return id;  
    }  
    public void setId(Long id)  
    {  
        this.id = id;  
    }  

    /**
     * Gets the value of the global property.
     * 
     * @return
     *     possible object is
     *     {@link Global }
     *     
     */
    @Transient
    public Global getGlobal() {
    	if (global == null) {
    		setGlobal(new Global());
    	}
        return global;
    }

    /**
     * Sets the value of the global property.
     * 
     * @param value
     *     allowed object is
     *     {@link Global }
     *     
     */
    public void setGlobal(Global value) {
        this.global = value;
    }

    /**
     * Gets the value of the steps property.
     * 
     * @return
     *     possible object is
     *     {@link Steps }
     *     
     */
    @Transient
    public Steps getSteps() {
        return steps;
    }

    /**
     * Sets the value of the steps property.
     * 
     * @param value
     *     allowed object is
     *     {@link Steps }
     *     
     */
    public void setSteps(Steps value) {
        this.steps = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Column(name="workflow_name", nullable=false) 
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }
    
    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(name="template", nullable=false)
    public String getTemplateXml() {
    	return templateXml;
    }
    public void setTemplateXml(String templateXml) {
    	this.templateXml = templateXml;
    }
    
    @ManyToOne
    @JoinColumn(name="user_id")
    public User getUser() {
    	return user;
    }
    public void setUser(User user) {
    	this.user = user;
    }
    
    @Column(name="active")
    public boolean isActive() {
    	return active;
    }
    public void setActive(boolean active) {
    	this.active = active;
    }
 
    @OneToMany(cascade=CascadeType.ALL, mappedBy="workflow", fetch=FetchType.EAGER)  
    public Set<Step> getTemplateSteps()  
    {  
    	if ((templateSteps == null) && (getSteps() != null)) {
    		List<Step> stepList = getSteps().getStep();
    		Iterator<Step> iter = stepList.iterator();
    		while (iter.hasNext()) {
    			iter.next().setWorkflow(this);
    		}
    		setTemplateSteps(new HashSet<Step>(stepList));
    	}
        return templateSteps;
    }  
    public void setTemplateSteps(Set<Step> steps)  
    {  
    	this.templateSteps = steps;
    }  
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="workflow", fetch=FetchType.EAGER)  
    public Set<WorkflowGlobalParameter> getGlobalParams()  
    {  
    	if (globalParams.isEmpty() && (getGlobal() != null) && (getGlobal().getParameters() != null)) {
    		List<Parameter> params = getGlobal().getParameters().getParameter();
    		Iterator<Parameter> iter = params.iterator();
    		while (iter.hasNext()) {
    			WorkflowGlobalParameter globalParam = new WorkflowGlobalParameter(iter.next());
    			globalParam.setWorkflow(this);
    			globalParams.add(globalParam);
    		}
    	}
        return globalParams;
    }  
    public void setGlobalParams(Set<WorkflowGlobalParameter> params)  
    {  
    	this.globalParams = params;
    } 
}
