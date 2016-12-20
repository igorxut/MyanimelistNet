import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javafx.util.Pair;

public class randomProxy {

    private static ArrayList<Pair> list = new ArrayList<>();

    static {
        try {
            FileInputStream fstream = new FileInputStream("./proxylist.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String link = "https://myanimelist.net/";
            String strLine;
            while ((strLine = br.readLine()) != null){
                String[] temp = strLine.split(":");
                for (String i : temp) {
                    System.out.println(i);
                }
                try {
                    Connection connection = Jsoup.connect(link).ignoreHttpErrors(true).proxy(temp[0], Integer.parseInt(temp[1])).maxBodySize(0).timeout(1000);
                    Response response = connection.method(Method.GET).execute();
                    if (response.statusCode() == 200) {
                        list.add(new Pair<>(temp[0], Integer.parseInt(temp[1])));
                    }
                } catch (Exception e) {
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Pair<String, Integer> getRandomProxy() {
        return list.get((int) Math.floor(Math.random() * list.size()));
    }

    public static void main(String[] args) {
        System.out.println(list.size());
        for (Pair i : list) {
            System.out.println(i.toString());
        }
    }
}
