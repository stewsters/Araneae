import javax.swing.text.BadLocationException;
import javax.swing.text.EditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Scrape implements Runnable {
    String address;
    String referrer;

    public Scrape(String addr, String ref) {
        address = addr;
        referrer = ref;
    }

    public void run() {
        if (Model.linkMap.containsKey(address)) {
            //someone already checked this page
            //add our referrer
            Model.linkMap.get(address).registerReferrer(referrer);
            return;
        }


        if (!address.startsWith(Model.domain)) {
                    //foreign, do not scrape this page
            LinkInfo li = new LinkInfo(address,referrer);
            li.status = ScrapeStatus.ignored;
            Model.linkMap.put(address, li);
        } else {
            //local

            try {
                //get data from webpage.

                LinkInfo li = new LinkInfo(address,referrer);
                URL url = new URL(address);
                HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
                String type = urlConn.getContentType();

                li.responseCode = urlConn.getResponseCode();
                if (urlConn.getResponseCode() != 200) {
                    System.out.println("Dead: " + address + " with status " + urlConn.getResponseCode() + " referrer: " + referrer);
                }

                Model.linkMap.put(address, li);

                if (!type.startsWith("text/html")) {
                    //System.out.println("Not read: " + address + " because of content type " + type);
                    return;
                }
                parseStreamForLinks(urlConn.getInputStream());

            } catch (IOException e) {
                LinkInfo li = new LinkInfo(address,referrer);
                li.responseCode = 404;
                li.status = ScrapeStatus.invalid;
                Model.linkMap.put(address, li);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * This searches in input stream for a link
     * @param data
     * @throws IOException
     * @throws BadLocationException
     */
    private void parseStreamForLinks(InputStream data) throws IOException, BadLocationException {
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(data));

        //parse data for links

        EditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
        doc.putProperty("IgnoreCharsetDirective", new Boolean(true));
        kit.read(inputReader, doc, 0);

        HTMLDocument.Iterator it = doc.getIterator(HTML.Tag.A);
        while (it.isValid()) {
            SimpleAttributeSet s = (SimpleAttributeSet) it.getAttributes();

            String link = (String) s.getAttribute(HTML.Attribute.HREF);
            if (link != null) {

                if (link.startsWith("/")) {

                    link = Model.domain + link;
                }

                Scrape seed = new Scrape(link, address);
                Model.linksToCheck.execute(seed);

            }
            it.next();
        }
    }
}
