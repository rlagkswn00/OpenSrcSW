import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class indexer {
    private String input_file = "./index.xml";
    private String output_flie = "./index.post";

    public indexer(String file) {
        this.input_file = file;
    }

    public void indexer() throws IOException, SAXException, ParserConfigurationException {

        //index.post 저장을 위한 파일스트림, 오브젝트파일스틑림 생성
        FileOutputStream fileStream = new FileOutputStream(this.output_flie);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);

        //이전에 만든 index.xml 파일 오픈
        File file = new File(this.input_file);

        //Dom 파서를 통해서 index.xml파일 내 문자 들고오기 위한 객체 생성
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(file);

        Element root = document.getDocumentElement();
        NodeList docNodes = root.getElementsByTagName("doc");

        //파일에 저장하기 전 해쉬맵 생성
        HashMap<String, ArrayList<HashMap>> key_id_freq_Hash = new HashMap();

        //5개의 문서에 대해 바디값 모으기
        for (int i = 0; i < docNodes.getLength(); i++) {
            Node item = docNodes.item(i);
            Node body = ((Element) item).getElementsByTagName("body").item(0); // body node
            Node body_text_node = body.getFirstChild(); // text in body node
            String id = ((Element) item).getAttribute("id");
            String body_text;

            //해당 번호의 body 내용을 string 배열에 저장
            body_text = body_text_node.getNodeValue();
            String[] splited_body_texts = body_text.split("#");

            for (String split : splited_body_texts) {
                ArrayList<HashMap> id_freq_ArrayList = new ArrayList();

                String[] key_freq = split.split(":"); //[0] : key [1] : freq
                String key = key_freq[0];
                // System.out.println("key = "+key);
                String freq = key_freq[1];
                HashMap id_freq = new HashMap();
                id_freq.put(id, freq);

                if (key_id_freq_Hash.containsKey(key)) {
                    //기존 키 존재
                    id_freq_ArrayList = key_id_freq_Hash.get(key);
                    id_freq_ArrayList.add(id_freq);
                    key_id_freq_Hash.put(key, id_freq_ArrayList);
                } else {
                    //세로 추가
                    id_freq_ArrayList.add(id_freq);
                    key_id_freq_Hash.put(key, id_freq_ArrayList);
                    //System.out.println(key_id_freq_Hash.keySet());
                }
     정          //for(int k = 0 ; k < id_freq_ArrayList.size() ; k++)
                //   System.out.println(i+" "+key_id_freq_Hash.get(key).get(0));

            }
        }

        // 정리된 자료를 가지고 결과 해시맵 작성
        HashMap<String, String> result_HashMap = new HashMap();

        for (String keyword : key_id_freq_Hash.keySet()) {
            float weight = 0.0f;
            float df = 0;
            float tf = 0;
            float N = docNodes.getLength();
            String value = new String();
            ArrayList<HashMap> arrayList = key_id_freq_Hash.get(keyword);
            int array_size = arrayList.size();

            String result = new String();

            //calculate weight
            for (int j = 0; j < docNodes.getLength(); j++) {
                df = 0;
                tf = 0;
                weight = 0;
                for (int i = 0; i < array_size; i++) {
                    if (arrayList.get(i).containsKey(Integer.toString(j))) {
                        tf = Integer.parseInt((String) arrayList.get(i).get(Integer.toString(j)));
                        df = array_size;
                    }
                }
                if (df != 0) {
                    weight = (float) (tf * Math.log(N / df));
                    weight = (float) (Math.round(weight * 100) / 100.0);
                }else {
                    weight =0.0f;
                }
                value += j + " " + weight + " ";
            }
           // System.out.println(keyword+ " -> " + value);
            result_HashMap.put(keyword,value);
        }


        //파일 스트림을 통해 index.post에 저장
        objectOutputStream.writeObject(result_HashMap);
        objectOutputStream.close();

    }

    //테스트 출력용 코드
    public void printHash() throws IOException, ClassNotFoundException {
        FileInputStream fileStream = new FileInputStream(output_flie);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();
        System.out.println(object.getClass());

        HashMap hashMap = (HashMap) object;
        Iterator<String> it = hashMap.keySet().iterator();

        while(it.hasNext()){
            String key = it.next();
            System.out.println(key+ " -> " + hashMap.get(key));
        }
    }

}
