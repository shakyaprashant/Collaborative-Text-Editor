package server.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class EditManager {
	private final Map<String, List<Edit>> editLog;
	private static final boolean DEBUG = true;
	

	public EditManager(){
		editLog = Collections.synchronizedMap(new HashMap<String, List<Edit>>());
	
	}
	

	public synchronized void createNewlog(String documentName){
		editLog.put(documentName, new ArrayList<Edit>());
	}
	

	public synchronized void logEdit(Edit edit){
		String documentName = edit.getDocumentName();
		editLog.get(documentName).add(edit);
		if (DEBUG){System.out.println(edit.toString());}
	}


	public synchronized String manageEdit(String documentName, int version,
			int offset) {
		List<Edit> list = editLog.get(documentName);
		int updatedOffset = offset;
		for (Edit edit : list) {
			if (edit.getVersion() >= version) {
				updatedOffset = manageOffset(updatedOffset, edit.getOffset(),
						edit.getLength());
				version = edit.getVersion();
			}
		}
		if (DEBUG){System.out.println("new offset: "+offset);}
		if (DEBUG){System.out.println("new version: "+version);}
		String result = documentName+" "+(version+1)+" "+offset;
		return result;
	}
	


	private int manageOffset(int currentOffset, int otherOffset, int length) {
		if (currentOffset < otherOffset) {
			return currentOffset;
		} else if (currentOffset < otherOffset + length && currentOffset >= otherOffset) {
			return otherOffset;
		} else {
			return currentOffset + length;
		}
	}

	
	
	

}
