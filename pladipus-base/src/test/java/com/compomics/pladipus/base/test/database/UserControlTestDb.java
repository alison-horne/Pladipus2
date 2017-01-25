package com.compomics.pladipus.base.test.database;

import static org.junit.Assert.*;

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

import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.base.config.BaseConfiguration;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
import com.compomics.pladipus.repository.config.TestRepositoryConfiguration;

/**
 * Tests for class which retrieves and stores user information, with database interaction.  Uses in-memory database.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BaseConfiguration.class, TestRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
public class UserControlTestDb {

	@Autowired
	private UserControl userControl;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	@Test
	public void testInvalidUserLogin() {
		try {
			userControl.login("bad_user", "password");
			fail("Login should fail for invalid user");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("db.userNotFound"), e.getMessage());
		}
	}
	
	@Test
	public void testValidUserLogin() {
		try {
			userControl.login("test_user1", "Password1");
			assertEquals(1, userControl.getUserId());
		} catch (PladipusReportableException e) {
			fail("Failed to login valid user: " + e.getMessage());
		}
	}
}
