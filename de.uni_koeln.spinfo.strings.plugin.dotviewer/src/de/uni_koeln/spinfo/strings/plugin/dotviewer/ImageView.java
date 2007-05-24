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
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.part.ViewPart;

public class ImageView extends ViewPart {
	ImageViewer viewer;

	Image image;

	private IFile file;

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
			setImage(file, true);
		}

	};

	DotDrawer drawer;

	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		viewer = new ImageViewer(parent, SWT.NONE);
		// getSelectionService().addSelectionListener(selectionListener);

		addLoadButton();
		addResetButton();

		// IWorkspace workspace = ResourcesPlugin.getWorkspace();
		// DotFileListener listener = new DotFileListener(this);
		// workspace.addResourceChangeListener(listener);
	}

	private void addResetButton() {
		Action addItemAction = new Action("Reset") {
			public void run() {
				ImageViewerPlugin.getDefault().getPreferenceStore().setValue(
						"dotpath", //$NON-NLS-1$
						"");
				drawer.initPaths(viewer.getShell());
			}
		};
		// TODO somethigns wrong here...
		ImageDescriptor desc = ImageViewerPlugin
				.getImageDescriptor("icons/update.gif");
		// Bundle bundle = Platform.getBundle(ImageViewerPlugin.ID);
		// IPath path = new Path("icons/update.gif");
		// URL url = FileLocator.find(bundle, path, new HashMap());
		// ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		addItemAction.setImageDescriptor(desc);
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(addItemAction);
	}

	private void addLoadButton() {
		Action addItemAction = new Action("Load") {
			public void run() {
				ResourceListSelectionDialog d;
				d = new ResourceListSelectionDialog(viewer.getShell(),
						ResourcesPlugin.getWorkspace().getRoot(),
						IResource.FILE);
				if (d.open() == ResourceListSelectionDialog.OK) {
					Object[] result2 = d.getResult();
					if (result2 != null) {
						file = (IFile) result2[0];

						setImage(file, true);

					}
					for (Object object : result2) {
						System.out.println("Object: " + object + ", class: "
								+ object.getClass());
					}
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IResourceChangeListener listener = new IResourceChangeListener() {
						public void resourceChanged(IResourceChangeEvent event) {
							System.out.println("Change: " + event.getType());

							// we are only interested in POST_CHANGE events
							if (event.getType() != IResourceChangeEvent.POST_BUILD
									&& event.getType() != IResourceChangeEvent.POST_CHANGE)
								return;
							IResourceDelta rootDelta = event.getDelta();
							// ausgegeben...
							System.out.println("updpdpdpdpdpdslposkpodksopk");
							IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {

								public boolean visit(IResourceDelta delta) {
									// only interested in changed resources
									// (not added or
									// removed)
									// if (delta.getKind() !=
									// IResourceDelta.CHANGED
									// && delta.getKind() !=
									// IResourceDelta.REPLACED
									// && delta.getKind() !=
									// IResourceDelta.ADDED)
									// return true;
									// // only interested in content changes
									// if ((delta.getFlags() &
									// IResourceDelta.CONTENT) == 0)
									// return true;
									IResource resource = delta.getResource();
									// if (!resource.getLocation().lastSegment()
									// .equals(
									// file.getLocation()
									// .lastSegment()))
									// return false;
									// only interested in files with the
									// right
									// extension
									if (resource.getType() == IResource.FILE
											&& "dot".equalsIgnoreCase(resource
													.getFileExtension())) {
										// IPath location =
										// resource.getLocation();
										// System.out.println("Resource
										// changed: " + location);
										try {
											final IFile f = (IFile) resource;
											final String l = f.getLocation()
													.toString();
											// TODO externalize
											IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
												public void run(
														IProgressMonitor monitor)
														throws CoreException {
													if (l.endsWith("." + "dot")) {
														setImage(f, true);
													}
												}
											};
											IWorkspace workspace = ResourcesPlugin
													.getWorkspace();
											workspace.run(runnable, null);

										} catch (Exception e) {
											// FIXME throws NullPointer...
											e.printStackTrace();
										}

									}
									return true;
								}
							};
							try {
								rootDelta.accept(visitor);
							} catch (CoreException e) {
								e.printStackTrace();
							}
						}
					};
					workspace.addResourceChangeListener(listener,
							IResourceChangeEvent.POST_BUILD
									| IResourceChangeEvent.POST_CHANGE);
				}

			}
		};
		// TODO somethigns wrong here...
		ImageDescriptor desc = ImageViewerPlugin
				.getImageDescriptor("icons/update.gif");
		// Bundle bundle = Platform.getBundle(ImageViewerPlugin.ID);
		// IPath path = new Path("icons/update.gif");
		// URL url = FileLocator.find(bundle, path, new HashMap());
		// ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		addItemAction.setImageDescriptor(desc);
		IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
		mgr.add(addItemAction);
		mgr.add(new Separator());
	}

	protected void setImage(IFile file, boolean refresh) {
		InputStream in = null;
		try {
			if (file.getFileExtension().equals("dot")) {
				file = generateImageFromDot(file, refresh);
				System.out.println("huhu");
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

	private IFile generateImageFromDot(IFile file, boolean refresh) {
		// String inFolder = Platform.getInstanceLocation().getURL().getPath()
		// + file.getParent().getFullPath().toString().substring(1) + "/";
		String folder = Platform.getInstanceLocation().getURL().getPath()
				+ file.getParent().getFullPath().toString().substring(1) + "/";
		System.out.println("in: " + folder);
		System.out.println("out: " + folder);
		String name = file.getName().split("\\.")[0] + ".png";

		drawer = new DotDrawer(folder, folder, file.getName(), name);
		drawer.initPaths(this.getViewSite().getShell());
		try {

			drawer.renderImage();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (refresh) {
			try {
				file.getParent().refreshLocal(IResource.DEPTH_ONE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		IFile res = (IFile) file.getParent().findMember(new Path(name));
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