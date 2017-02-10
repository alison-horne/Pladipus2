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
import com.compomics.pladipus.model.persist.User;
import com.compomics.pladipus.shared.PladipusMessages;
import com.compomics.pladipus.shared.PladipusReportableException;
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
	
	private static User USER_1 = new User();
	private static User USER_2 = new User();
	private static final String USER1 = "user1";
	private static final String PASSWORD1 = "password1";
	private static final String USER2 = "user2";
	private static final String PASSWORD2 = "password2";
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
			assertEquals(USER_1, userControl.getLoggedInUser());
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
			assertEquals(USER_1, userControl.getLoggedInUser());
			userControl.logout();
			userControl.getLoggedInUser();
			fail("Should throw exception getting user ID when logged out");
		} catch (PladipusReportableException e) {
			assertEquals(exceptionMessages.getMessage("base.noLogin"), e.getMessage());
		}
	}
	
	@Test
	public void testChangeUser() {
		try {
			userControl.login(USER1, PASSWORD1);
			assertEquals(USER_1, userControl.getLoggedInUser());
			userControl.login(USER2, PASSWORD2);
			assertEquals(USER_2, userControl.getLoggedInUser());
			Mockito.verify(userService).login(USER1, PASSWORD1);
			Mockito.verify(userService).login(USER2, PASSWORD2);
		} catch (PladipusReportableException e) {
			fail("Error on login: " + e.getMessage());
		}
	}
	
	private void loginExpect() {
		try {
			Mockito.when(userService.login(USER1, PASSWORD1)).thenReturn(USER_1);
			Mockito.when(userService.login(USER2, PASSWORD2)).thenReturn(USER_2);
			Mockito.doThrow(new PladipusReportableException(ERROR)).when(userService).login(USER1, PASSWORD2);
		} catch (PladipusReportableException e) {
			fail("Test setup failure: " + e.getMessage());
		}
	}
	
}
