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

import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.ParseException;
import java.util.Date;


/**
 * Formatter for Date values
 * User: RB
 * Date: 28.01.2007
 * Time: 16:33:36
 */
public class TextFieldFormatterForDate extends TextFieldFormatter {
    
	private static final long serialVersionUID = 1L;
	String dateFormat = "DD.MM.YY";
	boolean nullValuesEnabled = true; 

	/**
	 * Constructor
	 */
    public TextFieldFormatterForDate() {
    }
    
    /**
     * Returns a String representation of a Date localized by settings 
     * @param d the Date value
     * @return a String representation of a Date localized by settings
     */
    public String performValueToString(Object value) throws ParseException {
        return value.toString();//format((Date)value);
    }

    /**
     * Creates a Date by parsing a localized Date String
     */
    public Object performStringToValue(String numberString) throws ParseException {
        try {  
        	String[] parts=getFormattedTextField().getText().trim().replace(".", ",").replace("/",",").split(",");
        	if (parts.length!=3) {
        		throw new Exception("invalid date");
        	}

            String[] fmts = dateFormat.toUpperCase().replace(".", ",").replace("/",",").split(",");
            int d = 0;
            int m = 0;
            int y = 0;
            for (int i=0;i<fmts.length;i++) {
            	if (fmts[i].startsWith("D")) {
            		d=i;
            	} else if (fmts[i].startsWith("M")) {
            		m=i;
            	} else if (fmts[i].startsWith("Y")) {
            		y=i;
            	}
            }
            Date value = new Date(Integer.parseInt(parts[y]) + 100,Integer.parseInt(parts[m])-1,Integer.parseInt(parts[d]), 12, 0);
            return value;
        } catch(Exception e) {
            //setEditValid(false);
            getFormattedTextField().setBorder(new LineBorder(Color.red));
            throw new ParseException( numberString ,0);
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
