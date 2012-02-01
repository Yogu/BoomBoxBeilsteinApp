package de.boomboxbeilstein.android;

public class UnexcpectedResponseException extends Exception {
	private static final long serialVersionUID = 1138919404971273608L;

	public UnexcpectedResponseException(String message) {
		super(message);
	}

	public UnexcpectedResponseException(String message, Throwable inner) {
		super(message, inner);
	}

	public UnexcpectedResponseException(Throwable inner) {
		super(inner);
	}
}
