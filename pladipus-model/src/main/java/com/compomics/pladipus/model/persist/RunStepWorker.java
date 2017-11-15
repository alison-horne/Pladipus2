package com.compomics.pladipus.model.persist;

import java.io.Serializable;
import java.util.Date;
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

@Table(name="run_step_workers")
@Entity(name="run_step_workers")
@IdClass(value=RunStepWorker.Key.class)
public class RunStepWorker {
	
	private RunStep runStep;
	private String workerId;
	private Date start;
	private Date end;
	private String error;
	
	public RunStepWorker() {}
	public RunStepWorker(String workerId) {
		this.workerId = workerId;
		this.start = new Date();
	}
    
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
	@Column(name="worker_id")
	public String getWorkerId() {
		return workerId;
	}
	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}
	
	@Column(name="started", nullable=false)
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	
	@Column(name="ended", nullable=true)
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	
	@Column(name="error", nullable=true, length=1000)
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
    static class Key implements Serializable {

		private static final long serialVersionUID = 7633999287724159220L;
		private RunStep runStep;
        private String workerId;

        public RunStep getRunStep() { 
        	return runStep; 
        }
        public void setRunStep(RunStep runStep) { 
        	this.runStep = runStep; 
        }

        public String getWorkerId() { 
        	return workerId; 
        }
        public void setWorkerId(String workerId) { 
        	this.workerId = workerId; 
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
                   key.workerId.equals(this.workerId);
        }

        @Override
        public int hashCode() {
        	return Objects.hash(this.runStep, this.workerId);
        }
    }
}
