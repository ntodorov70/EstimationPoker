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

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.*;

public abstract class GenTableModel<T> extends AbstractTableModel  {
	private static final long serialVersionUID = -3295284165740583641L;

	private TableColumnModel colModel = new DefaultTableColumnModel();
	private Vector<Object> visibleColumnIDs = new Vector();
	Map<Object,Column> knownColumns = new HashMap<Object, Column>();

    abstract public AbstractDataProvider getDataProvider();

    @Override
	public Class<?> getColumnClass(int col) {
		return getColumn(col).getValueClass();
	}

	@Override
	public int getColumnCount() {
		return visibleColumnIDs.size();
	}

	@Override
	public int getRowCount() {
		return getDataProvider().getRowCount();
	}

	@Override
	public Object getValueAt(int row, int col) {
		T entry = getEntryAtRow(row);
        return getColumn(col).readValue(entry);
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		Column column = getColumn(col);
		T entry = getEntryAtRow( row);
		column.writeValue(entry, value);
	}

    @Override
    public boolean isCellEditable(int row, int col) {
		T entry = getEntryAtRow(row);
    	return getColumn(col).isEditable(entry);
    }
    
    public T getEntryAtRow(int row) {
		return getDataProvider().getEntryAtRow(row);
	}

	/**
     * Get the Column with the given index
     * @param index
     * @return the RefColumn with the given index
     */
    public Column getColumn(int index){
    	return  knownColumns.get(visibleColumnIDs.elementAt(index));
    }
    
	
	/**
	 * Insert a new Column at the given position
	 * @param col
	 * @param index
	 */
	public void insertColumnAt(Column col, int index){
		visibleColumnIDs.insertElementAt(col.getIdentifier(),index);
		col.setModelIndex(index);
		
		knownColumns.put(col.getIdentifier(), col);
		colModel.addColumn(col);
	}
	
	/**
	 * Add a new RefColumn
	 * @param colDesc
	 */
	public void addColumn(Column colDesc){
		insertColumnAt(colDesc, visibleColumnIDs.size());
	}

    public void removeColumn(Object id){
        Column colToRemove = knownColumns.remove(id);
        removeColumnID(id);
        colModel.removeColumn(colToRemove);
    }

    void removeColumnID(Object idToRemove){
        Iterator idIter = visibleColumnIDs.iterator();
        while (idIter.hasNext()) {
            Object id =  idIter.next();
            if(idToRemove.equals(id)){
                idIter.remove();
                return;
            }
        }
    }

	public TableColumnModel getTableColumnModel() {
		return colModel;
	}
	
	public abstract class Column extends TableColumn  {
		Class valueClass;
		Object id= null;
		Object headerValue= null;
		boolean editable = false;
		String toolTip= null;
		
		public Column(Object id, String headerValue){
			this(id, headerValue, Object.class, false);
		}
		
		public Column(Object id, String headerValue, Class valueClass){
			this(id, headerValue, valueClass, false);
		}
		
		public Column(Object id, Object headerValue, Class valueClass, boolean editable){
			this.id = id;
			this.headerValue = headerValue;
			this.valueClass = valueClass;
			this.editable = editable;
			setHeaderValue(headerValue);
		}
		
		public abstract Object readValue(T modelEntry);
		
		public void writeValue(T modelEntry, Object value){
			
		}
		
		@Override
		public Object getIdentifier(){
			return id; 
		}

        public boolean isEditable(T modelEntry){
			return editable;
		}
		
		public Class<?> getValueClass() {
			return valueClass;
		}
		
		public void setToolTip(String toolTip){
			this.toolTip = toolTip;
		}
		
		public String getToolTip() {
			return toolTip;
		}
	}

    public class DataProviderList extends AbstractDataProvider {
        List<T> dataEntries= new ArrayList<T>();

        public void useData( List<T> data ) {
            dataEntries = data;
        }

        public void setData(Iterable<T> iterableData){
            Integer i = 0;
            for (T dataEntry : iterableData) {
                dataEntries.add(dataEntry);
                i++;
            }
        }

        public int getRowCount() {
            return dataEntries==null?0:dataEntries.size();
        }

        public T getEntryAtRow(int row) {
            return dataEntries.get(row);
        }
    }


    public class DataProviderMap extends AbstractDataProvider {
        Map<? extends Comparable,T> dataEntries= new HashMap();
        List<Comparable> catalogOrder2Key = new ArrayList();
        Map< Comparable, Integer> catalogKey2Order = new HashMap();

        public void useData( Map<? extends Comparable,T> data ) {
            dataEntries = data;
            updateCatalogue();
        }

        public int getRowCount() {
            return dataEntries==null?0:dataEntries.size();
        }

        public T getEntryAtRow(int row) {
            Object key = catalogOrder2Key.get(row);
            return dataEntries.get(key);
        }

        public int getRowForKey(Comparable key) {
            return catalogKey2Order.get(key);
        }

        public void updateCatalogue(){
            catalogKey2Order.clear();
            catalogOrder2Key.clear();
            // sort the keys
            catalogOrder2Key.addAll(new TreeSet( dataEntries.keySet()));

            int order = 0;
            for (Comparable key : catalogOrder2Key) {
                catalogKey2Order.put(key, new Integer(order));
                order++;
            }
        }
    }
/*
    public class DataProviderMap extends AbstractDataProvider {
        Map< Object, T > dataEntries= new HashMap();

        Map< Integer, Object> catalogOrder2Key = new TreeMap();
        Map< Object, Integer> catalogKey2Order = new HashMap();

        public void useData( Map<Object,T> data ) {
            catalogOrder2Key.clear();
            int order = 0;
            for (Map.Entry<Object, T> dataEntry : data.entrySet()) {
                catalogOrder2Key.put(order, dataEntry.getKey());
                catalogKey2Order.put(dataEntry.getKey(), order);
                order++;
            }
            dataEntries = data;
        }

        public void setData(Map<Object,T> data){
            catalogOrder2Key.clear();
            int order = 0;
            for (Map.Entry<Object, T> dataEntry : data.entrySet()) {
                dataEntries.put(dataEntry.getKey(),dataEntry.getValue());
                catalogOrder2Key.put(order, dataEntry.getKey());
                catalogKey2Order.put(dataEntry.getKey(), order);
                order++;
            }
        }

        public int getRowCount() {
            return dataEntries==null?0:dataEntries.size();
        }

        public T getEntryAtRow(int row) {
            Object key = catalogOrder2Key.get(row);
            return dataEntries.get(key);
        }

        public int getRowForKey(Object key) {
            return catalogKey2Order.get(key);
        }

        public void updateCatalogue(){

            // consider new items
            for (Map.Entry<Object, T> dataEntry : dataEntries.entrySet()) {
                Integer order = catalogKey2Order.get(dataEntry.getKey());
                if(order==null) {
                    int newOrder = catalogOrder2Key.size();
                    catalogOrder2Key.put(newOrder, dataEntry.getKey());
                    catalogKey2Order.put(dataEntry.getKey(), newOrder);
                }
            }
            // cleanup regarding removed items
            for (Map.Entry<Object, Integer> key2OrderEntry : catalogKey2Order.entrySet()) {
                Object key = key2OrderEntry.getKey();
                T value = dataEntries.get(key);
                if(value==null) {
                    // no value found for key
                    Integer order = catalogKey2Order.remove(key);
                    catalogOrder2Key.remove(order);
                }
            }
        }
    }
*/
    public abstract class AbstractDataProvider{

        abstract int getRowCount();

        abstract T getEntryAtRow(int row);
    }
}
