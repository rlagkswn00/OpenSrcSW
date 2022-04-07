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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.HashMap;

public class searcher {
    private String input_file = "./index.post";
    private String query;

    public searcher(String file, String query) {
        this.input_file = file;
        this.query = query;
    }

    public int[] CalcSim(String[] kwrd_hashmap_string, Keyword[] kwrd, int[] tf) {
            return null;
    }

    public void searcher() throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
        // index.post Hash Map들고 오기
        FileInputStream fileStream = new FileInputStream(input_file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();

        //dom 파서로 collection.xml에 있는 title 값 따오기
        //이 값은 top3를 출력할 때 문자를 확인하기 위함
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse("./collection.xml");

        Element root = document.getDocumentElement();
        NodeList docNodes = root.getElementsByTagName("doc");

        String[] menus = new String[docNodes.getLength()];
        for (int i = 0; i < menus.length; i++) {
            Node item = docNodes.item(i);
            Element doc = (Element) item;
            menus[i] = doc.getElementsByTagName("title").item(0).getFirstChild().getNodeValue();
            //System.out.println(menus[i]);
        }

        //결과 top3가 저장될 string 공간
        //012 0번, 1번 2번 순으로 내림차순 정렬된 값 저장 예정

        int[] top3 = new int[3];

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(this.query, true);
        Keyword[] kwrd = new Keyword[kl.size()];

        //index.post에서 hashmap전체 뽑아내기
        HashMap<String, String> all_hashmap = (HashMap) object;

        //all_hashmap에서 쿼리문에서 나온 것들만 key로 검색해서 저장할 공간
        String[] kwrd_hashmap_string = new String[kl.size()];

        for (int j = 0; j < kl.size(); j++)
            kwrd[j] = kl.get(j);

        //쿼리에서 뽑아낸 키워드들의 tf들의 모임
        int[] tf = new int[kl.size()];

        //필요한 해시맵만 저장
        for (int i = 0; i < kl.size(); i++) {
            if(all_hashmap.containsKey(kwrd[i].getString())){
                kwrd_hashmap_string[i] = all_hashmap.get(kwrd[i].getString());
                tf[i] = kwrd[i].getCnt();
            }else{
                kwrd_hashmap_string[i] = "0 0.0 1 0.0 2 0.0 3 0.0 4 0.0";
            }
            // System.out.println(kwrd[i].getString() + " " + kwrd_hashmap_string[i] + " " + tf[i]);
        }

        //   for(String s : menus){
        //    System.out.println(s);
        // }
        top3 = CalcSim(kwrd_hashmap_string, kwrd, tf);
        if (top3[0] == -2 && top3[1] == -2 && top3[2] == -2) {
            System.out.println("검색된 문서가 없습니다.");
        } else {
            for (int i : top3) {
                if (i == -2) {
                    continue;
                } else {
                    System.out.println(menus[i]);
                }
            }
        }


/*
        //top3값에 따라 문자 출력
        for (int i = 0; i < top3.length(); i++) {
            System.out.println(menus[top3.charAt(i)-115]);
        }*/
    }
}
