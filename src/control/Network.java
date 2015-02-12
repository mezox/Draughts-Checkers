
package control;

import gui.Window;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Vytvori server, ktory caka na pripojenie od ineho klienta.
 * Spracuje poziadavok a rozbehne sietovu hru. Obsahuje metody
 * na vytvorenie spojenia, zapisu do socketu a citanie zo socketu.
 * Konstruktor bezi zvlast vo vlastnom vlakne aby neblokoval aplikaciu.
 */
public class Network
{
    protected int port = 6000;
    protected ServerSocket serverSocket = null;
    protected Socket clientSocket = null;
    protected PrintWriter out;
    protected BufferedReader in;
    protected Enumeration list = null;
    protected Manager m;
    protected Window w;

    /**
     * Rola serveru, caka na prijatie spojenia. Meni typ hry na vzdialenu hru.
     * @param m odkaz na managera
     * @param w odkaz na hlavne okno, kvoli spusteniu hry
     * @throws IOException 
     */
    public Network(Manager m, Window w) throws IOException
    {
        m.p2p = this;
        this.m = m;
        this.w = w;
        
        // prideluje cislo portu, postupne zvysuje ak je port obsadeny
        while (true) {
            try {
                serverSocket = new ServerSocket(port);
            } catch (java.net.BindException ex) {
                port++;
                continue;
            }
            break;
        }
        
        System.out.println("vytvaram thread pre server, port: " + port);

        // cakanie na prijatie spojenia
        clientSocket = serverSocket.accept();
        in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));

        wannaPlay();
    }
    
    /**
     * Cakanie na spravu od klienta ktory sa k nam pripojil
     * @throws IOException 
     */
    @SuppressWarnings("empty-statement")
    public void wannaPlay() throws IOException
    {
        // precita spravu od klienta, sprava "wanna play"
        String inputLine;
        while ((inputLine = in.readLine()) == null);
        
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        // nastavi typ hry na vzdialenu, grafike posle udaj o klientovi kt. sa pripojil
        if (w.acceptRemoteRequest(clientSocket.getInetAddress().getCanonicalHostName()) == false)
        {
            out.print("decline\n");
            out.flush();
            
            // odmietame spojenie
          //  clientSocket.close();
            m.getNetwork().wannaPlay();
        }
        else
        {
            // prijimame rozohratu partiu
            if (inputLine.equals("continue game"))
            {
                gainDrags();
                
                try {
                    w.replayGame(w.getMList().getListModel().elements(), false, null);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            out.print("accept\n");
            out.flush();
            
            // prijimame hru, hra sa od zaciatku
            w.getPortLabel().setText(port + " - Pripojeny");
                        
            w.disableBoardAndList();
            w.doRemoteMove("wanna play");
        }
    }
    
    /**
     * Vytvori spojenie s inym klientom, resp. serverom klienta
     * @param hostname dns nazov vzdialeneho hraca
     * @param port port na ktorom bezi server vzdialeneho hraca
     * @throws IOException 
     */
    public void connect(String hostname, int port, Enumeration list) throws IOException
    {
        this.list = list;
        Enumeration pom = list;
        
        clientSocket = new Socket(hostname, port);
        
//      System.out.println("spojenie vytvorene");
          
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        String mmy;

        // posle serveru (druhy klient) spravu ze s nim chce hrat po nete
        if (!pom.hasMoreElements())
        {
            mmy = "wanna play";
            out.print("wanna play\n");
        }
        else
        {
            mmy = "continue game";
            out.print("continue game\n");
            sendDrags();
        }
        out.flush();
        
        in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
        
        String input;
        while ((input = in.readLine()).contains(mmy) );
        
        if (input.contains("accept"))
        {   
            String pport = w.getPortLabel().getText();
            w.getPortLabel().setText(pport + " - Pripojeny");
        }
        else
            w.setRemotePlay();
    }
    
    /**
     * Zapise tah v zakladnej notacii (a3-b4) do socketu druheho klienta.
     * @param message
     * @throws IOException 
     */
    public void sendDrag(String message)
    {
        out.print(message + "\n");
        out.flush();

//        System.out.println("odoslany tah: " + message);
    }
    
    /**
     * Prijima spravu od druheho klienta. Caka a caka az kym nejaku spravu
     * neprijme, blokujuca metoda.
     * @param myDrag moj tah na porovnanie. Aby som neprecital tah, ktory som
     *               do streamu sam zapisal
     * @return  ziskany superov tah
     * @throws IOException 
     */
    @SuppressWarnings("empty-statement")
    public String receiveDrag(String myDrag) throws IOException
    {          
        in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
   
        String received_move = null;
 
        while ((received_move = in.readLine()).equals(myDrag) );
                
//        System.out.println("prijaty tah: " + received_move);
                
        return received_move;
    }
    
    /**
     * Posiela vsetky tahy - budeme hrat uz rozohranu partiu
     */
    public void sendDrags()
    {
        while (list.hasMoreElements())
            out.println(list.nextElement());
        
        out.println("thats all");
    }
    
    /**
     * Ziska tahy rozohratej partie a vlozi ich do svojho zoznamu
     * @throws IOException 
     */
    public void gainDrags() throws IOException
    {
        int num = 0;
        String line;
        String [] splitLine;
        while (!(line = in.readLine()).equals("thats all") )
        {
            splitLine = line.split(": ");
            splitLine = splitLine[1].split(" ");
            m.getPrintMove().appendToDocument(splitLine[0]);
            w.getMList().addToList(num++);
            m.getPrintMove().appendToDocument(splitLine[1]);
            w.getMList().addToList(num++);
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            w.getMList().getMovesList().revalidate();
            w.getMList().getMovesList().repaint();
        }
        m.getPrintMove().resetCnt();
    }
    
    // nepouzita, o zatvaranie sa stara GC
    public void closeConnection() throws IOException
    {
        clientSocket.close();
        serverSocket.close();
    }
    
    // vracia cislo portu na ktorom bezi server
    public int getPort()
    {
        return this.port;
    }
}