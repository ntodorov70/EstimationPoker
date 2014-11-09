/*
 * Copyright 2013 Nikolay Todorov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.lta.util;

import java.text.*;
import java.util.Currency;
import java.util.Locale;

/**
 * The DecimaFormat class parses numbers even if there are
 * letters between the digits. Unfortunately the result
 * is not acceptable for me. So I will throw an exception in such case.
 *
 * User: todorov
 * Date: 28.09.2003
 * Time: 15:41:00
 * To change this template use Options | File Templates.
 */
public class DecimalFormatFixed extends NumberFormat{
    private static char GROUPING_SEPARATOR;
    private static char DECIMAL_SEPARATOR;
    private static char MINUS;
    private static DecimalFormatSymbols symbols;
    private NumberFormat formatter;

    public DecimalFormatFixed(boolean parseIntegersOnly){
        if(symbols == null){
            symbols = new DecimalFormatSymbols(Locale.getDefault());
            GROUPING_SEPARATOR = symbols.getGroupingSeparator();
            DECIMAL_SEPARATOR  = symbols.getDecimalSeparator();
            MINUS             = symbols.getMinusSign();
        }
        if(! parseIntegersOnly ){
            formatter = NumberFormat.getNumberInstance(Locale.getDefault());
            formatter.setParseIntegerOnly(parseIntegersOnly);
            // We would use here a CurrencyFormatter, but we dont need the Currency symbol on the end.
            // So we have to set our Number Formatter like a CurrencyFormatter
            // The next section is copied from the DecimalFormat.adjustForCurrencyDefaultFractionDigits()
            Currency currency = null;
            try{
            	currency = Currency.getInstance(Locale.getDefault());
            }catch(Throwable e){
            	// in SG the line above explodes with 
            	//java.lang.IllegalArgumentException
            	//at java.util.Currency.getInstance(Currency.java:242)
            	//at de.sulzer.configurator.datamodeler.format.DecimalFormatFixed.<createColumns>(DecimalFormatFixed.java:41)
            }
            if (currency != null) {
                formatter.setMinimumFractionDigits(currency.getDefaultFractionDigits());
                formatter.setMaximumFractionDigits(8); //to be able for double 
                //formatter.setMaximumFractionDigits(currency.getDefaultFractionDigits());
//                  int digits = currency.getDefaultFractionDigits();
//                  if (digits != -1) {
//                      int oldMinDigits = formatter.getMinimumFractionDigits();
//                      // Common patterns are "#.##", "#.00", "#".
//                      // Try to adjust all of them in a reasonable way.
//                      if (oldMinDigits == formatter.getMaximumFractionDigits()) {
//                          formatter.setMinimumFractionDigits(digits);
//                          formatter.setMaximumFractionDigits(digits);
//                      } else {
//                          formatter.setMinimumFractionDigits(Math.min(digits, oldMinDigits));
//                          formatter.setMaximumFractionDigits(digits);
//                      }
//                  }
            }
        }else{
            formatter = NumberFormat.getIntegerInstance(Locale.getDefault());
        }
    }

    public Number parse(String source) throws ParseException{
        numberFormaterParseFix(source);
        return formatter.parse(source);
    }

    /**
     * The number format class parses numbers even if there are
     * letters between the digits. Unfortunately is the result not acceptable
     * for me. So I will throw an exception in such case.
     * @param number
     */
    void numberFormaterParseFix(String number) throws ParseException{
        if(!isDigitOrDoubleFormatSymbol(number.charAt(0))){
            // The first char is not digit.
            // The decimal format will throw an exception.
            // So we do nothing!!!
            return;
        }
        // check if there are digits prefixed with no digit characters
        boolean digitFounded = false;
        for(int i=number.length()-1;i>=0;i--){
            char ch = number.charAt(i);
            boolean currentChIsdigit;
            if(this.isParseIntegerOnly()){
                currentChIsdigit = isDigitOrIntegerFormatSymbol(ch);
            }else{
                currentChIsdigit = isDigitOrDoubleFormatSymbol(ch);
            }
            if(/*digitFounded && !*/!currentChIsdigit)
                throw new ParseException(number,i);
            digitFounded = currentChIsdigit;
        }
    }

    public boolean isDigitOrDoubleFormatSymbol(char ch){
        return isDigitOrIntegerFormatSymbol(ch) ||
                ch == DECIMAL_SEPARATOR;
    }

    public boolean isDigitOrIntegerFormatSymbol(char ch){
        return (ch>=0 && ch<=9) || Character.isDigit(ch) ||
                ch == GROUPING_SEPARATOR ||
                ch == MINUS;
    }

    public Number parse(String source, ParsePosition parsePosition) {
        return formatter.parse(source, parsePosition);
    }

    /**
     * The formatting works well. I dont change it.
     * @param number
     * @param toAppendTo
     * @param pos
     * @return
     */
    public StringBuffer format(double number,StringBuffer toAppendTo,FieldPosition pos) {
        return formatter.format(number, toAppendTo,pos);
    }

    public StringBuffer format(long number,StringBuffer toAppendTo,FieldPosition pos) {
        return formatter.format(number, toAppendTo, pos);
    }

}
