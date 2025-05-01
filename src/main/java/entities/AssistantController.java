package entities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import services.MedicamentService;

public class AssistantController {
    @FXML
    private Button backButton;
    
    @FXML
    private TextField userInput;
    
    @FXML
    private VBox chatArea;
    
    private MedicamentService medicamentService;
    
    @FXML
    public void initialize() {
        medicamentService = new MedicamentService();
        // Add welcome message
        addBotMessage("Bonjour! Je suis votre assistant IA. Comment puis-je vous aider aujourd'hui?");
    }

    @FXML
    public void retourGestionMedicaments() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/medicament.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
    }
    
    @FXML
    public void sendMessage() {
        String message = userInput.getText().trim();
        if (!message.isEmpty()) {
            addUserMessage(message);
            processUserMessage(message);
            userInput.clear();
        }
    }
    
    private void addUserMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_RIGHT);
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 10; " +
                             "-fx-background-radius: 15; -fx-max-width: 300;");
        
        messageBox.getChildren().add(messageLabel);
        chatArea.getChildren().add(messageBox);
    }
    
    private void addBotMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.setPadding(new Insets(5, 10, 5, 10));
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-padding: 10; " +
                             "-fx-background-radius: 15; -fx-max-width: 300;");
        
        messageBox.getChildren().add(messageLabel);
        chatArea.getChildren().add(messageBox);
    }
    
    private void processUserMessage(String message) {
        message = message.toLowerCase();
        try {
            // Check for greetings first
            if (isGreeting(message)) {
                respondToGreeting(message);
                return;
            }
            
            if (message.contains("liste") || message.contains("tous") || message.contains("médicaments")) {
                List<Medicament> medicaments = medicamentService.afficherTous();
                StringBuilder response = new StringBuilder("Voici la liste des médicaments:\n\n");
                for (Medicament med : medicaments) {
                    response.append("- ").append(med.getNom())
                            .append(" (").append(med.getDosage()).append(")\n");
                }
                addBotMessage(response.toString());
            } 
            else if (message.contains("dosage") || message.contains("dose")) {
                String medicamentName = extractMedicamentName(message);
                if (medicamentName != null) {
                    List<Medicament> medicaments = medicamentService.afficherTous();
                    Medicament bestMatch = findBestMatch(medicamentName, medicaments);
                    
                    if (bestMatch != null) {
                        addBotMessage("Le dosage pour " + bestMatch.getNom() + " est: " + bestMatch.getDosage());
                        
                        // If we found a similar but not exact match, suggest the correct spelling
                        if (!bestMatch.getNom().toLowerCase().contains(medicamentName)) {
                            addBotMessage("Note: J'ai supposé que vous parliez de '" + bestMatch.getNom() + 
                                         "'. Si ce n'est pas le cas, veuillez préciser le nom exact.");
                        }
                    } else {
                        // Suggest similar medications
                        List<Medicament> similarMeds = findSimilarMedications(medicamentName, medicaments);
                        if (!similarMeds.isEmpty()) {
                            StringBuilder suggestion = new StringBuilder("Je ne trouve pas exactement ce médicament. Vouliez-vous dire:\n\n");
                            for (Medicament med : similarMeds) {
                                suggestion.append("- ").append(med.getNom()).append("\n");
                            }
                            addBotMessage(suggestion.toString());
                        } else {
                            addBotMessage("Désolé, je ne trouve pas ce médicament. Pouvez-vous vérifier le nom?");
                        }
                    }
                } else {
                    addBotMessage("Pour quel médicament voulez-vous connaître le dosage?");
                }
            }
            else if (message.contains("horaire") || message.contains("quand") || message.contains("heure")) {
                String medicamentName = extractMedicamentName(message);
                if (medicamentName != null) {
                    List<Medicament> medicaments = medicamentService.afficherTous();
                    Medicament bestMatch = findBestMatch(medicamentName, medicaments);
                    
                    if (bestMatch != null) {
                        addBotMessage("L'horaire pour " + bestMatch.getNom() + " est: " + bestMatch.getSchedule());
                        
                        // If we found a similar but not exact match, suggest the correct spelling
                        if (!bestMatch.getNom().toLowerCase().contains(medicamentName)) {
                            addBotMessage("Note: J'ai supposé que vous parliez de '" + bestMatch.getNom() + 
                                         "'. Si ce n'est pas le cas, veuillez préciser le nom exact.");
                        }
                    } else {
                        // Suggest similar medications
                        List<Medicament> similarMeds = findSimilarMedications(medicamentName, medicaments);
                        if (!similarMeds.isEmpty()) {
                            StringBuilder suggestion = new StringBuilder("Je ne trouve pas exactement ce médicament. Vouliez-vous dire:\n\n");
                            for (Medicament med : similarMeds) {
                                suggestion.append("- ").append(med.getNom()).append("\n");
                            }
                            addBotMessage(suggestion.toString());
                        } else {
                            addBotMessage("Désolé, je ne trouve pas ce médicament. Pouvez-vous vérifier le nom?");
                        }
                    }
                } else {
                    addBotMessage("Pour quel médicament voulez-vous connaître l'horaire?");
                }
            }
            else if (message.contains("aide") || message.contains("help") || message.contains("comment")) {
                addBotMessage("Je peux vous aider avec:\n\n" +
                        "- La liste de tous les médicaments\n" +
                        "- Le dosage d'un médicament spécifique\n" +
                        "- Les horaires de prise d'un médicament\n\n" +
                        "Posez simplement votre question!");
            }
            else {
                addBotMessage("Désolé, je ne comprends pas votre demande. Essayez de poser une question sur:\n\n" +
                        "- La liste des médicaments\n" +
                        "- Le dosage d'un médicament\n" +
                        "- Les horaires de prise");
            }
        } catch (SQLException e) {
            addBotMessage("Désolé, une erreur s'est produite lors de la recherche des informations.");
        }
    }
    
    private String extractMedicamentName(String message) {
        // Common words to ignore
        String[] wordsToIgnore = {"le", "la", "les", "du", "de", "des", "pour", "dosage", "dose", 
                                 "horaire", "heure", "quand", "prendre", "médicament", "est", "quel"};
        
        // Split message into words
        String[] words = message.split("[\\s,;!?]+"); // Split on multiple types of separators
        StringBuilder medicamentName = new StringBuilder();
        
        boolean foundKeyword = false;
        for (String word : words) {
            word = word.toLowerCase().trim();
            if (word.isEmpty()) continue;
            
            // Skip common words
            boolean ignore = false;
            for (String ignoreWord : wordsToIgnore) {
                if (word.equals(ignoreWord)) {
                    ignore = true;
                    break;
                }
            }
            
            // If we find a keyword like "dosage" or "horaire", start collecting words after it
            if (word.equals("dosage") || word.equals("horaire")) {
                foundKeyword = true;
                medicamentName.setLength(0); // Clear any words before the keyword
                continue;
            }
            
            if (!ignore && word.length() > 1) { // Changed to > 1 to catch more potential names
                if (medicamentName.length() > 0) {
                    medicamentName.append(" ");
                }
                medicamentName.append(word);
            }
        }
        
        return medicamentName.length() > 0 ? medicamentName.toString() : null;
    }
    
    private Medicament findBestMatch(String query, List<Medicament> medicaments) {
        Medicament bestMatch = null;
        int bestScore = Integer.MIN_VALUE;
        query = query.toLowerCase();
        
        for (Medicament med : medicaments) {
            String medName = med.getNom().toLowerCase();
            
            // Try different matching strategies
            int score = 0;
            
            // Exact match (highest priority)
            if (medName.equals(query)) {
                return med;
            }
            
            // Contains match
            if (medName.contains(query) || query.contains(medName)) {
                score += 100;
            }
            
            // Common prefix match
            if (medName.startsWith(query) || query.startsWith(medName)) {
                score += 50;
            }
            
            // Levenshtein distance for fuzzy matching
            int distance = levenshteinDistance(query, medName);
            int similarityScore = 100 - (distance * 10); // Convert distance to similarity score
            score += similarityScore;
            
            // Update best match if this score is higher
            if (score > bestScore) {
                bestScore = score;
                bestMatch = med;
            }
        }
        
        // Only return a match if the score is above a threshold
        return bestScore > 0 ? bestMatch : null;
    }
    
    private List<Medicament> findSimilarMedications(String query, List<Medicament> medicaments) {
        List<Medicament> similarMeds = new ArrayList<>();
        query = query.toLowerCase();
        
        for (Medicament med : medicaments) {
            String medName = med.getNom().toLowerCase();
            
            // Use Levenshtein distance to find similar names
            int distance = levenshteinDistance(query, medName);
            
            // If the distance is small enough relative to the length of the strings
            if (distance <= Math.min(query.length(), medName.length()) / 2) {
                similarMeds.add(med);
            }
        }
        
        // Limit to top 3 similar medications
        return similarMeds.size() > 3 ? similarMeds.subList(0, 3) : similarMeds;
    }
    
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = Math.min(
                        dp[i - 1][j - 1] + (s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1),
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    );
                }
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    private boolean isGreeting(String message) {
        String[] greetings = {"bonjour", "salut", "hello", "hi", "hey", "coucou", "bonsoir"};
        message = message.toLowerCase().trim();
        
        for (String greeting : greetings) {
            if (message.contains(greeting)) {
                return true;
            }
        }
        return false;
    }
    
    private void respondToGreeting(String message) {
        // Get current hour to determine appropriate greeting
        int hour = java.time.LocalTime.now().getHour();
        String timeBasedGreeting;
        
        if (hour >= 5 && hour < 12) {
            timeBasedGreeting = "Bonjour";
        } else if (hour >= 12 && hour < 18) {
            timeBasedGreeting = "Bon après-midi";
        } else {
            timeBasedGreeting = "Bonsoir";
        }
        
        // Array of possible responses
        String[] responses = {
            timeBasedGreeting + "! Comment puis-je vous aider avec vos médicaments aujourd'hui?",
            timeBasedGreeting + "! Je suis là pour répondre à vos questions sur les médicaments.",
            "Salut! Avez-vous des questions sur vos médicaments?"
        };
        
        // Pick a random response
        int randomIndex = (int) (Math.random() * responses.length);
        addBotMessage(responses[randomIndex]);
    }
}
