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
        //query_id곱 결과 저장 배열 i번째 문서에 들어있는 내용과 쿼리의 결과문
        int[] top3 = new int[3];

        for (int i = 0; i < top3.length; i++)
            top3[i] = -1;

        float[] sum_innerProduct = new float[5];
        int[] temp_sum = new int[kwrd_hashmap_string.length];
        //문자 스플릿 하여 해당\
        HashMap<Keyword, String[]> splited_values = new HashMap<Keyword, String[]>();

        for (int i = 0; i < kwrd_hashmap_string.length; i++) {
            String[] splited_value = kwrd_hashmap_string[i].split(" ");
            splited_values.put(kwrd[i], splited_value);
        }

//        저장
        for (int k = 0; k < sum_innerProduct.length; k++) {
            float innerproduct = 0f;

            for (int i = 0; i < kwrd_hashmap_string.length; i++) {
                // for(String s : splited_value)
                //  System.out.println(s);
                String[] tmp = splited_values.get(kwrd[i]);
                float weight = Float.parseFloat(tmp[2 * k + 1]);
                innerproduct += (tf[i] * weight);
                //    System.out.println(weight + " " + tmp[2 * k + 1] + " " + i);

            }
            sum_innerProduct[k] += innerproduct;
            //  System.out.println("sum_innerproduckt = "+k+" "+sum_innerProduct[k]);
        }

        float original_innerProduct[] = new float[sum_innerProduct.length];
        for (int i = 0; i < sum_innerProduct.length; i++) {
            original_innerProduct[i] = sum_innerProduct[i];
        }
        Arrays.sort(sum_innerProduct); // 오름차순 정렬 한다.

        for (int i = sum_innerProduct.length - 1; i > sum_innerProduct.length - 4; i--) {
            float sorted_num = sum_innerProduct[i];
            if (sorted_num == 0) {
                top3[sum_innerProduct.length - 1 - i] = -2;
                continue;
            }
            for (int j = 0; j < original_innerProduct.length; j++) {
                float original_id_num = original_innerProduct[j];
                if (original_id_num == sorted_num) {
                    top3[sum_innerProduct.length - 1 - i] = j;
                    original_innerProduct[j] = -1;
                    break;
                }
            }
        }

        return top3;
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
