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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Table(name="run_steps")
@Entity(name="run_steps")
@NamedQuery(name="RunStep.findByStatus", query="SELECT r FROM run_steps r WHERE r.status = :status")
public class RunStep {
	
	private Run run;
	private Long runStepId;
	private String toolName;
	private String stepIdentifier;
	private RunStatus status = RunStatus.BLOCKED;
	private Set<RunStepParameter> parameters = new HashSet<RunStepParameter>();
	private Set<RunStepOutput> outputs = new HashSet<RunStepOutput>();
	private String errorText;
    private Set<RunStep> prereqs = new HashSet<RunStep>();
    private Set<RunStep> dependents = new HashSet<RunStep>();
	
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="run_id")
	public Run getRun() {
		return run;
	}
	public void setRun(Run run) {
		this.run = run;
	}
    
    @Id  
    @GeneratedValue(strategy=GenerationType.IDENTITY) 
    @Column(name="run_step_id")
    public Long getRunStepId()  
    {  
        return runStepId;  
    }  
    public void setRunStepId(Long id)  
    {  
        this.runStepId = id;  
    }
    
    @Column(name="tool", nullable=false)
    public String getToolName() {
    	return toolName;
    }
    public void setToolName(String toolName) {
    	this.toolName = toolName;
    }
    
    @Column(name="step_identifier", nullable=false)
    public String getStepIdentifier() {
    	return stepIdentifier;
    }
    public void setStepIdentifier(String stepIdentifier) {
    	this.stepIdentifier = stepIdentifier;
    }
    
    @Column(name="run_step_status", nullable=false)
    @Enumerated(EnumType.STRING)
    public RunStatus getStatus() {
        return status;
    }
    public void setStatus(RunStatus status) {
    	this.status = status;
    }
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="runStep", fetch=FetchType.EAGER)
    public Set<RunStepParameter> getParameters() {
    	return parameters;
    }
    public void setParameters(Set<RunStepParameter> parameters) {
    	this.parameters = parameters;
    }
    public void addParameter(String paramName, String paramValue) {
    	RunStepParameter rsp = new RunStepParameter();
    	rsp.setParamName(paramName);
    	rsp.setParamValue(paramValue);
    	rsp.setRunStep(this);
    	parameters.add(rsp);
    }
    
    @Column(name="error_text", nullable=true)
    public String getErrorText() {
    	return errorText;
    }
    public void setErrorText(String errorText) {
    	this.errorText = errorText;
    }
    public void addError(String errorText) {
    	setErrorText(errorText);
    	setStatus(RunStatus.ERROR);
    }
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="runStep", fetch=FetchType.EAGER)
    public Set<RunStepOutput> getOutputs() {
    	return outputs;
    }
    public void setOutputs(Set<RunStepOutput> outputs) {
    	this.outputs = outputs;
    }
    public void addOutput(String name, String value) {
    	RunStepOutput rso = new RunStepOutput();
    	rso.setRunStep(this);
    	rso.setOutputValue(value);
    	if (name != null) rso.setOutputId(name);
    	outputs.add(rso);
    }
    
	@ManyToMany(cascade={CascadeType.ALL})
	@JoinTable(name="run_step_dependencies",
		joinColumns={@JoinColumn(name="dependent_run_step")},
		inverseJoinColumns={@JoinColumn(name="prerequisite_run_step")})
	public Set<RunStep> getPrereqs(){
		return prereqs;
	}
	public void setPrereqs(Set<RunStep> steps) {
		prereqs.addAll(steps);
	}
	public void addPrereq(RunStep step) {
		prereqs.add(step);
	}

	@ManyToMany(mappedBy="prereqs")
	public Set<RunStep> getDependents() {
		return dependents;
	}
	public void setDependents(Set<RunStep> steps) {
		dependents.addAll(steps);
	}
}
