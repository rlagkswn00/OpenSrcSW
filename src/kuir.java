import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class kuir {

    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, SAXException, ClassNotFoundException {

        String command = args[0];
        String path = args[1];
        String query = null;
        if(args.length>2)
            query = args[3];

        if(command.equals("-c")) {
            makeCollection collection = new makeCollection(path);
            collection.makeXml();
        }
        else if(command.equals("-k")) {
            makeKeyword keyword = new makeKeyword(path);
            keyword.convertXml();
        }
        else if(command.equals("-i")) {
            indexer indexer = new indexer(path);
            indexer.indexer();
           //indexer.printHash();
        }else if(command.equals("-s")) {
            searcher searcher= new searcher(path,query);
            searcher.searcher();

        }else if(command.equals("-m")) {
            MidTerm midterm = new MidTerm(path,query);
            midterm.showSnippet();

        }
    }
}