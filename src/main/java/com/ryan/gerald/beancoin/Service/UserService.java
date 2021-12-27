package com.ryan.gerald.beancoin.Service;

import com.ryan.gerald.beancoin.Dao.UserDao;
import com.ryan.gerald.beancoin.entity.User;
import com.ryan.gerald.beancoin.entity.UserRepository;
import com.ryan.gerald.beancoin.entity.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	UserRepository userRepository;


	public User getUserByUsername(String username){
		return userRepository.findById(username).get();
	}

	public Optional<User> getUserOptionalByName(String username){
		return userRepository.findById(username);
	}




	@Deprecated
	private UserDao userD = new UserDao();



	/**
	 * Adds new user to Database, user object to be provided and returned
	 * 
	 * @param user
	 * @return
	 */
	public User addUserService(User user) {
		return userD.addUser(user);
	}

	/**
	 * gets user from database based on username as primary key. Returns null if
	 * none found;
	 * 
	 * @param username
	 * @return
	 */
	public User getUserService(String username) {
		return userD.getUser(username);

	}

	/**
	 * Adds wallet to database under user by username. One wallet to one user as an
	 * embedded
	 * 
	 * @param username
	 * @param wallet
	 * @return
	 */
	public Wallet addWalletService(String username, Wallet wallet) {
		return userD.addWallet(username, wallet);

	}

	/**
	 * Removes user from database with username provided
	 * 
	 * @param username
	 * @return
	 */
	public User removeUserService(String username) {
		return userD.removeUser(username);

	}

	/**
	 * Authenticates user to database provided a username and password
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean authenticateUserService(String username, String password) {
		return userD.authenticateUser(username, password);

	}

	public static void main(String[] args)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {

	}

}
