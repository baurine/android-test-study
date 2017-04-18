package com.baurine.androidtestdemo1;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by baurine on 4/18/17.
 */
public class CalculatorTest {

    private Calculator calculator;

    @Before
    public void setUp() throws Exception {
        calculator = new Calculator();
    }

    @Test
    public void testSum() throws Exception {
        assertEquals(6d, calculator.sum(1d,5d), 0);
    }

    @Test
    public void testSubstract() throws Exception {
        assertEquals(1d, calculator.substract(5d, 4d), 0);
    }

    @Test
    public void testDivide() throws Exception {
        assertEquals(4d, calculator.divide(20d, 5d), 0);
    }

    @Test
    public void testMultiply() throws Exception {
        assertEquals(10d, calculator.multiply(2d, 5d), 0);
    }
}