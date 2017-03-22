package com.compomics.pladipus.model.persist;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Table(name="run_step_outputs")
@Entity(name="run_step_outputs")
@IdClass(value=RunStepOutput.Key.class)
public class RunStepOutput {

	private RunStep runStep;
	private String outputId = "out";
	private String outputValue;
	private boolean finalStep = false;
    
	@Id
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="run_step_id")
	public RunStep getRunStep() {
		return runStep;
	}
	public void setRunStep(RunStep runStep) {
		this.runStep = runStep;
	}
	
	@Id
	@Column(name="output_id")
	public String getOutputId() {
		return outputId;
	}
	public void setOutputId(String outputId) {
		this.outputId = outputId;
	}
	
	@Column(name="output_value")
	public String getOutputValue() {
		return outputValue;
	}
	public void setOutputValue(String outputValue) {
		this.outputValue = outputValue;
	}
	
	@Column(name="final_step", nullable=false)
	public boolean isFinalStep() {
		return finalStep;
	}
	public void setFinalStep(boolean finalStep) {
		this.finalStep = finalStep;
	}

    static class Key implements Serializable {

    	private static final long serialVersionUID = 2090053687778015734L;
		private RunStep runStep;
        private String outputId;

        public RunStep getRunStep() { 
        	return runStep; 
        }
        public void setRunStep(RunStep runStep) { 
        	this.runStep = runStep; 
        }

        public String getOutputId() { 
        	return outputId; 
        }
        public void setOutputId(String outputId) { 
        	this.outputId = outputId; 
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
                   key.outputId.equals(this.outputId);
        }

        @Override
        public int hashCode() {
        	return Objects.hash(this.runStep, this.outputId);
        }
    }
}
