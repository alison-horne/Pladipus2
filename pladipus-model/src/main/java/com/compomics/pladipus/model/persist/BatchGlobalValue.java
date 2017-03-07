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

@Entity
@IdClass(value=BatchGlobalValue.Key.class)
@Table(name="batch_global_values")
public class BatchGlobalValue {

    private Long paramId;
    private BatchRun run;
    private String value;

    @Id
    @Column(name="workflow_global_id")
    public Long getParamId() { 
    	return paramId; 
    }
    public void setParamId(Long paramId) { 
    	this.paramId = paramId; 
    }
    
    @Id
    @ManyToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="batch_run_id")
    public BatchRun getRun() { 
    	return run; 
    }
    public void setRun(BatchRun run) { 
    	this.run = run; 
    }

    @Id
    public String getValue() { 
    	return value; 
    }
    public void setValue(String value) { 
    	this.value = value; 
    }
    
    public BatchGlobalValue(Long paramId, String value) {
    	setValue(value);
    	setParamId(paramId);
    }
    
    public BatchGlobalValue() {
    	
    }
    
    static class Key implements Serializable {

		private static final long serialVersionUID = -4294967619492156351L;
		private Long paramId;
        private String value;
        private BatchRun run;

        public Long getParamId() { 
        	return paramId; 
        }
        public void setParamId(Long paramId) { 
        	this.paramId = paramId; 
        }
        
        public BatchRun getRun() { 
        	return run; 
        }
        public void setRun(BatchRun run) { 
        	this.run = run; 
        }

        public String getValue() { 
        	return value; 
        }
        public void setValue(String value) { 
        	this.value = value; 
        }
        
        @Override
        public boolean equals(Object o) {
        	if (o == null) return false;
            if (o == this) return true;
           
            if (!(o instanceof Key)) {
                return false;
            }

            Key key = (Key) o;

            return key.paramId.equals(this.paramId) &&
                   key.value.equals(this.value) &&
                   key.run.equals(this.run);
        }

        @Override
        public int hashCode() {
        	return Objects.hash(this.paramId, this.value, this.run);
        }
    }
}