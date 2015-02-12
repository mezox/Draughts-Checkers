package gui;

import control.Manager;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;

import control.ReadMove;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Vector;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.DocumentException;

/**
 * Move List - zoznam tahov, zoznam ktory je vykreslovany
 * vpravo vedla sachovnice. Obsahuje tahy v notacii:
 * Tah c.1: c3-b4 b6-c5
 * Komunikuje s hlavnym oknom aj s Managerom, s Managerom
 * ohladom zapisu tahov.
 */
public class MList extends JFrame implements Listeners
{
    private JScrollPane listScroll; //scrollbar pre zoznam
    private JList<String> moves_list;       //container pre zoznam
    private DefaultListModel<String> moves; //defaultny zoznam tahov
    private ListSelectionModel listSelection;
    
    private Manager m;
    private Window wholeWindow;
    
    protected JButton chooseB;
    protected JButton addB;
    protected JButton playB;
    protected JButton cancelB;
    protected JButton addmoveB;
    protected JButton helpB;
    
    protected JLayeredPane w;
    protected JTextField addmoveF;
    
    boolean clickedPlay = false;
    boolean replaying = true;
    protected Thread thr;
    
    /**
     * Vykresluje JList a tlacitka Pridat tah, Vybrat partiu,
     * Prehrat, Zrusit a Napoveda
     * @param window      vrstva okna na ktorej lezia kompomenty
     * @param m           odkaz na triedy Manager
     * @param wholeWindow odkaz na triedu Window
     */
    public MList(JLayeredPane window, Manager m, Window wholeWindow)
    {
        this.w = window;
        this.m = m;
        this.wholeWindow = wholeWindow;
        
        //Vytvori zoznam, bude prazdny
        moves = new DefaultListModel<>();
        moves_list = new JList<>(moves);

        // nastavenie listenerov pre polozky v liste
        listSelection = moves_list.getSelectionModel();
        listSelection.addListSelectionListener(this);
        
        //Nastavi font, velkost pisma, hrubku
        moves_list.setFont(new Font("Calibri",Font.BOLD ,11));
        
        //vlozi scrollbar do zoznamu
        listScroll = new JScrollPane(moves_list);
        listScroll.setBounds(570, 50, 150, 350);
        window.add(listScroll);
        
        // textove pole pre zadanie dalsieho tahu
        addmoveF = new JTextField();
        addmoveF.setBounds(570, 410, 150, 20);
        window.add(addmoveF);
        addmoveF.addActionListener(this);
        
        /* Tlacitka pre pre pracu s partiami */
        addmoveB = new JButton("Pridať ťah");
        addmoveB.setBounds(570, 430, 150, 20);
        window.add(addmoveB);
        addmoveB.addActionListener(this);
        
        chooseB = new JButton("Vybrať partiu");
        chooseB.setBounds(570, 460, 150, 20);
        window.add(chooseB);
        chooseB.addActionListener(this);   

        playB = new JButton("Prehrať");
        playB.setBounds(570, 480, 150, 20);
        window.add(playB);
        playB.addActionListener(this);
        
        helpB = new JButton("Napoveda");
        window.add(helpB);
        helpB.setBounds(570, 530, 150, 20);
        helpB.addActionListener(this);
        
        cancelB = new JButton("Zrušiť");
        cancelB.setBounds(570, 500, 150, 20);
        window.add(cancelB);
        cancelB.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {
        // vyber subor s ulozenou partiou
        if (ae.getSource().equals(chooseB))
        {            
            //vytvori novu instanciu dialogu pre vyber suborou
            JFileChooser fileopen = new JFileChooser("examples");

            fileopen.setDialogTitle("Vyberte subor s partiou");

            //vytvori filtre pre pripony suborov, chceme podporovat XML a txt
            FileFilter filterXML = new FileNameExtensionFilter("XML", "xml");
            fileopen.addChoosableFileFilter(filterXML);
            FileFilter filterTXT = new FileNameExtensionFilter("txt", "txt");
            fileopen.addChoosableFileFilter(filterTXT);

            int ret = fileopen.showDialog(null,"Otvor");

            if (ret == JFileChooser.APPROVE_OPTION)
            {
                File file = fileopen.getSelectedFile();
                
                //Restart pri nacitani novej hry
                wholeWindow.restartGameG(true);

                try {
                    ReadMove read = new ReadMove(file,moves);
                }
                catch (IOException | DocumentException ex) {
                    Logger.getLogger(MList.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                try {
                    wholeWindow.replayGame(moves.elements(), false, null);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MList.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        // prehraj tahy v JListe
        else if (ae.getSource().equals(playB))
        {
            Runnable r = null;
            
            if (!moves.isEmpty())
            {
                if (replaying)
                {
                    m.getPrintMove().resetDocument();
                    wholeWindow.restartGameG(false);

                    // zakaz klikanie do listu
                    moves_list.setEnabled(false);
                    
                    replaying = false;
                    
                    // spust prehranie partie ako nove vlakno
                    r = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // prehraj hru nastavenou rychlostou vo window
                                wholeWindow.replayGame(moves.elements(), true, thr);

                                // povol klikanie do listu
                                moves_list.setEnabled(true);
                                
                                playB.setText("Reset figurok");
                                replaying = true;
                                
                                // index po ktory sa ma partia prehrat, chceme vsetko
                                // magic constant 1000, tolko tahov nikdy nebude
                                wholeWindow.toDrag = 1000;

                            } catch (InterruptedException ex) {
                                Logger.getLogger(MList.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                    thr = new Thread(r);
                    thr.start();         
                }

                // pozastav prehravanie    
                if(clickedPlay == true){
                    playB.setText("Prehrať");
                    clickedPlay = false;
                }
                // spust prehravanie
                else {
                    playB.setText("Pozastaviť");
                    clickedPlay = true;
                    
                    synchronized(thr) {
                        thr.notify();
                    }
                }                
            }
        }
        // pridaj tah textovo
        else if (ae.getSource().equals(addmoveB))
        {
            String loaded = addmoveF.getText();
            if (loaded.isEmpty())
            {
                addmoveF.setText("Nezadal si tah");
            }
            else {
                /* overi spravnost zadaneho tahu v pozadovanej notacii
                 * musi obsahovat - alebo x medzi jednotlivymi polickami
                 */
                if((loaded.charAt(2) == '-' || loaded.charAt(2) == 'x')) {
                    if( (loaded.charAt(0) >= 'a' && loaded.charAt(0) <= 'h') &&
                        (loaded.charAt(3) >= 'a' && loaded.charAt(3) <= 'h') && 
                        (loaded.charAt(1) >= 1 + '0' && loaded.charAt(1) <= 8 + '0') && 
                        (loaded.charAt(4) >= 1 + '0' && loaded.charAt(4) <= 8 + '0') ){
                        addmoveF.setText("ok");
                       
                        MouseEvent me = new MouseEvent(wholeWindow.squares[loaded.charAt(1) - '0' - 1][loaded.charAt(0) - 'a'], 0, 0, 0, 0, 0, 1, false);
                        wholeWindow.mouseClicked(me);
                        
                        me = new MouseEvent(wholeWindow.squares[loaded.charAt(4) - '0' - 1][loaded.charAt(3) - 'a'], 0, 0, 0, 0, 0, 1, false);
                        wholeWindow.mouseClicked(me);
                    }
                    else{ 
                        addmoveF.setText("Neplatny tah"); 
                    }
                }
                else {
                    addmoveF.setText("Neplatny tah");; 
                }
            }
        }
        // zobraz napovedu
        else if (ae.getSource().equals(helpB))
        {
            JDialog help = new JDialog(wholeWindow.getOwner(), "Napoveda");
            JPanel helpwin = new JPanel();

             //Vlozi okno do Jdialogu
            help.add(helpwin);

            JLabel title = new JLabel("Mozte vykonat tieto tahy: ");
            JTextArea movestext = new JTextArea(1,1);
            
            String helpprint = ""; 
            
            helpprint += wholeWindow.getPosMoves();
            movestext.setText(helpprint);
            movestext.setMargin(new Insets(10,50,10,50));
            
            movestext.setEditable(false);
            movestext.setBackground(Color.LIGHT_GRAY);

            //Prida elementy do okna
            helpwin.add(title);
            helpwin.add(movestext);

            help.setSize(200, 200);
            help.setVisible(true);
            help.setResizable(false);
            help.setBounds(400, 300, 200, 200);
            
            help.setDefaultCloseOperation(DISPOSE_ON_CLOSE );
        }
        // ukonci prehravanie
        if (ae.getSource().equals(cancelB))
        {   
            wholeWindow.speed = 0;
        }        
    }
    
    @Override
    public void valueChanged(ListSelectionEvent lse)
    {
        int index;
        ListSelectionModel lsm = (ListSelectionModel) lse.getSource();
        
        // ak bola hodnota zmenena - reaguje na jedno kliknutie mysi
        if ( lsm.getValueIsAdjusting() )
        {
            // zisti index elementu v liste
            index = lsm.getAnchorSelectionIndex();
            
            // ak je v liste zatial len jeden tah tak ho ignoruje
            if (moves.elementAt(index).toString().length() < 18)
                return;
            
            // vyber vsetky elementy od zaciatku az po zisteny index
            Enumeration en_items = moves.elements();
            Vector<String> items = new Vector<>();
            for (int i = 0; i <= index; i++)
                items.add( (String)en_items.nextElement() );

            // vytvor enumeration obsahujuci ziskane polozky
            final Enumeration enum_items = items.elements();
            
            // nastavi index po ktory tah sa ma partia prehrat
            wholeWindow.toDrag = index;
            
            // spust hru znovu
            m.getPrintMove().resetDocument();
            wholeWindow.restartGameG(false);
                       
            try {
                wholeWindow.replayGame(enum_items, false, null);
            } catch (InterruptedException ex) {
                Logger.getLogger(MList.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    public void addToList(int mvcnt)
    {
        if(mvcnt %2 == 0){
            this.moves.addElement(m.getPrintMove().writeToScreen(mvcnt) );
            moves_list.setEnabled(false);
        }
        else{
            this.moves.setElementAt(m.getPrintMove().writeToScreen(mvcnt), mvcnt / 2);
            moves_list.setEnabled(true);
        }

        this.moves_list.revalidate();
        this.moves_list.repaint();
    }
    
    public DefaultListModel getListModel()
    {
        return this.moves;
    }
        
    public JList getMovesList()
    {
        return this.moves_list;
    }
    
    public boolean getClickedPlay()
    {
        return this.clickedPlay;
    }
}
