/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
package org.eclipse.jdt.internal.core.refactoring.changes;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ISourceManipulation;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.refactoring.Assert;
import org.eclipse.jdt.internal.core.refactoring.base.Change;
import org.eclipse.jdt.internal.core.refactoring.base.ChangeAbortException;
import org.eclipse.jdt.internal.core.refactoring.base.ChangeContext;
import org.eclipse.jdt.internal.core.refactoring.base.IChange;

public class DeleteSourceManipulationChange extends AbstractDeleteChange {

	private String fHandle;;
	
	public DeleteSourceManipulationChange(ISourceManipulation sm){
		Assert.isNotNull(sm);
		fHandle= getJavaElement(sm).getHandleIdentifier();
	}

	/**
	 * @see IChange#getName()
	 */
	public String getName() {
		return "Delete";
	}

	/**
	 * @see IChange#getCorrespondingJavaElement()
	 */
	public IJavaElement getCorrespondingJavaElement() {
		return JavaCore.create(fHandle);
	}
	
	/**
	 * @see DeleteChange#doDelete(IProgressMonitor)
	 */
	protected void doDelete(IProgressMonitor pm) throws JavaModelException{
		//cast safe
		((ISourceManipulation)getCorrespondingJavaElement()).delete(false, pm);	
	}
		
	private static IJavaElement getJavaElement(ISourceManipulation sm){
		//all known ISourceManipulations are IJavaElements
		return (IJavaElement)sm;
	}
}

