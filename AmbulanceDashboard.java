import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AmbulanceDashboard extends JFrame {
    private JTextArea reportArea;
    // Simulated ambulance location
    private final double CURRENT_LAT = 24.8650;
    private final double CURRENT_LONG = 67.0100;

    public AmbulanceDashboard() {
        setTitle("Emergency Dispatch UI");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30)); // Dark mode

        JLabel title = new JLabel("AMBULANCE DISPATCH", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.setBackground(new Color(30, 30, 30));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton cardiacBtn = createMassiveButton("CARDIAC EMERGENCY\n(Requires Monitor)", new Color(200, 50, 50));
        cardiacBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispatch("Cardiac Monitor");
            }
        });

        buttonPanel.add(cardiacBtn);
        add(buttonPanel, BorderLayout.CENTER);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        reportArea.setBackground(new Color(45, 45, 45));
        reportArea.setForeground(new Color(150, 255, 150));
        reportArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JScrollPane(reportArea), BorderLayout.SOUTH);
    }

    private JButton createMassiveButton(String text, Color bgColor) {
        JButton btn = new JButton("<html><center>" + text.replaceAll("\n", "<br>") + "</center></html>");
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private void dispatch(String equipment) {
        reportArea.setText("Calculating route based on verified data...\n\n");
        Hospital best = DecisionEngine.findBestHospital(CURRENT_LAT, CURRENT_LONG, equipment);
        
        if (best != null) {
            reportArea.append("ROUTING TO:\n");
            reportArea.append(best.toString());
            reportArea.append("\n\n(Bypassed facilities with false paper capacity)");
        } else {
            reportArea.append("CRITICAL: No nearby hospitals with verified physical space and working equipment.");
        }
    }
}