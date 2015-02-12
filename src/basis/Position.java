
package basis;

/**
 * Reprezentuje policko na sachovnici. Pozna sachovnicu ktorej
 * nalezi a figurku, ktora sa na danom policku pripadne nachadza.
 * Dalej pozna pismeno stlpca a cislo riadku na ktorom sa 
 * v sachovnici nachadza a tiez farbu, ktoru policko ma.
 */
public class Position
{
    protected Desk d;
    protected char c;
    protected int r;
    protected Figure f = null;
    protected int color;        // 1 black, 0 white
        
    public Position(Desk d, char c, int r, int color)
    {
        this.d = d;
        this.c = c;
        this.r = r;
        this.color = color;
    }
    
    public Position nextPosition(int dC, int dR)
    {
        int cc = ((int)this.c - 'a') + dC;
        int rr = this.r + dR;
        
        return d.getPositionAt( (char) (cc + 'a'), rr);
    }
        
    public Figure putFigure(Figure f)
    {
        Figure ff = this.f;
        this.f = f;
        return ff;
    }
    
    public Figure removeFigure()
    {
        Figure ff = this.f;
        this.f = null;
        return ff;
    }
    
    public Figure getFigure()
    {
        return this.f;
    }
    
    public char getColl()
    {
        return c;
    }
    
    public int getRow()
    {
        return r;
    }
    
    public int getColor()
    {
        return color;
    }
    
    public Desk getDesk()
    {
        return d;
    }
}
