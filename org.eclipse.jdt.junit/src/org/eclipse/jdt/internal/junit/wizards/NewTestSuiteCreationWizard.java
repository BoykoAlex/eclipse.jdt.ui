/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.junit.wizards;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.junit.ui.JUnitPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;

import org.eclipse.jdt.junit.wizards.NewTestSuiteWizardPage;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * A wizard for creating test suites.
 */
public class NewTestSuiteCreationWizard extends JUnitWizard {

	private NewTestSuiteWizardPage fPage;
	
	public NewTestSuiteCreationWizard() {
		super();
		setWindowTitle(WizardMessages.getString("Wizard.title.new.testsuite")); //$NON-NLS-1$
		initDialogSettings();
	}

	/*
	 * @see Wizard#createPages
	 */	
	public void addPages() {
		super.addPages();
		fPage= new NewTestSuiteWizardPage();
		addPage(fPage);
		fPage.init(getSelection());
	}	

	/*
	 * @see Wizard#performFinish
	 */		
	public boolean performFinish() {
		IPackageFragment pack= fPage.getPackageFragment();
		String filename= fPage.getTypeName() + ".java"; //$NON-NLS-1$
		ICompilationUnit cu= pack.getCompilationUnit(filename);
		if (cu.exists()) {
			IEditorPart cu_ep= EditorUtility.isOpenInEditor(cu);
			if (cu_ep != null && cu_ep.isDirty()) {
				boolean saveUnsavedChanges= 
					MessageDialog.openQuestion(fPage.getShell(), 
						WizardMessages.getString("NewTestSuiteWiz.unsavedchangesDialog.title"), //$NON-NLS-1$
						WizardMessages.getFormattedString("NewTestSuiteWiz.unsavedchangesDialog.message", //$NON-NLS-1$
						filename));  
				if (saveUnsavedChanges) {
					try {
						PlatformUI.getWorkbench().getProgressService().busyCursorWhile(getRunnableSave(cu_ep));
					} catch (Exception e) {
						JUnitPlugin.log(e);
					}
				}
			}
			IType suiteType= cu.getType(fPage.getTypeName());
			IMethod suiteMethod= suiteType.getMethod("suite", new String[] {}); //$NON-NLS-1$
			if (suiteMethod.exists()) {
				try {
				ISourceRange range= suiteMethod.getSourceRange();
				IBuffer buf= cu.getBuffer();
				String originalContent= buf.getText(range.getOffset(), range.getLength());
				int start= originalContent.indexOf(NewTestSuiteWizardPage.START_MARKER);
				if (start > -1) {
					int end= originalContent.indexOf(NewTestSuiteWizardPage.END_MARKER, start);
					if (end < 0) {
						cannotUpdateSuiteError();
						return false;
					}
				} else {
					cannotUpdateSuiteError();
					return false;
				}
				} catch (JavaModelException e) {
					JUnitPlugin.log(e);
					return false;
				}
			}
		}
		
		if (finishPage(fPage.getRunnable())) {
			if (!fPage.hasUpdatedExistingClass())
				postCreatingType();	
			return true;
		}

		return false;		
	}
	
	private void cannotUpdateSuiteError() {
		MessageDialog.openError(getShell(), WizardMessages.getString("NewTestSuiteWizPage.cannotUpdateDialog.title"), //$NON-NLS-1$
			WizardMessages.getFormattedString("NewTestSuiteWizPage.cannotUpdateDialog.message", new String[] { NewTestSuiteWizardPage.START_MARKER, NewTestSuiteWizardPage.END_MARKER})); //$NON-NLS-1$

	}

	protected void postCreatingType() {
		IType newClass= fPage.getCreatedType();
		if (newClass == null)
			return;
		ICompilationUnit cu= newClass.getCompilationUnit();
		IResource resource= cu.getResource();
		if (resource != null) {
			selectAndReveal(resource);
			openResource(resource);
		}
	}

	public NewTestSuiteWizardPage getPage() {
		return fPage;
	}
	
	protected void initializeDefaultPageImageDescriptor() {
		try {
			ImageDescriptor id= ImageDescriptor.createFromURL(JUnitPlugin.makeIconFileURL("wizban/newtest_wiz.gif")); //$NON-NLS-1$
			setDefaultPageImageDescriptor(id);
	} catch (MalformedURLException e) {
			// Should not happen.  Ignore.
		}
	}

	public IRunnableWithProgress getRunnableSave(final IEditorPart cu_ep) {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (monitor == null) {
						monitor= new NullProgressMonitor();
					}
					cu_ep.doSave(monitor);
			}
		};
	}
}
