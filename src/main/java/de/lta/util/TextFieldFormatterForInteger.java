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

import java.text.ParseException;

/**
 * User: todorov
 * Date: 28.09.2003
 * Time: 16:38:47
  */
public class TextFieldFormatterForInteger extends TextFieldFormatter {

	private static final long serialVersionUID = 1L;
	
	private DecimalFormatFixed formatter = null;
	boolean nullValuesEnabled = false; 
    
    public TextFieldFormatterForInteger(boolean nullable) {
    	nullValuesEnabled = nullable;
        formatter = new DecimalFormatFixed(true);
    }

    public TextFieldFormatterForInteger() {
    	this(false);
    }
    
    public String performValueToString(Object newValue) throws ParseException {
    	String result = "";
    	if (newValue!=null) {
    		result = formatter.format(((Integer)newValue).longValue());
    	} 
    	return result;
    }

    public Object performStringToValue(String numberString) throws ParseException {
    	if (numberString!=null && numberString.trim().length()>0) {
    		return new Integer(formatter.parse(numberString).intValue());
    	} else {
    		if (nullValuesEnabled) {
    			return null;
    		} else {
    			return new Integer(0);
    		}
    	}
    }
}
