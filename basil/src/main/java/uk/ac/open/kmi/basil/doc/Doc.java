package uk.ac.open.kmi.basil.doc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author enridaga
 *
 */
public class Doc implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8091475762370792961L;

	public enum Field {
		NAME, DESCRIPTION
	}

	private Map<Field, String> doc = new HashMap<Field, String>();

	public void set(Field field, String value) {
		doc.put(field, value);
	}

	/**
	 * Returns an empty string if null
	 * 
	 * @param f
	 * @return
	 */
	public String get(Field f) {
		String v = doc.get(f);
		return (v == null) ? "" : v;
	}

	public boolean isEmpty() {
		return get(Field.NAME) == null && get(Field.DESCRIPTION) == null;
	}
}
