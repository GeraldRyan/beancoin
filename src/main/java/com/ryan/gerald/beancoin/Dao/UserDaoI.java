package com.ryan.gerald.beancoin.Dao;

import java.util.List;

import com.ryan.gerald.beancoin.entity.User;
import com.ryan.gerald.beancoin.entity.Wallet;



public interface UserDaoI {
	public User addUser(User user);

	public User getUser(String username);

	public Wallet addWallet(String username, Wallet wallet);

//	public User updateUser(User user);
	public User removeUser(String username);

	public boolean authenticateUser(String username, String password);

//	public List<User> getAllUsers(); // is this safe? It also gets their wallets

}
