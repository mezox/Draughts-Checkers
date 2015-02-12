package gui;

import control.Manager;
import basis.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import static java.awt.Component.CENTER_ALIGNMENT;

/**
 * Hlavne okno aplikacie. Spusta celu hru - vytvara Managera, 
 * ktory sa stara o logiku aplikacie a vykresluje gragicke
 * komponenty (sachovnicu) na zaklade logiky z Managera.
 * Realizuje prekreslovanie sachovnice pri tahoch, tahy sa vsak
 * testuju v logike. Pri vyhadzovani figurok k zmazaniu obrazku 
 * figurky vyuziva triedu DeletedFigure.
 */
public class Window extends JFrame implements MouseListener, ActionListener
{
    protected Manager m;
    protected int dim;
    protected boolean drag = false;
    protected Position drag_position;
    protected Component drag_image;
    protected Container drag_container;
        
    protected JLabel drag_label;
    protected Icon drag_label_img;
    
    ImageIcon image1;
    ImageIcon image2;
    ImageIcon image3;
    ImageIcon image4;
    ImageIcon image5;

    protected JPanel board;
    protected JScrollPane listScroll;
    protected JLayeredPane window;
    protected JPanel[][] squares;
    protected MList list;
    protected int mvcnt = 0;
    protected JLabel gameType;
    protected JLabel whosTurn;
    protected JLabel help;
    protected JLabel port;
    
    protected String posMoves;
    protected int speed = 600;
    protected int toDrag = 100;
    
    protected JTextField address;
    protected JTextField port_text;
    protected JButton go;

    protected boolean changeturn = false;

    protected JDialog connection;
    protected boolean remoteDrag = false;
    protected boolean remotePlay = false;
    protected int myPlayer = 1;
    
    protected boolean AIturn = true;
    
    /**
     * Vytvori hlavne okno aplikacie, hraciu plochu, vytvara Managera, ktori
     * vytvori a inicializuje logiku.
     */
    public Window()
    {
        super("Dama");
        Dimension boardSize = new Dimension(500, 500);
                
        window = new JLayeredPane();
        getContentPane().add(window);
        
        m = new Manager(this);
        dim = m.getDimension();
        Position[][] d = m.getDesk().getDesk2D();
        
        //Vytvori JLabel pre typ hry
        gameType = new JLabel("Hra dvoch hráčov");
        window.add(gameType);
        gameType.setBounds(50, 8, 200, 20);
        
        //Vytvori JLabel pre tah hraca
        whosTurn = new JLabel("Na ťahu je 1. hráč");
        window.add(whosTurn);
        whosTurn.setBounds(450, 8, 200, 20);
        
        //Vytvori JLabel pre napovedu tahu
        help = new JLabel();
        window.add(help);
        help.setBounds(300, 550, 300, 20);
        
        //Vytvori JLabel pre cislo portu mojej aplikacie
        port = new JLabel();
        window.add(port);
        port.setBounds(570, 30, 150, 20);
        
        board = new JPanel();
        //board.setPosition(50,100);
        window.add(board, JLayeredPane.DEFAULT_LAYER);
        board.setLayout( new GridLayout(dim, dim) );
        //board.setPreferredSize( boardSize );
        board.setBounds(50, 50, boardSize.width, boardSize.height);

        squares = new JPanel[dim][dim];
        
        image1 = new ImageIcon("lib/graphics/pawn1.png");
        image2 = new ImageIcon("lib/graphics/pawn2.png");
        image3 = new ImageIcon("lib/graphics/pawn3.png");
        image4 = new ImageIcon("lib/graphics/queen1.png");
        image5 = new ImageIcon("lib/graphics/queen2.png");

        for (int i = 0; i < dim; i++)
            for (int j = 0; j < dim; j++)
            {                     
                squares[i][j] = new JPanel(new BorderLayout() );
                
                board.add(squares[i][j]);
                
                // priradenie reakcie
                squares[i][j].addMouseListener(this);
                squares[i][j].setName( d[i][j].getColl() + Integer.toString(d[i][j].getRow()) );
                
                // nastavenie farby policok
                if ( d[i][j].getColor() == 1 )
                    squares[i][j].setBackground(Color.black);
                else
                    squares[i][j].setBackground(Color.white );
                
                // zobrazenie figurok
                if ( d[i][j].getFigure() != null && d[i][j].getFigure().getPlayer().getColor() == 1 )
                {
                    JLabel label = new JLabel("", image1, JLabel.CENTER);
                    squares[i][j].add( label, BorderLayout.CENTER );
                }
                else if ( d[i][j].getFigure() != null && d[i][j].getFigure().getPlayer().getColor() == 2 )
                {
                    JLabel label = new JLabel("", image2, JLabel.CENTER);
                    squares[i][j].add( label, BorderLayout.CENTER );
                }
                                
                squares[i][j].setVisible(true);
            } 
        
        //vytvorim list so scrollbarom
        list = new MList(window, m, this);
        
        /* Vytvor menu */
        WindowMenu menu = new WindowMenu(m, this);
        setJMenuBar(menu.getBar());
        
        if (System.getProperty("os.name").contains("Linux"))
            setSize(795,620);
        else
            setSize(800,650);
        
        setVisible(true);
    }
    
    /**
     * Restartuje hru do povodneho stavu, vymaze vsetky figurky a inicializuje
     * nove.Vola metodu restartGame() z triedy Manager, ktora restartuje logiku.
     * @param deleteList vymazanie listu s pouzitymi tahmi
     */
    public void restartGameG(boolean deleteList)
    {
        //zresetuje list
        if(deleteList == true)
            list.getListModel().clear();

        m.getPrintMove().resetCnt();
        mvcnt = 0;
                
        Position[][] d = m.getDesk().getDesk2D();
        m.restartGame();
        
        for (int i = 0; i < dim; i++)
            for (int j = 0; j < dim; j++)
            {
                squares[i][j].removeAll();
                
                // zobrazenie figurok
                if ( d[i][j].getFigure() != null && d[i][j].getFigure().getPlayer().getColor() == 1 )
                {
                    JLabel label = new JLabel("", image1, JLabel.CENTER);
                    squares[i][j].add( label, BorderLayout.CENTER );
                }
                else if ( d[i][j].getFigure() != null && d[i][j].getFigure().getPlayer().getColor() == 2 )
                {
                    JLabel label = new JLabel("", image2, JLabel.CENTER);
                    squares[i][j].add( label, BorderLayout.CENTER );
                }
            }
        this.board.revalidate();
        this.board.repaint();

        this.board.setEnabled(true);
        this.list.getMovesList().setEnabled(true);
        
        drag = false;
         
    }
    
    /**
     * Prehra partiu na zaklade dodanych tahov.
     * @param drags Enumeration (pole) jednotlivych tahov.
     *              Notacia tahu: "Tah 1: c3-d4 b6-c5"
     */
    public synchronized void replayGame(Enumeration drags, boolean slowly, Thread thr) throws InterruptedException
    {
        int enum_cnt = 0;
        Desk d = m.getDesk();
        mvcnt--;
        
        // zablokuj hraciu plochu
        this.board.setEnabled(false);
         
        while (drags.hasMoreElements())
        {
            // pozastavenie prehravania
            if (list.getClickedPlay() == false)
                if (thr != null)
                    synchronized(thr) {
                        try {
                            thr.wait();
                        }
                        catch (InterruptedException e) {
                        }
                    }

            // prehravanie do urciteho tahu
            if (toDrag < enum_cnt)
                break;
                        
            // rozparsuj zapis tahu
            String round = (String) drags.nextElement(); 
            round = round.substring( round.indexOf(":") + 2 );
            String [] movs_both_players = round.split(" ");
            
            // cyklus pre hracov - bezi teda 2x v jednom tahu
            for (int i = 0; i < movs_both_players.length; i++)
            {
                // ziskaj pozicie tahu jedneho hraca
                String [] movs = movs_both_players[i].split("-|x");
                Position first = d.getPositionAt(movs[0].charAt(0), movs[0].charAt(1) - '0');
                Position second = d.getPositionAt(movs[1].charAt(0), movs[1].charAt(1) - '0');    

                // sprav logicky tah    
                m.turn(first, second);

//                System.out.println( "ciselne: " + (first.getColl() - 'a') + Integer.toString( first.getRow() - 1) );
//                System.out.println( "povodne: " + first.getColl() + Integer.toString( first.getRow() - 1 ) + "   " + second.getColl() + Integer.toString( second.getRow() - 1 ));
//                System.out.println( "squares: " + squares[first.getRow() - 1][first.getColl() - 'a'].getName() + "    " + squares[second.getRow() - 1][second.getColl() - 'a'].getName() );

                // ziskaj label (obrazok) figurky a vymaz ho z povodnej pozicie
                JLabel gfig = (JLabel) squares[first.getRow() - 1][first.getColl() - 'a'].getComponent(0);
                squares[first.getRow() - 1][first.getColl() - 'a'].remove(0);
                
                // normalny move, nastav ulozeny obrazok novej pozicii
                if (m.getDeletedFigure().getQueen() == null)
                    squares[second.getRow() - 1][second.getColl() - 'a'].add(gfig);
                else    
                // PawnForQueen, nastav obrazok damy na zaklade hraca
                {
                    m.getDeletedFigure().setQueenNull();
                    
                    if ( gfig.getIcon().equals(image1) )
                        squares[second.getRow() - 1][second.getColl() - 'a'].add(new JLabel("", image4, JLabel.CENTER));
                    else
                        squares[second.getRow() - 1][second.getColl() - 'a'].add(new JLabel("", image5, JLabel.CENTER));
                }
         
                // zisti ci bola vyhodena nejaka figurka, ak ano vymaz obrazok
                if (m.getDeletedFigure().getFigure() != null)
                {
                    Position dp = m.getDeletedFigure().getFigure().getPosition();
                    m.getDeletedFigure().setFigureNull();
                    squares[ dp.getRow() - 1 ][ dp.getColl() - 'a' ].removeAll();
                }
                
                // pripocitaj cislo tahu, prekresli
                mvcnt++;
                this.board.revalidate();
                this.board.repaint();
                
//                m.printDesk(m.getDesk().getDesk2D());
                
                if (slowly)
                {
                    thr.sleep(speed);
                    list.getMovesList().setSelectedIndex(enum_cnt);
                }
            }
            enum_cnt++;
        }
        // ak v ulozenej partii bol posledny hrac 1, tak uprav mvcnt
        if (mvcnt % 2 == 1)
            mvcnt++;
        
        // ak je oznaceny posledny tah z listu tak povol hranie
        if (enum_cnt == list.getListModel().getSize())
            this.board.setEnabled(true);
        
        if (remotePlay)
            if (myPlayer == 2)
            {
                Runnable rr = new Runnable() {
                                @Override
                                public void run() {
                                  try {
                                        doRemoteMove(m.getPrintMove().getLastMove(myPlayer));
                                                
                                        board.revalidate();
                                        board.repaint();
                                    } catch (IOException ex) {
                                        Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                                        
                                        // spojenie bolo prerusene, toz resetujme hru
                                        
                                    }
                                }
                            };
                            Thread s = new Thread(rr);
                            s.start();  
                            
                            this.board.setEnabled(false);
            }
            else
                remoteDrag = false;
    }
    
    /**
     * Reakcia na kliknutie mysou. Spracuje klik nad jednotlivymi
     * JPanelmi reprezenutujucimi policka sachovnice. Prve kliknutie
     * oznaci figurku (ak ju policko obsahuje). V druhom kliknuti na ine
     * policko potom figurku presunie (ak je tento tah platny).
     * @param me MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent me)
    {      
        // ak je hracia plocha disabled tak ignoruj mouse event
        if (!this.board.isEnabled())
            return;
        
        // ziskaj suradnice policka na ktore sa prave kliklo
        Container cc = (Container) me.getSource();
        char coll = cc.getName().charAt(0);
        int row = cc.getName().charAt(1) - '0';
  
        // ziskaj poziciu na doske na zaklade ziskanych suradnic
        Position position = m.getDesk().getPositionAt(coll, row);

        // tahaj figurkou oznacenou v predoslom volani metody    
        if (drag)
        {   
            // zisti povinne tahy
            // vsetky povinne tahy sa appenduju do stringu posMoves pre prip. vypis napovedy
            posMoves = "";
            boolean mandatory_ok = false;

            // zoznam 2D poli (dvojic) povodna pozicia -> nova pozicia
            List<Position[]> possible_moves = m.checkMoves(m.getTurn());
            for (int i = 0; i < possible_moves.size(); i++)
            {
                posMoves +=  possible_moves.get(i)[0].getColl() + Integer.toString(possible_moves.get(i)[0].getRow()) + " -> ";  
                posMoves += possible_moves.get(i)[1].getColl() + Integer.toString(possible_moves.get(i)[1].getRow()) + "\n"; 
            
                // test ci sedia pozicie odkial a kam je figurka presuvana
                if (possible_moves.get(i)[0].equals(drag_position) && possible_moves.get(i)[1].equals(position))
                    mandatory_ok = true;
            }
            
            // zisti povinne tahy
            if  (possible_moves.size() > 0 && mandatory_ok == false)
            {
                help.setText("Máte možnosť vyhodiť súperovu figúrku");

                // policku nastavime povodnu figurku
                this.drag_label.setIcon( this.drag_label_img );
                this.drag_container.revalidate();
                this.drag_container.repaint();
            }
            else
            {   
                help.setText("");
                
                // platny tah
                if (m.turn(this.drag_position, position))
                {
                    // label pre vytvorenie noveho obrazku
                    JLabel label;   

                    // odstran figurky zo stareho policka
                    this.drag_container.remove(0);

                    // normalny tah
                    if (m.getDeletedFigure().getQueen() == null)
                    {
                        // vytvor taku istu figurku na novom policku
                        label = new JLabel("", this.drag_label_img, JLabel.CENTER);
                        cc.add( label, BorderLayout.CENTER );
                    }
                    else
                    // PawnForQueen
                    {
                        m.getDeletedFigure().setQueenNull();

                        // vytvor damu na zaklade hraca, kt. patrila povodna figurka
                        if ( this.drag_label_img.equals(image1) )
                            label = new JLabel("", image4, JLabel.CENTER);
                        else
                            label = new JLabel("", image5, JLabel.CENTER);

                        cc.add( label, BorderLayout.CENTER );
                    }

                    // zisti ci bola vyhodena nejaka figurka, ak ano, zmaz
                    if (m.getDeletedFigure().getFigure() != null)
                    {
                        Position dp = m.getDeletedFigure().getFigure().getPosition();
                        squares[ dp.getRow() - 1 ][ dp.getColl() - 'a' ].removeAll();

                        // koniec hry
                        if (m.getDeletedFigure().getFigure().getPlayer().getFiguresNum() == 0)
                            gameOver();

                        m.getDeletedFigure().setFigureNull();
                        
                        // uchovam si skutocnost ze sa vyhadzovalo
                        changeturn = true;
                    }
                    else
                        changeturn = false;
                    
                    // IMPLEMENTACIA VIACSKOKOV
                    // ------------------------
                    // v pripade ze sa vyhadzovalo otestujem
   /*                 if (changeturn)
                    {
                        // ziskam povinne tahy
                        possible_moves = m.checkMoves(!m.getTurn());

                        posMoves = "";
                        mandatory_ok = false;
                        for (int i = 0; i < possible_moves.size(); i++)
                        {
                            posMoves +=  possible_moves.get(i)[0].getColl() + Integer.toString(possible_moves.get(i)[0].getRow()) + " -> ";  
                            posMoves += possible_moves.get(i)[1].getColl() + Integer.toString(possible_moves.get(i)[1].getRow()) + "\n"; 
                        
                            // test ci pozicia kam som sa posunul nema nejake povinny tah
                            if (possible_moves.get(i)[0].equals(position))
                                mandatory_ok = true;
                        }
                        
                        // hrac moze, resp. musi spravit dvoj alebo viacskok
                        if (mandatory_ok)
                        {
                            m.setTurn(!m.getTurn());
                        }
                    }
    */                // ------------------------
                    
                    // vypis aktualneho hraca na tahu
                    if(m.getTurn() == true)
                        whosTurn.setText("Na ťahu je 1. hráč");
                    else
                        whosTurn.setText("Na ťahu je 2. hráč");
                    
                    // prida tah do zoznamu v okne
                    list.addToList(mvcnt++);

                    this.board.revalidate();
                    this.board.repaint();
                    
//                    m.printDesk(m.getDesk().getDesk2D());
                    
                    // vzdialena hra, odoslem tah a budem cakat na superov tah
                    if (remotePlay)
                    {
                        if (remoteDrag == false)
                        {          
                            // odosli svoj tah druhemu klientovi
                            m.getNetwork().sendDrag(m.getPrintMove().getLastMove(myPlayer));
                            
                            drag = false;

                            // superov tah ziskany po sieti spracuje samostatne vlakno
                            // inak totiz vykresli tah az ked ziska aj ten superov
                            // invokelater = vlakno sa vytvori az ked dobehne main vlakno
                /*            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                  try {
                                      try {
                                          Thread.currentThread().join();
                                      } catch (InterruptedException ex) {
                                          Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                                      }
                                        doRemoteMove(m.getPrintMove().getLastMove(myPlayer));
                                    } catch (IOException ex) {
                                        Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            });
                   */         
                            // superov tah ziskany po sieti spracuje samostatne vlakno, aby
                            // nasa aplikacia nezamrzla a dala sa pripadne vypnut
                            Runnable rr = new Runnable() {
                                @Override
                                public void run() {
                                  try {
                                        doRemoteMove(m.getPrintMove().getLastMove(myPlayer));
                                                
                                        board.revalidate();
                                        board.repaint();
                                    } catch (IOException ex) {
                                        Logger.getLogger(Window.class.getName()).log(Level.SEVERE, null, ex);
                                        
                                        // spojenie bolo prerusene, toz resetujme hru
                                        
                                    }
                                }
                            };
                            Thread s = new Thread(rr);
                            s.start();  
                            
                            this.board.setEnabled(false);
                 //           this.list.getMovesList().setEnabled(false);
                        }
                    }
                    remoteDrag = false;
                    
                    // hra jedneho hraca
                    if (m.getAIPlayer() == true)
                    {
                        if (AIturn)
                        {
                            AIturn = false;
                            drag = false;
                            doAIMove();
                        }
                        AIturn = true;
                    }

                }
                // neplatny tah
                else
                {
                    // policku nastavime povodnu figurku
                    this.drag_label.setIcon( this.drag_label_img );

                    this.drag_container.revalidate();
                    this.drag_container.repaint();
                }
            }
            
            drag = false;
        }    
        else 
        // oznac figurku na tah    
        {
            this.drag_position = position;
            this.drag_container = cc;
                        
            if (this.drag_position.getFigure() != null)
            {
                // ulozime aktualnu figurku z aktualnej (prvej) pozicie
                JLabel lab_icon = ((JLabel) this.drag_container.getComponent(0));
                this.drag_label_img = lab_icon.getIcon();
                        
                // odstranime figurku a nahradime tzv. shadow
                this.drag_container.remove(0);
                JLabel label = new JLabel("", image3, JLabel.CENTER);
                this.drag_container.add( label, BorderLayout.CENTER );
                
                // ulozime label -- resp. novu figurku ktorej mozeme nastavit iny obrazok
                this.drag_label = label;
                
                this.drag_container.revalidate();
                this.drag_container.repaint();
                                
                drag = true;
            }
        }      
    }

    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }
    
    /**
     * Vykreslenie tahu pocitaca v hre jedneho hraca.
     * O umelu inteligenciu sa stara Manager
     */
    public void doAIMove()
    {
        Position [] poss = m.AIMove();

        if (poss[0] != null && poss[1] != null)
        {
            MouseEvent mee;
            mee = new MouseEvent(squares[ poss[0].getRow() - 1 ][ poss[0].getColl()  - 'a'], 0, 0, 0, 0, 0, 1, false);    
            mouseClicked(mee);

            mee = new MouseEvent(squares[ poss[1].getRow() - 1 ][ poss[1].getColl()  - 'a'], 0, 0, 0, 0, 0, 1, false);    
            mouseClicked(mee);
        }
    }
    
    /**
     * Vykreslenie okna v pripade prichadzajuceho spojenia.
     * Aplikacia v tomto pripade vystupuje ako server (resp. nie je
     * iniciatorom hry). Mame moznost prijat alebo odmietnut.
     * @param addr ip adresa iniciatora
     * @return true ak hru prijimame
     * @throws IOException 
     */
    public boolean acceptRemoteRequest(String addr) throws IOException
    {
        // dialog na prijatie hry
        Object[] options = {"Prijať", "Odmietnuť"};
        int n = JOptionPane.showOptionDialog(this,
            "Hráč s IP adresou " + addr + " Vami chce hrat vzdialenu hru",
            "Potvrdenie spojenia",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        // ak neprijme vrati sa na cakaciu smycku
        if ( n == 1 ) {
     //       m.getNetwork().wannaPlay();
            return false;
        }
        else
        {
            this.myPlayer = 2;
            this.remotePlay = true;
            gameType.setText("Vzdialená hra dvoch hráčov");
            return true;
        }
    }
    
    /**
     * V pripade sietovej hry caka na tah supera.
     * V pripade iniciatora je tato metoda volana bezprostredne
     * po prvom svojom tahu a potom po kazdom dalsom.
     * @param myDrag moj tah, kontrola na zhodu aby som sam neprecital to co som poslal
     * @throws IOException 
     */
    public void doRemoteMove(String myDrag) throws IOException
    {             
        String received_move = null;
        
        received_move = m.getNetwork().receiveDrag(myDrag);
//      System.out.println("TAHAM VZDIALENE");
        
        remoteDrag = true;
        this.board.setEnabled(true);
        this.list.getMovesList().setEnabled(true);
        
        MouseEvent mee;
        mee = new MouseEvent(squares[received_move.charAt(1) - '0' - 1][received_move.charAt(0) - 'a'], 0, 0, 0, 0, 0, 1, false);    
        mouseClicked(mee);
        
        mee = new MouseEvent(squares[received_move.charAt(4) - '0' - 1][received_move.charAt(3) - 'a'], 0, 0, 0, 0, 0, 1, false);
        mouseClicked(mee);
        remoteDrag = false;
    }
    
    public void disableBoardAndList()
    {
        this.board.setEnabled(false);
   //     this.list.getMovesList().setEnabled(false);
    }
  
    public void gameOver()
    {
        JDialog end_dialog = new JDialog(this, "Koniec hry");
        JLabel winner = new JLabel ("Vyhral hráč číslo " + (m.getTurn() ? "2" : "1"), (int) CENTER_ALIGNMENT);
        
        end_dialog.add(winner);
        end_dialog.setBounds(300, 300, 150, 100);
        end_dialog.setVisible(true);
    }
    
    /**
     * Metoda vykreslujuca okno pre pripojenie k vzdialenemu pocitacu.
     * Zadava sa hostname (dns nazov) a cislo portu
     */
    public void ConnectToRemote()
    {
        port.setText( "Port: " + Integer.toString(m.getNetwork().getPort()) );
        
        connection = new JDialog(this, "Connect to Remote");
        JPanel connwin = new JPanel();
               
         //Vlozi okno do Jdialogu
        connection.add(connwin);
        
        //vytvori elementy
        address = new JTextField("localhost",20);
        port_text = new JTextField("6000",5);
        go = new JButton("Pripoj");
        go.addActionListener(this);
        
        //Prida elementy do okna
        connwin.add(address);
        connwin.add(port_text);
        connwin.add(go);

        //Naastavi oknu vlastnosti
        connection.setBounds(150, 150, 400, 80);
        connection.setVisible(true);
        connection.setResizable(false);
        connection.setDefaultCloseOperation(DISPOSE_ON_CLOSE );
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {
        if(ae.getSource().equals(go))
        {
            m.remotePlay(address.getText(), Integer.parseInt( port_text.getText() ), list );
            this.remotePlay = true;
            this.connection.dispose();
        }
    }
        
    public String getPosMoves()
    {
        return posMoves;
    }
    
    public MList getMList()
    {
        return list;
    }
    
    public JLabel getWhosTurn()
    {
        return whosTurn;
    }
    
    public JLabel getPortLabel()
    {
        return port;
    } 
    
    public void setRemotePlay()
    {
        this.remotePlay = false;
    }
           
}