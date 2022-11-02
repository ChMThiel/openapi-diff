package org.openapitools.openapidiff.core.output;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.openapitools.openapidiff.core.model.ChangedOpenApi;

public class Markdown2HtmlRender implements Render {

  @Override
  public String render(ChangedOpenApi aDiff) {
    String md = new MarkdownRender().render(aDiff);
    MutableDataSet options = new MutableDataSet();
    // uncomment to set optional extensions
    //    options.set(
    //        Parser.EXTENSIONS,
    //        Arrays.asList(TablesExtension.create(), StrikethroughExtension.create()));
    // uncomment to convert soft-breaks to hard breaks
    options.set(HtmlRenderer.SOFT_BREAK, "<br />\n");
    Parser parser = Parser.builder(options).build();
    HtmlRenderer renderer = HtmlRenderer.builder(options).build();

    // You can re-use parser and renderer instances
    Node document = parser.parse(md);
    String html = renderer.render(document);
    return """
           <!DOCTYPE html>
           <html>
             <head>
               <meta charset="UTF-8">
               <title>Changelog</title>
               <link href="https://www.w3schools.com/w3css/4/w3.css" rel="stylesheet"/>
               <style>
                  :root {
                    --sans-font: -apple-system, BlinkMacSystemFont, "Avenir Next", Avenir,
                      "Nimbus Sans L", Roboto, Noto, "Segoe UI", Arial, Helvetica,
                      "Helvetica Neue", sans-serif;
                    --mono-font: Consolas, Menlo, Monaco, "Andale Mono", "Ubuntu Mono",  monospace;

                    --base-fontsize: 1.15rem;

                    --bg: #fff;
                    --accent-bg: #f5f7ff;
                    --text: #212121;
                    --text-light: #585858;
                    --border: #d8dae1;
                    --accent: #0d47a1;
                    --accent-light: #90caf9;
                    --code: #d81b60;
                    --preformatted: #444;
                    --marked: #ffdd33;
                    --disabled: #efefef;
                  }

                  html {
                    font-family: var(--sans-font);
                  }

                  body {
                    color: var(--text);
                    background: var(--bg);
                    font-size: var(--base-fontsize);
                    line-height: var(--line-height);
                    min-height: 100vh;
                    flex: 1;
                    margin: 0 auto;
                    max-width: 85rem;
                    padding: 0 0.5rem;
                    overflow-x: hidden;
                    word-break: break-word;
                    overflow-wrap: break-word;
                  }

                  header {
                    background: var(--accent-bg);
                    border-bottom: 1px solid var(--border);
                    text-align: center;
                    padding: 2rem 0.5rem;
                    width: 100vw;
                    position: relative;
                    box-sizing: border-box;
                    left: 50%;
                    right: 50%;
                    margin-left: -50vw;
                    margin-right: -50vw;
                  }

                  header h1,
                  header p {
                    margin: 0;
                  }

                  main {
                    padding-top: 1.5rem;
                  }

                  h1,
                  h2,
                  h3 {
                    line-height: 1.1;
                  }

                  nav {
                    font-size: 1rem;
                    padding: 1rem 0;
                  }

                  nav a {
                    margin: 1rem 1rem 0 0;
                    border: 1px solid var(--border);
                    border-radius: 5px;
                    color: var(--text) !important;
                    display: inline-block;
                    padding: 0.1rem 1rem;
                    text-decoration: none;
                    transition: 0.4s;
                  }

                  nav a:hover {
                    color: var(--accent) !important;
                    border-color: var(--accent);
                  }

                  nav a.current:hover {
                    text-decoration: none;
                  }

                  footer {
                    margin-top: 4rem;
                    padding: 2rem 1rem 1.5rem 1rem;
                    color: var(--text-light);
                    font-size: 0.9rem;
                    text-align: center;
                    border-top: 1px solid var(--border);
                  }

                  h1 {
                    font-size: calc(
                      var(--base-fontsize) * var(--header-scale) * var(--header-scale) *
                        var(--header-scale) * var(--header-scale)
                    );
                    margin-top: calc(var(--line-height) * 1.5rem);
                  }

                  h2 {
                    font-size: calc(
                      var(--base-fontsize) * var(--header-scale) * var(--header-scale) *
                        var(--header-scale)
                    );
                    margin-top: calc(var(--line-height) * 1.5rem);
                  }

                  h3 {
                    font-size: calc(
                      var(--base-fontsize) * var(--header-scale) * var(--header-scale)
                    );
                  }

                  h4 {
                    font-size: calc(var(--base-fontsize) * var(--header-scale) * 1.3);
                  }

                  h5 {
                    font-size: calc(var(--base-fontsize) * 1.3);
                  }

                  h6 {
                    font-size: calc(var(--base-fontsize) / var(--header-scale));
                  }

                         code,
                  pre,
                  pre span,
                  kbd,
                  samp {
                    font-size: 1.075rem;
                    font-family: var(--mono-font);
                    color: var(--code);
                  }

                  kbd {
                    color: var(--preformatted);
                    border: 1px solid var(--preformatted);
                    border-bottom: 3px solid var(--preformatted);
                    border-radius: 5px;
                    padding: 0.1rem;
                  }

                  pre {
                    padding: 1rem 1.4rem;
                    max-width: 100%;
                    overflow: auto;
                    overflow-x: auto;
                    color: var(--preformatted);
                    background: var(--accent-bg);
                    border: 1px solid var(--border);
                    border-radius: 5px;
                  }

                  pre code {
                    color: var(--preformatted);
                    background: none;
                    margin: 0;
                    padding: 0;
                  }
               </style>
             </head>
             <body>
               HTML_HERE
             </body>
           """
        + "</html>\n".replace("HTML_HERE", html);
  }
}
