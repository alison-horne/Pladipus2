package com.compomics.pladipus.repository;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.model.persist.Default;
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.repository.config.RepositoryConfiguration;
import com.compomics.pladipus.repository.config.TestRepositoryConfiguration;
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
	private PladipusMessages exceptionMessages;
	
	private static User USER1;
	private static User USER2;
	
	static {
		USER1 = new User();
		USER2 = new User();
		USER1.setUserId(1L);
		USER2.setUserId(2L);
	}

	private static final String NAME1 = "default1";
	private static final String NAME2 = "default2";
	
	@Test
	public void testInsertFailWithDefaultSameNameForUser() {
		try {
			Default def = new Default();
			def.setName(NAME1);
			def.setValue("value");
			def.setUser(USER1);
			defaultService.insertDefault(def);
			fail("Should not insert default with same name");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.defaultExists", NAME1), e.getMessage());
		}
	}
	
	@Test
	public void testInsertFailWithDefaultSameNameNullUser() {
		try {
			Default def = new Default();
			def.setName(NAME2);
			def.setValue("value");
			def.setUser(USER1);
			defaultService.insertDefault(def);
			fail("Should not insert default with same name");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.defaultExists", NAME2), e.getMessage());
		}
	}
	
	@Test
	public void testInsertSuccessWithDefaultSameNameDifferentUser() {
		try {
			Default def = new Default();
			def.setName(NAME1);
			def.setValue("value");
			def.setUser(USER2);
			assertNull(def.getDefaultId());
			defaultService.insertDefault(def);
			assertNotNull(def.getDefaultId());
			assertTrue(def.getDefaultId() > 3);
		} catch (PladipusReportableException e) {
			fail("Failed to insert default: " + e.getMessage());
		}
	}
	
	@Test
	public void testInsertSuccessWithDefaultNewName() {
		try {
			Default def = new Default();
			def.setName("New_name");
			def.setValue("value");
			def.setUser(USER1);
			def.setType("type");
			assertNull(def.getDefaultId());
			defaultService.insertDefault(def);
			assertNotNull(def.getDefaultId());
			assertTrue(def.getDefaultId() > 3);
		} catch (PladipusReportableException e) {
			fail("Failed to insert default: " + e.getMessage());
		}
	}
	
	@Test
	public void testAddTypeNoneAlreadySet() {
		try {
			Default def = defaultService.getDefaultById(1L);
			assertNull(def.getType());
			defaultService.addType(def, "type");
			assertEquals("type", def.getType());
		} catch (PladipusReportableException e) {
			fail("Failed to add type: " + e.getMessage());
		}
	}
	
	@Test
	public void testUpdateTypeAlreadySet() {
		try {
			Default def = defaultService.getDefaultById(3L);
			assertEquals("type3", def.getType());
			defaultService.addType(def, "type");
			assertEquals("type", def.getType());
		} catch (PladipusReportableException e) {
			fail("Failed to add type: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetDefaultsReturnsNullUser() {
		try {
			List<Default> defaults = defaultService.getDefaultsForUser(USER2);
			assertEquals(1, defaults.size());
			assertTrue(defaults.get(0).getDefaultId().equals(2L));
		} catch (PladipusReportableException e) {
			fail("Failed to get defaults: " + e.getMessage());
		}
	}
	
	@Test
	public void testGetDefaultsReturnsUserAndNullUser() {
		try {
			List<Default> defaults = defaultService.getDefaultsForUser(USER1);
			assertEquals(3, defaults.size());
			for (Default def: defaults) {
				if (def.getDefaultId() == 2L) {
					// Null user
					assertNull(def.getUser());
				} else if (def.getDefaultId() == 1L || def.getDefaultId() == 3L) {
					// User 1
					assertTrue(def.getUser().getUserId().equals(1L));
				} else {
					fail("Unexpected default returned, id: " + def.getDefaultId());
				}
			}
		} catch (PladipusReportableException e) {
			fail("Failed to get defaults: " + e.getMessage());
		}
	}
	
	@Test
	public void testAddGlobalDefault() {
		try {
			Default def = new Default();
			def.setName("NewDefault");
			def.setValue("NewValue");
			assertNull(def.getDefaultId());
			defaultService.insertDefault(def);
			assertNotNull(def.getDefaultId());
		} catch (PladipusReportableException e) {
			fail("Failed to add global default: " + e.getMessage());
		}
	}
	
	@Test
	public void testAddGlobalDefaultFailWithSameName() {
		try {
			Default def = new Default();
			def.setName(NAME2);
			def.setValue("NewValue");
			defaultService.insertDefault(def);
			fail("Should not be able to insert default with same name");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.defaultExists", NAME2), e.getMessage());
		}
	}
	
	@Test
	public void testAddGlobalDefaultFailWithSameNameAsUserDefault() {
		try {
			Default def = new Default();
			def.setName(NAME1);
			def.setValue("NewValue");
			defaultService.insertDefault(def);
			fail("Should not be able to insert default with same name");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.defaultExists", NAME1), e.getMessage());
		}
	}
}
