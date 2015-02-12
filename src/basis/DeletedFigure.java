
package basis;

/**
 * Udrziava informacie o vyhodenej figurke, pripadne zmene
 * pesiaka na damu. Vyhodene igurky dodavaju triedy
 * Pawn a Queen (metody set), nasledovne spracovanie sa riesi v triede
 * Window (metody get + metody setNull)
 */
public class DeletedFigure {
    
    protected Figure deleted;
    protected Figure queen;
    
    public DeletedFigure()
    {
        this.deleted = null;
    }
    
    public void setFigure(Figure deleted)
    {
        this.deleted = deleted;
        deleted.getPlayer().decreaseFiguresNum();
    }
    
    public void setFigureNull()
    {
        this.deleted = null;
    }
        
    public Figure getFigure()
    {
        return this.deleted;
    }
    
    public void setQueen(Figure queen)
    {
        this.queen = queen;
    }
    
    public void setQueenNull()
    {
        this.queen = null;
    }
    
    public Figure getQueen()
    {
        return this.queen;
    }
}
