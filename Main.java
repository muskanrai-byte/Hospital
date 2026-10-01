import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ensure GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            AmbulanceDashboard dashboard = new AmbulanceDashboard();
            dashboard.setVisible(true);
        });
    }
}