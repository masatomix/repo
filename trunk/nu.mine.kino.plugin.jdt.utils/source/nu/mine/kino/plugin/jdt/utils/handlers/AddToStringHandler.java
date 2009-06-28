/******************************************************************************
 * Copyright (c) 2009 Masatomi KINO and others. 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * Contributors:
 *      Masatomi KINO - initial API and implementation
 * $Id$
 ******************************************************************************/
//�쐬��: 2009/06/27
package nu.mine.kino.plugin.jdt.utils.handlers;

import java.lang.reflect.InvocationTargetException;

import nu.mine.kino.plugin.jdt.utils.JDTUtils;
import nu.mine.kino.plugin.jdt.utils.WorkbenchRunnableAdapter;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.ui.PlatformUI;

/**
 * @author Masatomi KINO
 * @version $Revision$
 */
public class AddToStringHandler extends AbstractHandler implements IHandler {

    public Object execute(ExecutionEvent event) throws ExecutionException {
        // event����CompilationUnit���擾�B
        final ICompilationUnit unit = JDTUtils.getCompilationUnit(event);
        // unit����A�q�v�f������IJavaElement�̔z��Ƃ��Ď擾�B
        final IJavaElement[] elements = JDTUtils.unit2IJavaElements(unit);

        try {
            AddToStringThread op = new AddToStringThread(unit, elements);
            PlatformUI.getWorkbench().getProgressService().runInUI(
                    PlatformUI.getWorkbench().getProgressService(),
                    new WorkbenchRunnableAdapter(op, op.getScheduleRule()),
                    op.getScheduleRule());
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ////////////////////////////////////////////////////////////////////////////
        return null;
    }

    class AddToStringThread implements IWorkspaceRunnable {

        private final ICompilationUnit unit;

        private final IJavaElement[] javaElements;

        public AddToStringThread(ICompilationUnit unit,
                IJavaElement[] javaElements) {
            this.unit = unit;
            this.javaElements = javaElements;
        }

        public ISchedulingRule getScheduleRule() {
            return ResourcesPlugin.getWorkspace().getRoot();
        }

        public void run(IProgressMonitor monitor) throws CoreException {
            addToString(unit, javaElements, monitor);
        }
    }

    /**
     * @param unit
     * @param elements
     * @param monitor
     * @throws CoreException
     */
    private void addToString(ICompilationUnit unit, IJavaElement[] elements,
            IProgressMonitor monitor) throws CoreException {
        try {
            monitor.beginTask("toString��ǉ����܂�", 5);
            // ITextFileBufferManager�̎擾�B
            ITextFileBufferManager manager = FileBuffers
                    .getTextFileBufferManager();
            IPath path = unit.getPath();
            // �t�@�C����connect
            SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, 4);
            subMonitor.beginTask("", elements.length);
            manager.connect(path, LocationKind.IFILE, subMonitor);
            try {
                // document�擾�B
                IDocument document = manager.getTextFileBuffer(path,
                        LocationKind.IFILE).getDocument();
                IJavaProject project = unit.getJavaProject();

                // �G�f�B�b�g�p�N���X�𐶐��B
                MultiTextEdit edit = new MultiTextEdit();

                // �q�v�f�́A�p�b�P�[�W�錾��������A�N���X�������肷��B��̃\�[�X�ɕ����N���X�������Ă���ꍇ�����邵�B
                for (final IJavaElement javaElement : elements) {
                    // ���^(�N���X)��������΁AIType�ɃL���X�g���Ă����B
                    if (javaElement.getElementType() == IJavaElement.TYPE) {
                        // �z���g�̓R�R�ŁA�I�����ꂽType���������s���Ĕ��f���K�v�B
                        // �n���h������cu������������_�ŁA�ǂ�����JavaElement�����ď���ێ����Ă����Ȃ��Ɠ���ȁB
                        IType type = (IType) javaElement;
                        // ���\�b�h�ꗗ���擾�B
                        IMethod[] methods = type.getMethods();

                        IMethod lastMethod = methods[methods.length - 1];
                        String code = JDTUtils.createIndentedCode(JDTUtils
                                .createToString(type), lastMethod, document,
                                project);

                        // �I�t�Z�b�g�ʒu���v�Z����B
                        int endOffSet = JDTUtils.getMemberEndOffset(lastMethod,
                                document);

                        edit.addChild(new InsertEdit(endOffSet, code)); // �I�t�Z�b�g�ʒu�ɁA�}������B
                    }
                    subMonitor.worked(1);
                }
                edit.apply(document); // apply all edits
            } catch (BadLocationException e) {
                e.printStackTrace();
            } finally {
                manager.disconnect(path, LocationKind.IFILE, subMonitor);
                subMonitor.done();
            }
        } finally {
            monitor.worked(1);
            monitor.done();
        }
    }

}