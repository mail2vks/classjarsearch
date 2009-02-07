package com.vivek.swt;

import java.util.Collection;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Multimap;
import com.vivek.file.DirectoryReader;
import com.vivek.file.JarFilter;
import com.vivek.utils.AppUtils;

public class WindowObserver implements Observer
{

    Observable observable;

    /**
     * Height for main window
     */
    private static final int MAIN_WINDOW_HEIGHT = 400;

    /**
     * Width of main window
     */
    private static final int MAIN_WINDOW_WIDTH = 600;

    /**
     * Text box to enter search criteria
     */
    private Text searchText;

    /**
     * Text box to store directory name where to perform search
     */
    private Text dirText;

    private Button browseButton;

    private Button searchButton;

    private Shell shell;

    private Text statusText;

    private Table table;

    private DirectoryReader reader;

    /**
     * @param value the observable to set
     */
    public void setObservable(Observable value)
    {
        this.observable = value;
    }

    /**
     * @param args
     */
    public void draw()
    {
        Display display = new Display();

        // Configure shell
        shell = new Shell(display, SWT.CLOSE);
        shell.setText("Search Jars in class files");
        shell.setSize(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        shell.setImage(new Image(display, AppUtils
            .loadFromClassPath("jar.ico")));

        createTextBox(shell);

        createButtons(shell);

        shell.open();

        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        display.dispose();
    }

    /**
     * Helper method to create text box attached with shell provided
     * 
     * @param shell - the shell to which add textbox to
     */
    private void createTextBox(Shell shell)
    {
        // Text box to enter search criteria
        searchText = new Text(shell, SWT.SINGLE | SWT.BORDER);
        searchText.setBounds(10, 10, MAIN_WINDOW_WIDTH - 105, 20);

        // Text box to store directory name where to perform search
        dirText = new Text(shell, SWT.SINGLE | SWT.BORDER);
        dirText.setBounds(10, 40, MAIN_WINDOW_WIDTH - 105, 20);

        statusText = new Text(shell, SWT.SINGLE | SWT.READ_ONLY);
        statusText.setBounds(10, MAIN_WINDOW_HEIGHT - 50,
            MAIN_WINDOW_WIDTH - 10, 20);

    }

    /**
     * @param shell - the shell to which ass buttons to
     */
    private void createButtons(final Shell shell)
    {
        final WindowObserver windowObserver = this;

        // Button selected to start search
        searchButton = new Button(shell, SWT.PUSH);
        searchButton.setBounds(MAIN_WINDOW_WIDTH - 90, 10, 80, 20);
        searchButton.setText("Start Search");
        searchButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                if (reader == null)
                {
                    reader = new DirectoryReader();
                    reader.addObserver(windowObserver);
                    reader.setStatusText(statusText);
                    windowObserver.setObservable(reader);
                }
                disableButtons();
                reader.search(new JarFilter(), searchText.getText(),
                    dirText.getText());
            }
        });

        // Button selected to Browse for directory
        browseButton = new Button(shell, SWT.PUSH);
        browseButton.setBounds(MAIN_WINDOW_WIDTH - 90, 40, 80, 20);
        browseButton.setText("Browse");
        browseButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                DirectoryDialog dlg = new DirectoryDialog(shell);
                dlg.setFilterPath(dirText.getText());
                dlg.setText("Select a Directory");
                dlg.setMessage("Select a Directory");

                String dir = dlg.open();
                if (dir != null)
                {
                    dirText.setText(dir);
                }
            }
        });

    }

    private Table createTable(Shell shell,
        Multimap<String, String> results)
    {
        if (table == null)
        {
            table = new Table(shell, SWT.MULTI
                                     | SWT.FULL_SELECTION
                                     | SWT.BORDER);
            table.setBounds(10, 70, MAIN_WINDOW_WIDTH - 25,
                MAIN_WINDOW_HEIGHT - 125);

            table.setHeaderVisible(true);
            table.setLinesVisible(true);

            TableColumn className = new TableColumn(table, SWT.LEFT);
            className.setText("ClassName");
            className.setWidth( (MAIN_WINDOW_WIDTH - 30) / 2);

            TableColumn location = new TableColumn(table, SWT.LEFT);
            location.setText("Location");
            location.setWidth( (MAIN_WINDOW_WIDTH - 30) / 2);
        }

        Collection<Entry<String, String>> resultCollection = results
            .entries();

        Iterator<Entry<String, String>> iterator = resultCollection
            .iterator();

        while (iterator.hasNext())
        {
            Entry<String, String> entry = iterator.next();
            TableItem item = new TableItem(table, SWT.NONE);
            item.setText(0, entry.getKey());
            item.setText(1, entry.getValue());
            table.update();
        }

        return table;
    }

    public void update(Observable o, Object arg)
    {
        if (o instanceof DirectoryReader)
        {
            DirectoryReader reader = (DirectoryReader) o;
            Multimap<String, String> results = reader.getResults();
            if (results.size() > 0)
            {
                createTable(shell, results);
            }
            enableButtons();
        }
    }

    private void enableButtons()
    {
        searchButton.setEnabled(true);
        browseButton.setEnabled(true);
    }

    private void disableButtons()
    {
        if (table != null)
        {
            table.removeAll();
            table.update();
        }
        searchButton.setEnabled(false);
        browseButton.setEnabled(false);
    }

    public static void main(String[] args)
    {
        WindowObserver window = new WindowObserver();
        window.draw();
    }

}
