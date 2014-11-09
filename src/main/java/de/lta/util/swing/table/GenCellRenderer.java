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

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class GenCellRenderer implements TableCellRenderer{
    private static SimpleDateFormat dateFormatter = (SimpleDateFormat) SimpleDateFormat.getDateInstance(DateFormat.MEDIUM);
	private static NumberFormat floatFormatter = NumberFormat.getNumberInstance();
	private static NumberFormat integerFormatter = NumberFormat.getIntegerInstance();
	
	protected DefaultTableCellRenderer defaultRenderer;
	//the default boolean checkbox renderer
	protected JCheckBox booleanRenderer;
	
	public static Color selectionBackground = null;
    public static Color evenRowBackground = Color.white;
   	public static Color oddRowBackground  = new Color(245, 245, 245);
   	
	public GenCellRenderer(){
		defaultRenderer = new DefaultTableCellRenderer();
		booleanRenderer = new JCheckBox();
	}
	
	protected Color getBackground(JTable table, int row, int column, boolean isSelected) {
		if(selectionBackground==null) selectionBackground = table.getSelectionBackground();
		return isSelected ?
				selectionBackground
					: row % 2 == 0 ? 
							evenRowBackground 
							: oddRowBackground;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus, int row, int column) {
		TableModel refTableModel = (TableModel)table.getModel();
		TableColumn col = table.getColumnModel().getColumn(column);

		JComponent renderer = (JComponent) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		renderer = prepareRenderer(value, renderer);
		
		Color c = getBackground(table, row, column, isSelected);

		if (!isSelected && !refTableModel.isCellEditable(row,column)) {
			c = darker(c);
		}
		
		if(c!=null)
			renderer.setBackground(c);

		return renderer;
	}
	
	protected JComponent prepareRenderer(Object value, JComponent renderer) {
		if(renderer instanceof JLabel){
			((JLabel)renderer).setIcon(null);
		}
		// String 
		if (value instanceof String) {
			((JLabel)renderer).setHorizontalAlignment(JLabel.LEFT);
		}
		// Integer 
		else if (value instanceof  Integer) {
			((JLabel)renderer).setHorizontalAlignment(JLabel.RIGHT);
			((JLabel)renderer).setText(integerFormatter.format((Integer) value));
		}
		// Double
		else if (value instanceof Double) {
			((JLabel)renderer).setHorizontalAlignment(JLabel.RIGHT);
			((JLabel)renderer).setText(floatFormatter.format((Double) value));
		}		
		// Date
		else if (value instanceof Date) {
			((JLabel)renderer).setHorizontalAlignment(JLabel.CENTER);
			((JLabel)renderer).setText(dateFormatter.format((Date) value));
		}
		// Boolean
		else if (value instanceof Boolean) {
			booleanRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			booleanRenderer.setSelected(((Boolean) value).booleanValue());
			renderer = booleanRenderer;
		}
		// ImageIcon
		else if (value instanceof ImageIcon){
			((JLabel)renderer).setHorizontalAlignment(JLabel.CENTER);
			((JLabel)renderer).setIcon((ImageIcon) value);
			((JLabel)renderer).setText(null);
		}
		return renderer;
	}

    public static SimpleDateFormat getDateFormatter() {
        return dateFormatter;
    }

    public static void setDateFormatter(SimpleDateFormat dateFormatter) {
        GenCellRenderer.dateFormatter = dateFormatter;
    }

    public static NumberFormat getFloatFormatter() {
        return floatFormatter;
    }

    public static void setFloatFormatter(NumberFormat floatFormatter) {
        GenCellRenderer.floatFormatter = floatFormatter;
    }

    /**
	 * 
	 */
	public static void setDefaultRender(JTable table) {
		GenCellRenderer renderer = new GenCellRenderer();
		table.setDefaultRenderer(Object.class, renderer);
        table.setDefaultRenderer(Double.class, renderer);
	}
	
    public static Color darker(Color c) {
    	double factor = 0.98;
    	return new Color(
    		Math.max((int)(c.getRed()   * factor), 0), 
			Math.max((int)(c.getGreen() * factor), 0),
			Math.max((int)(c.getBlue()  * factor), 0));
    }
}
