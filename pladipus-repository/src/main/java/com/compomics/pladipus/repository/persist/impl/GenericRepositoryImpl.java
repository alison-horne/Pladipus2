package com.compomics.pladipus.repository.persist.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.repository.persist.GenericRepository;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;

@Transactional
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
    		throw new PladipusReportableException(exceptionMessages.getMessage("db.insertError", entityClass.getSimpleName(), e.getMessage()));
    	}
	}
	
    @Override
	public void merge(T t) throws PladipusReportableException {
		try {
			entityManager.merge(t);
		} catch (Exception e) {
			throw new PladipusReportableException(exceptionMessages.getMessage("db.updateError", e.getMessage()));
		}
	}
    
    @Override
    public List<T> findAll() throws PladipusReportableException {
    	String q = "SELECT t FROM " + entityClass.getName() + " t";
    	try {
    		return entityManager.createQuery(q, entityClass).getResultList();
    	} catch (Exception e) {
    		throw new PladipusReportableException(exceptionMessages.getMessage("db.errorGetQuery", e.getMessage()));
    	}
    }
    
    @Override
    public T findById(final Long id) throws PladipusReportableException {
    	try {
    		return entityManager.find(entityClass, id);
    	} catch (Exception e) {
    		throw new PladipusReportableException(exceptionMessages.getMessage("db.errorGetQuery", e.getMessage()));
    	}
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
			throw new PladipusReportableException(exceptionMessages.getMessage("db.errorGetQuery", e.getMessage()));
		}
    }
    
    protected List<T> getResultsList(TypedQuery<T> query) throws PladipusReportableException {
    	try {
    		return query.getResultList();
    	} catch (Exception e) {
    		throw new PladipusReportableException(exceptionMessages.getMessage("db.errorGetQuery", e.getMessage()));
    	}
    }
}
