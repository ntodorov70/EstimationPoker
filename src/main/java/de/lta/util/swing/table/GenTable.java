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
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.*;

public class GenTable extends JTable {
	Set<String> columnIdentifiersToBeFiltered = null;
	JPopupMenu popupMenu = new JPopupMenu(); 

	public GenTable(){
        setDefaultRenderer();
        setDefaultEditors();
		setComponentPopupMenu(popupMenu);
	}

    @Override
    public void tableChanged(TableModelEvent e) {
        RowSorter sorter = getRowSorter();
        if(sorter!=null)
            sorter.allRowsChanged();
        super.tableChanged(e);
    }

    @Override
	public void setModel(TableModel dataModel) {
		super.setModel(dataModel);
		if(dataModel instanceof GenTableModel)
			super.setColumnModel(((GenTableModel)dataModel).getTableColumnModel());
		setRowSorter(new TableRowSorter(dataModel));
	}
	
	protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(this.getColumnModel()) {
            public String getToolTipText(MouseEvent e) {
            	if(getModel() instanceof GenTableModel){
	            	GenTableModel model= (GenTableModel) getModel();
	                java.awt.Point p = e.getPoint();
	                int index = columnModel.getColumnIndexAtX(p.x);
	                int realIndex = columnModel.getColumn(index).getModelIndex();
	                GenTableModel.Column column = model.getColumn(realIndex);
	                return column.getToolTip();
            	}
            	return super.getToolTipText();
            }
        };
	}
	
	public List getSelectedEntries(){
		if(getModel() instanceof GenTableModel){
			GenTableModel genModel = (GenTableModel) getModel();

			int selected[] = super.getSelectedRows();
			ArrayList selectedObjects = new ArrayList(selected.length);
		
			for(int i = 0; i < selected.length;i++){
				selectedObjects.add( genModel.getEntryAtRow(convertRowIndexToModel(selected[i])));
			}
			
			return selectedObjects;
		}
		return null;
	}

    public void setDefaultRenderer() {
        GenCellRenderer.setDefaultRender(this);
    }

	public void setDefaultEditors() {
		setDefaultEditor(Double.class, new CellEditorForDouble());
		setDefaultEditor(String.class, 
		 		new javax.swing.DefaultCellEditor( new javax.swing.JTextField()) {
					{this.clickCountToStart = 1;}
				}
		);
		setDefaultEditor(Boolean.class, 
				new javax.swing.DefaultCellEditor( 
					new javax.swing.JCheckBox(){
						{	
							this.setHorizontalAlignment(SwingConstants.CENTER);
						}
					}
				) 
		);
	}
	
	public void setFilterableColumns(String ... columnIdentifiers){
		if(columnIdentifiers.length > 0)
			columnIdentifiersToBeFiltered = new HashSet(Arrays.asList(columnIdentifiers));
		else
			columnIdentifiersToBeFiltered = null;
	}
	
	/**
	 * filter the content applying the regex on columns with the given IDs 
	 */
	public void filterTableContent(String regEx) {
		RowFilter<Object,Object> rf = null; 
		try {
			List<RowFilter<Object,Object>> filters = new ArrayList<RowFilter<Object,Object>>(5);
			
			//int colIndex = 0;
			Enumeration<TableColumn> columns = this.getColumnModel().getColumns();
			while (columns.hasMoreElements()) {
				TableColumn tableColumn = (TableColumn) columns.nextElement();
				if(columnIdentifiersToBeFiltered==null || columnIdentifiersToBeFiltered.contains(tableColumn.getIdentifier())){
					filters.add(RowFilter.regexFilter(regEx, tableColumn.getModelIndex()));
				}
			}
			
			rf = RowFilter.orFilter(filters);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		((DefaultRowSorter) getRowSorter()).setRowFilter(rf);
	}
	
	public JPopupMenu getPopupMenu(){
		return popupMenu;
	}
	
	public void enhancedPrint(){
		try {
			MessageFormat header = new MessageFormat("" + new Date().toString());
			MessageFormat footer = new MessageFormat("Page {0,number}");
			super.print(PrintMode.FIT_WIDTH, header, footer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
