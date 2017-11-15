package com.compomics.pladipus.model.persist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

import com.compomics.pladipus.model.core.BatchRunOverview;

@Table(name="batch_runs")  
@Entity(name="batch_runs")
@NamedQueries({
	@NamedQuery(name="BatchRun.findById", query="SELECT r FROM batch_runs r WHERE r.id = :id")
})
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
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="batch_id")
    public Batch getBatch() {
    	return batch;
    }
    public void setBatch(Batch batch) {
    	this.batch = batch;
    }
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="run", fetch=FetchType.EAGER)
    public Set<BatchStepValue> getStepValues() {
    	return stepValues;
    }
    public void setStepValues(Set<BatchStepValue> stepValues) {
    	this.stepValues = stepValues;
    }
    public void addStepValue(Long stepParamId, String header, String value) {
    	BatchStepValue val = new BatchStepValue(stepParamId, header, value);
    	val.setRun(this);
    	stepValues.add(val);
    }
    @Transient
    public Map<Long, Set<String>> getStepParamMap() {
    	Map<Long, Set<String>> map = new HashMap<Long, Set<String>>();
    	for (BatchStepValue stepVal : stepValues) {
    		if (map.get(stepVal.getParamId()) == null) {
    			map.put(stepVal.getParamId(), new HashSet<String>());
    		}
    		map.get(stepVal.getParamId()).add(stepVal.getValue());
    	}
    	return map;
    }

    @OneToMany(cascade=CascadeType.ALL, mappedBy="run", fetch=FetchType.EAGER)
    public Set<BatchGlobalValue> getGlobalValues() {
    	return globalValues;
    }
    public void setGlobalValues(Set<BatchGlobalValue> globalValues) {
    	this.globalValues = globalValues;
    }
    public void addGlobalValue(Long globalParamId, String header, String value) {
    	BatchGlobalValue val = new BatchGlobalValue(globalParamId, header, value);
    	val.setRun(this);
    	globalValues.add(val);
    }
    @Transient
    public Map<Long, Set<String>> getGlobalParamMap() {
    	Map<Long, Set<String>> map = new HashMap<Long, Set<String>>();
    	for (BatchGlobalValue globalVal : globalValues) {
    		if (map.get(globalVal.getParamId()) == null) {
    			map.put(globalVal.getParamId(), new HashSet<String>());
    		}
    		map.get(globalVal.getParamId()).add(globalVal.getValue());
    	}
    	return map;
    }
    @Transient
    public BatchRunOverview getBatchRunOverview() {
    	BatchRunOverview bro = new BatchRunOverview(name, id);
    	Map<String, String> paramMap = new HashMap<String, String>();
    	for (BatchGlobalValue globalVal : globalValues) {
    		String val = "";
    		if (paramMap.containsKey(globalVal.getHeader())) {
    			val = paramMap.get(globalVal.getHeader()) + ",";
    		}
    		val += globalVal.getValue();
    		paramMap.put(globalVal.getHeader(), val);
    	}
    	for (BatchStepValue stepVal : stepValues) {
    		String val = "";
    		if (paramMap.containsKey(stepVal.getHeader())) {
    			val = paramMap.get(stepVal.getHeader()) + ",";
    		}
    		val += stepVal.getValue();
    		paramMap.put(stepVal.getHeader(), val);
    	}
    	bro.setParameters(paramMap);
    	return bro;
    }
}
