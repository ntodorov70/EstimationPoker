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
package de.lta.estp;

import de.lta.util.swing.table.CellEditorForDouble;
import de.lta.util.swing.table.GenCellRenderer;
import de.lta.util.swing.table.GenTable;

import java.text.NumberFormat;

/**
 * Created by todorov on 09.11.2014.
 */
public class MainTable extends GenTable {


    @Override
    public void setDefaultRenderer() {
        super.setDefaultRenderer();
        GenCellRenderer renderer = (GenCellRenderer) this.getDefaultRenderer(Double.class);
        NumberFormat format = renderer.getFloatFormatter();
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);

    }

    @Override
    public void setDefaultEditors() {
        super.setDefaultEditors();
        setDefaultEditor(Double.class, new CellEditorForDouble( true , 2) );
    }
}
