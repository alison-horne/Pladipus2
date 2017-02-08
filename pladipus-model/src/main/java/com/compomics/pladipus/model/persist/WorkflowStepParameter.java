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

@Table(name="workflow_step_params")  
@Entity(name="workflow_step_params")
public class WorkflowStepParameter {
	private Long stepParamId;
	private Step step;
	private String name;
	private Set<WorkflowStepValue> values = new HashSet<WorkflowStepValue>();
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="workflow_step_param_id")
    public Long getStepParamId()  
    {  
        return stepParamId;  
    }  
    public void setStepParamId(Long id)  
    {  
        this.stepParamId = id;  
    }
    
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="step_id")
    public Step getStep()  
    {  
        return step;  
    }  
    public void setStep(Step step)  
    {  
        this.step = step;
    }
    
    @Column(name="parameter_name")
    public String getName() {
    	return name;
    }
    public void setName(String name) {
    	this.name = name;
    }
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="param", fetch=FetchType.EAGER)
    public Set<WorkflowStepValue> getValues() {
    	return values;
    }
    public void setValues(Set<WorkflowStepValue> values) {
    	this.values = values;
    }
    
    public WorkflowStepParameter(Parameter param) {
    	setName(param.getName());
    	Iterator<String> iter = param.getValue().iterator();
    	while (iter.hasNext()) {
    		WorkflowStepValue stepValue = new WorkflowStepValue(iter.next());
    		stepValue.setParam(this);
    		values.add(stepValue);
    	}
    }
    public WorkflowStepParameter() {}
}
