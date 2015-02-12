package figures;

import basis.*;
import control.*;

/**
 * Implementuje abstraktnu metodu canMove z triedy Figure.
 * Kontroluje ci je mozne spravit tah, pripadne tah s vyhadzovanim,
 * v tom pripade vyhodenu figurku uchova v objekte triedy DeletedFigure.
 * Ak je tah platny, tak tento tah zapise do zoznamu tahov a vrati true.
 */
public class Pawn extends basis.Figure
{
    public Pawn(Position p, Player player, PrintMove printMove, DeletedFigure df)
    {
        super(p,player,printMove, df);
    }

    // Test, ci sa da figurka presunut na novu poziciu. Abstraktna.
    public boolean canMove(Position p)
    {
        Position act_pos = super.p;             //zisti aktualnu poziciu figurky
        Desk act_desk 	= act_pos.getDesk();	//plocha ktorej pesiak prinalezi
        int new_row 	= p.getRow();		//riadok kam sa chcem posunut
        char new_coll 	= p.getColl();		//stlpec kam sa chcem posunut
        String move;                            //string pre printer

        //Overi ci pozicia na ktoru sa posuvame je o stlpec vlavo / vpravo (posun sikmo)
        if((new_coll == (act_pos.getColl() + 1)) || (new_coll == (act_pos.getColl() - 1))) {

            /**
             * zisti o ktoreho hraca sa jedna (pre posun hore/dole)
             * @ 1 = hrac ktory ma cierne kamene
             * @ 2 = hrac ktory ma biele kamene 
             */  
            if(super.player.getColor() == 1){

                //Overi ci pozicia na ktoru sa posuvame je o riadok vyssie, je na ploche
                if((new_row == (act_pos.getRow() + 1)) && (act_desk.getPositionAt(new_coll,new_row) != null)){

                    //Test ci nie je policko obsadene
                    if(act_desk.getFigureAt(new_coll, new_row) == null){

                            //vygenerujem string pre printer
                        move = (char)((int)act_pos.getColl()) + Integer.toString(act_pos.getRow()) + '-' + (char)((int)new_coll) + Integer.toString(new_row);

                        //poslem report printeru
                        printMove.appendToDocument(move);

                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            else if(super.player.getColor() == 2){

                //Overi ci pozicia na ktoru sa posuvame je o riadok nizsie, je na ploche, a nie je na nej figurka
                if((new_row == (act_pos.getRow() - 1)) && (act_desk.getPositionAt(new_coll,new_row) != null) && (act_desk.getFigureAt(new_coll, new_row) == null)){

                    //Test ci nie je policko obsadene
                    if(act_desk.getFigureAt(new_coll, new_row) == null){

                            //vygenerujem string pre printer
                        move = (char)((int)act_pos.getColl()) + Integer.toString(act_pos.getRow()) + '-' + (char)((int)new_coll) + Integer.toString(new_row);

                        //poslem report printeru
                        printMove.appendToDocument(move);

                        return true;	
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
        }
        //Zisti ci sa jedna o posun pri vyhadzovani figurok
        else if((new_coll  == (act_pos.getColl() + 2)) || (new_coll  == (act_pos.getColl() - 2))){

            /*
             * zisti o ktoreho hraca sa jedna (pre posun hore/dole)
             * @ 1 = hrac ktory ma cierne kamene
             * @ 2 = hrac ktory ma biele kamene */

            //ziskam figurky ktore by mali byt vyhodene
            Figure blowNW, blowNE, blowSW, blowSE;

            blowNW = act_desk.getFigureAt((char)((int)act_pos.getColl() - 1), act_pos.getRow() - 1);
            blowNE = act_desk.getFigureAt((char)((int)act_pos.getColl() + 1), act_pos.getRow() - 1);
            blowSW = act_desk.getFigureAt((char)((int)act_pos.getColl() - 1), act_pos.getRow() + 1);
            blowSE = act_desk.getFigureAt((char)((int)act_pos.getColl() + 1), act_pos.getRow() + 1);

            //HRAC 1
            if(super.player.getColor() == 1){

                //Overi ci pozicia na ktoru sa posuvame je o 2 riadky vyssie, je na ploche, a nie je na nej figurka
                if((new_row == (act_pos.getRow() + 2)) && (act_desk.getPositionAt(new_coll,new_row) != null) && (act_desk.getFigureAt(new_coll, new_row) == null)){

                    //Test figurky na medzipozicii - hrac ktoremu prinalezi
                    if((blowSE != null) && (blowSE.getPlayer().getColor() == 2) && new_coll == (int)act_pos.getColl() + 2){

                        //zmazem figurku
                        Figure deleted = blowSE.getPosition().removeFigure();
                        df.setFigure(deleted);

                        //vygenerujem string pre printer
                        move = (char)((int)act_pos.getColl()) + Integer.toString(act_pos.getRow()) + 'x' + (char)((int)new_coll) + Integer.toString(new_row);

                            //poslem report printeru
                        printMove.appendToDocument(move);

                        //presun figurky sa moze uskutocnit, vraciam true
                        return true;
                                    }
                    else if((blowSW != null) && (blowSW.getPlayer().getColor() == 2) && new_coll == (int)act_pos.getColl() - 2){

                        //zmazem figurku
                        Figure deleted = blowSW.getPosition().removeFigure();
                        df.setFigure(deleted);

                        //vygenerujem string pre printer
                        move = (char)((int)act_pos.getColl()) + Integer.toString(act_pos.getRow()) + 'x' + (char)((int)new_coll) + Integer.toString(new_row);

                        //poslem report printeru
                        printMove.appendToDocument(move);

                        //presun figurky sa moze uskutocnit, vraciam true
                        return true;
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
            //HRAC 2
            else if(super.player.getColor() == 2){

                //Overi ci pozicia na ktoru sa posuvame je o riadok nizsie, je na ploche, a nie je na nej figurka
                if((new_row == (act_pos.getRow() - 2)) && (act_desk.getPositionAt(new_coll,new_row) != null) && (act_desk.getFigureAt(new_coll, new_row) == null)){

                    //Test figurky na medzipozicii - hrac ktoremu prinalezi
                    if((blowNE != null) && (blowNE.getPlayer().getColor() == 1) &&  new_coll == (int)act_pos.getColl() + 2){

                        //zmazem figurku
                        Figure deleted = blowNE.getPosition().removeFigure();
                        df.setFigure(deleted);

                        //vygenerujem string pre printer
                        move = (char)((int)act_pos.getColl()) + Integer.toString(act_pos.getRow()) + 'x' + (char)((int)new_coll) + Integer.toString(new_row);

                            //poslem report printeru
                        printMove.appendToDocument(move);

                        //presun figurky sa moze uskutocnit, vraciam true
                        return true;
                    }
                    else if((blowNW != null) && (blowNW.getPlayer().getColor() == 1) && new_coll == (int)act_pos.getColl() - 2){

                        //zmazem figurku
                        Figure deleted = blowNW.getPosition().removeFigure();
                        df.setFigure(deleted);

                        //vygenerujem string pre printer
                        move = (char)((int)act_pos.getColl()) + Integer.toString(act_pos.getRow()) + 'x' + (char)((int)new_coll) + Integer.toString(new_row);

                            //poslem report printeru
                        printMove.appendToDocument(move);

                        //presun figurky sa moze uskutocnit, vraciam true
                        return true;	
                    }
                    else {
                        return false;
                    }
                }
                else {
                    return false;
                }
            }
        }	
        else return false;

        return true;
    }   
}