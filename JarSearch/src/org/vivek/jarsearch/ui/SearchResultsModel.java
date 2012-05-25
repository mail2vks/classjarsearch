package org.vivek.jarsearch.ui;

import com.google.common.collect.Multimap;
import java.util.Map.Entry;
import java.util.*;
import javax.swing.table.AbstractTableModel;

/**
 * @author singh.kr.vivek
 * 
 * Implements FilenameFilter to include .jar and .zip files
 */
public class SearchResultsModel extends AbstractTableModel {

    private Multimap<String, String> results = JarSearchFrame.reader.getResults();
    private Set<Entry<String, Collection<String>>> entrySet = results.asMap().entrySet();
    private List<String[]> rowList = new ArrayList<String[]>();
    private static String COL_NAME_CLASS = "Class Name";
    private static String COL_NAME_JAR = "Jar/Zip File Name";

    @Override
    public int getRowCount() {
        int count = 0;
        Iterator<Entry<String, Collection<String>>> itr = entrySet.iterator();
        while (itr.hasNext()) {
            count += itr.next().getValue().size();
        }

        return count;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rowList.get(rowIndex)[columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        String value = "";

        switch (columnIndex) {
            case 0:
                value = COL_NAME_CLASS;
                break;

            case 1:
                value = COL_NAME_JAR;
                break;
        }
        return value;

    }

    public void refresh() {
        results = JarSearchFrame.reader.getResults();
        entrySet = results.asMap().entrySet();
        populateRowList(entrySet);

        this.fireTableDataChanged();
    }

    private void populateRowList(Set<Entry<String, Collection<String>>> entrySet) {
        rowList.clear();
        Iterator<Entry<String, Collection<String>>> itr = entrySet.iterator();
        while (itr.hasNext()) {
            Entry<String, Collection<String>> collectionEntry = itr.next();
            Iterator<String> vcItr = collectionEntry.getValue().iterator();
            while (vcItr.hasNext()) {
                rowList.add(new String[]{collectionEntry.getKey(), vcItr.next()});
            }
        }
    }
}
