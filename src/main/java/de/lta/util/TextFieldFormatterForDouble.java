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
 * Created by IntelliJ IDEA.
 * User: todorov
 * Date: 28.09.2003
 * Time: 16:33:36
 * To change this template use Options | File Templates.
 */
public class TextFieldFormatterForDouble extends TextFieldFormatter {
    
	private static final long serialVersionUID = 1L;

	boolean nullValuesEnabled = false; 
    
	private DecimalFormatFixed formatter = null;

    public TextFieldFormatterForDouble(boolean nullable,int decimals) {
    	nullValuesEnabled=nullable;
        formatter = new DecimalFormatFixed(false);
        formatter.setMaximumFractionDigits(decimals);
    }

    public TextFieldFormatterForDouble() {
        this(false,2);
    }

    public String performValueToString(Object newValue) throws ParseException {
        return formatter.format(((Double)newValue).doubleValue());
    }

    public Object performStringToValue(String numberString) throws ParseException {
    	if (numberString.length()==0) {
    		if (nullValuesEnabled) {
    			return null;
    		} else {
    			return new Double(0.0d);
    		}
    	} else {
    		return new Double(formatter.parse(numberString).doubleValue());
    	}
    }
    
    /**
     * If nullValue mode is set and the editor 
     * contains empty string this formater returns null as value.
     *
     */
    public void enableNullValue(){
    	nullValuesEnabled = true;
    }
}
