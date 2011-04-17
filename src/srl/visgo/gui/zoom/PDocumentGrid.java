package srl.visgo.gui.zoom;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.data.Entry;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

public class PDocumentGrid extends PNode {
	Collection<Entry> mEntries  = new LinkedList<Entry>();
	private int mColCount = 5;


	public PDocumentGrid(){
		super();
	}
	public PDocumentGrid(Collection<Entry> entries){
		this();
		setEntries(entries);
	}
	public void setEntries(Collection<Entry> entries){
		mEntries = entries;
	}

	public void invalidate(){
		this.removeAllChildren();
		int i=0;
		for(Entry entry: mEntries){
			if(entry instanceof Document){
				PDocument docNode = new PDocument((Document)entry);

				this.addChild(docNode);
				int col = i % mColCount;
				int row = i / mColCount;
				docNode.setOffset(new Point2D.Double(col*50,row*50));
				docNode.setScale(.5);
			}
//			else if(entry instanceof DocumentGroup){
//				PDocumentGroup groupNode = new PDocumentGroup((DocumentGroup)entry);
//				//TODO: Increment i to accommodate contents of group
//				this.addChild(groupNode);
//				int col = i % mColCount;
//				int row = i / mColCount;
//				groupNode.setOffset(new Point2D.Double(col*50,row*50));
//				groupNode.setScale(.5);
//			}
			i++;
		}
	}
}
