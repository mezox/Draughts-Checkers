
package main;

import gui.Window;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;

/**
 * Vytvara samotnu hru. Vytvori grafiku, ktora sa stara o vsetko ostatne
 * vratane vytvorenia logiky hry. Nastavuje pozadie hlavneho okna.
 */
public class Main
{
    public static void main(String args[]) throws IOException
    {
        JFrame frame = new Window();     
                
        try {
            File f = new File("lib/graphics/damabg4.png");
            frame.add(new JLabel(new ImageIcon(ImageIO.read(f))));
        } catch (IOException ex) {
            // nepodarilo sa otvorit obrazok pre pozadie
        }
                
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setResizable(false);
    }
}
