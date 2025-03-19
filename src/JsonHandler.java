import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class JsonHandler {
    public static final String FILE_NAME = "tache.json";

    // Adapter pour sérialiser/désérialiser LocalDate avec Gson
    private static class LocalDateAdapter implements com.google.gson.JsonSerializer<LocalDate>, com.google.gson.JsonDeserializer<LocalDate> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        @Override
        public com.google.gson.JsonElement serialize(LocalDate src, java.lang.reflect.Type typeOfSrc, com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(src.format(formatter));
        }

        @Override
        public LocalDate deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, com.google.gson.JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString(), formatter);
        }
    }

    // Méthode utilitaire pour générer une clé AES à partir d'un mot de passe
    private static SecretKeySpec getSecretKey(String myKey) throws Exception {
        byte[] key = myKey.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        return new SecretKeySpec(key, "AES");
    }

    // Chiffrement AES
    public static String encrypt(String strToEncrypt, String secret) throws Exception {
        SecretKeySpec secretKey = getSecretKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Déchiffrement AES
    public static String decrypt(String strToDecrypt, String secret) throws Exception {
        SecretKeySpec secretKey = getSecretKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
        return new String(decrypted, "UTF-8");
    }

    // Sauvegarde la liste des tâches dans le fichier JSON
    public static void saveTasks(List<Task> tasks, boolean encryptFlag, String password) {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .setPrettyPrinting()
                    .create();
            String json = gson.toJson(tasks);
            if (encryptFlag) {
                json = encrypt(json, password);
            }
            Files.write(Paths.get(FILE_NAME), json.getBytes("UTF-8"));
            System.out.println("Tâches sauvegardées dans " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        } catch (Exception ex) {
            System.err.println("Erreur de chiffrement : " + ex.getMessage());
        }
    }

    // Charge la liste des tâches depuis le fichier JSON
    public static List<Task> loadTasks(boolean decryptFlag, String password) {
        List<Task> tasks = new ArrayList<>();
        try {
            if (!Files.exists(Paths.get(FILE_NAME))) {
                System.out.println("Fichier non trouvé, création automatique d'un fichier vide.");
                saveTasks(tasks, false, "");
                return tasks;
            }
            byte[] fileContent = Files.readAllBytes(Paths.get(FILE_NAME));
            String json = new String(fileContent, "UTF-8");
            if (decryptFlag) {
                json = decrypt(json, password);
            }
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                    .create();
            tasks = gson.fromJson(json, new TypeToken<List<Task>>(){}.getType());
            return tasks;
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            return tasks;
        } catch (Exception ex) {
            System.err.println("Erreur de déchiffrement : " + ex.getMessage());
            return tasks;
        }
    }
}
