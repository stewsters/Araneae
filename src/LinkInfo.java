import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LinkInfo {

    String url;
    public ScrapeStatus status;
    int responseCode;

    Set<String> placesThatLinkedHere;

    LinkInfo(String urls, String referrer) {
        url = urls;
        placesThatLinkedHere = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
        placesThatLinkedHere.add(referrer);
    }


    public void registerReferrer(String referrer) {
        placesThatLinkedHere.add(referrer);
    }


}