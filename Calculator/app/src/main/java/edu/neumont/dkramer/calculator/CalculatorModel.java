package edu.neumont.dkramer.calculator;

/**
 * Backing model for a calculator that keeps track of the running total
 * and provides functions for different calculation operations.
 * Created by dkramer on 4/5/17.
 */

public class CalculatorModel {

    /*
     * Operator constants
     */
    public static final String ADD      = "+";
    public static final String SUB      = "-";
    public static final String MULT     = "×";
    public static final String DIV      = "÷";
    public static final String DECIMAL  = ".";

    // the active number that is being entered
    protected String m_currentNum;

    // all the numbers and operators that have been entered
    protected String m_calcText;

    // last entered operator
    protected String m_lastOperator;

    // the current running total after performing all operations
    protected double m_runningTotal;

    // running total that is currently under evaluation
    protected double m_tempRunningTotal;

    // flag to check if current number contains a decimal point
    protected boolean m_currentNumHasDecimal;

    // flag to check if last number before the operator has been captured
    protected boolean m_didCaptureNumBeforeOperator;


    public CalculatorModel() {
        clear();
    }

    public void processToken(String token) {
        if (isValidToken(token)) {
            updateText(token);

            if (isDigit(token)) {
                processDigit(token);
            } else {
                processOperator(token);
            }
        }
    }

    protected boolean isValidToken(String token) {
        boolean isValid = !isTokenBeforeAnyNumber(token) && !isDuplicateDecimal(token);
        return isValid;
    }

    protected boolean isTokenBeforeAnyNumber(String token) {
        boolean result = (!isDigit(token) && (m_currentNum.isEmpty() && m_runningTotal == 0.0));
        return result;
    }

    protected boolean isDuplicateDecimal(String token) {
        boolean result = (token.equals(".") && m_currentNumHasDecimal);
        return result;
    }

    protected void updateText(String token) {
        m_calcText += token;
    }

    public void clear() {
        m_currentNum = "";
        m_calcText = "";
        m_lastOperator = "";
        m_runningTotal = 0.0;
        m_tempRunningTotal = 0.0;
        m_currentNumHasDecimal = false;
        m_didCaptureNumBeforeOperator = false;
    }

    protected void processOperator(String op) {
        switch (op) {
            case DECIMAL:
                checkDecimal();
                break;
            case ADD:
            case SUB:
            case MULT:
            case DIV:
                m_lastOperator = op;
                checkNumCapture();
                break;
            default:
                throw new IllegalArgumentException("Invalid Operator: [" + op + "]");
        }
    }

    protected void processDigit(String digit) {
        m_didCaptureNumBeforeOperator = false;
        m_currentNum += digit;

        // perform evaluation on the last operator
        evaluate();
    }

    protected void evaluate() {
        if (m_calcText.isEmpty()) { return; }
        double num = Double.parseDouble(m_currentNum);
        evaluate(num);
    }

    protected void evaluate(double num) {
        switch(m_lastOperator) {
            case ADD:
                m_tempRunningTotal = (m_runningTotal + num);
                break;
            case SUB:
                m_tempRunningTotal = (m_runningTotal - num);
                break;
            case MULT:
                m_tempRunningTotal = (m_runningTotal * num);
                break;
            case DIV:
                m_tempRunningTotal = (m_runningTotal / num);
                break;

            // still entering the first number
            default:
                m_tempRunningTotal = num;
                break;
        }
    }

    public String getRunningTotalDisplay() {
        String result = "" + m_tempRunningTotal;
        return result;
    }

    public double getRunningTotal() {
        return m_tempRunningTotal;
    }

    public double getResult() {
        return m_runningTotal;
    }

    public String getCalcText() {
        return m_calcText;
    }

    protected void checkNumCapture() {
        if (m_currentNum.isEmpty() && m_runningTotal == 0.0) { return; }

        if (!m_didCaptureNumBeforeOperator) {
            double num = Double.parseDouble(m_currentNum);

            m_runningTotal = m_tempRunningTotal;
            m_didCaptureNumBeforeOperator = true;

            // clear out current num, now that we've captured it
            m_currentNum = "";

            // reset decimal flag, since we're on to another number
            m_currentNumHasDecimal = false;
        }
    }


    protected boolean isDigit(String s) {
        boolean isDigit = s.matches("\\d+");
        return isDigit;
    }

    protected boolean checkDecimal() {
        boolean didAdd = false;

        if (!m_currentNumHasDecimal) {
            m_currentNum += ".";
            m_currentNumHasDecimal = true;
            didAdd = true;
        }
        return didAdd;
    }
}
