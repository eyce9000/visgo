package srl.visgo.data;

import gDocsFileSystem.GFileSystem;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class DataSaver implements Runnable{
	private Queue<Entry> queue;
	private HashMap<String,Entry> map;
	private GFileSystem mFileSystem;

	private final Semaphore modifyList = new Semaphore(1,true);

	DataSaver(GFileSystem filesystem){
		mFileSystem = filesystem;
		map = new HashMap<String,Entry>();
		queue = new LinkedList<Entry>();
	}

	@Override
	public void run() {
		while(true){
			Entry e =null;
			modifyList.acquireUninterruptibly();
			//stuff?
			if(!queue.isEmpty()){
				e = queue.remove();
				String lookup = e.getId()+e.getClass().getCanonicalName()+":"+e.getId();
				map.remove(lookup);
			}
			modifyList.release();
			if(e!=null){
				mFileSystem.store(e);
			}
			while(queue.isEmpty()){
				try {
					Thread.sleep(2000);
				} catch (InterruptedException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}
	}
	public synchronized void saveEntry(Entry e){
		modifyList.acquireUninterruptibly();
		String lookup = e.getId()+e.getClass().getCanonicalName()+":"+e.getId();
		if(map.containsKey(lookup)){
			Entry prevEntry = map.remove(lookup);
			if(prevEntry!=null){
				queue.remove(prevEntry);
			}
		}
		//Entry clone = e.clone();
		map.put(lookup, e);
		queue.add(e);
		modifyList.release();
	}
}
