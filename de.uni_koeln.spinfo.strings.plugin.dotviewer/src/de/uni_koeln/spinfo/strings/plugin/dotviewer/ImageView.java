/*******************************************************************************
 * Copyright (c) 2005 The Eclipse Foundation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    The Eclipse Foundation - initial API and implementation
 *******************************************************************************/
package de.uni_koeln.spinfo.strings.plugin.dotviewer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;


public class ImageView extends ViewPart {
    ImageViewer viewer;

    Image image;

    /*
     * The selectionListener listens for changes in the workbench's selection
     * service.
     */
    private ISelectionListener selectionListener = new ISelectionListener() {
        public void selectionChanged(IWorkbenchPart part, ISelection selection) {
            System.out.println("Selection: " + selection);
            if (selection instanceof IStructuredSelection) {
                selectionChanged((IStructuredSelection) selection);
            }
        }

        private void selectionChanged(IStructuredSelection selection) {
            if (selection.size() == 0)
                return;
            Object first = selection.getFirstElement();
            if (first instanceof IFile) {
                selectionChanged((IFile) first);
            }
        }

        private void selectionChanged(IFile file) {
            setImage(file);
        }

    };

    public void createPartControl(Composite parent) {
        parent.setLayout(new FillLayout());
        viewer = new ImageViewer(parent, SWT.NONE);

        getSelectionService().addSelectionListener(selectionListener);
    }

    protected void setImage(IFile file) {
        InputStream in = null;
        try {
            if (file.getFileExtension().equals("dot")) {
                file = generateImageFromDot(file);
            }
            in = file.getContents();
            Image newImage = new Image(Display.getDefault(), in);
            viewer.setImage(newImage);
            disposeImage();
            image = newImage;
        } catch (Exception e) {
            // If there's an exception, do nothing. Life goes on...
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private IFile generateImageFromDot(IFile file) {
        String inFolder = file.getParent().getLocation().toPortableString();
        String outFolder = file.getParent().getLocation().toPortableString();
        System.out.println("in: " + inFolder);
        System.out.println("out: " + outFolder);
        DotDrawer drawer = new DotDrawer(inFolder, outFolder, file.getName(),
                "result.png");
        drawer.initPaths(this.getViewSite().getShell());
        try {
            drawer.renderImage();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        IFile res = (IFile) file.getParent().findMember(new Path("result.png"));
        try {
            res.refreshLocal(IResource.DEPTH_ZERO, null);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return res;
    }

    public void dispose() {
        super.dispose();
        getSelectionService().removeSelectionListener(selectionListener);
        disposeImage();
    }

    private void disposeImage() {
        if (image == null)
            return;
        image.dispose();
        image = null;
    }

    public void setFocus() {
        viewer.setFocus();
    }

    private ISelectionService getSelectionService() {
        return getSite().getWorkbenchWindow().getSelectionService();
    }
}