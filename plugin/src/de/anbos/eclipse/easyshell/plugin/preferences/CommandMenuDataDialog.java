/*******************************************************************************
 * Copyright (c) 2014 - 2016 Andre Bossert.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Bossert - initial API and implementation and/or initial documentation
 *******************************************************************************/

package de.anbos.eclipse.easyshell.plugin.preferences;

import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.anbos.eclipse.easyshell.plugin.Activator;

public class CommandMenuDataDialog extends StatusDialog {

    private CommandMenuData data;
    private CommandDataStore cmdStore;
    private List<CommandData> cmdList;
    private boolean edit;

    private Button  enabledCheckBox;
    private Text    nameText;
    private Text    commandText;
    private CCombo  commandCombo;

    public CommandMenuDataDialog(Shell parent, CommandMenuData data, CommandDataStore cmdStore, List<CommandData> cmdList, boolean edit) {
        super(parent);
        this.data = data;
        this.cmdStore = cmdStore;
        this.cmdList = cmdList;
        this.edit = edit;
        // do layout and title
        setShellStyle(getShellStyle() | SWT.MAX);
        // set title
        String title = null;
        if(edit) {
            title = Activator.getResourceString("easyshell.menu.editor.dialog.edit.title"); //$NON-NLS-1$
        } else {
            title = Activator.getResourceString("easyshell.menu.editor.dialog.new.title"); //$NON-NLS-1$
        }
        setTitle(title);
    }

    @Override
    protected boolean isResizable() {
      return true;
    }

    public Control createDialogArea(Composite parent) {
    	Composite pageComponent = new Composite(parent,SWT.NULL);
        GridLayout layout0 = new GridLayout();
        layout0.numColumns = 1;
        layout0.makeColumnsEqualWidth = false;
        layout0.marginWidth = 5;
        layout0.marginHeight = 4;
        pageComponent.setLayout(layout0);
        GridData data0 = new GridData(GridData.FILL_HORIZONTAL);
        data0.widthHint = 640;
        pageComponent.setLayoutData(data0);
        pageComponent.setFont(parent.getFont());
    	// define group1
    	Group pageGroup1 = new Group(pageComponent, SWT.SHADOW_ETCHED_IN);
    	pageGroup1.setText(Activator.getResourceString("easyshell.menu.editor.dialog.title"));
        GridLayout layout1 = new GridLayout();
        layout1.numColumns = 2;
        layout1.makeColumnsEqualWidth = false;
        layout1.marginWidth = 5;
        layout1.marginHeight = 4;
        pageGroup1.setLayout(layout1);
        GridData data1 = new GridData(GridData.FILL_HORIZONTAL);
        pageGroup1.setLayoutData(data1);
        pageGroup1.setFont(parent.getFont());
        // create activity checkbox
        createEnabledCheckBox(pageGroup1);
        // create selected command combo
        createCommandCombo(pageGroup1);
        //create input nameText field
        commandText = createTextField(pageGroup1, Activator.getResourceString("easyshell.menu.editor.dialog.command.label"), data.getCommandData().getCommand(), false);
        //create input nameText field
        nameText = createTextField(pageGroup1, Activator.getResourceString("easyshell.menu.editor.dialog.name.label"), data.getName(), true);

        //if (edit) {
	    	// send event to refresh
	    	Event event = new Event();
			event.item = null;
			commandCombo.notifyListeners(SWT.Selection, event);
        //}

        return pageComponent;
    }

    protected void okPressed() {
        if (!validateValues()) {
            return;
        }
        data.setEnabled(enabledCheckBox.getSelection());
        data.setCommandData(cmdList.get(commandCombo.getSelectionIndex()));
        data.setName(nameText.getText());
        super.okPressed();
    }

    private boolean validateValues() {

    	final String title = Activator.getResourceString("easyshell.menu.editor.dialog.error.incompletedata.title");

    	// check type
        if ( (commandCombo.getText() == null) || (commandCombo.getText().length() <= 0)) {
        	MessageDialog.openError(getShell(), title, Activator.getResourceString("easyshell.menu.editor.dialog.error.type.text"));
        	return false;
        }

    	boolean valid = true;

        // check name
        String text  = Activator.getResourceString("easyshell.menu.editor.dialog.error.name.text");
        if ( (nameText.getText() == null) || (nameText.getText().length() <= 0)) {
            valid = false;
        }

        // show error message
        if (!valid) {
            MessageDialog.openError(getShell(), title, text);
        }
        return valid;
    }

    private void createEnabledCheckBox(Composite parent) {
        // draw label
        Label comboLabel = new Label(parent,SWT.LEFT);
        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        comboLabel.setText(Activator.getResourceString("easyshell.menu.editor.dialog.active.label")); //$NON-NLS-1$
        // draw checkbox
        enabledCheckBox = new Button(parent,SWT.CHECK);
        if(edit) {
            enabledCheckBox.setSelection(this.data.isEnabled());
        } else {
        	enabledCheckBox.setSelection(true);
        }
    }

    private String[] getAllCommandsAsComboNames(List<CommandData> list) {
        String[] arr = new String[list.size()];
        for (int i=0;i<list.size();i++) {
            CommandData dt = list.get(i);
            arr[i] = dt.getOS().getName() + " - " + dt.getCommandType().getName() + " - " + dt.getName();
        }
        return arr;
    }

    private void createCommandCombo(Composite parent) {
        // draw label
        Label comboLabel = new Label(parent,SWT.LEFT);
        comboLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        comboLabel.setText(Activator.getResourceString("easyshell.menu.editor.dialog.combo.label")); //$NON-NLS-1$
        // draw combo
        commandCombo = new CCombo(parent,SWT.BORDER);
        commandCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        commandCombo.setEditable(false);
        commandCombo.setItems(getAllCommandsAsComboNames(cmdList));
        commandCombo.select(0);
        commandCombo.addSelectionListener(new SelectionListener() {
            @Override
			public void widgetSelected(SelectionEvent e) {
                int index = commandCombo.getSelectionIndex();
				//String text = commandCombo.getItem(index);
				data.setCommandData(cmdList.get(index));
				data.setDefaultName();
				nameText.setText(data.getName());
				commandText.setText(data.getCommandData().getCommand());
			}
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub
            }
		});
        if (edit) {
            for(int i = 0 ; i < cmdList.size() ; i++) {
                if (cmdList.get(i).equals(this.data.getCommandData())) {
                    commandCombo.select(i);
                    return;
                }
            }
        } else {
            commandCombo.select(0);
        }
    }

    private Text createTextField(Composite parent, String labelText, String editValue, boolean editable) {
        // draw label
        Label label = new Label(parent,SWT.LEFT);
        label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
        label.setText(labelText);    //$NON-NLS-1$
        // draw textfield
        Text text = new Text(parent,SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        if(edit) {
            text.setText(editValue);
            text.setEditable(editable);
        }
        return text;
    }

}
