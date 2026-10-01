public class Hospital {
    private String name;
    private double latitude;
    private double longitude;
    private double truthScore;
    private int physicalBedsFree;
    private int functionalEquipment;
    private double distanceScore;

    public Hospital(String name, double latitude, double longitude, double truthScore, int physicalBedsFree, int functionalEquipment) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.truthScore = truthScore;
        this.physicalBedsFree = physicalBedsFree;
        this.functionalEquipment = functionalEquipment;
    }

    public void calculateDistanceScore(double currentLat, double currentLong) {
        // Simple Euclidean distance for the prototype
        this.distanceScore = Math.sqrt(Math.pow(this.latitude - currentLat, 2) + Math.pow(this.longitude - currentLong, 2));
    }

    public String getName() { return name; }
    public double getTruthScore() { return truthScore; }
    public double getDistanceScore() { return distanceScore; }
    public int getPhysicalBedsFree() { return physicalBedsFree; }
    
    @Override
    public String toString() {
        return String.format("%s\n- Beds Free: %d\n- Trust Score: %.1f%%\n- Distance Metric: %.4f", 
                name, physicalBedsFree, truthScore, distanceScore);
    }
}