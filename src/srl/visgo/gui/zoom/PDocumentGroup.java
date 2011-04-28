package srl.visgo.gui.zoom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import gDocsFileSystem.GFileSystem;
import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.data.Entry;
import srl.visgo.data.Revision;
import srl.visgo.gui.Visgo;

public class PDocumentGroup extends PNode{
	DocumentGroup mDocumentGroup;
	PPath backgroundNode;
	List<PDocument> docNodes;
	int mColCount = 1;
	public static PBounds currentBounds;
	PDocGroupEventHandler eventHandler;
	private PRevisionActivity activityBar;
	PDocumentGrid grid;
	
	private boolean invalid=false;

	
	
	public PDocumentGroup(DocumentGroup group){
		super();
		
		mDocumentGroup = group;
			
		docNodes = new ArrayList<PDocument>();

		backgroundNode = PPath.createRectangle(0f, 0f, 50, 50);
		PDragEventHandler handler = new PDragEventHandler();
		backgroundNode.addInputEventListener(handler);
		eventHandler = new PDocGroupEventHandler(this);
		this.addInputEventListener(eventHandler);
		this.setPaint(Color.LIGHT_GRAY);
		backgroundNode.setPaint(Color.LIGHT_GRAY);
		this.addChild(backgroundNode);
		initialize();
	}
	public void setColumnCount(int count){
		mColCount = count;
	}
	public int getColumnCount(){
		return mColCount;
	}
	
	public ArrayList<PNode> getPDocs(){
		return null;
	}
	
	/**
	 * Use to initialize the group. Sets the draggable background node's size to that of 
	 * the group's title.
	 */
	private void initialize(){
		backgroundNode.removeAllChildren();
		Collection<Entry> docs = mDocumentGroup.getRootEntries();
		grid = new PDocumentGrid(docs);
		backgroundNode.addChild(grid);
		grid.invalidate();
		grid.setOffset(0,40);
		PText nameNode = new PText(mDocumentGroup.getName());
		backgroundNode.addChild(nameNode);
		nameNode.setPickable(false);
		PBounds nameBounds = nameNode.getGlobalFullBounds();
		backgroundNode.setPathToRectangle((float)nameBounds.getX(), 
				(float)nameBounds.getY(), (float)nameBounds.width, (float)nameBounds.height);

		this.setOffset(mDocumentGroup.getOffsetX(), mDocumentGroup.getOffsetY());
		rebuild();
	}
	
	//Group -> backgroundNode -> Grid -> Docs
	
	private void rebuild(){
		backgroundNode.removeAllChildren();
		Collection<Entry> docs = mDocumentGroup.getRootEntries();
		grid = new PDocumentGrid(docs);
		backgroundNode.addChild(grid);
		grid.invalidate();
		grid.setOffset(0,40);
		PText nameNode = new PText(mDocumentGroup.getName());
		backgroundNode.addChild(nameNode);
		nameNode.setPickable(false);
		
		//TODO
		//Replace with
		Collection<Revision> revisions = mDocumentGroup.getRevisionHistory();
		activityBar = new PRevisionActivity(revisions,PRevisionActivity.Orientation.Horizontal);
		activityBar.setOffset(0,-10);
		backgroundNode.addChild(activityBar);
		invalid = false;
	}
	
	public void invalidate(){
		invalid = true;
		grid.setEntries(mDocumentGroup.getRootEntries());
		grid.invalidate();
		
		activityBar.setRevisions(mDocumentGroup.getRevisionHistory());
		activityBar.invalidate();
		
		if(this.getParent()!=null)
		this.getParent().repaint();
	}
	
	int INDENT = 10;

    PBounds cachedChildBounds = new PBounds();
    PBounds comparisonBounds = new PBounds();

    /**
     * Change the default paint to fill an expanded bounding box based on its
     * children's bounds
     */
    public void paint(final PPaintContext ppc) {
    	if(invalid){
    		rebuild();
    	}
    	
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

		mDocumentGroup.setOffsetX(result.getX());
		mDocumentGroup.setOffsetY(result.getY());
		mDocumentGroup.save();
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
    
    public DocumentGroup getDocumentGroup(){
    	return mDocumentGroup;
    }
    
    public PBounds getCachedBounds(){
    	return cachedChildBounds;
    }
    
    public void greyDocument(PDocument pDoc){
//    	for(Document d : mGroup.getDocuments() )
//    	{
//    		if(d.getId().equals(pDoc.getDocument().getId()))
//    		{
//    			pDoc.backgroundNode.setPaint(Color.BLUE);
//    		}
//    	}
    	invalidate();
    }
    
    /**
     * Remove a document from this group. Updates the group visually
     * @param pDoc
     */
    public void removeDocument(PDocument pDoc){
    	mDocumentGroup.removeDocument(pDoc.getDocument());
    	if(mDocumentGroup.getRootEntries().size()>0){
    		invalidate();
    	}
    	else{
    		Visgo.workspace.removeChild(this);
    	}
    }
    
    /**
     * Add a document to a group, if it is not already in it.
     * @param pDoc
     */
    public void addDocument(PDocument pDoc){
    	if(!mDocumentGroup.getDocuments().contains(pDoc.getDocument()))
    	{
    		mDocumentGroup.addDocument(pDoc.getDocument());
    		//TODO: Add File/Folder update here
    		invalidate();
    		
    	}
    	pDoc.backgroundNode.setPaint(Color.GRAY);
    }
}

class PDocGroupEventHandler extends PBasicInputEventHandler{
	PDocumentGroup mDocGroup;
	
	PDocGroupEventHandler(PDocumentGroup group){
		mDocGroup = group;
	}
	
	@Override
	public void mouseDragged(PInputEvent event){
		event.setHandled(true);
	}
	
	
}
