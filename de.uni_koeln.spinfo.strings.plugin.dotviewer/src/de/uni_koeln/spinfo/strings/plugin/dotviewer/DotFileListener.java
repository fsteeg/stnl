/**
 * Sprachliche Informationsverarbeitung
 * Institut für Linguistik, Universität zu Köln
 * http://www.spinfo.uni-koeln.de/
 *
 * Created on 11.06.2006
 * @author Fabian Steeg
 */
package de.uni_koeln.spinfo.strings.plugin.dotviewer;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * Listener for the visual chain view, listens to file selections in the
 * navigator, selected editor and resource changes
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class DotFileListener implements IResourceChangeListener,
        ISelectionListener {
    private ImageView view;

    String ENDING = "dot";

    public DotFileListener(ImageView view) {
        this.view = view;
    }

    /**
     * Listen for changes to the resource, if a process chain is edited, the
     * view is updated
     * 
     * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org.eclipse.core.resources.IResourceChangeEvent)
     */
    public void resourceChanged(IResourceChangeEvent event) {
        // we are only interested in POST_CHANGE events
        if (event.getType() != IResourceChangeEvent.POST_BUILD)
            return;
        IResourceDelta rootDelta = event.getDelta();
        //TODO hier weitermachen... das wird nie ausgegeben...
        System.out.println("updpdpdpdpdpdslposkpodksopk");
        IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
            
            public boolean visit(IResourceDelta delta) {
                // only interested in changed resources (not added or
                // removed)
                if (delta.getKind() != IResourceDelta.CHANGED)
                    return true;
                // only interested in content changes
                if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
                    return true;
                IResource resource = delta.getResource();
                // only interested in files with the right
                // extension
                if (resource.getType() == IResource.FILE
                        && ENDING.equalsIgnoreCase(resource.getFileExtension())) {
                    // IPath location = resource.getLocation();
                    // System.out.println("Resource changed: " + location);
                    try {
                        IFile f = (IFile) resource;
                        updateView(f);
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

    /**
     * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart,
     *      org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        // // System.out.println("Selection changed, location is: " +
        // // view.location);
        // // a selected file
        // if (selection instanceof TreeSelection) {
        // TreeSelection s = (TreeSelection) selection;
        // Object firstElement = s.getFirstElement();
        // if (firstElement instanceof IFile) {
        // IFile f = (IFile) firstElement;
        // updateView(f);
        // }
        // }
        // // a file open in the editor
        // else if (part instanceof IEditorPart) {
        // IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
        // if (editorInput instanceof IFileEditorInput) {
        // IFileEditorInput in = (IFileEditorInput) editorInput;
        // IFile file = in.getFile();
        // updateView(file);
        // }
        // }
        // System.out.println("After selection changed, location is: "
        // + view.location);
    }

    private void updateView(IFile f) {
        String l = f.getLocation().toString();
        // TODO externalize
        
        if (l.endsWith("." + ENDING)) {
            view.setImage(f, true);
        }
    }

    // private String getFileName() {
    // return view.location.substring(view.location
    // .lastIndexOf(File.separatorChar) + 1);
    // }
}
