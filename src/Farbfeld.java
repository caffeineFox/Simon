import java.awt.Color;

import javax.swing.JPanel;

public class Farbfeld extends JPanel {
    String farbe;
    int colId;  // benötigt um sinnvollen Wert in EingabeArrayList
    // zu schreiben um Eingabe mit gegebener Merkfolge zu vergleichen
    
    public Farbfeld(String farbe, int id) {
        this.farbe = farbe;
        this.colId = id;
    }

    public void highlight() {
        int r = this.getBackground().getRed()+60;
        int g = this.getBackground().getGreen()+60;
        int b = this.getBackground().getBlue()+60;
        this.setBackground(new Color(r, g, b));
    }
    
    public void unhighlight() {
        int r = this.getBackground().getRed()-60;
        int g = this.getBackground().getGreen()-60;
        int b = this.getBackground().getBlue()-60;
        this.setBackground(new Color(r, g, b));
    }
    
}
