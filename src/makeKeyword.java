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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class makeKeyword {
    private String input_file;
    private String output_flie = "./index.xml";

    public makeKeyword(String file) {
        this.input_file = file;
    }

    public static File[] makeFileList(String path) {
        File dir = new File(path);
        return dir.listFiles();
    }

    public void convertXml() throws IOException, SAXException, ParserConfigurationException, TransformerException {


        //file open
        File file = new File(this.input_file);
        File files[] = makeFileList("file/");

        //initialize
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(file);
        Document resultDoc = docBuilder.newDocument();
        
        //외부 파일에 대한 참조 유/무 옵션 속성 설정
        resultDoc.setXmlStandalone(true);

        //init KeyWordExtractor
        KeywordExtractor ke = new KeywordExtractor();

        //파일 파싱 및 xml 구조 작성
        Element docs = resultDoc.createElement("docs");
        resultDoc.appendChild(docs);

        //root 엘리먼트 및 child node list 생성
        Element root = document.getDocumentElement();
        NodeList docNodes = root.getElementsByTagName("doc");

        for(int i = 0 ; i < docNodes.getLength() ; i ++) {
            Node item = docNodes.item(i);
            Node prev_title = item.getFirstChild().getNextSibling(); //title node
            Node prev_menu = prev_title.getFirstChild(); // text in title node
            Node prev_bdy = ((Element) item).getElementsByTagName("body").item(0); // body node
            Node prev_recipe = prev_bdy.getFirstChild(); // text in body node

            String before_kkma = (String)prev_recipe.getNodeValue();
            String after_kkma = new String();

            //extract keywords
            KeywordList kl = ke.extractKeyword(before_kkma,true);

            //create new string made by kkma
            for(int j = 0 ; j  < kl.size() ; j++){
                Keyword kwrd = kl.get(j);
                after_kkma += kwrd.getString()+":"+kwrd.getCnt()+"#";
            }

            //xml 구조생성
            Element doc = resultDoc.createElement("doc");
            docs.appendChild(doc);

            doc.setAttribute("id", String.valueOf(i));

            Element title = resultDoc.createElement("title");
            title.appendChild(resultDoc.createTextNode(prev_menu.getNodeValue()));
            doc.appendChild(title);

            Element body = resultDoc.createElement("body");
            body.appendChild(resultDoc.createTextNode(after_kkma));
            doc.appendChild(body);
        }
        //xml 파일 생성
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(resultDoc);
        StreamResult result = new StreamResult(new FileOutputStream(new File(this.output_flie)));

        //들여쓰기
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(source, result);
    }
}