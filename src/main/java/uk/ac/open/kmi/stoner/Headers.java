package uk.ac.open.kmi.stoner;

public final class Headers {
	private Headers() {
	}

	public static final String PREFIX = "X-Stoner-";
	public static final String Error = PREFIX + "Error";
	public static final String Endpoint = PREFIX + "Endpoint";
	public static final String Api = PREFIX + "Api";
	public static final String Spec = PREFIX + "Spec";
	public static final String Format = PREFIX + "Format";
	public static final String Store = PREFIX + "Store";
	
	public static String getHeader(String parameter) {
		return PREFIX + parameter;
	}
	
	public static String asParameter(String Header){
		return Header.substring(PREFIX.length()).toLowerCase();
	}
}
