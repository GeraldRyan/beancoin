package com.ryan.gerald.beancoin.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import com.ryan.gerald.beancoin.Service.UserService;
import com.google.gson.Gson;

@Entity
public class User {
	@Id
	@NotNull
	@Column(unique = true)
	String username;
	String password;
	String hint;
	String answer;
	String email;
	@OneToOne
	@JoinColumn(name = "ownerID")
	Wallet wallet;

	public User(String username, String password, String hint, String answer, String email, Wallet wallet) {
		super();
		this.username = username;
		this.password = password;
		this.hint = hint;
		this.answer = answer;
		this.email = email;
	}

	public User(String username, String password, String hint, String answer, String email) {
		super();
		this.username = username;
		this.password = password;
		this.hint = hint;
		this.answer = answer;
		this.email = email;
	}

	public User() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * this is no longer primary means of using wallet. instead we pull separate
	 * from database.
	 * 
	 * @return
	 */
	public Wallet getWallet() {
		return wallet;
	}

	/**
	 * this is no longer primary means of using wallet. instead we pull separate
	 * from database.
	 * 
	 * @return
	 */
	public void setWallet(Wallet wallet) {
		this.wallet = wallet;
	}

	public static void main(String[] args) {

	}

}
