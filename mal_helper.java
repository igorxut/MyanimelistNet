import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Date;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class searchRelatedLinks {

    public static void main(String[] args) throws IOException {

        String site = "https://myanimelist.net";
        String user = "user";
        String[] list_type = {"anime", "manga"};

        SimpleDateFormat time_format_print = new SimpleDateFormat("HH:mm:ss.SSSSS");
        SimpleDateFormat time_format_file = new SimpleDateFormat("HH-mm-ss");

        ArrayList<String> user_list = new ArrayList<>();
        HashSet<String> visited_set = new HashSet<>();
        PriorityQueue<String> queue = new PriorityQueue<>();

        String log;
        String link;
        Response page;
        try {
            File file_error_log = new File("./".concat(getTime(time_format_file)).concat("_").concat("errors.tsv"));
            File file_result = new File("./".concat(getTime(time_format_file)).concat("_").concat("result.tsv"));
            if (file_error_log.delete() && file_result.delete()) {
                log = getTime(time_format_print).concat("\tFiles deleted.");
                System.out.println(log);
            } else if (file_error_log.createNewFile() && file_result.createNewFile()) {
                log =getTime(time_format_print).concat("\tFiles created.");
                System.out.println(log);
            }
            FileWriter error_writer = new FileWriter(file_error_log, true);
            FileWriter result_writer = new FileWriter(file_result, true);

            for (String type : list_type) {
                link = site.concat("/malappinfo.php?u=").concat(user).concat("&status=all&type=").concat(type);
                page = getPage(link, error_writer, time_format_print);
                log = getTime(time_format_print).concat("\tTry to parse page \"").concat(link).concat("\".");
                System.out.println(log);
                ArrayList<String> temp_list = parsePage(page, type, time_format_print);
                user_list.addAll(temp_list);
                log = getTime(time_format_print).concat("\t").concat(Integer.toString(temp_list.size())).concat(" tasks added to queue.");
                System.out.println(log);
            }
            queue.addAll(user_list);
            String task;
            while ((task = queue.poll()) != null) {
                if (visited_set.contains(task)) {
                    log = getTime(time_format_print).concat("\tVisited_set already contains task \"").concat(task).concat("\". Go to next loop.");
                    System.out.println(log);
                    continue;
                } else {
                    visited_set.add(task);
                    log = getTime(time_format_print).concat("\tTask \"").concat(task).concat("\" added to visited_set.");
                    System.out.println(log);
                }
                String[] title = task.split("/");
                String title_type = title[0];
                String title_id = title[1];
                if (!user_list.contains(task)) {
                    log = title_type.concat("\t").concat(title_id).concat("\t").concat(site).concat("/").concat(task).concat("/");
                    writeFile(result_writer, log);
                    log = getTime(time_format_print).concat("\tTask \"").concat(task).concat("\" wrote to result_file.");
                    System.out.println(log);
                }
                link = site.concat("/").concat(task).concat("/");
                page = getPage(link, error_writer, time_format_print);
                if (page != null) {
                    log = getTime(time_format_print).concat("\tTry to parse page \"").concat(link).concat("\".");
                    System.out.println(log);
                    ArrayList<String> urls = parsePage(page, "title", time_format_print);
                    for (String item : urls) {
                        if (!visited_set.contains(item)) {
                            queue.add(item);
                            log = getTime(time_format_print).concat("\tTask \"").concat(item).concat("\" added to queue.");
                            System.out.println(log);
                        } else {
                            log = getTime(time_format_print).concat("\tVisited_set already contains task \"").concat(item).concat("\". Go to next loop.");
                            System.out.println(log);
                        }
                    }
                }

            }
            log = getTime(time_format_print).concat("\tQueue is empty, mission complete.");
            System.out.println(log);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String getTime(
            SimpleDateFormat time_format
    ) {
        Date date = new Date();
        return time_format.format(date);
    }

    private static void writeFile(
        FileWriter file,
        String text
    ) {
        try {
            file.write(text);
            file.append('\n');
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Response getPage(
            String link,
            FileWriter error_writer,
            SimpleDateFormat time_format
    ) {
        Response response = null;
        String log;
        try {
            log = getTime(time_format).concat("\tTry to get page \"").concat(link).concat("\".");
            System.out.println(log);
            Connection connection = Jsoup.connect(link).ignoreHttpErrors(true).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").maxBodySize(0).timeout(0);
            response = connection.method(Method.GET).execute();
            while (response.statusCode() != 200) {
                if (response.statusCode() == 404) {
                    log = getTime(time_format).concat("\tpage \"").concat(link).concat("\" not found.\tpage not found");
                    System.out.println(log);
                    writeFile(error_writer, log);
                    break;
                }
                connection = Jsoup.connect(link).ignoreHttpErrors(true).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").maxBodySize(0).timeout(0);
                response = connection.method(Method.GET).execute();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null) {
            log = getTime(time_format).concat("\tGet page \"").concat(link).concat("\" failed.");
            System.out.println(log);
        } else {
            log = getTime(time_format).concat("\tGet page \"").concat(link).concat("\" succeed.");
            System.out.println(log);
        }
        return response;
    }

    private static ArrayList<String> parsePage(
            Response response,
            String parse_type,
            SimpleDateFormat time_format
    ) {
        ArrayList<String> list = new ArrayList<>();
        try {
            Document document = response.parse();
            if (parse_type.equals("anime") || parse_type.equals("manga")) {
                Elements elements = document.getElementsByTag("series_".concat(parse_type).concat("db_id"));
                for (Element element : elements) {
                    list.add(parse_type.concat("/").concat(element.text()));
                }
            }
            if (parse_type.equals("title")) {
                Elements elements = document.select("table.anime_detail_related_anime a");
                for (Element element : elements) {
                    String[] link = element.attr("href").split("/", 4);
                    String item_type = link[1];
                    String item_id = link[2];
                    if (item_id.equals("")) {
                        continue;
                    }
                    if (item_type.equals("anime") || item_type.equals("manga")) {
                        list.add(item_type.concat("/").concat(item_id));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        String log = getTime(time_format).concat("\tParsing complete.");
        System.out.println(log);
        return list;
    }

}
