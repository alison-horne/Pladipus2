package com.compomics.pladipus.model.persist;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="run_step_parameters")
@Entity(name="run_step_parameters")
@IdClass(value=RunStepParameter.Key.class)
public class RunStepParameter {
	
	private RunStep runStep;
	private String paramName;
	private String paramValue;
    
	@Id
    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="run_step_id")
	public RunStep getRunStep() {
		return runStep;
	}
	public void setRunStep(RunStep runStep) {
		this.runStep = runStep;
	}
	
	@Id
	@Column(name="parameter_name")
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	@Column(name="parameter_value")
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	
    static class Key implements Serializable {

		private static final long serialVersionUID = 6775056643927834982L;
		private RunStep runStep;
        private String paramName;

        public RunStep getRunStep() { 
        	return runStep; 
        }
        public void setRunStep(RunStep runStep) { 
        	this.runStep = runStep; 
        }

        public String getParamName() { 
        	return paramName; 
        }
        public void setParamName(String paramName) { 
        	this.paramName = paramName; 
        }
        
        @Override
        public boolean equals(Object o) {
        	if (o == null) return false;
            if (o == this) return true;
           
            if (!(o instanceof Key)) {
                return false;
            }

            Key key = (Key) o;

            return key.runStep.equals(this.runStep) &&
                   key.paramName.equals(this.paramName);
        }

        @Override
        public int hashCode() {
        	return Objects.hash(this.runStep, this.paramName);
        }
    }
}
