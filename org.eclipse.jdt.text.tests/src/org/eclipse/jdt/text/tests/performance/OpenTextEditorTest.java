/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.jdt.text.tests.performance;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.ui.PartInitException;

public class OpenTextEditorTest extends OpenEditorTest {

	public static final int N_OF_COPIES= 20;
	
	public static final String PATH= "/Eclipse SWT/win32/org/eclipse/swt/graphics/";
	
	public static final String FILE_PREFIX= "TextLayout";
	
	public static final String FILE_SUFFIX= ".txt";
	
	public static final String ORIG_FILE= PATH + FILE_PREFIX + ".java";
	
	public void testOpenTextEditor1() throws PartInitException {
		// cold run
		measureOpenInEditor(findFiles(OpenEditorTestSetup.PROJECT + PATH + FILE_PREFIX, FILE_SUFFIX, 0, N_OF_COPIES));
	}
	public void testOpenTextEditor2() throws PartInitException {
		// warm run
		measureOpenInEditor(findFiles(OpenEditorTestSetup.PROJECT + PATH + FILE_PREFIX, FILE_SUFFIX, 0, N_OF_COPIES));
	}
	
	public static void setUpWorkspace() throws IOException {
		String workspacePath= ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/";
		String textPrefix= workspacePath + OpenEditorTestSetup.PROJECT + PATH + FILE_PREFIX;
		for (int i= 0; i < N_OF_COPIES; i++)
			FileTool.copy(new File(ORIG_FILE), new File(textPrefix + i + FILE_SUFFIX));
	}
}
