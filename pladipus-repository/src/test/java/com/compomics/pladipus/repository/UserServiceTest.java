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

import com.compomics.pladipus.model.core.User;
import com.compomics.pladipus.model.exceptions.PladipusMessages;
import com.compomics.pladipus.model.exceptions.PladipusReportableException;
import com.compomics.pladipus.repository.config.RepositoryConfiguration;
import com.compomics.pladipus.repository.config.TestRepositoryConfiguration;
import com.compomics.pladipus.repository.service.UserService;

/**
 * Tests the UserService bean using in-memory database
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={RepositoryConfiguration.class, TestRepositoryConfiguration.class}, loader=AnnotationConfigContextLoader.class)
@Transactional
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
						  TransactionalTestExecutionListener.class })
public class UserServiceTest {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PladipusMessages exceptionMessages;

	@Test
	public void testGetAllUsers() {
		try {
			List<User> users = userService.getAllUsers();
			assertEquals(2, users.size());
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetUserByWrongNameReturnsNull() {
		try {
			assertNull(userService.getUserByName("NotAUser"));
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testGetUserByName() {
		try {
			User user1 = userService.getUserByName("test_user1");
			assertNotNull(user1);
			// Check some user variables to make sure it's the correct user
			assertEquals("test@user.one", user1.getEmail());
			assertTrue(user1.isActive());
			assertTrue(user1.isAdmin());
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoginWrongUserThrowsException() {
		try {
			userService.login("NotAUser", "password");
			fail("Login should throw exception if invalid username");
		} catch (PladipusReportableException e) {
			assertTrue(e.getMessage().equals(exceptionMessages.getMessage("db.userNotFound")));
		}
	}
	
	@Test
	public void testLoginWrongPasswordThrowsException() {
		try {
			userService.login("test_user1", "wrongpassword");
			fail("Login should throw exception if invalid password");
		} catch (PladipusReportableException e) {
			assertTrue(e.getMessage().equals(exceptionMessages.getMessage("db.wrongPassword")));
		}
	}
	
	@Test
	public void testCorrectLogin() {
		try {
			User user1 = userService.login("test_user1", "Password1");
			assertNotNull(user1);
			assertEquals(1, user1.getId());
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSetActive() {
		try {
			// test_user1 is initially setup as active, test_user2 inactive
			User user1 = userService.getUserByName("test_user1");
			User user2 = userService.getUserByName("test_user2");
			assertTrue(user1.isActive());
			assertFalse(user2.isActive());
			userService.setActive(user1, false);
			userService.setActive(user2, true);
			// Check objects have been updated
			assertFalse(user1.isActive());
			assertTrue(user2.isActive());
			// Get objects from database again to check db updated
			user1 = userService.getUserByName("test_user1");
			user2 = userService.getUserByName("test_user2");
			assertFalse(user1.isActive());
			assertTrue(user2.isActive());
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testSetAdmin() {
		try {
			// test_user1 is initially setup as an admin, test_user2 as user
			User user1 = userService.getUserByName("test_user1");
			User user2 = userService.getUserByName("test_user2");
			assertTrue(user1.isAdmin());
			assertFalse(user2.isAdmin());
			userService.setAdmin(user1, false);
			userService.setAdmin(user2, true);
			// Check objects have been updated
			assertFalse(user1.isAdmin());
			assertTrue(user2.isAdmin());
			// Get objects from database again to check db updated
			user1 = userService.getUserByName("test_user1");
			user2 = userService.getUserByName("test_user2");
			assertFalse(user1.isAdmin());
			assertTrue(user2.isAdmin());
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCreateUser() {
		try {
			User user = new User();
			user.setUsername("test_user3");
			user.setEmail("test@user.three");
			User createdUser = userService.createUser(user, "Password3");
			// Test that password was encrypted before saving in db
			assertNotEquals(createdUser.getPasswordEncrypted(), "Password3");
			// Test that defaults set correctly
			assertTrue(createdUser.isActive());
			assertFalse(createdUser.isAdmin());
			assertTrue(createdUser.getId() > 2);
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testCreateUserFailWithNoUsername() {
		try {
			User user = new User();
			userService.createUser(user, "Password4");
		} catch (PladipusReportableException e) {
			assertTrue(e.getMessage().equals(exceptionMessages.getMessage("db.invalidInsert", "user")));
		}
	}
	
	@Test
	public void testChangePassword() {
		try {
			String oldPasswordEncrypted = "e1STQoRCV9yok7U+mHMVo3oZTHo51VpL";
			String newPassword = "Password5";
			User user = userService.getUserByName("test_user1");
			assertEquals(user.getPasswordEncrypted(), oldPasswordEncrypted);
			userService.changePassword(user, newPassword);
			// Check that password updated in object, and encrypted
			assertNotEquals(user.getPasswordEncrypted(), oldPasswordEncrypted);
			assertNotEquals(user.getPasswordEncrypted(), newPassword);
			// Check that user can now login with new password - db updated correctly
			assertNotNull(userService.login("test_user1", newPassword));
		} catch (PladipusReportableException e) {
			fail(e.getMessage());
		}
	}
}
