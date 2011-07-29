import java.util.HashMap;
import java.util.concurrent.ThreadPoolExecutor;


public class Model {

    public static String domain = "http://www.google.com";


    public static HashMap<String, LinkInfo> linkMap = new HashMap<String, LinkInfo>();

    public static ThreadPoolExecutor linksToCheck ;
}
