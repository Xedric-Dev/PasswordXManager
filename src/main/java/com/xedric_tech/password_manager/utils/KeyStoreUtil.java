package com.xedric_tech.password_manager.utils;

import com.xedric_tech.password_manager.models.KeystoreResponse;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

@Component
public class KeyStoreUtil {

    @Setter
    @Getter
    private Key aesKey;

    private final VerifyPasswordUtil verifyPasswordUtil;

    public KeyStoreUtil(VerifyPasswordUtil verifyPasswordUtil) {
        this.verifyPasswordUtil = verifyPasswordUtil;
    }

    public KeystoreResponse checkKeyStore(String location, String storePass, String keyPass){
        File keyStoreFile = new File(location);
        if(!keyStoreFile.exists()){
             return generateKeyStore(location, storePass, keyPass);
        }else{
            return loadAesKey(location, storePass, keyPass);
        }
    }

    private KeystoreResponse loadAesKey(String location, String storePass, String keyPass){
        try (InputStream keyStoreStream = new FileInputStream(location)) {
            KeyStore keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load(keyStoreStream, storePass.toCharArray());
            if (!keyStore.containsAlias("aesKey")) {
                generateAlertOnError(new RuntimeException(), "No key with specified alias found.");
                return new KeystoreResponse ("Something went wrong.", false,false);
            }
            setAesKey(keyStore.getKey("aesKey", keyPass.toCharArray()));
        } catch (CertificateException e) {
            generateAlertOnError(e, "Something went wrong while loading the keystore certificate.");
            return new KeystoreResponse ("Something went wrong.", false,false);
        } catch (KeyStoreException e) {
            generateAlertOnError(e, "General error occurred while loading the keystore.");
            return new KeystoreResponse ("Something went wrong.", false,false);
        } catch (IOException | UnrecoverableKeyException e) {
            generateAlertOnError(e, "One or both password inserted are not valid or incorrect.\n Please retry to login.");
            return new KeystoreResponse ("Something went wrong.", false,false);
        } catch (NoSuchAlgorithmException e) {
            generateAlertOnError(e, "Something went wrong while loading the decrypting algorithm.");
            return new KeystoreResponse ("Something went wrong.", false,false);
        }

        return new KeystoreResponse ("AES Key loaded", true,false);
    }

    private KeystoreResponse generateKeyStore(String location, String storePass, String keyPass){

        if(!verifyPasswordUtil.verifyPassword(storePass)){
            return new KeystoreResponse("Archive Password does not meet the criteria for password security. Cannot proceed, please modify it and try again.", false,true);
        }else if(!verifyPasswordUtil.verifyPassword(keyPass)){
            return new KeystoreResponse("Access Key does not meet the criteria for password security. Cannot proceed, please modify it and try again.", false,true);
        }else {
            String[] cmd = {
                    "keytool","-genseckey",
                    "-keystore",location,
                    "-storetype","jceks",
                    "-storepass",storePass,
                    "-keyalg", "AES",
                    "-keysize","256",
                    "-alias", "aesKey",
                    "-keypass", keyPass
            };

            try{
                Runtime.getRuntime().exec(cmd);
            }catch (IOException e){
                throw new RuntimeException();
            }

            return new KeystoreResponse("Keystore generated", true,false);

        }

    }

    private void generateAlertOnError(Exception e, String contentText){

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Exception Dialog");
            alert.setHeaderText(null);
            alert.setContentText(contentText);

            var stringWriter = new StringWriter();
            var printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);

            var textArea = new TextArea(stringWriter.toString());
            textArea.setEditable(false);
            textArea.setWrapText(false);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            var content = new GridPane();
            content.setMaxWidth(Double.MAX_VALUE);
            content.add(new Label("Full stacktrace:"), 0, 0);
            content.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(content);
            alert.showAndWait();

    }

}
