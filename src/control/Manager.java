
package control;

import basis.*;
import figures.*;
import gui.MList;
import gui.Window;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vytvara a inicializuje hraciu plochu. Kontroluje striedanie hracov. 
 * Vytvara a kontroluje objekt, ktory sa stara o zapisovanie 
 * partie do xml suboru. Kontroluje povinne tahy. Vytvara vlakno pre
 * server pre moznost vzdialenej hry.
 */
public class Manager {
    
    protected int dim = 8;
    protected int numberOfFigures = 12;
    protected Desk d;
    protected Player P1;
    protected Player P2;
    protected PrintMove printMove;
    protected boolean turn = true;
    protected DeletedFigure df;
    
    protected boolean AIplayer = false;
    protected Network p2p;
    protected int myPlayer = 1;
    
    /**
     * Vytvori hraciu plochu a priradi figurky. Vytvara server.
     * @param w odkaz na hlavne okno aplikacie
     */
    public Manager(final Window w)
    {
        d = new Desk(dim);
        Position [][] desk = d.getDesk2D();
        
        // ulozenie vyhodenej figurky
        this.df = new DeletedFigure();
        
        int color1 = 1;     // black
        int color2 = 2;     // white
        
        P1 = new Player(color1, numberOfFigures);
        P2 = new Player(color2, numberOfFigures);
        
        printMove = new PrintMove();
                
        // inicializacia figurok na desk
        for (int row = 0; row < dim; row++)
        {
            for (int col = 0; col < dim; col++)
            {
                // figurky hraca 1 na prvych troch riadkoch
                if (row >= 0 && row < 3)
                    if (desk[row][col].getColor() == 1)
                        desk[row][col].putFigure( new Pawn(desk[row][col], P1, printMove, df) );
                
                // figurky hraca 2 na poslednych troch riadkoch
                if (row >= dim - 3 && row <= dim)
                    if (desk[row][col].getColor() == 1)
                        desk[row][col].putFigure( new Pawn(desk[row][col], P2, printMove, df) );
            }    
        }       
    
        // vytvara server v samostatnom vlakne
        final Manager m = this;
        Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            try {                                
                                p2p = new Network(m,w);
                            } catch (IOException ex) {
                                Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
                            } 
                        }
                    };
        Thread server = new Thread(r);
        server.start();       
    }
    
    /** 
     * Striedanie hracov a kontrola ci hrac manipuluje so svojou figurkou
     * @param act  aktualna pozicia
     * @param goal pozicia na ktoru chceme figurku premiestnit
     * @return true ak sa tah podaril
     */
    public boolean turn(Position act, Position goal)
    {
        if (turn == true)
        {
            if (act.getFigure() != null && act.getFigure().getPlayer() == P1)
            {
                if (act.getFigure().move(goal) == true)
                {
                    turn = false;              
                    return true;    // tah sa podaril
                }
            }
        }
        else
        {
            if (act.getFigure() != null && act.getFigure().getPlayer() == P2)
            {
                if (act.getFigure().move(goal) == true)
                {      
                    turn = true;
                    return true;    // tah sa podaril
                }
            }
        }
        return false;   // tah sa nepodaril
    }
    
    /**
     * Vymaze vsetky figurky na hracej ploche a znovu ich vytvori
     * na zaciatocnych poziciach.
     */
    public void restartGame()
    {
        Position [][] desk = d.getDesk2D();
        this.turn = true;
        this.P1.setFiguresNum(numberOfFigures);
        this.P2.setFiguresNum(numberOfFigures);
        
        // inicializacia figurok na desk
        for (int row = 0; row < dim; row++)
        {
            for (int col = 0; col < dim; col++)
            {
                desk[row][col].removeFigure();
                
                // figurky hraca 1 na prvych troch riadkoch
                if (row >= 0 && row < 3)
                    if (desk[row][col].getColor() == 1)
                        desk[row][col].putFigure( new Pawn(desk[row][col], P1, printMove, df) );
                
                // figurky hraca 2 na poslednych troch riadkoch
                if (row >= dim - 3 && row <= dim)
                    if (desk[row][col].getColor() == 1)
                        desk[row][col].putFigure( new Pawn(desk[row][col], P2, printMove, df) );
            }    
        }
    }
      
    /**
     * Logika pre automaticky tah pocitaca v pripade hry jedneho hraca.
     * @return  vypocitany tah pocitaca, dvojprvkove pole kde na
     *          prvom prvku je pozicia odkial a na druhom kam
     */
    public Position [] AIMove()
    {
        int dr, color;
        
        if (turn == false)
        {
            dr = -1;
            color = 2;
        }
        else
        {
            dr = 1;
            color = 1;
        }
        
        Position [] poss = new Position [2];
        Position [][] desk = d.getDesk2D();
        
        // skontrolujem povinne tahy
        List<Position[]> possible_moves = checkMoves(turn);

        if (!possible_moves.isEmpty())
        {
            poss[0] = possible_moves.get(0)[0];
            poss[1] = possible_moves.get(0)[1];
            
            return poss;
        }
        
        // ak nie su povinne tahy, vyberiem iny  
        for (int row = 0; row < dim; row++)
            for (int col = 0; col < dim; col++)
            {
                 if (desk[row][col].getFigure() != null && desk[row][col].getFigure().getPlayer().getColor() == color)
                 {
                     if (desk[row][col].nextPosition(-1, dr) != null && desk[row][col].nextPosition(-1, dr).getFigure() == null)
                     {
                         poss[0] = desk[row][col];
                         poss[1] = desk[row][col].nextPosition(-1, dr);
                         return poss;
                     }
                     else if (desk[row][col].nextPosition( 1, dr) != null && desk[row][col].nextPosition( 1, dr).getFigure() == null)
                     {
                         poss[0] = desk[row][col];
                         poss[1] = desk[row][col].nextPosition( 1, dr);
                         return poss;
                     }
                     
                     // pre damu
                     if (desk[row][col].nextPosition(-1, -dr) != null && desk[row][col].nextPosition(-1, -dr).getFigure() == null
                        && desk[row][col].getFigure() instanceof Queen)
                     {
                         poss[0] = desk[row][col];
                         poss[1] = desk[row][col].nextPosition(-1, -dr);
                         return poss;
                     }
                     else if (desk[row][col].nextPosition( 1, -dr) != null && desk[row][col].nextPosition( 1, -dr).getFigure() == null
                        && desk[row][col].getFigure() instanceof Queen)
                     {
                         poss[0] = desk[row][col];
                         poss[1] = desk[row][col].nextPosition( 1, -dr);
                         return poss;
                     }
                 }
            }

        // v pripade ze neexistuje ziadny validny tah vracia null
        return poss;
    }
   
    /** 
     * Kontrola povinnych tahov. Ak mame moznosti vyhodit superovu figurku
     * musime tak spravit
     * @param turn
     * @return zoznam dvojprvkovych poli, v ktorom je ako prvy prvok aktualna
     *         pozicia a ako druhy pozicia na ktoru sa musime premiestnit.
     */
    public List<Position[]> checkMoves(boolean turn)
    {
        Figure f;
        Position p;
        Position NW,NE,SW,SE;
        int playerTurn = turn ? 1 : 2;
        int i = 0;

        List<Position[]> possible_moves = new ArrayList<>();
                
        // otestuje vsetky figurky
        for (int row = 0; row < dim; row++)
            for (int col = 0; col < dim; col++)
            {
                p = d.getPositionAt((char)(col + 'a'), row + 1);
                f = p.getFigure() != null ? p.getFigure() : null;

                NW = p.nextPosition(-1, -1);
                NE = p.nextPosition( 1, -1);
                SW = p.nextPosition(-1,  1);
                SE = p.nextPosition( 1,  1);

                if (f instanceof Pawn)
                {        
                    //HRAC 1
                    if(f.getPlayer().getColor() == 1 && playerTurn == 1){

                        if (SW != null && SW.getFigure() != null && 
                            SW.getFigure().getPlayer().getColor() != 1 && 
                            SW.nextPosition(-1, 1) != null &&
                            SW.nextPosition(-1, 1).getFigure() == null)
                        {
                            possible_moves.add( new Position[2] );
                            possible_moves.get(i)[0] = p;
                            possible_moves.get(i++)[1] = SW.nextPosition(-1, 1);
                        }
                        if (SE != null && SE.getFigure() != null && 
                            SE.getFigure().getPlayer().getColor() != 1 && 
                            SE.nextPosition(1, 1) != null &&
                            SE.nextPosition(1, 1).getFigure() == null)
                        {
                            possible_moves.add( new Position[2] );
                            possible_moves.get(i)[0] = p;
                            possible_moves.get(i++)[1] = SE.nextPosition(1, 1);
                        }
                    }
                    //HRAC 2
                    else if (f.getPlayer().getColor() == 2 && playerTurn == 2)
                    {
                        if (NW != null && NW.getFigure() != null && 
                            NW.getFigure().getPlayer().getColor() == 1 && 
                            NW.nextPosition(-1, -1) != null &&
                            NW.nextPosition(-1, -1).getFigure() == null)
                        {
                            possible_moves.add( new Position[2] );
                            possible_moves.get(i)[0] = p;
                            possible_moves.get(i++)[1] = NW.nextPosition(-1, -1);
                        }
                        if (NE != null && NE.getFigure() != null && 
                            NE.getFigure().getPlayer().getColor() == 1 && 
                            NE.nextPosition(1, -1) != null &&
                            NE.nextPosition(1, -1).getFigure() == null)
                        {
                            possible_moves.add( new Position[2] );
                            possible_moves.get(i)[0] = p;
                            possible_moves.get(i++)[1] = NE.nextPosition(1, -1);
                        }
                    }
                }
                else if (f instanceof Queen)
                {
                    
                    int color = f.getPlayer().getColor();
                    int x = 0,y = 0;
                    Position dir = f.getPosition();

                    int mycnt = 0;
                    int enemycnt = 0;
                    int spacecnt = 0; 
                    int dircnt = 0;

                    if(playerTurn == color){
                        while(dircnt < 4){                        

                            //SE
                            if(dircnt == 0){
                                dir = p.nextPosition(++y,++x);
                            }
                            //SW
                            else if(dircnt == 1){
                                dir = p.nextPosition(--y,++x);
                            }
                            //NE
                            else if(dircnt == 2){
                                dir = p.nextPosition(++y,--x);
                            }
                            //NW
                            else if(dircnt == 3){
                                dir = p.nextPosition(--y,--x);
                            }                    

                            /* Test ci je dana pozicia na sachovnici
                             * a ci je na policku figurka
                             */
                            if(dir != null){
                                if(dir.getFigure() != null){

                                    //Figurka na policku je moja, menim smer, reset
                                    if(dir.getFigure().getPlayer().getColor() == color){
                                        dircnt++;
                                        x = 0;
                                        y = 0;
                                        mycnt = 0;
                                        enemycnt = 0;
                                        spacecnt = 0;
                                    }
                                    /*figurka nie je moja, ale musim osetrit
                                     *ci v ceste neboli moje figurky
                                     */
                                    else{
                                        if(mycnt == 0){
                                            enemycnt++; 
                                        }
                                        else{
                                            dircnt++;
                                            x = 0;
                                            y = 0;
                                            mycnt = 0;
                                            enemycnt = 0;
                                            spacecnt = 0;                                  
                                        }     
                                    }                 
                                }
                                /* Jedna sa o medzeru, spocitam si
                                 * medzery za figurkou
                                 */
                                else {
                                    if(enemycnt == 1){
                                       spacecnt++; 
                                    }

                                    if(enemycnt == 1 && spacecnt > 0)
                                    {
                                        possible_moves.add( new Position[2] );
                                        possible_moves.get(i)[0] = p;
                                        possible_moves.get(i++)[1] = dir;
                                    }                            
                                }
                            }
                            else{
                                dircnt++;
                                x = 0;
                                y = 0;
                                mycnt = 0;
                                enemycnt = 0;
                                spacecnt = 0;                        
                            }
                        }
                    }
                }
            }
        return possible_moves;
    }
    
    /**
     * Nastavenie spojenia. Pripojenia na ineho klienta na zaklade 
     * DNS nazvu a portu na ktorom aplikacie nacuva
     * @param hostname
     * @param port 
     */
    public void remotePlay(String hostname, int port, MList list)
    {
        try {
            p2p.connect(hostname, port, list.getListModel().elements());
        } catch (IOException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // referencia na triedu realizujucu spojenie
    public Network getNetwork()
    {
        return this.p2p;
    }
    
    public void setTurn(boolean trn)
    {
        this.turn = trn;
    }   
        
    public boolean getTurn()
    {
        return turn;
    }
    
    /**
     * Otvori, zapise a zavrie subor
     * @param f 
     */
    public void printToXML(File f)
    {
        printMove.writeToXml(f);
    }
    
    public Desk getDesk()
    {
        return d;
    }
    
    public PrintMove getPrintMove()
    {
        return printMove;
    }
    
    public int getDimension()
    {
        return dim;
    }
    
    public DeletedFigure getDeletedFigure()
    {
        return df;
    }
    
    public void setAIPlayer(boolean bool)
    {
        this.AIplayer = bool;
    }
    
    public boolean getAIPlayer()
    {
        return this.AIplayer;
    }
       
    /**
     * Test rozostavenia figurok. Vypis sachovnice v znakoch na stdout.
     * @param desk pole pozicii
     */
    public void printDesk(Position [][] desk)
    {                
        for (int i = 0; i < dim; i++)
        {
            for (int j = 0; j < dim; j++)
                if ( desk[i][j].getFigure() == null )
                    System.out.print("-");
                else
                    if (desk[i][j].getFigure().getPlayer() == P1)
                        System.out.print("x");    
                    else
                        System.out.print("y");    
                   
            System.out.println();
        }
    }    
}
