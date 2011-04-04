package srl.visgo.gui.zoom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;

public class PDocumentGroup extends PNode{
	DocumentGroup mGroup;
	List<PDocument> docNodes;
	int mColCount = 1;
	public static PBounds currentBounds;
	
	public PDocumentGroup(DocumentGroup group){
		super();
		mGroup = group;
		//mGroup.setPDocGroup(this);
			
		docNodes = new ArrayList<PDocument>();
		this.setPaint(Color.LIGHT_GRAY);
		invalidate();
	}
	public void setColumnCount(int count){
		mColCount = count;
	}
	public int getColumnCount(){
		return mColCount;
	}
	
	//add docs, set dragging to docs, set size of area, etc.
	public void invalidate(){
		this.removeAllChildren();
		Collection<Document> docs = mGroup.getDocuments();
		int i=0;
		for(Document doc: docs){
			PDocument docNode = new PDocument(doc);

			docNodes.add(docNode);
			this.addChild(docNode);
			int col = i % mColCount;
			int row = i / mColCount;
			docNode.setOffset(new Point2D.Double(col*50,row*50));
			docNode.setScale(.5);
			i++;
		}
		PText nameNode = new PText(mGroup.getName());
		this.addChild(nameNode);
		nameNode.setOffset(0,-40);
	}
	
	int INDENT = 10;

    PBounds cachedChildBounds = new PBounds();
    PBounds comparisonBounds = new PBounds();

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
    
    public DocumentGroup getDocumentGroup(){
    	return mGroup;
    }
    
    public PBounds getCachedBounds(){
    	return cachedChildBounds;
    }
}
