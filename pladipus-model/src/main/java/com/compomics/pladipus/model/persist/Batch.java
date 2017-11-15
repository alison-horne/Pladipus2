package com.compomics.pladipus.model.persist;

import java.util.HashSet;
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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.compomics.pladipus.model.core.BatchOverview;

@Table(name="batches")  
@Entity(name="batches")
@NamedQueries({
	@NamedQuery(name="Batch.findById", query="SELECT b FROM batches b WHERE b.id = :id"),
	@NamedQuery(name="Batch.findNamedActive", query="SELECT b FROM batches b WHERE b.name = :name AND b.workflow = :workflow AND b.active = true"),
	@NamedQuery(name="Batch.findActiveForWorkflows", query="SELECT b FROM batches b WHERE b.active = true AND b.workflow IN :workflows"),
	@NamedQuery(name="Batch.findNamedActiveForWorkflows", query="SELECT b FROM batches b WHERE b.name = :name AND b.active = true AND b.workflow IN :workflows")
})
public class Batch {
    
    private Long id;
    private String name;
    private Workflow workflow;
    private boolean active = true;
    private Set<BatchRun> runs = new HashSet<BatchRun>();
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="batch_id")
    public Long getId()  
    {  
        return id;  
    }  
    public void setId(Long id)  
    {  
        this.id = id;  
    }  

    @Column(name="batch_name") 
    public String getName() {
        return name;
    }
    public void setName(String value) {
        this.name = value;
    }
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="workflow_id")
    public Workflow getWorkflow() {
    	return workflow;
    }
    public void setWorkflow(Workflow workflow) {
    	this.workflow = workflow;
    }
    
    @Column(name="active", nullable=false)
    public boolean getActive() {
    	return active;
    }
    public void setActive(boolean active) {
    	this.active = active;
    }
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="batch", fetch=FetchType.EAGER)
    public Set<BatchRun> getRuns() {
    	return runs;
    }
    public void setRuns(Set<BatchRun> runs) {
    	this.runs = runs;
    }
    public void addRun(BatchRun run) {
    	run.setBatch(this);
    	runs.add(run);
    }
    
    @Transient
    public BatchOverview getBatchOverview() {
    	BatchOverview bo = new BatchOverview(name, id);
    	for (BatchRun run: runs) {
    		bo.addRun(run.getBatchRunOverview());
    	}
    	return bo;
    }
}
