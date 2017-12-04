import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } 
        catch (Exception e){
            e.printStackTrace();
        }
        Gui window = new Gui();
        window.Menubar();
        window.setVisible(true);
    }
}
