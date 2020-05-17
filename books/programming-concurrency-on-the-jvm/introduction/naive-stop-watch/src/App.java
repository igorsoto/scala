import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App {
    private JButton buttonStartStop;
    private JPanel panelMain;
    private JLabel labelTime;

    private Boolean running = false;

    private void startCounting() {
        buttonStartStop.setText("Stop");
        running = true;

        for (int count = 0; running; count++) {
            labelTime.setText(String.format("%d", count));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void stopCounting(){
        running = false;
        buttonStartStop.setText("Start");
    }

    public App() {
        buttonStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (running) stopCounting(); else startCounting();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}