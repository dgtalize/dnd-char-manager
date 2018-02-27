package com.dgtalize.dndcharmanager;

import android.content.Intent;
import android.net.ParseException;
import android.text.TextUtils;

/**
 * Created by Diego on 2017-05-06.
 */

public class ConvertUtils {
    public static String generateLevelKey(int level) {
        return generateLevelKey(level, true);
    }

    public static String generateLevelKey(int level, Boolean max20) {
        if (max20 && level > 20) {
            level = 20;
        }
        return String.format("l%d", level);
    }

    public static int getLevelFromKey(String levelKey) {
        return Integer.parseInt(levelKey.substring(1));
    }

    /**
     * Checks if the string passed is in fact a valid Points string
     *
     * @param inputPoints
     * @return
     */
    public static boolean isPoints(CharSequence inputPoints) {
        Boolean isPoints = false;

        //not empty
        //has to be only digits or the -/+ signs
        isPoints = !TextUtils.isEmpty(inputPoints)
                && inputPoints.toString().matches("^[\\-\\+]?\\d+$");

        //validate any other case
        if (isPoints) {
            try {
                int parsedInt = Integer.parseInt(inputPoints.toString());
            } catch (ParseException parseEx) {
                isPoints = false;
            } catch (NumberFormatException nfEx) {
                isPoints = false;
            }
        }

        return isPoints;
    }

    /**
     * Encode an email address to be used as Key en Firebase DB
     *
     * @param email
     * @return The DB key
     */
    public static String encodeEmailDB(String email) {
        return email.replace('.', ',');
    }

    /**
     * Decode an Firebase DB key that is originally an email address
     *
     * @param emailKey
     * @return The email address
     */
    public static String decodeEmailDB(String emailKey) {
        return emailKey.replace(',', '.');
    }

}
