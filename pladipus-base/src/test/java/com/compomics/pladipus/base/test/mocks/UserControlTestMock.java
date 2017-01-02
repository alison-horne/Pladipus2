package com.compomics.pladipus.base.test.mocks;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.compomics.pladipus.base.UserControl;
import com.compomics.pladipus.base.config.BaseConfiguration;
import com.compomics.pladipus.model.core.User;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.repository.config.MockRepositoryConfiguration;
import com.compomics.pladipus.repository.service.UserService;

/**
 * Tests to check userControl function without calling database.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BaseConfiguration.class, MockRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
@DirtiesContext(classMode=ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControlTestMock {
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserControl userControl;
	
	@Autowired
	private PladipusMessages exceptionMessages;
	
	private static final String USER1 = "user1";
	private static final String PASSWORD1 = "password1";
	private static final int USERID1 = 1;
	private static final String USER2 = "user2";
	private static final String PASSWORD2 = "password2";
	private static final int USERID2 = 2;
	private static final String ERROR = "Error string";
	
	@Before
	public void setUp() {
		Mockito.reset(userService);
		loginExpect();
	}
	
	@Test
	public void testLoginSuccess() {
		try {
			userControl.login(USER1, PASSWORD1);
			Mockito.verify(userService).login(USER1, PASSWORD1);
			assertEquals(USERID1, userControl.getUserId());
		} catch (PladipusReportableException e) {
			fail("Login should have succeeded: " + e.getMessage());
		}
	}
	
	@Test
	public void testLoginFailure() throws PladipusReportableException {
		try {
			userControl.login(USER1, PASSWORD2);
			fail("Mock error - login with wrong password should fail");
		} catch (PladipusReportableException e) {
			Mockito.verify(userService).login(USER1, PASSWORD2);
			assertEquals(ERROR, e.getMessage());
		}
	}
	
	@Test
	public void testLogout() {
		try {
			userControl.login(USER1, PASSWORD1);
			assertEquals(USERID1, userControl.getUserId());
			userControl.logout();
			userControl.getUserId();
			fail("Should throw exception getting user ID when logged out");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("base.noLogin"), e.getMessage());
		}
	}
	
	@Test
	public void testChangeUser() {
		try {
			userControl.login(USER1, PASSWORD1);
			assertEquals(USERID1, userControl.getUserId());
			userControl.login(USER2, PASSWORD2);
			assertEquals(USERID2, userControl.getUserId());
			Mockito.verify(userService).login(USER1, PASSWORD1);
			Mockito.verify(userService).login(USER2, PASSWORD2);
		} catch (PladipusReportableException e) {
			fail("Error on login: " + e.getMessage());
		}
	}
	
	private void loginExpect() {
		User user1 = new User();
		user1.setId(USERID1);
		User user2 = new User();
		user2.setId(USERID2);
		try {
		Mockito.when(userService.login(USER1, PASSWORD1)).thenReturn(user1);
		Mockito.when(userService.login(USER2, PASSWORD2)).thenReturn(user2);
		Mockito.doThrow(new PladipusReportableException(ERROR)).when(userService).login(USER1, PASSWORD2);
		} catch (PladipusReportableException e) {
			fail("Test setup failure: " + e.getMessage());
		}
	}
	
}
