
package basis;

/**
 * Vytvorenie hracej dosky, dvojrozmerneho pole pozicii
 * na ktore pri vytvarani umiestnuje farby jednotlivych policok
 */
public class Desk
{
    protected Position[][] desk;
    protected int dim;
    
    public Desk(int dim)
    {
        this.dim = dim;
        this.desk = new Position[dim][dim];
        int color = 1;      // black
        int color_calc = 1; // vypocet farby policka
        
        for (int row = 0; row < dim; row++)
        {
            for (int col = 0; col < dim; col++)
            {   
                if ( col == 0 )
                    color_calc--;
                
                if ( (color_calc % 2) == 0)
                    color = 1; // black
                else
                    color = 0; // white
                
                color_calc++;
                
                this.desk[row][col] = new Position(this, (char) (col + 'a'), row + 1, color);
            }
        }
    }
    
    public Position getPositionAt(char c, int row)
    {
        row = row - 1;
        int col = (int) (c - 'a');
        
        if ( col >= dim || col < 0 || row >= dim || row < 0 )
            return null;
        else
            return this.desk[row][col];
    }
    
    public Figure getFigureAt(char c, int row)
    {
        row = row - 1;
        int col = (int) (c - 'a');
        
        if ( col >= dim || col < 0 || row >= dim || row < 0 || desk[row][col].getFigure() == null)
            return null;
        else
            return desk[row][col].getFigure();
    }
    
    public int getDim()
    {
        return this.dim;
    }
    
    public Position[][] getDesk2D()
    {
        return desk;
    }
}
