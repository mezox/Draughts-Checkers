
package control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


/**
 * Stara sa o zapis tahov do dokumentu, z ktoreho je nasledne vypisovany
 * zoznam tahov do hlavneho okna aplikacie a na poziadanie je mozne tieto
 * tahy exportovat do xml.
 */
public class PrintMove {
    
    protected FileWriter out;
    protected Document document;
    protected Element root;
    protected int move_cnt = 0;
    protected String p1_move;
    protected String p2_move;
    
    /**
     * Vytvori xml dokument
     */
    public PrintMove()
    {
        this.document = DocumentHelper.createDocument();
        this.root = this.document.addElement( "root" );
    }

    /**
     * Po jednom tahu pridava do dokumentu. Tvar xml (priklad):
     * <move cnt=1><P1>a3-b4</P1><P2>b6-c5</P2></move>
     * @param move_str aktualny tah
     */
    public void appendToDocument(String move_str)
    {
        if (move_cnt % 2 == 0)
            this.p1_move = move_str;
        else
            this.p2_move = move_str;
        
        if (move_cnt % 2 == 1)
        {
            Element move = root.addElement( "move" )
            .addAttribute( "cnt", Integer.toString((move_cnt/2)+1));
            
            move.addElement( "P1" ).addText( p1_move );
            move.addElement( "P2" ).addText( p2_move );
        }   
        
        move_cnt++;
    }
    
    /**
     * Otvori novy subor, zapise do neho dokument s tahmi v tvare xml a subor zavrie.
     * @param filename 
     */
    public void writeToXml(File filename)
    {
        try {
            this.out = new FileWriter( filename );
            this.document.write( this.out );
            this.out.close();
        } catch (IOException ex) {
            Logger.getLogger(PrintMove.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Vracia aktualny tah, pripadne tahy oboch hracov v jednom kompletnom tahu.
     * @param mvcnt pocitadlo celkovych tahov
     * @return retazec v ktorom su zapisane tahy hracov, prip. jedneho hraca
     */
    public String writeToScreen(int mvcnt)
    {
        String move = null;
        if(mvcnt %2 == 1)
            move = "ťah " + Integer.toString(move_cnt/2) + ": " + p1_move + " " + p2_move;
        else {
            move = "ťah " + Integer.toString(move_cnt/2 + 1) + ": " + p1_move;
        }
        return move;
    }
    
    /**
     * Vracia posledny tah daneho hraca. Vyuzite v pripade vzdialenej hry na
     * poslanie svojho tahu druhemu klientovi - hracovi.
     * @param myPlayer cislo 1 pre prveho hraca(biely) a 2 pre druheho
     * @return posledny uskutocneny tah konkretneho hraca
     */
    public String getLastMove(int myPlayer)
    {       
        return myPlayer == 1 ? p1_move : p2_move;
    }
        
    public void resetDocument()
    {
        this.document.remove(this.root);
        this.root = this.document.addElement( "root" );
    }
    
    public void resetCnt()
    {
        move_cnt = 0;
    }
}