package srl.visgo.gui.zoom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.data.listeners.PingEvent;
import srl.visgo.data.listeners.PingListener;
import srl.visgo.gui.Visgo;
import srl.visgo.gui.listeners.EditDocumentEvent;
import srl.visgo.gui.listeners.EditDocumentListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class PWorkspace extends PNode{
	private static final long serialVersionUID = 1L;
	
	public static PBounds wBounds;
	public static PBounds currentBounds;
	boolean initialPaint = true;
	int INDENT = 10;

    PBounds cachedChildBounds = new PBounds();
    PBounds comparisonBounds = new PBounds();
	private ArrayList<PingListener> listeners = new ArrayList<PingListener>();

    
    private Queue<EditDocumentListener> editDocumentListeners;
	
	
	public PWorkspace(){
		super();
		this.setPaint(Color.CYAN);
		load();
		editDocumentListeners = new LinkedList<EditDocumentListener>();
	}
	
	public void addEditDocumentListener(EditDocumentListener listener){
		if(!editDocumentListeners.contains(listener)){
			editDocumentListeners.add(listener);
		}
	}
	private void fireEditDocumentListener(Document doc){
		EditDocumentEvent event = new EditDocumentEvent(doc);
		for(EditDocumentListener listener:editDocumentListeners){
			listener.onEditDocument(event);
		}
	}

	public void onEditDocument(Document doc) {
		fireEditDocumentListener(doc);
	}
	
	/**
	 * Initial load of the workspace
	 */
	private void load(){
		try{
			int i = 1;
			for(DocumentGroup group : Visgo.data.workspace.getRootDocumentGroups()){

				PDocumentGroup projectNode = new PDocumentGroup(group);
				projectNode.setColumnCount(3);
				projectNode.invalidate();
				i++;
				this.addChild(projectNode);
			}
			for(Document doc : Visgo.data.workspace.getRootDocuments()){

				PDocument projectNode = new PDocument(doc);
				this.addChild(projectNode);
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Redraw the workspace to reflect changes made by users
	 */
	public void invalidate(){
		this.removeAllChildren();
		load();
	}
	
    /**
     * Change the default paint to fill an expanded bounding box based on its
     * children's bounds
     */
    public void paint(final PPaintContext ppc) {
        final Paint paint = getPaint();
        if (paint != null) {
            final Graphics2D g2 = ppc.getGraphics();
            g2.setPaint(paint);

            final PBounds bounds = getUnionOfChildrenBounds(null);
            bounds.setRect(bounds.getX() - INDENT, bounds.getY() - INDENT, bounds.getWidth() + 2 * INDENT, bounds
                    .getHeight()
                    + 2 * INDENT);
            currentBounds = bounds;
            g2.fill(bounds);

            if(initialPaint){
	            PBounds test = this.getGlobalFullBounds();
	    		Visgo.canvas.getCamera().animateViewToCenterBounds(test.getBounds2D(), true, 100);
	    		initialPaint = false;
            }
        }
    }

    /**
     * Change the full bounds computation to take into account that we are
     * expanding the children's bounds Do this instead of overriding
     * getBoundsReference() since the node is not volatile
     */
    public PBounds computeFullBounds(final PBounds dstBounds) {
        final PBounds result = getUnionOfChildrenBounds(dstBounds);

        cachedChildBounds.setRect(result);
        result.setRect(result.getX() - INDENT, result.getY() - INDENT, result.getWidth() + 2 * INDENT, result
                .getHeight()
                + 2 * INDENT);
        localToParent(result);

        return result;
    }

    /**
     * This is a crucial step. We have to override this method to invalidate the
     * paint each time the bounds are changed so we repaint the correct region
     */
    public boolean validateFullBounds() {
        comparisonBounds = getUnionOfChildrenBounds(comparisonBounds);

        if (!cachedChildBounds.equals(comparisonBounds)) {
            setPaintInvalid(true);
        }
        return super.validateFullBounds();
    }
    
    /**
     * Add a PingListener to the array of listeners
     * @param e
     */
	public void addPingListener(PingListener e){
		listeners.add(e);
	}

	/**
	 * Send a ping event to the set of listeners
	 * @param pingEvent
	 */
	public void sendPingEvent(PingEvent pingEvent) {
		for (PingListener listener: listeners)
			listener.onPing(pingEvent);
		
	}

}
