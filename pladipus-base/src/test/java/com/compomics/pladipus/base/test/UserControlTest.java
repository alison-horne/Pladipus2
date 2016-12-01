package com.compomics.pladipus.base.test;

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
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.repository.config.TestRepositoryConfiguration;

/**
 * Tests for class which retrieves and stores user information.  Uses in-memory database.
 * Login cases already unit tested in repository module.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BaseConfiguration.class, TestRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
public class UserControlTest {

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
			assertTrue(e.getMessage().equals(exceptionMessages.getMessage("db.userNotFound")));
		}
	}
	
	@Test
	public void testGetUserId() {
		try {
			userControl.login("test_user1", "Password1");
			assertEquals(userControl.getUserId(), 1);
		} catch (PladipusReportableException e) {
			fail("Failed to login valid user");
		}
	}
	
	@Test
	public void testGetUserIdNoLogin() {
		try {
			// Make sure no user logged in
			userControl.logout();
			userControl.getUserId();
			fail("Should not return ID if no user logged in");
		} catch (PladipusReportableException e) {
			assertTrue(e.getMessage().equals(exceptionMessages.getMessage("base.noLogin")));
		}
	}
}
