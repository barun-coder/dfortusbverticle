package com.displayfort.dfortusb.validation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Husain
 */
public class ValidationUtils {
    private final static String NAME_PATTERN = "^[a-zA-ZéüöêåÁÅÉá .´'`-].{1,50}$";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]{1,2}+)*(\\.[A-Za-z]{1,})$";
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,15}$";
    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})";
    private static final String MobilePattern = "[0-9]{10}";
    private static final String UK_POSTCODE_PATTERN =
            "(GIR 0AA)|((([A-Z-[QVX]][0-9][0-9]?)|(([A-Z-[QVX]][A-Z-[IJZ]][0-9][0-9]?)|(([A-Z-[QVX]][0-9][A-HJKPSTUW])|([A-Z-[QVX]][A-Z-[IJZ]][0-9][ABEHMNPRVWXY])))) [0-9][A-Z-[CIKMOV]]{2})";
    private static final String CARD_DATE_VALID = ("(?:0[1-9]|1[0-2])/[0-9]{2}");

    public static boolean isValidPostcode(String postcode) {
//        Pattern pattern = Pattern.compile(UK_POSTCODE_PATTERN);
//        Matcher matcher = pattern.matcher(postcode);
        return isUkPostCodeValid(postcode);

    }

    /**
     * Validates email with regular expression
     *
     * @param email the email to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmailAddress(String email) {

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email.toLowerCase());

        return matcher.matches();
    }

    public static boolean isValidEmail(String email) {
        if (email != null && email.length() > 0) {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email.toLowerCase());
            // "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        } else {
            return false;
        }
    }

    /**
     * Validates user lastName with regular expression
     *
     * @param username the username to validate
     * @return true if user lastName is valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }

    public static boolean isValidString(String username, int limit) {
        return (username != null && username.length() >= limit) ? true : false;
    }

    public static boolean isValidFirstName(String firstname) {
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(firstname);
        return matcher.matches();
    }

    /**
     * Validate password with regular expression
     *
     * @param password password for validation
     * @return true valid password, false invalid password
     */
    public static boolean isValidPassword(final String password) {

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();

    }

    //---- validation on mobile lastName..
    public static boolean isValidMobile(String phone) {
        Pattern pattern = Pattern.compile(MobilePattern);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public static boolean isCardDateValid(String input) {
        Pattern pattern = Pattern.compile(CARD_DATE_VALID);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static boolean isCreditCardDateValid(String input) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
        simpleDateFormat.setLenient(false);
        Date expiry = null;
        try {
            expiry = simpleDateFormat.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return expiry.before(new Date());
    }


    public static boolean isUkPostCodeValid(String value) {
        Set<String> postCodeList = new HashSet<>();
        postCodeList.add("BS981TL");
        postCodeList.add("BX11LT");
        postCodeList.add("BX21LB");
        postCodeList.add("BX32BB");
        postCodeList.add("BX55AT");
        postCodeList.add("CF101BH");
        postCodeList.add("CF991NA");
        postCodeList.add("DE993GG");
        postCodeList.add("DH981BT");
        postCodeList.add("DH991NS");
        postCodeList.add("E161XL");
        postCodeList.add("E202AQ");
        postCodeList.add("E202BB");
        postCodeList.add("E202ST");
        postCodeList.add("E203BS");
        postCodeList.add("E203EL");
        postCodeList.add("E203ET");
        postCodeList.add("E203HB");
        postCodeList.add("E203HY");
        postCodeList.add("E981SN");
        postCodeList.add("E981ST");
        postCodeList.add("E981TT");
        postCodeList.add("EC2N2DB");
        postCodeList.add("EC4Y0HQ");
        postCodeList.add("EH991SP");
        postCodeList.add("G581SB");
        postCodeList.add("GIR0AA");
        postCodeList.add("IV212LR");
        postCodeList.add("L304GB");
        postCodeList.add("LS981FD");
        postCodeList.add("N19GU");
        postCodeList.add("N811ER");
        postCodeList.add("NG801EH");
        postCodeList.add("NG801LH");
        postCodeList.add("NG801RH");
        postCodeList.add("NG801TH");
        postCodeList.add("SE18UJ");
        postCodeList.add("SN381NW");
        postCodeList.add("SW1A0AA");
        postCodeList.add("SW1A0PW");
        postCodeList.add("SW1A1AA");
        postCodeList.add("SW1A2AA");
        postCodeList.add("SW1P3EU");
        postCodeList.add("SW1W0DT");
        postCodeList.add("TW89GS");
        postCodeList.add("W1A1AA");
        postCodeList.add("W1D4FA");
        postCodeList.add("W1N4DJ");
        postCodeList.add("AI-2640");
        postCodeList.add("ASCN1ZZ");
        postCodeList.add("STHL1ZZ");
        postCodeList.add("TDCU1ZZ");
        postCodeList.add("BBND1ZZ");
        postCodeList.add("BIQQ1ZZ");
        postCodeList.add("FIQQ1ZZ");
        postCodeList.add("GX111AA");
        postCodeList.add("PCRN1ZZ");
        postCodeList.add("SIQQ1ZZ");
        postCodeList.add("TKCA1ZZ");
        String mPostcodeVal = value.toUpperCase().replace(" ", "");
        if (postCodeList.contains(mPostcodeVal)) {
            return true;
        } else {
            int length = mPostcodeVal.length();
            char[] valueArray = mPostcodeVal.toCharArray();
            switch (length) {
                case 7:
                    if (Character.isLetter(valueArray[0]) && Character.isLetter(valueArray[1]) && Character.isLetter(valueArray[5]) && Character.isLetter(valueArray[6]) && Character.isDigit(valueArray[2]) && Character.isDigit(valueArray[4])) {
                        if (Character.isLetter(valueArray[3]) || Character.isDigit(valueArray[3])) {
                            return true;
                        } else {
                            return false;
                        }

                    } else {
                        return false;
                    }
                case 6:
                    if (Character.isLetter(valueArray[0]) && Character.isLetter(valueArray[4]) && Character.isLetter(valueArray[5]) && Character.isDigit(valueArray[3])) {
                        if (Character.isLetter(valueArray[1]) && Character.isDigit(valueArray[2])) {
                            return true;
                        } else if (Character.isDigit(valueArray[1]) && (Character.isDigit(valueArray[2]) || Character.isDigit(valueArray[2]))) {
                            return true;
                        } else {
                            return false;
                        }

                    } else {
                        return false;
                    }
                case 5:
                    if (Character.isLetter(valueArray[0]) && Character.isLetter(valueArray[4]) && Character.isLetter(valueArray[3])) {
                        if (Character.isDigit(valueArray[1]) && Character.isDigit(valueArray[2])) {
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
            }
        }

        return false;
    }


}
