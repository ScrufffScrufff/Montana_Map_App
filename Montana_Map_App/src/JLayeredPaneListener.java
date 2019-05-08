import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class JLayeredPaneListener extends ComponentAdapter {
    JScrollPane parentWindow;
    public JLayeredPaneListener(JScrollPane j){
        parentWindow = j;
    }
    public void componentResized(ComponentEvent e) {
        e.getComponent().setSize(parentWindow.getSize().width - 18 ,parentWindow.getSize().height - 18);
        e.getComponent().setLocation(parentWindow.getLocation());
    }
}

