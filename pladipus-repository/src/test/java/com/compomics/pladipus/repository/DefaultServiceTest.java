package com.compomics.pladipus.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.model.core.Default;
import com.compomics.pladipus.model.db.DefaultsColumn;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.config.RepositoryConfiguration;
import com.compomics.pladipus.repository.config.TestRepositoryConfiguration;
import com.compomics.pladipus.repository.dao.BaseDAO;
import com.compomics.pladipus.repository.dao.Query;
import com.compomics.pladipus.repository.service.DefaultService;

/**
 * Tests the DefaultService bean using in-memory database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RepositoryConfiguration.class, TestRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
public class DefaultServiceTest {
	
	@Autowired
	private DefaultService defaultService;
	
	@Autowired
	private BaseDAO<Default> defaultDAO;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private static final int USER1 = 1;
	private static final int USER2 = 2;
	private static final String NAME1 = "default1";
	private static final String NAME2 = "default2";
	
	@Test
	public void testInsertFailWithDefaultSameNameForUser() throws PladipusReportableException {
		try {
			Default def = new Default();
			def.setName(NAME1);
			def.setValue("value");
			def.setUserId(USER1);
			defaultService.insertDefault(def);
			fail("Should not insert default with same name");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.defaultExists", NAME1), e.getMessage());
		}
	}
	
	@Test
	public void testInsertFailWithDefaultSameNameNullUser() throws PladipusReportableException {
		try {
			Default def = new Default();
			def.setName(NAME2);
			def.setValue("value");
			def.setUserId(USER1);
			defaultService.insertDefault(def);
			fail("Should not insert default with same name");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.defaultExists", NAME2), e.getMessage());
		}
	}
	
	@Test
	public void testInsertSuccessWithDefaultSameNameDifferentUser() throws PladipusReportableException {
		try {
			Default def = new Default();
			def.setName(NAME1);
			def.setValue("value");
			def.setUserId(USER2);
			assertEquals(-1, def.getId());
			defaultService.insertDefault(def);
			assertNotEquals(-1, def.getId());
		} catch (PladipusReportableException e) {
			fail("Failed to insert default: " + e.getMessage());
		}
	}
	
	@Test
	public void testInsertSuccessWithDefaultNewName() throws PladipusReportableException {
		try {
			Default def = new Default();
			def.setName("New_name");
			def.setValue("value");
			def.setUserId(USER1);
			def.setType("type");
			assertEquals(-1, def.getId());
			defaultService.insertDefault(def);
			assertNotEquals(-1, def.getId());
		} catch (PladipusReportableException e) {
			fail("Failed to insert default: " + e.getMessage());
		}
	}
	
	@Test
	public void testAddTypeNoneAlreadySet() throws PladipusReportableException {
		try {
			Default def = getDefaultFromDB(1);
			assertNull(def.getType());
			defaultService.addType(def, "type");
			assertEquals("type", def.getType());
			assertEquals("type", getDefaultFromDB(1).getType());
		} catch (PladipusReportableException e) {
			fail("Failed to add type: " + e.getMessage());
		}
	}
	
	@Test
	public void testUpdateTypeAlreadySet() throws PladipusReportableException {
		try {
			Default def = getDefaultFromDB(3);
			assertEquals("type3", def.getType());
			defaultService.addType(def, "type");
			assertEquals("type", def.getType());
			assertEquals("type", getDefaultFromDB(3).getType());
		} catch (PladipusReportableException e) {
			fail("Failed to add type: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetDefaultsReturnsNullUser() throws PladipusReportableException {
		try {
			List<Default> defaults = defaultService.getDefaultsForUser(USER2);
			assertEquals(1, defaults.size());
			assertEquals(2, defaults.get(0).getId());
		} catch (PladipusReportableException e) {
			fail("Failed to get defaults: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetDefaultsReturnsUserAndNullUser() throws PladipusReportableException {
		try {
			List<Default> defaults = defaultService.getDefaultsForUser(USER1);
			assertEquals(3, defaults.size());
			for (Default def: defaults) {
				if (def.getId() == 2) {
					// Null user
					assertEquals(-1, def.getUserId());
				} else if (def.getId() == 1 || def.getId() == 3) {
					// User 1
					assertEquals(1, def.getUserId());
				} else {
					fail("Unexpected default returned, id: " + def.getId());
				}
			}
		} catch (PladipusReportableException e) {
			fail("Failed to get defaults: " + e.getMessage());
		}
	}
	
	private Default getDefaultFromDB(int defId) throws PladipusReportableException {
		Query query = new Query();
		query.setWhereClause("WHERE " + DefaultsColumn.DEFAULT_ID.name() + " = :defId");
		query.setParameters(new MapSqlParameterSource().addValue("defId", defId));
		return defaultDAO.get(query);
	}
}
