import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class Gui extends JFrame {
    private JPanel constPNL;
    private final String[] farbenStr = {"gruen", "rot", "blau", "gelb"};
    private ArrayList<Farbfeld> farbfelder; // farbige Felder in GUI (extended JPanels)
    private ArrayList<Integer> farbenFolge, eingabeFolge;   // zu merkende Folge & Eingabefolge
    private final Color[] farbenCol = {
            new Color(0, 180, 0),       // gruen
            new Color(180, 0, 0),       // rot
            new Color(0, 0, 180),       // blau
            new Color(180, 180, 0)};    // gelb
    private Timer timer;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem menuItem;
    private boolean running = false;
    
    public Gui() {
        farbfelder = new ArrayList<Farbfeld>();
        farbenFolge = new ArrayList<Integer>();
        eingabeFolge = new ArrayList<Integer>();
        setBounds(70, 50, 600, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Simon");
        
        constPNL = new JPanel();
        constPNL.setLayout(null);
        constPNL.setBounds(0, 0, 600, 600);
        
        for (int i = 0; i < 4; i++) {   // Farbfelder generieren
            farbfelder.add(new Farbfeld(farbenStr[i], i));
            farbfelder.get(i).setBounds(        // Position & Größe
                    50+((i%2)*10)+((i%2)*240),  // x
                    25+((i/2)*10)+((i/2)*240),  // y
                    240,                        // width
                    240);                       // height
            farbfelder.get(i).setBackground(farbenCol[i]);
            constPNL.add(farbfelder.get(i));
        }
        
        getContentPane().add(constPNL);
    }
    
    public void Menubar() {
        menuBar = new JMenuBar();
        
        menu = new JMenu("Spiel");
        
        menuItem = new JMenuItem("Start");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                running = true;
                spielRoutine();
            }
        });
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Neustart");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.SHIFT_MASK));
        menuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                farbenFolge = new ArrayList<Integer>();
                eingabeFolge = new ArrayList<Integer>();
                try {
                    timer.stop();
                } catch (Exception e1) {}
                for (int i = 0; i < 4; i++)
                    farbfelder.get(i).setBackground(farbenCol[i]);
                running = true;
                spielRoutine();
            }
        });
        menu.add(menuItem);
        
        menu.addSeparator();
        
        menuItem = new JMenuItem("Beenden");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        menuItem.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e2) {
                System.exit(0);
            }
        });
        menu.add(menuItem);
        
        menuBar.add(menu);
        
        this.setJMenuBar(menuBar);
    }
    
    public int generateItem() {
        return new Random().nextInt(4);
    }
    
    public void spielRoutine() {
        // alle MouseListener von Farbfeldern nehmen damit während Anzeige nicht
        // gestört/rumgeklickt werden kann
        for (int j = 0; j < 4; j++)
            for (MouseListener m : farbfelder.get(j).getMouseListeners())
                    farbfelder.get(j).removeMouseListener(m);
        
        // neues Merkelement erzeugen
        farbenFolge.add(generateItem());
        
        // Timer für Blinkmechanismus instanziieren
        timer = new Timer(500, new ActionListener() {
            int i, z = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                // Blinkmechanismus (keine Vermeidung von Duplikaten gewollt -> Blinken nötig)
                if (z%2 == 0) {
                    if (i == 0 || i < farbenFolge.size())   // Element highlighten
                        farbfelder.get(farbenFolge.get(i)).highlight();
                    i++;
                } else {
                    farbfelder.get(farbenFolge.get(i-1)).unhighlight();
                    if (i == farbenFolge.size()) { 
                        // Timer wird gestoppt sobald letztes Element nicht mehr gehighlighted ist
                        // daraus folgend ergibt sich, dass nach letztem Aufblinken nicht erst
                        // 500 ms gewartet werden muss
                        timer.stop();   // eigentliches Ende von spielRoutine()
                        giveMouseListener();    // wenn nach timer.start() ausgeführt, dann hat Blockade keinen Sinn
                    }
                }
                z++;
            }
        });
        timer.start();  // abschließend Timer starten
    }
    
    public void giveMouseListener() {
        // MouseListener auf Farbfelder setzen
        // (wurden zur Fehlervermeidung in spielRoutine() entfernt
        for (int k = 0; k < 4; k++) {
            farbfelder.get(k).addMouseListener(new MouseAdapter() {
                
                @Override
                public void mousePressed(MouseEvent e) {
                    // bei Klick aufblinken (für Dauer des Klicks)
                    Farbfeld c = (Farbfeld) e.getComponent();
                    c.highlight();
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    // Maustaste wird losgelassen -> Farbe zurücksetzen
                    Farbfeld c = (Farbfeld) e.getComponent();
                    c.unhighlight();
                    eingabeFolge.add(c.colId);
                    if (eingabeFolge.size() == farbenFolge.size())
                        eingabeVergleich();
                }
            });
        }
    }
    
    public void eingabeVergleich() {
        // Vergleich der Merkfolge mit der eingegebenen Folge
        for (int i = 0; i < farbenFolge.size(); i++) {
            if (eingabeFolge.get(i) != farbenFolge.get(i)) {
                // Falsche Eingabe
                JOptionPane.showConfirmDialog(this, "Leider verloren! Ein neues Spiel wird mit einem Klick auf \"Start\" gestartet.", "Leider verloren!", JOptionPane.PLAIN_MESSAGE);
                farbenFolge = new ArrayList<Integer>();
                eingabeFolge = new ArrayList<Integer>();
                running = false;
                break;  // damit bei mehreren falschen Elementen nicht Dialog mehrfach kommt
            }
        }
        if (running) {
            // bei komplett korrekter Eingabe nächste Runde anfangen
            eingabeFolge = new ArrayList<Integer>();
            spielRoutine();
        }
    }
}
