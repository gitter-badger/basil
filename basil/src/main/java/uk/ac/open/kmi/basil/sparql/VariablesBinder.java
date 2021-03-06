package uk.ac.open.kmi.basil.sparql;

import com.hp.hpl.jena.datatypes.BaseDatatype;

import com.hp.hpl.jena.query.ParameterizedSparqlString;
import com.hp.hpl.jena.query.Query;

/**
 * To bind parameter values to variables in the SPARQL query.
 * 
 * @author enridaga
 *
 */
public class VariablesBinder {

	private ParameterizedSparqlString pss;
	private Specification spec;

	/**
	 * Constructor.
	 * 
	 * @param spec
	 * @param bindings
	 *            - Optional as sequence of strings:
	 *            param,value,param2,value2...
	 * @see Specification
	 */
	public VariablesBinder(Specification spec, String... bindings) {
		this.spec = spec;
		this.pss = new ParameterizedSparqlString(spec.getQuery());
		for (int x = 0; x < bindings.length; x += 2) {
			bind(bindings[x], bindings[x + 1]);
		}

	}

	/**
	 * Binds a value to the parameter {@code name}. Delegetes to one of the
	 * specialized methods depending on the associated {@link QueryParameter}.
	 * Defaults to {@link #bindPlainLiteral(String, String)}.
	 * 
	 * @param name
	 * @param value
	 * @return a reference to this object.
	 * @see #bindIri(String, String)
	 * @see #bindLangedLiteral(String, String, String)
	 * @see #bindPlainLiteral(String, String)
	 * @see #bindNumber(String, String)
	 * @see #bindTypedLiteral(String, String, String)
	 */
	public VariablesBinder bind(String name, String value) {
		if (spec.hasParameter(name)) {
			QueryParameter p = spec.getParameter(name);
			if (p.isIri()) {
				bindIri(name, value);
			} else if (p.isLangedLiteral()) {
				bindLangedLiteral(name, value, p.getLang());
			} else if (p.isTypedLiteral()) {
				bindTypedLiteral(name, value, p.getDatatype());
			} else if (p.isNumber()) {
				bindNumber(name, value);
			} else {
				// Default is PlainLiteral
				bindPlainLiteral(name, value);
			}
		}
		return this;
	}

	/**
	 * Binds a value as typed literal.
	 * 
	 * @param name
	 * @param value
	 * @param datatype
	 * @return a reference to this object.
	 */
	public VariablesBinder bindTypedLiteral(String name, String value,
			String datatype) {
		pss.setLiteral(spec.getVariableOfParameter(name), value,
				new BaseDatatype(datatype));
		return this;
	}

	/**
	 * Binds a value as typed literal.
	 * 
	 * @param name
	 * @param value
	 * @param datatype
	 * @return a reference to this object.
	 */
	public VariablesBinder bindNumber(String name, String value) {
		if (value.contains(".")) {
			pss.setLiteral(spec.getVariableOfParameter(name), Double.valueOf(value));	
		}else{
			pss.setLiteral(spec.getVariableOfParameter(name), Integer.valueOf(value));
		}
		return this;
	}

	/**
	 * Binds a value as simple literal.
	 * 
	 * @param name
	 * @param value
	 * @return a reference to this object.
	 */
	public VariablesBinder bindPlainLiteral(String name, String value) {
		pss.setLiteral(spec.getVariableOfParameter(name), value);
		return this;
	}

	/**
	 * Alias of {@link #bindPlainLiteral}
	 * 
	 * @param name
	 * @param value
	 * @see #bindPlainLiteral
	 * @return a reference to this object.
	 */
	public VariablesBinder bindLiteral(String name, String value) {
		return this.bindPlainLiteral(name, value);
	}

	/**
	 * Binds a value as langed literal.
	 * 
	 * @param name
	 * @param value
	 * @param lang
	 * @return a reference to this object.
	 */
	public VariablesBinder bindLangedLiteral(String name, String value,
			String lang) {
		pss.setLiteral(spec.getVariableOfParameter(name), value, lang);
		return this;
	}

	/**
	 * Binds a value as IRI.
	 * 
	 * @param name
	 * @param value
	 * @return a reference to this object.
	 */
	public VariablesBinder bindIri(String name, String value) {
		pss.setIri(spec.getVariableOfParameter(name), value);
		return this;
	}

	/**
	 * Returns the query with the replaced bindings.
	 * 
	 * @return the query as String.
	 * 
	 */
	public String toString() {
		return toQuery().toString();
	}

	/**
	 * Returns the query with the replaced bindings.
	 * 
	 * @return the query as Query.
	 */
	public Query toQuery() {
		return pss.asQuery();
	}
}
