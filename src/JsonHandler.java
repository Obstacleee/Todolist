import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.util.Base64;

public class JsonHandler {
    public static final String FILE_NAME = "tache.json";

    // Génère une clé secrète à partir d'un mot de passe
    private static SecretKeySpec getSecretKey(String myKey) throws Exception {
        byte[] key = myKey.getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        key = sha.digest(key);
        return new SecretKeySpec(key, "AES");
    }

    // Chiffre une chaîne de caractères avec AES/ECB/PKCS5Padding
    public static String encrypt(String strToEncrypt, String secret) throws Exception {
        SecretKeySpec secretKey = getSecretKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(strToEncrypt.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Déchiffre une chaîne de caractères avec AES/ECB/PKCS5Padding
    public static String decrypt(String strToDecrypt, String secret) throws Exception {
        SecretKeySpec secretKey = getSecretKey(secret);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(strToDecrypt));
        return new String(decrypted, "UTF-8");
    }

    // Sauvegarde la liste des tâches dans le fichier JSON (avec ou sans chiffrement)
    public static void saveTasks(List<Task> tasks, boolean encryptFlag, String password) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("[\n");
            for (int i = 0; i < tasks.size(); i++) {
                sb.append(tasks.get(i).toJson());
                if (i < tasks.size() - 1) {
                    sb.append(",\n");
                }
            }
            sb.append("\n]");
            String json = sb.toString();
            if (encryptFlag) {
                json = encrypt(json, password);
            }
            Files.write(Paths.get(FILE_NAME), json.getBytes("UTF-8"));
            System.out.println("Tâches sauvegardées dans " + FILE_NAME);
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    // Charge la liste des tâches depuis le fichier JSON (avec ou sans déchiffrement)
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
            // Suppression des espaces superflus et retrait des crochets
            json = json.trim();
            if (json.startsWith("[")) {
                json = json.substring(1);
            }
            if (json.endsWith("]")) {
                json = json.substring(0, json.length() - 1);
            }
            // Découpage en objets JSON individuels
            String[] taskJsonArray = json.split("},");
            for (int i = 0; i < taskJsonArray.length; i++) {
                String taskJson = taskJsonArray[i].trim();
                if (!taskJson.endsWith("}")) {
                    taskJson = taskJson + "}";
                }
                if (taskJson.length() == 0) continue;
                Task task = Task.fromJson(taskJson);
                tasks.add(task);
            }
            return tasks;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement : " + e.getMessage());
            return tasks;
        }
    }
}
