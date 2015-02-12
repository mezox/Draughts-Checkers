package figures;

import basis.*;
import control.*;

public class Queen extends basis.Figure
{
    enum Direction { RIGHTUP, RIGHTDOWN, LEFTUP, LEFTDOWN, STAY}
      
    public Queen(Position p, Player player, PrintMove printMove, DeletedFigure df)
    {
        super(p,player, printMove, df);
    }
    
    /* Funkcia getDir zisti, ktorym smerom sa budeme pohybovat s figurkou damy
     * na zaklade kliknutia na plochu. Odpoveda smerom pri zakladnom
     * pohlade na hraciu plochy
     */
    public Direction getDir(int new_row, int new_coll){
            
        if(new_row > p.getRow()){
            if(((int)new_coll) > ((int)p.getColl())){
                return Direction.RIGHTDOWN;
            }
            else if(((int)new_coll) < ((int)p.getColl())){
                return Direction.LEFTDOWN;
            }
        }
        else if(new_row < p.getRow()){
            if(((int)new_coll) > ((int)p.getColl())){
                return Direction.RIGHTUP;
            }
            else if(((int)new_coll) < ((int)p.getColl())){
                return Direction.LEFTUP;
            }
        }
        
        return Direction.STAY;
    }
    
    /* Testuje, ci je mozne presunut figurku na pozadovane policko
     * vracia true pokial to mozne je, v opacnom pripade vracia false.
     * Taktiez figurky vyhadzuje pokial sa jedna o korektny tah
     */
    public boolean canMove(Position p)
    {
        Position act_pos = super.p;                 //zisti aktualnu poziciu figurky
        Desk act_desk = act_pos.getDesk();          //plocha ktorej pesiak prinalezi

        int new_row     = p.getRow();               //riadok kam sa chcem posunut
        char new_coll   = p.getColl();              //stlpec kam sa chcem posunut
        int pcolor      = super.getPlayer().getColor();
        String move;
        
        int mycnt = 0;
        int notmycnt = 0;
        
        Figure deleted = null;
        
        //Test validity pozicie
        if( Math.abs( new_coll - act_pos.getColl())  == Math.abs(new_row - act_pos.getRow())){
        
            //Overime ci je pozadovana pozicia prazdna
            if(act_desk.getFigureAt(new_coll, new_row) == null){

                int tmp_r = super.p.getRow();
                char tmp_c = super.p.getColl();
                
                //Test vyskytu vlastnej figurky v ceste
                while(tmp_r != new_row && tmp_c != new_coll){
                    
                    if(getDir(new_row, new_coll) == Direction.RIGHTUP){
                        tmp_r--;
                        tmp_c = (char)((int)tmp_c + 1);
                        
                        //figurku na dalsom policku je moja
                        if(act_desk.getFigureAt(tmp_c, tmp_r) != null && act_desk.getFigureAt(tmp_c, tmp_r).getPlayer().getColor() == pcolor){
                            mycnt++;
                        }
                        //figurka na dalsom policku nie je moja
                        else if(act_desk.getFigureAt(tmp_c, tmp_r) != null && act_desk.getFigureAt(tmp_c, tmp_r).getPlayer().getColor() != pcolor){                    
                            notmycnt++;
                            deleted = act_desk.getFigureAt(tmp_c, tmp_r);
                        }
                    }
                    else if(getDir(new_row, new_coll) == Direction.LEFTUP){
                        tmp_r--;
                        tmp_c = (char)((int)tmp_c - 1);
                        
                        if(act_desk.getFigureAt(tmp_c, tmp_r) != null && act_desk.getFigureAt(tmp_c, tmp_r).getPlayer().getColor() == pcolor){
                            mycnt++;
                        }
                        else if(act_desk.getFigureAt(tmp_c, tmp_r) != null && act_desk.getFigureAt(tmp_c, tmp_r).getPlayer().getColor() != pcolor){                    
                            notmycnt++;
                            deleted = act_desk.getFigureAt(tmp_c, tmp_r);
                        }                        
                    }
                    else if(getDir(new_row, new_coll) == Direction.RIGHTDOWN){
                        tmp_r++;
                        tmp_c = (char)((int)tmp_c + 1);
                        
                        if(act_desk.getFigureAt(tmp_c, tmp_r) != null && act_desk.getFigureAt(tmp_c, tmp_r).getPlayer().getColor() == pcolor){
                            mycnt++;
                        }
                        else if(act_desk.getFigureAt(tmp_c, tmp_r) != null && act_desk.getFigureAt(tmp_c, tmp_r).getPlayer().getColor() != pcolor){                    
                            notmycnt++;
                            deleted = act_desk.getFigureAt(tmp_c, tmp_r);
                        }                        
                    }
                    else if(getDir(new_row, new_coll) == Direction.LEFTDOWN){
                        tmp_r++;
                        tmp_c = (char)((int)tmp_c - 1);
                        
                        if(act_desk.getFigureAt(tmp_c, tmp_r) != null && act_desk.getFigureAt(tmp_c, tmp_r).getPlayer().getColor() == pcolor){
                            mycnt++;
                        }
                        else if(act_desk.getFigureAt(tmp_c, tmp_r) != null && act_desk.getFigureAt(tmp_c, tmp_r).getPlayer().getColor() != pcolor){                    
                            notmycnt++;
                            deleted = act_desk.getFigureAt(tmp_c, tmp_r);
                        }                        
                    }
                    else if(getDir(new_row, new_coll) == Direction.STAY){
                        return false;
                    }
                }
                //pohyb s vyhodenim
                if(tmp_r == new_row && tmp_c == new_coll && mycnt == 0 && notmycnt == 1){
                            
                    //vygenerujem string pre printer
                    move = (char)((int)act_pos.getColl()) + Integer.toString(act_pos.getRow()) + 'x' + (char)((int)new_coll) + Integer.toString(new_row);

                    //poslem report printeru
                    printMove.appendToDocument(move);

                    //zmazem figurku
                    Figure todel = deleted.getPosition().removeFigure();
                    df.setFigure(todel);

                    return true;
                }
                //
                else if(tmp_r == new_row && tmp_c == new_coll && mycnt == 0 && notmycnt == 0){
                    
                    //vygenerujem string pre printer
                    move = (char)((int)act_pos.getColl()) + Integer.toString(act_pos.getRow()) + '-' + (char)((int)new_coll) + Integer.toString(new_row);

                    //poslem report printeru
                    printMove.appendToDocument(move);       
                    
                    return true;
                }
                else
                    return false;
            }
            else
                return false;
        }
        else        
            return false;
    }
}