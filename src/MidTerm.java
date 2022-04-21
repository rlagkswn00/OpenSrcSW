import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MidTerm {

    private String input_file = "./collection.xml";
    private String query;

    public MidTerm(String file, String query) {
        this.input_file = file;
        this.query = query;
    }

    public void showSnippet() throws ParserConfigurationException, IOException, SAXException {
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(input_file);

        Element root = document.getDocumentElement();
        NodeList docNodes = root.getElementsByTagName("doc");


        ArrayList result_array = new ArrayList();
        for (int i = 0; i < docNodes.getLength(); i++) {
            Node item = docNodes.item(i);
            Node prev_title = item.getFirstChild().getNextSibling(); //title node
            Node prev_menu = prev_title.getFirstChild(); // text in title node
            Node prev_bdy = ((Element) item).getElementsByTagName("body").item(0); // body node
            Node prev_recipe = prev_bdy.getFirstChild(); // text in body node

            String title = (String) prev_menu.getNodeValue();
            String body_text = (String) prev_recipe.getNodeValue();


            for (int j = 0; j < body_text.length() - 30; j++) {
                String sub = body_text.substring(j, j + 30);
                KeywordExtractor ke2 = new KeywordExtractor();
                KeywordList kl2 = ke.extractKeyword(sub, true);

                String result_title = new String();
                String result_body_text = new String();
                int point = 0;

                for (Keyword body_word : kl2) {
                    for (Keyword query_word : kl) {
                        if (body_word.getString().equals(query_word.getString())) {
                            //있으면
                            point++;
                            result_title = title;
                            result_body_text = sub;
                        }
                    }
                }
                if (point != 0) {
                    String result_text = result_title + "," + result_body_text + "," + point;
                    result_array.add(result_array.size(), result_text);
                }
            }
        }
        if (!result_array.isEmpty()) {
            for (int i = 0; i < result_array.size(); i++) {
                System.out.println(result_array.get(i));
            }
        }
    }
}