import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DecisionEngine {

    public static Hospital findBestHospital(double ambulanceLat, double ambulanceLong, String requiredEquipment) {
        List<Hospital> validHospitals = new ArrayList<>();
        
        // This query bypasses Paper_Reported_Full and checks physical reality
        String query = "SELECT h.Name, h.Location_Lat, h.Location_Long, h.Truth_Score, " +
                       "lc.Physical_Beds_Free, (es.Verified_Functional - es.Currently_In_Use) AS Available_Equip " +
                       "FROM Hospitals h " +
                       "JOIN Live_Capacity lc ON h.Hospital_ID = lc.Hospital_ID " +
                       "JOIN Equipment_Status es ON h.Hospital_ID = es.Hospital_ID " +
                       "WHERE lc.Physical_Beds_Free > 0 " +
                       "AND es.Equipment_Type = ? " +
                       "HAVING Available_Equip > 0";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, requiredEquipment);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Hospital h = new Hospital(
                    rs.getString("Name"),
                    rs.getDouble("Location_Lat"),
                    rs.getDouble("Location_Long"),
                    rs.getDouble("Truth_Score"),
                    rs.getInt("Physical_Beds_Free"),
                    rs.getInt("Available_Equip")
                );
                h.calculateDistanceScore(ambulanceLat, ambulanceLong);
                validHospitals.add(h);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rankHospitals(validHospitals);
    }

    private static Hospital rankHospitals(List<Hospital> hospitals) {
        if (hospitals.isEmpty()) return null;

        Hospital best = hospitals.get(0);
        for (Hospital h : hospitals) {
            // Logic: Lowest distance score is better. We penalize distance if truth score is low.
            double currentBestScore = best.getDistanceScore() * (100 / best.getTruthScore());
            double challengerScore = h.getDistanceScore() * (100 / h.getTruthScore());
            
            if (challengerScore < currentBestScore) {
                best = h;
            }
        }
        return best;
    }
}