package couponsPhase3.login;

import couponsPhase3.facade.ClientFacade;

public class Session {

	private ClientFacade cFacade;
	private long lastAccessed;

	public Session(ClientFacade cFacade, long lastAccessed) {

		this.cFacade = cFacade;
		this.lastAccessed = lastAccessed;
	}

	public ClientFacade getClientFacade() {
		return cFacade;
	}

	public long getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(long lastAccessed) {
		this.lastAccessed = lastAccessed;
	}
}