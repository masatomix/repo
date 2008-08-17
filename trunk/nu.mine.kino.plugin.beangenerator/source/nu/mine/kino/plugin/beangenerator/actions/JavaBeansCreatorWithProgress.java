/******************************************************************************
 * Copyright (c) 2008 Masatomi KINO and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *      Masatomi KINO - initial API and implementation
 * $Id$
 ******************************************************************************/
//�쐬��: 2008/08/15
package nu.mine.kino.plugin.beangenerator.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nu.mine.kino.plugin.beangenerator.Activator;
import nu.mine.kino.plugin.beangenerator.JavaBeansCreator;
import nu.mine.kino.utils.beangenerator.sheetdata.ClassInformation;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.actions.AddGetterSetterAction;
import org.eclipse.jdt.ui.actions.FormatAllAction;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchSite;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
public class JavaBeansCreatorWithProgress implements IRunnableWithProgress {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(JavaBeansCreatorWithProgress.class);

    private IStructuredSelection ss;

    private List<ICompilationUnit> list = new ArrayList<ICompilationUnit>();

    private IWorkbenchSite site;

    public JavaBeansCreatorWithProgress(IStructuredSelection ss,
            IWorkbenchSite site) {
        this.ss = ss;
        this.site = site;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException,
            InterruptedException {
        logger.debug("run(IProgressMonitor) - start");

        int totalWork = ss.size() * 2;
        monitor.beginTask("JavaBeans�������E�E�E", totalWork);

        Iterator<IFile> e = ss.iterator();
        // �I�����ꂽExcel�t�@�C�������A�J��Ԃ��B
        while (e.hasNext()) {
            IFile file = e.next();
            // IFile ����AJavaProject�ցBResource�n����AJDT�̐��E�ւ��B
            IProject project = file.getProject();
            IJavaProject javaProject = JavaCore.create(project);
            try {
                monitor.subTask("������:" + file.getFullPath());
                monitor.worked(1);
                List<ClassInformation> classInformations = Activator
                        .getDefault().getClassInformations(file);
                // ���Excel�t�@�C������A�����̃N���X��񂪎擾�ł���̂ŁA�擾����
                // �N���X�����A�J��Ԃ��B
                for (ClassInformation classInformation : classInformations) {
                    try {
                        monitor.subTask("������:"
                                + classInformation.getClassNameJ());
                        ICompilationUnit cu = new JavaBeansCreator(javaProject)
                                .create(classInformation);

                        list.add(cu);
                    } catch (CoreException ee) {
                        // �����ƃG���[��������ƁB
                        ee.printStackTrace();
                    }
                    if (monitor.isCanceled()) {
                        throw new InterruptedException(
                                "Cancel has been requested.");
                    }
                }
            } catch (CoreException e1) {
                // �����ƃG���[��������ƁB
                e1.printStackTrace();
            }
            monitor.worked(1);
        }

        // �ȉ��A���`�����B
        Display display = Display.getDefault();
        display.syncExec(new Runnable() {
            public void run() {
                ICompilationUnit[] units = (ICompilationUnit[]) list
                        .toArray(new ICompilationUnit[list.size()]);
                // OrganizeImportsAction importsAction = new
                // OrganizeImportsAction(
                // site);
                // FormatAllAction formatAllAction = new FormatAllAction(site);
                IStructuredSelection selection = new StructuredSelection(units);
                // importsAction.run(selection);
                // formatAllAction.run(selection);

                AddGetterSetterAction getterAction = new AddGetterSetterAction(
                        site);
                getterAction.run(selection);
            }
        });
        monitor.worked(totalWork);
        monitor.done();
        logger.debug("run(IProgressMonitor) - end");
    }
}