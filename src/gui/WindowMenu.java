package gui;

import control.Manager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/* Trieda vytvarajuca menu a jeho polozky v hlavnom
 * okne aplikacie. Komunikuje s Managerom a Window
 * za ucelom nastavenia typu hry prip. modifikacie
 * rychlosti prehravania partie.
 */
public class WindowMenu implements ActionListener
{
    protected Manager m;
    protected Window w;
    
    protected JMenuBar bar;
    protected JMenu bar_game;
    protected JMenu bar_play;
    protected JMenu for_one;
    protected JMenuItem p1;
    protected JMenuItem p2;
    
    protected JMenuItem for_two;
    protected JMenuItem remote;
    protected JMenuItem restart;
    protected JMenuItem export_xml;
    
    protected JMenuItem s_half;
    protected JMenuItem s_one;
    protected JMenuItem s_oneahalf;
    protected JMenuItem s_two;
    protected JMenuItem s_three;
    protected JMenuItem s_four;
    protected JMenuItem s_five;
    
    public WindowMenu(Manager m, Window w)
    {
        this.m = m;
        this.w = w;
        
        /* Menu */
        bar = new JMenuBar();
        
        bar_game = new JMenu("Hra");
        bar.add(bar_game);
            for_one = new JMenu("Pre jedneho..");
            bar_game.add(for_one);
                        
            p1 = new JMenuItem("Hrac 1");
            for_one.add(p1);
            p1.addActionListener(this);
            p2 = new JMenuItem("Hrac 2");
            for_one.add(p2);
            p2.addActionListener(this);
        
            for_two = new JMenuItem("Pre dvoch..");
            bar_game.add(for_two);
            for_two.addActionListener(this);
            
            remote = new JMenuItem("Vzdialene..");
            bar_game.add(remote);
            remote.addActionListener(this);
            
            restart = new JMenuItem("Restart..");
            bar_game.add(restart);
            restart.addActionListener(this);
            
            export_xml = new JMenuItem("Export xml..");
            bar_game.add(export_xml);
            export_xml.addActionListener(this);
            
        bar_play = new JMenu("Rychlost prehravania");
        bar.add(bar_play);
            s_half = new JMenuItem("0.5s");
            bar_play.add(s_half);
            s_half.addActionListener(this);         
        
            s_one = new JMenuItem("1s");
            bar_play.add(s_one);
            s_one.addActionListener(this); 
            
            s_oneahalf = new JMenuItem("1,5s");
            bar_play.add(s_oneahalf);
            s_oneahalf.addActionListener(this);             

            s_two = new JMenuItem("2s");
            bar_play.add(s_two);
            s_two.addActionListener(this); 
            
            s_three = new JMenuItem("3s");
            bar_play.add(s_three);
            s_three.addActionListener(this); 
            
            s_four = new JMenuItem("4s");
            bar_play.add(s_four);
            s_four.addActionListener(this); 
            
            s_five = new JMenuItem("5s");
            bar_play.add(s_five);
            s_five.addActionListener(this);             
            
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource().equals(p1))
        {
            w.gameType.setText("Hra jedného hráča");
            m.setAIPlayer(true);
            w.remotePlay = false;
        }
        else if (ae.getSource().equals(p2))
        {
            w.gameType.setText("Hra jedného hráča");
            m.setAIPlayer(true);
            w.remotePlay = false;
            m.setTurn(false);
        }
        else if (ae.getSource().equals(for_two))
        {
            w.gameType.setText("Hra dvoch hráčov");
            m.setAIPlayer(false);
            w.remotePlay = false;
        }
        else if (ae.getSource().equals(remote))
        {
            w.gameType.setText("Vzdialená hra dvoch hráčov");
            m.setAIPlayer(false);            
            w.ConnectToRemote();
        }        
        else if (ae.getSource().equals(restart))
        {
            w.whosTurn.setText("Na ťahu je 1. hráč");
            w.restartGameG(true);
        }
        else if (ae.getSource().equals(export_xml))
        {
            //vytvori novu instanciu dialogu pre vyber suborou
            JFileChooser filesave = new JFileChooser("examples");

            filesave.setDialogTitle("Vyberte subor na ulozenie partie");

            //vytvori filtre pre pripony suborov, chceme podporovat XML a txt
            FileFilter filterXML = new FileNameExtensionFilter("XML", "xml");
            filesave.addChoosableFileFilter(filterXML);
            FileFilter filterTXT = new FileNameExtensionFilter("txt", "txt");
            filesave.addChoosableFileFilter(filterTXT);

            int ret = filesave.showDialog(null,"Uloz");

            if (ret == JFileChooser.APPROVE_OPTION)
            {
                File file = filesave.getSelectedFile();
                m.printToXML(file);
            }            
        }
        else if (ae.getSource().equals(s_half))
        {
            w.speed = 500;
        }
        else if (ae.getSource().equals(s_one))
        {
            w.speed = 1000;
        }
        else if (ae.getSource().equals(s_oneahalf))
        {
            w.speed = 1500;
        }
        else if (ae.getSource().equals(s_two))
        {
            w.speed = 2000;
        }
        else if (ae.getSource().equals(s_three))
        {
            w.speed = 3000;
        }
        else if (ae.getSource().equals(s_four))
        {
            w.speed = 4000;
        }   
        else if (ae.getSource().equals(s_five))
        {
            w.speed = 5000;
        }           
    }
    
    public JMenuBar getBar()
    {
        return bar;
    }
}
