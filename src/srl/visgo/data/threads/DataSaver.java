package srl.visgo.data.threads;

import gDocsFileSystem.GFileSystem;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.mongodb.GroupCommand;

import srl.visgo.data.Document;
import srl.visgo.data.DocumentGroup;
import srl.visgo.data.Entry;
import srl.visgo.util.chat.ChatManager;
import srl.visgo.util.chat.listeners.CommandMessage;

public class DataSaver implements Runnable{
	ObjectMapper mapper = new ObjectMapper();
	private Queue<Entry> queue;
	private HashMap<String,Entry> map;
	private GFileSystem mFileSystem;
	private ChatManager mChatManager;

	private final Semaphore modifyList = new Semaphore(1,true);

	public DataSaver(GFileSystem filesystem,ChatManager chatManager){
		mFileSystem = filesystem;
		map = new HashMap<String,Entry>();
		queue = new LinkedList<Entry>();
		mChatManager = chatManager;
	}

	@Override
	public void run() {
		while(true){
			Entry e =null;
			modifyList.acquireUninterruptibly();
			//stuff?
			if(!queue.isEmpty()){
				e = queue.remove();
				Map serialMap;
				if(e instanceof DocumentGroup){
					serialMap = DocumentGroup.serialize((DocumentGroup)e);
				}
				else{
					serialMap = Document.serialize((Document) e);
				}
				String lookup = e.getId()+e.getClass().getCanonicalName()+":"+e.getId();
				try {
					String serialized = mapper.writeValueAsString(serialMap);
					//System.out.println(serialized);
					CommandMessage command = new CommandMessage("dataChange", serialized);
					mChatManager.sendGroupCommand(command);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				map.remove(lookup);
			}
			modifyList.release();
			if(e!=null){
				mFileSystem.store(e);
			}
			while(queue.isEmpty()){
				try {
					Thread.sleep(500);
				} catch (InterruptedException ex) {
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
