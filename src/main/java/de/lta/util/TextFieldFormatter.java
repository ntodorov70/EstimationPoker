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

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.ParseException;

/**
 * This class handels the formatting and parsing of numbers
 * in colaboration with JFormatedTextField.
 *
 * User: todorov
 * Date: 26.09.2003
 * Time: 10:17:11
 */
public abstract class TextFieldFormatter extends JFormattedTextField.AbstractFormatter {

    protected static final Border origBorder = new JFormattedTextField().getBorder();

    public void install(JFormattedTextField ftf) {
        ftf.setFocusLostBehavior(JFormattedTextField.COMMIT);
        ftf.setHorizontalAlignment(JTextField.RIGHT);
        super.install(ftf);
    }

    public Object stringToValue(String numberString) throws ParseException {
        getFormattedTextField().setBorder(origBorder);
        try {
            return performStringToValue(numberString.trim());
            //this.getFormattedTextField().setText(valueToString(value));
        }catch (Exception e) {
            //setEditValid(false);
            getFormattedTextField().setBorder(new LineBorder(Color.red));
            throw new ParseException( numberString ,0);
        }
    }

    public String valueToString(Object newValue) throws ParseException{
        if(newValue == null) return "";

        getFormattedTextField().setBorder(origBorder);
	    try {
            return performValueToString(newValue);
	    }
	    catch (Exception e) {
            getFormattedTextField().setBorder(new LineBorder(Color.red));
            throw new ParseException( newValue.toString() ,0);
	    }
    }

    public abstract String performValueToString(Object newValue) throws ParseException;
    public abstract Object performStringToValue(String numberString) throws ParseException ;
}
