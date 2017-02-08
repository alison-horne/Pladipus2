package com.compomics.pladipus.model.persist;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name="workflow_global_params")  
@Entity(name="workflow_global_params")
public class WorkflowGlobalParameter {
	private Long globalId;
	private Workflow workflow;
	private String name;
	private Set<WorkflowGlobalValue> values = new HashSet<WorkflowGlobalValue>();
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="workflow_global_id")
    public Long getGlobalId()  
    {  
        return globalId;  
    }  
    public void setGlobalId(Long id)  
    {  
        this.globalId = id;  
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
    
    @Column(name="parameter_name")
    public String getName() {
    	return name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="param", fetch=FetchType.EAGER)
    public Set<WorkflowGlobalValue> getValues() {
    	return values;
    }
    public void setValues(Set<WorkflowGlobalValue> values) {
    	this.values = values;
    }
    
    public WorkflowGlobalParameter(Parameter param) {
    	setName(param.getName());
    	Iterator<String> iter = param.getValue().iterator();
    	while (iter.hasNext()) {
    		WorkflowGlobalValue globalValue = new WorkflowGlobalValue(iter.next());
    		globalValue.setParam(this);
    		values.add(globalValue);
    	}
    }
    public WorkflowGlobalParameter() {}
}
