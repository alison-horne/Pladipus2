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
@IdClass(value=WorkflowGlobalValue.Key.class)
@Table(name="workflow_global_values")
public class WorkflowGlobalValue {

    private WorkflowGlobalParameter param;
    private String value;

    @Id
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="workflow_global_id")
    public WorkflowGlobalParameter getParam() { 
    	return param; 
    }
    public void setParam(WorkflowGlobalParameter param) { 
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
    
    public WorkflowGlobalValue(String value) {
    	setValue(value);
    }
    public WorkflowGlobalValue() {}
    
    static class Key implements Serializable {

		private static final long serialVersionUID = -5302660933845257187L;
		private WorkflowGlobalParameter param;
        private String value;

        public WorkflowGlobalParameter getParam() { 
        	return param; 
        }
        public void setParam(WorkflowGlobalParameter param) { 
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