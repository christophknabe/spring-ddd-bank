package de.beuth.knabe.spring_ddd_bank.domain;

import javax.persistence.Embeddable;
import static multex.MultexUtil.create;

/**
 * Value Object representing a Euro money amount with two decimal fraction
 * digits.
 */
@Embeddable
public class Amount {

	/**
	 * Returns the maximum positive value that should be used as a Euro amount.
	 * 
	 * @return maximum double value to be converted to {@link Amount}
	 */
	public static double maxValue() {
		return 9E13;
	}

	/**
	 * Returns the minimum negative value that should be used as a Euro amount.
	 * 
	 * @return minimum double value to be converted to {@link Amount}
	 */
	public static double minValue() {
		return -maxValue();
	}

	public static final Amount ZERO = new Amount(0, 0);

	/** The money amount in Euro Cents */
	private long cents;

	/** Necessary for attributes of JPA entities internally. */
	@SuppressWarnings("unused")
	private Amount() {
	}

	/**
	 * Constructs an amount from the given euros and cents. The arguments cannot
	 * exceed the working range. You can pass negative arguments, as well.
	 * 
	 * @param euros
	 *            number of EUR
	 * @param cents
	 *            number of EUR cents
	 */
	public Amount(final int euros, final int cents) {
		final double centsAsDouble = 100.0 * euros + cents;
		this.cents = Math.round(centsAsDouble);
		// System.out.printf("Amount of %d euros and %d cents results in %d cents.\n",
		// euros, cents, this.cents);
	}

	/**
	 * Constructs an amount of the given euros, rounded to the nearest cent value.
	 * 
	 * @param euros
	 *            an amount of EUR given as a floating point value
	 * @throws RangeExc
	 *             the given EURvalue is less than {@link #minValue()} or greater
	 *             than {@link #maxValue()}
	 * @throws IllegalArgumentException
	 *             the resulting value is out of range
	 */
	public Amount(final double euros) {
		if (euros < minValue() || maxValue() < euros) {
			throw create(RangeExc.class, euros, minValue(), maxValue());
		}
		final long result = Math.round(euros * 100.0);
		// System.out.printf("Amount of %f euros results in %d cents.\n", euros,
		// result);
		if (result == Long.MIN_VALUE || result == Long.MAX_VALUE) {
			throw new IllegalArgumentException(String.format("Amount of %f euros is out of range.", euros));
		}
		cents = result;
	}

	/** The amount of {0} euros is out of range. It must be between {1} and {2}. */
	@SuppressWarnings("serial")
	public static class RangeExc extends multex.Exc {
	}

	public long getCents() {
		return cents;
	}

	/**
	 * Returns the sum of this Amount and the other Amount.
	 * 
	 * @param other
	 *            the other {@link Amount} to be added to this one
	 * @return the sum of this and the other {@link Amount}
	 * @throws IllegalArgumentException
	 *             the resulting value is out of range
	 */
	public Amount plus(final Amount other) {
		final double doubleResult = this.toDouble() + other.toDouble();
		final Amount result = new Amount(doubleResult);
		// System.out.printf("Sum of %s euros and %s euros results in %s euros.\n",
		// this, other, result);
		return result;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other)
			return true;
		if (!(other instanceof Amount))
			return false;
		final Amount otherAmount = (Amount) other;
		return cents == otherAmount.cents;
	}

	@Override
	public int hashCode() {
		return (int) (cents ^ (cents >>> 32));
	}

	public int compareTo(final Amount other) {
		return Long.compare(cents, other.cents);
	}

	/**
	 * Returns the difference of this Amount and the other Amount.
	 * 
	 * @param other
	 *            the other {@link Amount} which will be subtracted from this one
	 * @return the difference
	 * @throws IllegalArgumentException
	 *             the resulting value is out of range
	 */
	public Amount minus(final Amount other) {
		final double result = this.toDouble() - other.toDouble();
		return new Amount(result);
	}

	/**
	 * Returns the product of this Amount and the factor.
	 * 
	 * @param factor
	 *            the factor by which this {@link Amount} has to be multiplied
	 * 
	 * @return the product
	 * @throws IllegalArgumentException
	 *             the resulting value is out of range
	 */
	public Amount times(final double factor) {
		final double result = this.toDouble() * factor;
		return new Amount(result);
	}

	/**
	 * Converts this {@link Amount} of EUR to its double value.
	 * 
	 * @return the amount of EUR contained in this object, with 2 fractional decimal
	 *         digits.
	 */
	public double toDouble() {
		return cents / 100.0;
	}

	public String toString() {
		return String.format("%.2f", toDouble());
	}

}
