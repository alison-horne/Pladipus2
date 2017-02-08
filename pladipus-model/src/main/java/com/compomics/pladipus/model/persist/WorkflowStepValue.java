package com.compomics.pladipus.model.persist;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@IdClass(value=WorkflowStepValue.Key.class)
@Table(name="workflow_step_values")
public class WorkflowStepValue {

    private WorkflowStepParameter param;
    private String value;

    @Id
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="workflow_step_param_id")
    public WorkflowStepParameter getParam() { 
    	return param; 
    }
    public void setParam(WorkflowStepParameter param) { 
    	this.param = param; 
    }

    @Id
    @Column(name="parameter_value")
    public String getValue() { 
    	return value; 
    }
    public void setValue(String value) { 
    	this.value = value; 
    }
    
    public WorkflowStepValue(String value) {
    	setValue(value);
    }
    public WorkflowStepValue() {}
    
    static class Key implements Serializable {

		private static final long serialVersionUID = -7265406879023633975L;
		private WorkflowStepParameter param;
        private String value;

        public WorkflowStepParameter getParam() { 
        	return param; 
        }
        public void setParam(WorkflowStepParameter param) { 
        	this.param = param; 
        }

        public String getValue() { 
        	return value; 
        }
        public void setValue(String value) { 
        	this.value = value; 
        }
    }
}