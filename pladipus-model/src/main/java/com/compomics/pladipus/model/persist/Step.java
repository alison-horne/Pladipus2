package com.compomics.pladipus.model.persist;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for step complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="step"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}ID"/&gt;
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="parameters" type="{}parameters" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "step", propOrder = {
    "id",
    "runAfter",
    "name",
    "parameters"
})
@Entity(name="workflow_steps")
@Table(name="workflow_steps")
public class Step {

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlIDREF
    protected Set<Step> runAfter;
    @XmlElement(required = true)
    protected String name;
    protected Parameters parameters;
    @XmlTransient
    private Long stepId;
    @XmlTransient
    private Workflow workflow;
    @XmlTransient
    private Set<Step> prereqs = new HashSet<Step>();
    @XmlTransient
    private Set<Step> dependents = new HashSet<Step>();
    @XmlTransient
    private Set<WorkflowStepParameter> stepParams = new HashSet<WorkflowStepParameter>();
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="step_id")
    public Long getStepId()  
    {  
        return stepId;  
    }  
    public void setStepId(Long stepId)  
    {  
        this.stepId = stepId;  
    }  

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Column(name="step_identifier")
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }
    
    @Transient
    public Set<Step> getRunAfter ()
    {
        return runAfter;
    }

    public void setRunAfter (Set<Step> runAfter)
    {
        this.runAfter = runAfter;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Column(name="tool")
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

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link Parameters }
     *     
     */
    @Transient
    public Parameters getParameters() {
    	if (parameters == null) {
    		setParameters(new Parameters());
    	}
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link Parameters }
     *     
     */
    public void setParameters(Parameters value) {
        this.parameters = value;
    }
    
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="workflow_id")
    public Workflow getWorkflow()  
    {  
        return workflow;  
    }  
    public void setWorkflow(Workflow workflow)  
    {  
        this.workflow = workflow;
    }
    
	@ManyToMany(cascade={CascadeType.ALL})
	@JoinTable(name="step_dependencies",
		joinColumns={@JoinColumn(name="dependent_step")},
		inverseJoinColumns={@JoinColumn(name="prerequisite_step")})
	public Set<Step> getPrereqs(){
		if (runAfter != null) {
			for (Step after: runAfter) {
				prereqs.add(after);
			}
		}
		return prereqs;
	}
	public void setPrereqs(Set<Step> steps) {
		prereqs.addAll(steps);
	}
	public void addPrereq(Step step) {
		prereqs.add(step);
	}

	@ManyToMany(mappedBy="prereqs")
	public Set<Step> getDependents() {
		return dependents;
	}
	public void setDependents(Set<Step> steps) {
		dependents.addAll(steps);
	}
	
    @OneToMany(cascade=CascadeType.ALL, mappedBy="step")  
    public Set<WorkflowStepParameter> getStepParams()  
    {  
    	if (stepParams.isEmpty() && (getParameters() != null)) {
    		List<Parameter> params = getParameters().getParameter();
    		Iterator<Parameter> iter = params.iterator();
    		while (iter.hasNext()) {
    			WorkflowStepParameter stepParam = new WorkflowStepParameter(iter.next());
    			stepParam.setStep(this);
    			stepParams.add(stepParam);
    		}
    	}
        return stepParams;
    }  
    public void setStepParams(Set<WorkflowStepParameter> params)  
    {  
    	this.stepParams = params;
    }
}
