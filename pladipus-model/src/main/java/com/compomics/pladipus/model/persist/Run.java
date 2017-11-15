package com.compomics.pladipus.model.persist;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.compomics.pladipus.model.core.RunOverview;
import com.compomics.pladipus.model.core.RunStepOverview;

@Table(name="runs")
@Entity(name="runs")
@NamedQueries({
	@NamedQuery(name="Run.findByStatus", query="SELECT r FROM runs r WHERE r.status = :status"),
	@NamedQuery(name="Run.findByBatch", query="SELECT r FROM runs r WHERE r.active = true AND r.batchRun IN :batch"),
	@NamedQuery(name="Run.findByBatchRun", query="SELECT r FROM runs r WHERE r.active = true AND r.batchRun = :batchRun")
})
public class Run {
	
	private Long runId;
	private BatchRun batchRun;
	private RunStatus status = RunStatus.READY;
	private boolean active = true;
	private String runIdentifier;
	private long timestamp = -1;
	private Set<RunStep> runSteps = new HashSet<RunStep>();
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="run_id")
    public Long getRunId()  
    {  
        return runId;  
    }  
    public void setRunId(Long id)  
    {  
        this.runId = id;  
    }  
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="batch_run_id")
    public BatchRun getBatchRun() {
    	return batchRun;
    }
    public void setBatchRun(BatchRun batchRun) {
    	this.batchRun = batchRun;
    }
    
    @Column(name="run_status", nullable=false)
    @Enumerated(EnumType.STRING)
    public RunStatus getStatus() {
        return status;
    }
    public void setStatus(RunStatus status) {
    	this.status = status;
    }
    
    @Column(name="active")
    public boolean isActive() {
    	return active;
    }
    public void setActive(boolean active) {
    	this.active = active;
    }
    
    @Column(name="run_identifier", nullable=false)
    public String getRunIdentifier() {
    	return runIdentifier;
    }
    public void setRunIdentifier(String runIdentifier) {
    	this.runIdentifier = runIdentifier;
    }
    
    @Column(name="status_time", nullable=false)
    public long getTimestamp() {
    	return timestamp;
    }
    public void setTimestamp(long timestamp) {
    	this.timestamp = timestamp;
    }
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="run", fetch=FetchType.EAGER)
    public Set<RunStep> getRunSteps() {
    	return runSteps;
    }
    public void setRunSteps(Set<RunStep> runSteps) {
    	this.runSteps = runSteps;
    }
    public void addRunStep(RunStep runStep) {
    	runStep.setRun(this);
    	runSteps.add(runStep);
    }
    
    @Transient
    public RunOverview getRunOverview() {
    	RunOverview ro = new RunOverview(runId, batchRun.getId(), status);
    	for (RunStep step: runSteps) {
    		RunStepOverview rso = step.getRunStepOverview();
    		ro.addStep(rso);
    		if (status.equals(RunStatus.ABORT)) rso.setStatus(RunStatus.ABORT);
    	}
    	return ro;
    }
}
