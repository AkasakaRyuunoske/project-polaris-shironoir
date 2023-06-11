package projectpolaris.ProjectPolarisShironoir.Handshake;

import lombok.extern.log4j.Log4j2;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

@Component
@Log4j2
public class Camellia {
    @Value("${security.symmetric-key.key}")
    private String key;

    @Value("${security.symmetric-key.algorithm-name}")
    private String algorithm_name;

    @Value("${security.symmetric-key.algorithm-transformation}")
    private String algorithm_transformation;

    private IvParameterSpec ivParameterSpec;

    private SecretKeySpec secretKeySpec;

    private Cipher cipher;
    public void generateSymmetricKeys() throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {

        Security.addProvider(new BouncyCastleProvider());

        // Generate a random IV
        byte[] ivBytes = new byte[16];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);
        ivParameterSpec = new IvParameterSpec(ivBytes);

//        // Create a Camellia cipher instance and initialize it with the key and IV
        cipher = Cipher.getInstance(algorithm_transformation, "BC");
        secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm_name);
//        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

        // Encrypt the message
//        byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
//
//        // Encode the encrypted bytes and IV using Base64 for convenient display or transmission
//        String encryptedMessage = Base64.getEncoder().encodeToString(encryptedBytes);
//        String encodedIV = Base64.getEncoder().encodeToString(ivBytes);
//        System.out.println("Encrypted message: " + encryptedMessage);
//        System.out.println("IV: " + encodedIV);
//
//        // Create a new cipher instance for decryption and initialize it with the key and IV
//        Cipher decryptionCipher = Cipher.getInstance("Camellia/CBC/PKCS7Padding", "BC");
//        decryptionCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
//
//        // Decode the Base64 encoded message and IV
//        byte[] decodedBytes = Base64.getDecoder().decode(encryptedMessage);
//        byte[] decodedIV = Base64.getDecoder().decode(encodedIV);
//
//        // Decrypt the message
//        byte[] decryptedBytes = decryptionCipher.doFinal(decodedBytes);
//
//        // Convert the decrypted bytes back to a string
//        String decryptedMessage = new String(decryptedBytes, StandardCharsets.UTF_8);
//        System.out.println("Decrypted message: " + decryptedMessage);
    }

    public String enchant(String plain_string){
        log.info("Enchant was called!");
        return null; //#todo write enchant logic
    }

    public String disenchant(String enchanted_string) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException {
        log.info("Disenchant was called!");

        // Create a new cipher instance for decryption and initialize it with the key and IV
        Cipher decryptionCipher = Cipher.getInstance(algorithm_transformation, "BC");
        decryptionCipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

        // Decode the Base64 encoded message and IV
        byte[] decodedBytes = Base64.getDecoder().decode(enchanted_string);

        // Decrypt the message
        byte[] decryptedBytes = decryptionCipher.doFinal(decodedBytes);

        // Convert the decrypted bytes back to a string
        String decryptedMessage = new String(decryptedBytes, StandardCharsets.UTF_8);

        log.info("Decrypted message: " + decryptedMessage);

        return decryptedMessage; //#todo write disenchant logic
    }


    // Getters and Setters
    public IvParameterSpec getIvParameterSpec() {
        return ivParameterSpec;
    }

    public void setIvParameterSpec(IvParameterSpec ivParameterSpec) {
        this.ivParameterSpec = ivParameterSpec;
    }

    public SecretKeySpec getSecretKeySpec() {
        return secretKeySpec;
    }

    public void setSecretKeySpec(SecretKeySpec secretKeySpec) {
        this.secretKeySpec = secretKeySpec;
    }
}
