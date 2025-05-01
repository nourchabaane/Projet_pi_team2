package entities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import services.MedicamentService;

public class DashboardController {
    @FXML private PieChart adherenceChart;
    @FXML private BarChart<String, Number> usageChart;
    @FXML private Label totalMedsLabel;
    @FXML private Label adherenceScoreLabel;
    @FXML private Label nextDoseLabel;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    
    private MedicamentService medicamentService;
    
    @FXML
    public void initialize() {
        medicamentService = new MedicamentService();
        refreshData();
    }
    
    @FXML
    public void refreshData() {
        try {
            List<Medicament> medicaments = medicamentService.afficherTous();
            
            // Update total medications
            updateTotalMedications(medicaments);
            
            // Update adherence chart
            updateAdherenceChart(medicaments);
            
            // Update usage frequency chart
            updateUsageChart(medicaments);
            
            // Update next dose
            updateNextDose(medicaments);
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Show error message to user
        }
    }
    
    private void updateTotalMedications(List<Medicament> medicaments) {
        totalMedsLabel.setText(String.valueOf(medicaments.size()));
    }
    
    private void updateAdherenceChart(List<Medicament> medicaments) {
        double adherenceRate = calculateAdherence(medicaments);
        adherenceScoreLabel.setText(String.format("%.1f%%", adherenceRate));
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Pris à temps", adherenceRate),
            new PieChart.Data("Manqué", 100 - adherenceRate)
        );
        
        adherenceChart.setData(pieChartData);
        
        // Add percentage labels to pie chart slices
        pieChartData.forEach(data -> 
            data.nameProperty().bind(
                javafx.beans.binding.Bindings.concat(
                    data.getName(), " ", String.format("%.1f%%", data.getPieValue())
                )
            )
        );
    }
    
    private void updateUsageChart(List<Medicament> medicaments) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doses par semaine");
        
        // Calculate usage frequency for each medication
        for (Medicament med : medicaments) {
            int frequency = calculateUsageFrequency(med);
            series.getData().add(new XYChart.Data<>(med.getNom(), frequency));
        }
        
        usageChart.getData().clear();
        usageChart.getData().add(series);
    }
    
    private void updateNextDose(List<Medicament> medicaments) {
        LocalDateTime nextDose = findNextDose(medicaments);
        if (nextDose != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            nextDoseLabel.setText(nextDose.format(formatter));
        } else {
            nextDoseLabel.setText("Aucune");
        }
    }
    
    private double calculateAdherence(List<Medicament> medicaments) {
        // For now, using a simulated adherence rate
        // In a real implementation, this would be calculated from actual usage logs
        int totalScheduled = 0;
        int totalTaken = 0;
        
        for (Medicament med : medicaments) {
            // Parse schedule to count total scheduled doses
            String schedule = med.getSchedule();
            if (schedule != null && !schedule.isEmpty()) {
                // Count number of times in schedule
                String[] times = schedule.split(",");
                totalScheduled += times.length * 7; // 7 days per week
                
                // Simulate some doses being taken (75-95% adherence randomly)
                double randomAdherence = 75 + Math.random() * 20;
                totalTaken += (int)(times.length * 7 * (randomAdherence / 100));
            }
        }
        
        return totalScheduled > 0 ? (totalTaken * 100.0 / totalScheduled) : 100.0;
    }
    
    private int calculateUsageFrequency(Medicament med) {
        // Calculate weekly frequency based on schedule
        String schedule = med.getSchedule();
        if (schedule != null && !schedule.isEmpty()) {
            String[] times = schedule.split(",");
            return times.length * 7; // Times per day * 7 days
        }
        return 0;
    }
    
    private LocalDateTime findNextDose(List<Medicament> medicaments) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextDose = null;
        
        for (Medicament med : medicaments) {
            String schedule = med.getSchedule();
            if (schedule != null && !schedule.isEmpty()) {
                String[] times = schedule.split(",");
                for (String time : times) {
                    try {
                        // Parse time (assuming format like "8:00 AM")
                        String[] parts = time.trim().split(" ");
                        String[] hourMin = parts[0].split(":");
                        int hour = Integer.parseInt(hourMin[0]);
                        int minute = Integer.parseInt(hourMin[1]);
                        
                        // Adjust for PM times
                        if (parts.length > 1 && parts[1].equalsIgnoreCase("PM") && hour != 12) {
                            hour += 12;
                        }
                        // Adjust for AM 12
                        if (parts.length > 1 && parts[1].equalsIgnoreCase("AM") && hour == 12) {
                            hour = 0;
                        }
                        
                        LocalDateTime doseTime = now.withHour(hour).withMinute(minute);
                        
                        // If this dose is in the past, move to tomorrow
                        if (doseTime.isBefore(now)) {
                            doseTime = doseTime.plusDays(1);
                        }
                        
                        // Update nextDose if this is sooner
                        if (nextDose == null || doseTime.isBefore(nextDose)) {
                            nextDose = doseTime;
                        }
                    } catch (Exception e) {
                        // Skip invalid time formats
                        continue;
                    }
                }
            }
        }
        
        return nextDose;
    }
    
    @FXML
    public void goBack() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/medicament.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
    }
}
