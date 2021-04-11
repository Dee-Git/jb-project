package couponsPhase3.login;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import couponsPhase3.tables.ClientType;

@Component
@Scope("prototype")
public class LoginBean {

	private String email;

	private String password;

	private ClientType type;

	public LoginBean() {
	}

	public LoginBean(String email, String password, ClientType type) {

		this.email = email.strip();
		this.password = password.strip();
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public ClientType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "LoginBean [email=" + email + ", type=" + type + "]";
	}
}
