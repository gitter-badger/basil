package uk.ac.open.kmi.basil.swagger;

import javax.servlet.http.HttpServlet;

import com.wordnik.swagger.config.ConfigFactory;
import com.wordnik.swagger.config.FilterFactory;
import com.wordnik.swagger.model.ApiInfo;

/**
 * Created by Luca Panziera on 14/03/15.
 */
public class Bootstrap extends HttpServlet {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static {
        // do any additional initialization here, such as set your base path programmatically as such:
        // ConfigFactory.config().setBasePath("http://www.foo.com/");

        // add a custom filter
        FilterFactory.setFilter(new CustomFilter());

        ApiInfo info = new ApiInfo(
                "BASIL API",                             /* title */
                "Tool for building Web APIs on top of SPARQL endpoints.",
                "",                  /* TOS URL */
                "",                            /* Contact */
                "Apache 2.0",                                     /* license */
                "http://www.apache.org/licenses/LICENSE-2.0" /* license URL */
        );

        ConfigFactory.config().setApiInfo(info);

    }
}
