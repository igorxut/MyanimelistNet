import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Date;
import javafx.util.Pair;
import java.net.URL;
import javax.xml.parsers.*;
import org.w3c.dom.*;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;

public class mal_helper {

    public static void main(String[] args) {

        String site = "https://myanimelist.net";
        String file_error_log = "errors.tsv";
        String file_result = "result.tsv";
        String user = "XuT";

        ArrayList<Pair> user_list = new ArrayList<>();
        HashSet<Pair> visited_set = new HashSet<>();

        user_list.addAll(getList(user_list, user, site));

    }

    private static String getTime() {
        Date date = new Date();
        SimpleDateFormat time_format = new SimpleDateFormat("HH:mm:ss.SSSSS");
        return time_format.format(date);
    }

    private static ArrayList<Pair> getList(ArrayList<Pair> user_list, String user, String site) {

        for(String item_type : new String[] {"anime", "manga"}) {
            String link = site.concat("/malappinfo.php?u=").concat(user).concat("&status=all&type=").concat(item_type);
            System.out.println(getTime().concat("\tparsing of xml from url \"").concat(link).concat("\"."));
            try {
                DocumentBuilderFactory doc_builder_factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder doc_builder = doc_builder_factory.newDocumentBuilder();
                Document document = doc_builder.parse(new URL(link).openStream());
                document.getDocumentElement().normalize();
                NodeList nodelist = document.getElementsByTagName("series_animedb_id");

                for(int i = 0; i < nodelist.getLength(); i++) {
                    Node node = nodelist.item(i);
                    Element element = (Element) node;
                    user_list.add(new Pair<> (item_type, Integer.parseInt(element.getTextContent())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        System.out.println(getTime().concat("\tgetting list is complete."));
        return user_list;
    }

}
