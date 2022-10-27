package org.openapitools.openapidiff.core.output;

import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import java.util.Arrays;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class Markdown2HtmlRender implements Render {

  @Override
  public String render(ChangedOpenApi aDiff) {
    String md = new MarkdownRender().render(aDiff);
    MutableDataSet options = new MutableDataSet();
    // uncomment to set optional extensions
    options.set(
        Parser.EXTENSIONS,
        Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));
    // uncomment to convert soft-breaks to hard breaks
    options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
    Parser parser = Parser.builder(options).build();
    HtmlRenderer renderer = HtmlRenderer.builder(options).build();

    // You can re-use parser and renderer instances
    Node document = parser.parse(md);
    return renderer.render(document);
  }
}
