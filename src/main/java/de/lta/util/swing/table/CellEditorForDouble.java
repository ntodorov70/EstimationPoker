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

package de.lta.util.swing.table;

import de.lta.util.TextFieldFormatterForDouble;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CellEditorForDouble extends AbstractCellEditor implements TableCellEditor, ActionListener{

	private static final long serialVersionUID = 1L;
	
	JFormattedTextField editor;

    public CellEditorForDouble(boolean nullable, int decimals) {
        editor = new JFormattedTextField(new TextFieldFormatterForDouble(nullable,decimals));
        editor.addActionListener(this);
    }
    
    public CellEditorForDouble(boolean nullable) {
    	this(nullable,2);
    }
    
    public CellEditorForDouble() {
    	this(false,2);
    }

	public boolean stopCellEditing() {
        try{
            editor.commitEdit();
            return super.stopCellEditing();
        }catch(Exception e){
            return false;
        }
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
	    editor.setValue(value);
        return editor;
	}

	public Object getCellEditorValue() {
	    return editor.getValue();
	}

    public void actionPerformed(ActionEvent e) {
        stopCellEditing();
    }
    
	/**
	 * Returns a reference to the editor component.
	 *
	 * @return the editor <code>Component</code>
	 */
	public Component getComponent() {
	return editor;
	}
}

