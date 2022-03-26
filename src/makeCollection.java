import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class makeCollection {
    private String data_path;
    private String output_flie = "./collection.xml";

    public makeCollection(String path) {
        this.data_path = path;
    }

    public static File[] makeFileList(String path) {
        File dir = new File(path);
        return dir.listFiles();
    }



    public void makeXml() throws ParserConfigurationException, TransformerException, IOException {
        //파일 리스트 받기
        File files[] = makeFileList(this.data_path);

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        Document document = docBuilder.newDocument();

        //외부 파일에 대한 참조 유/무 옵션 속성 설정
        document.setXmlStandalone(true);

        //파일 파싱 및 xml 구조 작성
        Element docs = document.createElement("docs");
        document.appendChild(docs);

        for (int i = 0; i < files.length; i++) {
            org.jsoup.nodes.Document html = Jsoup.parse(files[i], "UTF-8");

            Element doc = document.createElement("doc");
            docs.appendChild(doc);

            doc.setAttribute("id", String.valueOf(i));

            Element title = document.createElement("title");
            title.appendChild(document.createTextNode(html.title()));
            doc.appendChild(title);

            Element body = document.createElement("body");
            body.appendChild(document.createTextNode(html.body().text()));
            doc.appendChild(body);
        }

        //xml 파일 작성
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new FileOutputStream(new File(this.output_flie)));

        //들여쓰기
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        transformer.transform(source, result);


    }


}
