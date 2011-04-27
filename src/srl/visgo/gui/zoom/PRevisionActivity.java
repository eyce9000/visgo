package srl.visgo.gui.zoom;

import java.awt.Color;
import java.awt.Paint;
import java.util.List;

import srl.visgo.data.Revision;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

public class PRevisionActivity  extends PNode{
	enum Orientation{
		Vertical,
		Horizontal
	}
	
	private static float MAX_TIME = 300000;
	
	private boolean invalid = true;
	private List<Revision> mRevisions;
	private Orientation mOrientation;
	
	private long currentTime;
	
	public PRevisionActivity(List<Revision> revisions,Orientation orientation){
		super();
		mOrientation = orientation;
		setRevisions(revisions);
		rebuild();
		
	}
	public void setRevisions(List<Revision> revisions){
		mRevisions = revisions;
		invalidate();
	}
	public void invalidate(){
		invalid = true;
		currentTime = System.currentTimeMillis();
	}
	private void rebuild(){
		this.removeAllChildren();
		int i=0;
		for(Revision revision:mRevisions){
			//PPath back = PPath.createRoundRectangle(0f, 0f, 10, 10, 0f, 0f);
			PPath block = PPath.createRoundRectangle(0f, 0f, 10, 10, 5f, 5f);
			block.setTransparency(getTransparency(revision));
			block.setPaint(revision.getModifiedBy().getColor());
			//back.setPaint(Color.WHITE);
			switch(mOrientation){
			case Vertical:
				block.setOffset(0, i*11);
				//back.setOffset(0,i*11);
				break;
			case Horizontal:
				block.setOffset(i*11,0);
				//back.setOffset(i*11, 0);
				break;
			}
			//this.addChild(back);
			this.addChild(block);
			i++;
		}
	}
	@Override
	public void paint(final PPaintContext ppc) {
		if(invalid){
			rebuild();
			invalid = false;
		}
		super.paint(ppc);
	}
	private float getTransparency(Revision rev){
		float diff = currentTime-rev.getModifiedTime();
		if(diff > MAX_TIME){
			return 0.0f;
		}
		else if(diff <=0){
			return 1.0f;
		}
		else{
			return 1.0f-(diff/MAX_TIME);
		}
	}
}
