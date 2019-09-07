package server.handlers;

/**
 * Class representing an edit on the document. It stores the type of edit, the text added if the
 * type is Insert, the length of the edit, the offset, and the version of the document the edit was made on.
 * 
 */
public class Edit {
	
	/**
	 * the types that an edit can be
	 */
	public static enum Type {INSERT, REMOVE}

	private final String documentName;
	private final Type type;
	private final String text;
	private final int length;
	private final int offset;
	private final int version;

	// Rep invariant:
	// type and text cannot be null
	// editType can only be insert or remove

	/**
	 * Creates a new Edit
	 * @param documentName
	 * @param editType
	 * @param text
	 * @param version
	 * @param offset
	 * @param length
	 */
	public Edit(String documentName, Type editType, String text, int version,
			int offset, int length) {
		this.documentName = documentName;
		this.type = editType;
		this.text = text;
		this.offset = offset;
		this.length = length;
		this.version = version;
		checkRep();
	}
    
	/**
	 * check the rep invariant
	 */
	private void checkRep() {
		assert documentName != null;
		assert type != null;
	}

	/** @return the type of the edit */
	public Type getType() {
		return type;
	}

	/**
	 * 
	 * @return the text of the edit. Text is "" if the type of edit is an
	 *         insert.
	 */
	public String getText() {
		return text;
	}

	/** @return the offset of the edit. */
	public int getOffset() {
		return offset;
	}

	/** @return the length of the edit. */
	public int getLength() {
		return length;
	}

	/** @return the version of the edit. */
	public int getVersion() {
		return version;
	}

	/** @return the documentName of the edit. */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * Creates a string representation of the edit
	 */
	public String toString() {
		return "Edit: " + documentName + " type: " + type + " v: " + version
				+ " offset: " + offset + " length: " + length + " text: " + text;
	}

}
