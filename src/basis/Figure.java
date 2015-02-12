
package basis;

import control.*;
import figures.*;

/**
 * Informacie o figurke. Figurka pozna svoju poziciu a hraca
 * ktoremu patri. Obsahuje referenciu na triedu PrintMove
 * v ktorej v pripade uspesneho tahu prislusna implementacia 
 * metody canMove vola metodu na zapis tohoto tahu.
 * Abstraktnu metodu canMove implementuju triedy Pawn a Queen.
 */
public abstract class Figure
{
    protected Position p;
    protected Player player;
    protected PrintMove printMove;
    protected DeletedFigure df;

    public Figure(Position p, Player player, PrintMove printMove, DeletedFigure df)
    {
        this.p = p;
        this.player = player;
        this.printMove = printMove;
        this.df = df;
        p.putFigure(this);
    }

    /**
     * Realizuje presun figurky v logike pokial bol tah platny
     * @param p nova pozicia
     * @return true ak sa tah podaril
     */
    public boolean move(Position p)
    {
        if (canMove(p))
        {
            this.p.removeFigure();

            if( ((p.getRow() == 8 && this.player.getColor() == 1) || 
                ((p.getRow() == 1) && this.player.getColor() == 2)) &&
                (this instanceof Pawn) )
            {                
                this.PawnForQueen(p);
            }
            else
            {
                p.putFigure(this);
            }
           
            this.p = p;
            return true;
        }
        else
            return false;
    }
    
    /**
     * Implementuju triedy Pawn a Queen
     * @param p nova pozicia
     * @return true ak je mozne spravit tah
     */
    public abstract boolean canMove(Position p);
    
    
    /**
     * Premena pesiaka na damu
     * @param p aktualna pozicia pesiaka
     */
    public void PawnForQueen(Position p)
    {        
        Figure f = new Queen(p, this.player, this.printMove, this.df);
        p.putFigure(f);
        df.setQueen(f);
    }
    
    public Position getPosition()
    {
        return p;
    }
    
    public boolean isAtPosition(Position p)
    {
        if (this.p.equals(p))
            return true;
        else
            return false;
    }
        
    public Player getPlayer()
    {
        return player;
    }
}
