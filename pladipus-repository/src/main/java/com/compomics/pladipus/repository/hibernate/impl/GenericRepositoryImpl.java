package com.compomics.pladipus.repository.hibernate.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;

import com.compomics.pladipus.repository.hibernate.GenericRepository;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

public abstract class GenericRepositoryImpl<T> implements GenericRepository<T> {
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private final Class<T> entityClass;
	
    @PersistenceContext
    private EntityManager entityManager;
    
    public GenericRepositoryImpl(final Class<T> entityClass) {
    	super();
        this.entityClass = entityClass;
    }
	
    @Override
	public void persist(T t) throws PladipusReportableException {
    	try {
    		entityManager.persist(t);
    	} catch (Exception e) {
    		throw new PladipusReportableException(exceptionMessages.getMessage("db.insertError", entityClass.getSimpleName(), getExceptionCause(e)));
    	}
	}
	
    @Override
	public void merge(T t) throws PladipusReportableException {
		try {
			entityManager.merge(t);
		} catch (Exception e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.updateError", getExceptionCause(e)));
		}
	}
    
    @Override
    public List<T> findAll() {
    	String q = "SELECT t FROM " + entityClass.getName() + " t";
    	return entityManager.createQuery(q, entityClass).getResultList();
    }
    
    @Override
    public T findById(final Long id) {
        return entityManager.find(entityClass, id);
    }
    
    protected TypedQuery<T> getNamedQuery(final String queryName) {
    	return entityManager.createNamedQuery(queryName, entityClass);
    }
    
    protected T getSingleResult(TypedQuery<T> query) throws PladipusReportableException {
    	try {
    		return query.getSingleResult();
    	} catch (NoResultException e){
    		return null;
    	} catch (NonUniqueResultException e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.nonUniqueGet", entityClass.getSimpleName()));
		} catch (Exception e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.invalidGetQuery", getExceptionCause(e)));
		}
    }
    
	protected String getExceptionCause(Exception e) {
		return (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
	}
}
