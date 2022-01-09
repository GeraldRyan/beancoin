package com.ryan.gerald.beancoin.dao;

import java.util.NoSuchElementException;

import com.ryan.gerald.beancoin.entity.User;

@Deprecated
public class UserDao extends DBConnection {

	
	public User addUser(User user) {
		this.connect();
		User existing = em.find(User.class, user.getUsername());

		if (existing == null) {
			em.getTransaction().begin();
			em.persist(user);
			em.getTransaction().commit();
			this.disconnect();
			return user;
		}
		this.disconnect();
		return null;
	}

	
	public User getUser(String username) {
		this.connect();
		User u = em.find(User.class, username);
		this.disconnect();
		return u;
	}

//	
//	public User updateUser(User user) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	
	public User removeUser(String username) {
		this.connect();
		User u = em.find(User.class, username);
		em.remove(u); // how the heck does this work???
		this.disconnect();
		return u;
	}

	
	public boolean authenticateUser(String username, String password) {
		this.connect();
		User u = em.find(User.class, username);
		boolean result = false;
		if (u == null) {
			throw new NoSuchElementException("The selected user was not found in the database");
		}
		if (u.getPassword() == password) {
			result = true;
		}
		this.disconnect();
		return result;
	}
}
