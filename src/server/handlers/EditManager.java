package server.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class that manages the edit queue for the documents on the server
 * @author computerjunky28
 *
 */
public class EditManager {
	private final Map<String, List<Edit>> editLog;
	private static final boolean DEBUG = true;
	
	/**
	 * Creates a new EditManager for the server with a new map
	 * of documentNames to lists of Edits
	 */
	public EditManager(){
		editLog = Collections.synchronizedMap(new HashMap<String, List<Edit>>());
	
	}
	
	/**
	 * Creates a new log for the new document - called when a new
	 * document is created
	 * @param documentName name of new document
	 * 
	 */
	public synchronized void createNewlog(String documentName){
		editLog.put(documentName, new ArrayList<Edit>());
	}
	
	/**
	 * Adds the edit to the list for the document.
	 * Document names are taken from the edit
	 * @param edit the edit made
	 */
	public synchronized void logEdit(Edit edit){
		String documentName = edit.getDocumentName();
		editLog.get(documentName).add(edit);
		if (DEBUG){System.out.println(edit.toString());}
	}

	/**
	 * Goes through the versionEditLog and attempts to add the edit at the
	 * correct index by using operational transform. 
	 * This is called when the client updates an out of date version
	 * of the document. 
	 * It goes through each edit that has been made with a version that is equal to or greater than
	 * the specified version and finds the correct index.
	 * @param documentName name of document
	 * @param version version the edit was made one
	 * @param offset the place the edit was first inserted
	 * @return the corrected offset
	 */
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
	

	/**
	 * Takes in the current offset and compares it to the otherOffset, the
	 * offset of the already completed edit and it's length,
	 * and corrects the currentOffset if necessary.
	 * @param currentOffset   the current position of the offset
	 * @param otherOffset     another position of the offset
	 * @param length          the length of an already completed edit
	 * @return corrected offset  the final corrected position of the offset
	 */
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
