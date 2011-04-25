package srl.visgo.gui.zoom;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;

import srl.visgo.data.DocumentGroup;
import srl.visgo.gui.Visgo;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class PWorkspace extends PNode{

	//PDocs, PDocGroups in HashMaps (DocID, name)
	//TODO: Find nodes on changes and visually refresh
	public static PBounds wBounds;
	public static PBounds currentBounds;
	boolean initialPaint = true;
	int INDENT = 10;

    PBounds cachedChildBounds = new PBounds();
    PBounds comparisonBounds = new PBounds();
	
	
	public PWorkspace(){
		super();
		this.setPaint(Color.CYAN);
		this.addInputEventListener(new PWorkspaceEventHandler(this));
		load();
		
	}
	
	private void load(){
		try{
			int i = 1;
			PDocumentGroup prevNode = null;
			for(DocumentGroup group : Visgo.data.workspace.getRootDocumentGroups()){

				PDocumentGroup projectNode = new PDocumentGroup(group);
				projectNode.setColumnCount(3);
				projectNode.invalidate();
				if(prevNode!=null){
					PBounds bounds = prevNode.computeFullBounds(null);
					double yOffset =prevNode.getOffset().getY()+bounds.height+10;
					double xOffset = prevNode.getOffset().getX();
					//projectNode.setOffset(xOffset, yOffset);
				}
				else{
					//projectNode.setOffset(i*200, 100);
				}
				i++;
				this.addChild(projectNode);
			}

		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//General invalidate
	public void invalidate(){
		
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

//		mGroup.setOffsetX(result.getX());
//		mGroup.setOffsetY(result.getY());
//		mGroup.save();
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

}

class PWorkspaceEventHandler extends PBasicInputEventHandler{
	PWorkspace workspace;
	
	
	public PWorkspaceEventHandler(PWorkspace space){
		workspace = space;
	}
	
	@Override
	public void mousePressed(PInputEvent event){
		if(event.getClickCount() == 2){
			PBounds test = Visgo.workspace.getGlobalFullBounds();
			Visgo.canvas.getCamera().animateViewToCenterBounds(test.getBounds2D(), true, 2000);
		}
	}
}
