package uk.ac.open.kmi.basil.view.rhino;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.open.kmi.basil.view.Items;

public class RhinoMediator extends ScriptableObject {
	private static Logger log = LoggerFactory.getLogger(RhinoMediator.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init(final Writer writer){
		try {
			// Initialize string builder
			final Writer bridge = writer;
			BaseFunction mf = new BaseFunction() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object call(Context cx, Scriptable scope,
						Scriptable thisObj, Object[] args) {

					for (Object o : args) {
						try {
							bridge.append(o.toString());
						} catch (IOException e) {
							cx.getErrorReporter().runtimeError(e.getMessage(), "", 0, "", 0);
							log.error(this.getClassName(), e);
						}
					}
					return thisObj;
				}
			};
			put("print", this, mf);
		} catch (SecurityException e) {
			log.error("", e);
		}
	}
	
	@Override
	public String getClassName() {
		return RhinoMediator.class.getName();
	}

	public void bindItems(Items items) throws Exception {
		put("items", this, new ItemsWrapper(items.call()));
	}

	class ItemsWrapper extends ScriptableObject {
		private static final long serialVersionUID = 1L;

		public ItemsWrapper(final Iterator<Map<String, String>> iterator) {
			
			put("hasNext", this, new BaseFunction() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object call(Context cx, Scriptable scope,
						Scriptable thisObj, Object[] args) {
					return iterator.hasNext();
				}
			});
			put("next", this, new BaseFunction() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object call(Context cx, Scriptable scope,
						Scriptable thisObj, Object[] args) {
					final Map<String, String> item = iterator.next();
					return new MapWrapper(item);
				}
			});
		}

		@Override
		public String getClassName() {
			return this.getClassName();
		}

	}

	class MapWrapper extends ScriptableObject {
		private static final long serialVersionUID = 1L;

		public MapWrapper(final Map<String, String> map) {
			for (Entry<String, String> e : map.entrySet()) {
				put(e.getKey(), this, e.getValue());
			}
		}

		@Override
		public String getClassName() {
			return getClassName();
		}
	}
}
