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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class ImageViewerPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static ImageViewerPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public ImageViewerPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static ImageViewerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("de.uni_koeln.spinfo.strings.plugin.dotviewer", path);
	}
    
     /**
     * @param path
     *            The files path
     * @return Return the file
     */
    public File getFileInPlugin(IPath path) {
        try {
            Bundle bundle = getDefault().getBundle();
            URL installURL = new URL(bundle.getEntry("/"), path.toString());
            URL localURL = FileLocator.toFileURL(installURL);
            return new File(localURL.getFile());
        } catch (IOException e) {
            return null;
        }
    }
}
