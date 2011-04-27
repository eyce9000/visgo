package srl.visgo.data.threads;

import srl.visgo.data.DocumentList;

public class RevisionChecker implements Runnable{
	DocumentList doclist;
	public RevisionChecker(DocumentList list){
		doclist = list;
	}

	@Override
	public void run() {
		while(true){
			System.out.println("===Begin Revision Check===");
			doclist.updateAllRevisionHistory();
			try {
				Thread.sleep(15000);
			} catch (InterruptedException ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
			}
		}
	}

}
