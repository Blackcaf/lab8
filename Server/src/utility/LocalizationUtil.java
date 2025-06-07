package utility;

import models.HumanBeing;

import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationUtil {
    public static ResourceBundle getBundle(HumanBeing argument) {
        String lang = "ru";
        if (argument != null && argument.getName() != null && !argument.getName().trim().isEmpty()) {
            lang = argument.getName().trim();
        }
        try {
            return ResourceBundle.getBundle("messages", new Locale(lang));
        } catch (Exception e) {
            return ResourceBundle.getBundle("messages", new Locale("ru"));
        }
    }
}