//import java.io.File;
//import java.io.IOException;
//import java.io.FileWriter;
//import java.net.MalformedURLException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.PriorityQueue;
//import java.util.Date;
//import javafx.util.Pair;
//import java.net.URL;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import org.w3c.dom.NodeList;
//import org.w3c.dom.Node;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//
//import org.jsoup.Jsoup;
////import org.jsoup.nodes.Document;
//
//public class mal_helper {
//
//    public static void main(String[] args) throws IOException {
//
//        URL site = new URL("https://myanimelist.net");
//        String user = "XuT";
//
//        String file_error_name = "errors.tsv";
//        String file_result_name = "result.tsv";
//
//        SimpleDateFormat time_format_print = new SimpleDateFormat("HH:mm:ss.SSSSS");
//        SimpleDateFormat time_format_file = new SimpleDateFormat("HH-mm-ss");
//
//        ArrayList<Pair> user_list = new ArrayList<>();
//        HashSet<Pair> visited_set = new HashSet<>();
//        PriorityQueue<Pair> queue = new PriorityQueue<>();
//
//        user_list.addAll(getList(user_list, user, site, time_format_print));
//        queue.addAll(user_list);
//        Pair element;
//
//        try {
//            File file_error_log = new File("./".concat(getTime(time_format_file)).concat("_").concat(file_error_name));
//            File file_result = new File("./".concat(getTime(time_format_file)).concat("_").concat(file_result_name));
//            if (file_error_log.delete() && file_result.delete()) {
//                System.out.println(getTime(time_format_print).concat("\tfiles is deleted."));
//            } else if (file_error_log.createNewFile() && file_result.createNewFile()) {
//                System.out.println(getTime(time_format_print).concat("\tfiles is created."));
//            }
//            FileWriter error_writer = new FileWriter(file_error_log, true);
//            FileWriter result_writer = new FileWriter(file_result, true);
//
//            while ((element = queue.poll()) != null) {
//                if (visited_set.contains(element)) {
//                    continue;
//                } else if (!user_list.contains(element)) {
//                    writeFile(result_writer,
//                            element.getKey().toString()
//                                    .concat("\t")
//                                    .concat(element.getValue().toString())
//                                    .concat("\t")
//                                    .concat(site.toString())
//                                    .concat("/")
//                                    .concat(element.getKey().toString())
//                                    .concat("/")
//                                    .concat(element.getValue().toString())
//                    );
//                    visited_set.add(element);
//                    System.out.println(
//                            getTime(time_format_print)
//                                    .concat(element.toString())
//                                    .concat("\tadded to visited_set.")
//                    );
//                }
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private static void writeFile(
//        FileWriter file,
//        String text
//    ) {
//        try {
//            file.write(text);
//            file.append('\n');
//            file.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static String getTime(
//        SimpleDateFormat time_format
//    ) {
//        Date date = new Date();
//        return time_format.format(date);
//    }
//
//    private static ArrayList<Pair> getList(
//        ArrayList<Pair> user_list,
//        String user,
//        URL site,
//        SimpleDateFormat time_format_print
//    ) {
//
//        try {
//            for(String item_type : new String[] {"anime", "manga"}) {
//                URL link = new URL(
//                        site.toString()
//                                .concat("/malappinfo.php?u=")
//                                .concat(user)
//                                .concat("&status=all&type=")
//                                .concat(item_type)
//                );
//                System.out.println(
//                        getTime(time_format_print)
//                                .concat("\tparsing of xml from url \"")
//                                .concat(link.toString())
//                                .concat("\".")
//                );
//                try {
//                    DocumentBuilderFactory doc_builder_factory = DocumentBuilderFactory.newInstance();
//                    DocumentBuilder doc_builder = doc_builder_factory.newDocumentBuilder();
//                    Document document = doc_builder.parse(link.openStream());
//                    document.getDocumentElement().normalize();
//                    NodeList nodelist = document.getElementsByTagName("series_animedb_id");
//
//                    for(int i = 0; i < nodelist.getLength(); i++) {
//                        Node node = nodelist.item(i);
//                        Element element = (Element) node;
//                        user_list.add(new Pair<> (item_type, Integer.parseInt(element.getTextContent())));
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//            System.out.println(getTime(time_format_print).concat("\tgetting list is complete."));
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        return user_list;
//    }
//
//    private static ???? getPage(
//        URL site,
//        Pair element,
//        FileWriter error_writer,
//        FileWriter result_writer
//    ) {
//        try {
//            org.jsoup.nodes.Document doc = Jsoup.connect(
//                    site.toString()
//                            .concat("/")
//                            .concat(element.getKey().toString())
//                            .concat("/")
//                            .concat(element.getValue().toString())
//                            .concat("/")
//            ).get();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//}
