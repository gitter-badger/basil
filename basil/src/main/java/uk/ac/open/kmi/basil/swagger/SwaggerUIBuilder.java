package uk.ac.open.kmi.basil.swagger;

import javax.ws.rs.core.UriInfo;

/**
 * Created by Luca Panziera on 22/05/15.
 */
public class SwaggerUIBuilder {
    public static String build(UriInfo swaggerUri) {
    	// Base URL
    	StringBuilder bb = new StringBuilder();
    	bb.append(swaggerUri.getBaseUri().getHost());
    	if(swaggerUri.getBaseUri().getPort() > 0){
    		bb.append(":").append(swaggerUri.getBaseUri().getPort());
    	}
        String base = bb.toString();
    	
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n");
        html.append("<head>\n");
        html.append("  <title>Swagger UI</title>\n");
        html.append("  <link rel=\"icon\" type=\"image/png\" href=\"images/favicon-32x32.png\" sizes=\"32x32\" />\n");
        html.append("  <link rel=\"icon\" type=\"image/png\" href=\"images/favicon-16x16.png\" sizes=\"16x16\" />\n");
        html.append("  <link href='http://").append(base).append("/swagger-ui/css/typography.css' media='screen' rel='stylesheet' type='text/css'/>\n");
        html.append("  <link href='http://").append(base).append("/swagger-ui/css/reset.css' media='screen' rel='stylesheet' type='text/css'/>\n");
        html.append("  <link href='http://").append(base).append("/swagger-ui/css/screen.css' media='screen' rel='stylesheet' type='text/css'/>\n");
        html.append("  <link href='http://").append(base).append("/swagger-ui/css/reset.css' media='print' rel='stylesheet' type='text/css'/>\n");
        html.append("  <link href='http://").append(base).append("/swagger-ui/css/print.css' media='print' rel='stylesheet' type='text/css'/>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/jquery-1.8.0.min.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/jquery.slideto.min.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/jquery.wiggle.min.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/jquery.ba-bbq.min.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/handlebars-2.0.0.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/underscore-min.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/backbone-min.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/swagger-ui.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/highlight.7.3.pack.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/marked.js' type='text/javascript'></script>\n");
        html.append("  <script src='http://").append(base).append("/swagger-ui/lib/swagger-oauth.js' type='text/javascript'></script>\n");
        html.append("\n");
        html.append("  <script type=\"text/javascript\">\n");
        html.append("    $(function () {\n");
        html.append("      window.swaggerUi = new SwaggerUi({\n");
        html.append("        url: \"").append(swaggerUri.getRequestUri().toString()).append("\",\n");
        html.append("        dom_id: \"swagger-ui-container\",\n");
        html.append("        supportedSubmitMethods: ['get', 'post', 'put', 'delete', 'patch'],\n");
        html.append("        onFailure: function(data) {\n");
        html.append("          log(\"Unable to Load SwaggerUI\");\n");
        html.append("        },\n");
        html.append("        docExpansion: \"full\",\n");
        html.append("        apisSorter: \"alpha\"\n");
        html.append("      });\n");
        html.append("\n");
        html.append("      window.swaggerUi.load();\n");
        html.append("\t  \n");
        html.append("\n");
        html.append("      function log() {\n");
        html.append("        if ('console' in window) {\n");
        html.append("          console.log.apply(console, arguments);\n");
        html.append("        }\n");
        html.append("      }\n");
        html.append("  });\n");
        html.append("  </script>\n");
        html.append("</head>\n");
        html.append("\n");
        html.append("<body class=\"swagger-section\">\n");
        html.append("<div id=\"message-bar\" class=\"swagger-ui-wrap\">&nbsp;</div>\n");
        html.append("<div id=\"swagger-ui-container\" class=\"swagger-ui-wrap\"></div>\n");
        html.append("</body>\n");
        html.append("</html>");
        return html.toString();
    }
}
