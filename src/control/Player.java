
package control;

/**
 * Informacie o hracovi, farba ktoru ma hrac pridelenu
 * a aktualny pocet figurok na hracej ploche, na zaciatku 12.
 */
public class Player {
    
    protected int color;
    protected int numberOfFigures;
    
    public Player(int color, int number)
    {
        this.color = color;
        this.numberOfFigures = number;
    }
        
    public void decreaseFiguresNum()
    {
        this.numberOfFigures--;
    }
    
    public int getColor()
    {
        return color;
    }
    
    public void setFiguresNum(int number)
    {
        this.numberOfFigures = number;
    }
    
    public int getFiguresNum()
    {
        return this.numberOfFigures;
    }
}
