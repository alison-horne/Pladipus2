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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name="runs")
@Entity(name="runs")
public class Run {
	
	private Long runId;
	private BatchRun batchRun;
	private RunStatus status = RunStatus.READY;
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
    
    @ManyToOne
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
}
