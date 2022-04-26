package de.beuth.knabe.spring_ddd_bank.domain;

import multex.Exc;
import static multex.MultexUtil.create;

import java.util.Objects;

/**
 * Typed Value Object always holding an {@link Account} number used to uniquely
 * identify an Account.
 */
public final class AccountNo {
	
	/**
	 * The internal account number, at the moment equal to the technical ID of the
	 * Account.
	 */
	private long number;

	/** Constructs an AccountNo holding the given number.
	 * @param number the contained Long number
	 * @throws IllegalExc The number is null or negative. */
	public AccountNo(final Long number) {
		if (number == null) {
			throw create(IllegalExc.class, "null");
		}
		this.number = number.longValue();
		if (this.number < 0) {
			throw create(IllegalExc.class, number);
		}
	}

	/**
	 * Constructs an AccountNo from a String.
	 * 
	 * @param number
	 *            An account number as String. It must not be null or empty and
	 *            contain only decimal digits.
	 * @throws IllegalExc The number String is null or contains non-digit characters.
	 */
	public AccountNo(final String number) {
		if (number == null) {
			throw create(IllegalExc.class, "null");
		}
		if (!number.matches("\\d+")) {
			throw create(IllegalExc.class, number);
		}
		this.number = Long.parseLong(number);
	}

	/** Illegal account number \"{0}\". Must consist only of digits. */
	@SuppressWarnings("serial")
	public static class IllegalExc extends Exc {
	}

	/**
	 * Returns the contained number.
	 * 
	 * @return the contained number.
	 */
	public long toLong() {
		return number;
	}

	@Override
	public String toString() {
		return Long.toString(number);
	}

	@Override
	public int hashCode() {
		return Objects.hash(number);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AccountNo other = (AccountNo) obj;
		return number == other.number;
	}

}
