package de.beuth.knabe.spring_ddd_bank.domain;

import javax.persistence.Embeddable;

/**
 * Value Object representing a Euro money amount with two decimal fraction digits.
 */
@Embeddable
public class Amount {

    /**The maximum positive value that should be used as a Euro amount. The corresponding negative number is usable as minimum amount.*/
    public static final double MAX_VALUE = 9E16;

    public static final Amount ZERO = new Amount(0,0);

    /**The money amount in Euro Cents*/
    private long cents;

    /**Necessary for JPA object recreation when reading from database*/
    Amount() {}

    /**Constructs an amount from the given euros and cents. The arguments cannot exceed the working range.*/
    public Amount(final int euros, final int cents){
        final double centsAsDouble = 100.0*euros + cents;
        this.cents = Math.round(centsAsDouble);
        //System.out.printf("Amount of %d euros and %d cents results in %d cents.\n", euros, cents, this.cents);
    }

    /**Constructs an amount of the given euros, rounded to the nearest cent value.
     * @throws IllegalArgumentException  the resulting value is out of range
     */
    public Amount(final double euros){
        final long result = Math.round(euros *100.0);
        //System.out.printf("Amount of %f euros results in %d cents.\n", euros, result);
        if(result ==Long.MIN_VALUE || result ==Long.MAX_VALUE){
            throw new IllegalArgumentException(String.format("Amount of %f euros is out of range.", euros));
        }
        cents = result;
    }

    public long getCents(){return cents;}

    /**Returns the sum of this Amount and the other Amount.
     * @throws IllegalArgumentException  the resulting value is out of range
     */
    public Amount plus(final Amount other){
        final double result = this.toDouble() + other.toDouble();
        return new Amount(result);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (!(other instanceof Amount)) return false;
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

    /**Returns the difference of this Amount and the other Amount.
     * @throws IllegalArgumentException  the resulting value is out of range
     */
    public Amount minus(final Amount other){
        final double result = this.toDouble() - other.toDouble();
        return new Amount(result);
    }

    /**Returns the product of this Amount and the factor.
     * @throws IllegalArgumentException  the resulting value is out of range
     */
    public Amount times(final double factor){
        final double result = this.toDouble() * factor;
        return new Amount(result);
    }

    public double toDouble(){
        return cents / 100.0;
    }

    public String toString(){
        return String.format("%.2f", toDouble());
    }


}
