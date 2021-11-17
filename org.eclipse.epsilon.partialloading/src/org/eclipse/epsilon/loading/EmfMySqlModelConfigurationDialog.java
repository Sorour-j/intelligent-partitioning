/*******************************************************************************
 * Copyright (c) 2008-2012 The University of York, Antonio García-Domínguez.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 *     Antonio García-Domínguez - allow loading more than one metamodel
 ******************************************************************************/
package org.eclipse.epsilon.loading;

import org.eclipse.epsilon.common.dt.launching.dialogs.AbstractModelConfigurationDialog;
import org.eclipse.epsilon.emc.emfmysql.EmfMySqlModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class EmfMySqlModelConfigurationDialog extends AbstractModelConfigurationDialog {

	@Override
	protected String getModelName() {
		return "EmfSqlModel model";
	}

	@Override
	protected String getModelType() {
		// TODO Auto-generated method stub
		return "EmfSql";
	}

	protected Text databaseText;
	protected Text metamodelNsuri;
	protected Text serverText;	
	protected Text portText;	
	protected Text usernameText;	
	protected Text passwordText;
	protected Button readonlyButton;
	protected Button streamedResultsButton;
	
	protected void createGroups(Composite control) {
		super.createGroups(control);
		createFilesGroup(control);
	}
	
	protected Composite createFilesGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "Database", 2);
		
		serverText = createLabeledText(groupContent, "Server");
		metamodelNsuri = createLabeledText(groupContent, "metamodelNsuri");
		portText = createLabeledText(groupContent, "Port");
		databaseText = createLabeledText(groupContent, "Database");
		usernameText = createLabeledText(groupContent, "Username");
		passwordText = createLabeledText(groupContent, "Password", SWT.BORDER | SWT.PASSWORD);
		readonlyButton = createLabeledButton(groupContent, "Read-only", SWT.CHECK);
		//only allow read-only connections for now
		readonlyButton.setEnabled(false);
		readonlyButton.setSelection(true);
		streamedResultsButton = createLabeledButton(groupContent, "Stream results", SWT.CHECK);
		//only allow streamed connections for now
		streamedResultsButton.setEnabled(false);
		streamedResultsButton.setSelection(true);
		
		final Label modelFileLabel = new Label(groupContent, SWT.NONE);
		modelFileLabel.setText("Metamodel NSURI: ");
		
//		metamodelUri = new Text(groupContent, SWT.BORDER);
//		metamodelUri.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

//		final Button browseModelFile = new Button(groupContent, SWT.NONE);
//		browseModelFile.setText("Browse Workspace...");
//		browseModelFile.addListener(SWT.Selection,
//			new BrowseWorkspaceForModelsListener(metamodelUri, "Ecore files in the workspace", "Select an ecore file") {
//		});
//
//		
		groupContent.layout();
		groupContent.pack();
		return groupContent;
	}
	
	protected Button createLabeledButton(Composite parent, String label, int style) {
		Label controlLabel = new Label(parent, SWT.NONE);
		controlLabel.setText(label + ": ");
		Button button = new Button(parent, style);
		return button;
	}
	
	protected Text createLabeledText(Composite parent, String label, int style) {
		Label controlLabel = new Label(parent, SWT.NONE);
		controlLabel.setText(label + ": ");
		Text text = new Text(parent, style);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}
	
	protected Text createLabeledText(Composite parent, String label) {
		return createLabeledText(parent, label, SWT.BORDER);
	}
	
	protected void loadProperties(){
		super.loadProperties();
		
		if (properties == null) { 
			serverText.setText("localhost");
			portText.setText("3306");
			readonlyButton.setSelection(true);
			return;
		}
		
		databaseText.setText(properties.getProperty(EmfMySqlModel.PROPERTY_DATABASE));
		metamodelNsuri.setText(properties.getProperty(EmfMySqlModel.PROPERTY_METAMODELNSURI));
		serverText.setText(properties.getProperty(EmfMySqlModel.PROPERTY_SERVER));
		portText.setText(properties.getProperty(EmfMySqlModel.PROPERTY_PORT));
		usernameText.setText(properties.getProperty(EmfMySqlModel.PROPERTY_USERNAME));
		passwordText.setText(properties.getProperty(EmfMySqlModel.PROPERTY_PASSWORD));
		readonlyButton.setSelection(properties.getBooleanProperty(EmfMySqlModel.PROPERTY_READONLY, true));
		streamedResultsButton.setSelection(properties.getBooleanProperty(EmfMySqlModel.PROPERTY_STREAMRESULTS, true));
	}
	
	protected void storeProperties(){
		super.storeProperties();
		properties.put(EmfMySqlModel.PROPERTY_DATABASE, databaseText.getText());
		properties.put(EmfMySqlModel.PROPERTY_METAMODELNSURI, metamodelNsuri.getText());
		properties.put(EmfMySqlModel.PROPERTY_SERVER, serverText.getText());
		properties.put(EmfMySqlModel.PROPERTY_PORT, portText.getText());
		properties.put(EmfMySqlModel.PROPERTY_USERNAME, usernameText.getText());
		properties.put(EmfMySqlModel.PROPERTY_PASSWORD, passwordText.getText());
		properties.put(EmfMySqlModel.PROPERTY_READONLY, readonlyButton.getSelection() + "");
		properties.put(EmfMySqlModel.PROPERTY_STREAMRESULTS, streamedResultsButton.getSelection() + "");
	}
}
