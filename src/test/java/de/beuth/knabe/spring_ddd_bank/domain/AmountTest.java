/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.beuth.knabe.spring_ddd_bank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

/**Test driver for the value object {@linkplain Amount}*/
public class AmountTest {

    /**The exactness, by which euros have to be calculated when represented as double floating point numbers.*/
    public static final double DELTA = 0.0001;

    @Before
    public void setUp(){
        Locale.setDefault(Locale.GERMANY);
    }

    @Test
    public void insideRangeConstruction(){
        new Amount(Integer.MAX_VALUE, Integer.MAX_VALUE);
        new Amount(Integer.MIN_VALUE, Integer.MIN_VALUE);
        {
            final Amount amount = new Amount(0, 0);
            final double result = amount.toDouble();
            assertEquals(0.00, result, DELTA);
            assertEquals("0,00", amount.toString());
        }
        {
            final Amount amount = new Amount(Integer.MAX_VALUE, 0);
            final double result = amount.toDouble();
            assertEquals(2147483647.00, result, DELTA);
            assertEquals("2147483647,00", amount.toString());
        }
        {
            final Amount amount = new Amount(Integer.MIN_VALUE, 0);
            final double result = amount.toDouble();
            assertEquals(-2147483648.00, result, DELTA);
        }
        {
            final Amount amount = new Amount(0, Integer.MAX_VALUE);
            final double result = amount.toDouble();
            assertEquals(21474836.47, result, DELTA);
            assertEquals("21474836,47", amount.toString());
        }
        {
            final Amount amount = new Amount(0, Integer.MIN_VALUE);
            final double result = amount.toDouble();
            assertEquals(-21474836.48, result, DELTA);
            assertEquals("-21474836,48", amount.toString());
        }
        {
            final Amount amount = new Amount(Amount.maxValue());
            final double result = amount.toDouble();
            assertEquals(Amount.maxValue(), result, DELTA);
        }
        {
            final Amount amount = new Amount(Amount.minValue());
            final double result = amount.toDouble();
            assertEquals(Amount.minValue(), result, DELTA);
        }
    }

    @Test
    public void outsideRangeConstruction(){
        final double overgreatValue = Amount.maxValue() + 0.01;
        try{
            new Amount(overgreatValue);
            fail("Amount.RangeExc expected");
        }catch(Amount.RangeExc expected){}
        try{
            new Amount(-overgreatValue);
            fail("Amount.RangeExc expected");
        }catch(Amount.RangeExc expected){}
    }

    @Test
    public void plus(){
        {
            final Amount amount = new Amount(1112223334,99);
            final Amount result = amount.plus(new Amount(332221114,01));
            assertEquals(1444444449.00, result.toDouble(), DELTA);
        }
        {
            final Amount amount = new Amount(100000,0);
            final Amount result = amount.plus(new Amount(0,-1));
            assertEquals(99999.99, result.toDouble(), DELTA);
        }
        {
            final Amount max = new Amount(Amount.maxValue());
            final Amount badDelta = new Amount(0,1);
            try{
                max.plus(badDelta);
                fail("Amount.RangeExc expected");
            }catch(Amount.RangeExc expected){}
        }
        {
            final Amount min = new Amount(Amount.minValue());
            final Amount badDelta = new Amount(0,-1);
            try{
                min.plus(badDelta);
                fail("(Amount.RangeExc expected");
            }catch(Amount.RangeExc expected){}
        }
    }

    @Test
    public void minus(){
        {
            final Amount amount = new Amount(1112223334,99);
            final Amount result = amount.minus(new Amount(1102203304,66));
            assertEquals(10020030.33, result.toDouble(), DELTA);
        }
        {
            final Amount amount = new Amount(100000,0);
            final Amount result = amount.minus(new Amount(0,1));
            assertEquals(99999.99, result.toDouble(), DELTA);
        }
        {
            final Amount max = new Amount(Amount.maxValue());
            final Amount badDelta = new Amount(0,-1);
            try{
                max.minus(badDelta);
                fail("Amount.RangeExc expected");
            }catch(Amount.RangeExc expected){}
        }
        {
            final Amount min = new Amount(Amount.minValue());
            final Amount badDelta = new Amount(0,1);
            try{
                min.minus(badDelta);
                fail("Amount.RangeExc expected");
            }catch(Amount.RangeExc expected){}
        }
    }

    @Test
    public void times(){
        {
            final Amount amount = new Amount(1112223333,11);
            final Amount result = amount.times(3.0);
            assertEquals(3336669999.33, result.toDouble(), DELTA);
        }
        {
            final Amount amount = new Amount(100000,0);
            final Amount result = amount.times(-1.0);
            assertEquals(-100000.0, result.toDouble(), DELTA);
        }
        {
            final Amount max = new Amount(Amount.maxValue());
            try{
                max.times(1.00000000000001);
                fail("Amount.RangeExc expected");
            }catch(Amount.RangeExc expected){}
        }
        {
            final Amount min = new Amount(Amount.minValue());
            try{
                min.times(1.00000000000001);
                fail("Amount.RangeExc expected");
            }catch(Amount.RangeExc expected){}
        }
    }

    @Test
    public void compareTo(){
        final Amount testAmount = new Amount(999999999,99);
        final Amount lower = new Amount(999999999,98);
        assertEquals(+1, testAmount.compareTo(lower));
        final Amount equal = new Amount(999999999,99);
        assertEquals(0, testAmount.compareTo(equal));
        final Amount higher = new Amount(1000000000,00);
        assertEquals(-1, testAmount.compareTo(higher));
    }

}
