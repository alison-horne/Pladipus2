package com.compomics.pladipus.model.persist;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name="batch_runs")  
@Entity(name="batch_runs")
public class BatchRun {

	private Long id;
    private String name;
    private Batch batch;   
    private Set<BatchGlobalValue> globalValues = new HashSet<BatchGlobalValue>();
    private Set<BatchStepValue> stepValues = new HashSet<BatchStepValue>();
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="batch_run_id")
    public Long getId()  
    {  
        return id;  
    }  
    public void setId(Long id)  
    {  
        this.id = id;  
    }  

    @Column(name="run_identifier") 
    public String getName() {
        return name;
    }
    public void setName(String value) {
        this.name = value;
    }
    
    @ManyToOne
    @JoinColumn(name="batch_id")
    public Batch getBatch() {
    	return batch;
    }
    public void setBatch(Batch batch) {
    	this.batch = batch;
    }
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="run")
    public Set<BatchStepValue> getStepValues() {
    	return stepValues;
    }
    public void setStepValues(Set<BatchStepValue> stepValues) {
    	this.stepValues = stepValues;
    }
    public void addStepValue(Long stepParamId, String value) {
    	BatchStepValue val = new BatchStepValue(stepParamId, value);
    	val.setRun(this);
    	stepValues.add(val);
    }

    @OneToMany(cascade=CascadeType.ALL, mappedBy="run")
    public Set<BatchGlobalValue> getGlobalValues() {
    	return globalValues;
    }
    public void setGlobalValues(Set<BatchGlobalValue> globalValues) {
    	this.globalValues = globalValues;
    }
    public void addGlobalValue(Long globalParamId, String value) {
    	BatchGlobalValue val = new BatchGlobalValue(globalParamId, value);
    	val.setRun(this);
    	globalValues.add(val);
    }
}
