package control;

import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * Nacita xml subor do zoznamu (Listu), ktoreho obsah sa nasledne
 * zobrazi vedla sachovnice (zobrazia sa tahy hracov)
 */
public class ReadMove {
    
    private Document document;
    private Element root;
    
    private DefaultListModel moves_list;
    
    private Element P1;
    private Element P2;
    private Element elem;
    
    public ReadMove(File filename, DefaultListModel<String> m) throws IOException, DocumentException
    {
        m.clear();
        
        try{        
            SAXReader reader = new SAXReader();
            this.document = reader.read(filename);
            root = document.getRootElement();
            
            //Ziskam vsetky tahy       
            List moves = document.selectNodes("//move");
            Iterator iter = moves.iterator();        

            //Iterujem cez vsetky tahy
            while(iter.hasNext()){

                //Ziskam node
                elem = (Element)iter.next();
                
                //Atribut cnt v aktualnom node
                Attribute attr = elem.attribute("cnt");//(Attribute)elem;//.next();
                
                //Ziskam node P1
                Iterator P1it = elem.elementIterator("P1");
                P1 = (Element)P1it.next();
                
                //Ziskam node P2
                Iterator P2it = elem.elementIterator("P2");
                P2 = (Element)P2it.next();
                
                //System.out.println(P1.getText());
                //System.out.println(P2.getText());
                
                //ulozim si tah do layoutmodelu
                m.addElement("ťah " + attr.getValue()+ ": " + P1.getText() + " " + P2.getText());
            }
        }
        catch(DocumentException e){
            System.err.println(e.getMessage());
        }
    }
    
    public DefaultListModel getMoves()
    {
        return this.moves_list;
    }    
    
    public void close_file() throws IOException
    {
        //input.close();
    }
    
}