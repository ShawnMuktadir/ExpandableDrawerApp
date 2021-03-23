package www.fiberathome.com.parkingapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.ui.booking.newBooking.BookingActivity;

public class TextUtils {
    private static TextUtils textUtils;

    public static TextUtils getInstance() {
        if (textUtils == null) {
            textUtils = new TextUtils();
        }

        return textUtils;
    }

    public void setTextColor(TextView tvText, Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvText.setTextColor(context.getColor(resId));
        } else {
            tvText.setTextColor(context.getResources().getColor(resId));
        }
    }

    public void setTextColor(TextInputEditText textInputEditText, Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textInputEditText.setTextColor(context.getColor(resId));
        } else {
            textInputEditText.setTextColor(context.getResources().getColor(resId));
        }
    }

    public void setSeparateTextColor(TextView tvText, String color, String text, String coloredText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvText.setText(Html.fromHtml(text + "<font color='" + color + "'>" + coloredText + "</font>", 0));
        } else {
            tvText.setText(Html.fromHtml(text + "<font color='" + color + "'>" + coloredText + "</font>"));
        }
    }

    public void setHintTextColor(TextView tvText, Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvText.setHintTextColor(context.getColor(resId));
        } else {
            tvText.setHintTextColor(context.getResources().getColor(resId));
        }
    }

    public void setHintTextColor(TextInputEditText tvText, Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvText.setHintTextColor(context.getColor(resId));
        } else {
            tvText.setHintTextColor(context.getResources().getColor(resId));
        }
    }

    public boolean textContainsBangla(String text) {
        for (char c : text.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BENGALI) {
                return true;
            }
        }
        return false;
    }

    public boolean textContainsEnglish(String str) {
        return ((!str.equals(""))
                && (str != null)
                && (str.matches("^[a-zA-Z]*$")));
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty() || str.equals("")) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String capitalizeFirstLetter(final String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return str;
        }

        final char firstChar = str.charAt(0);
        final char newChar = Character.toTitleCase(firstChar);
        if (firstChar == newChar) {
            // already capitalized
            return str;
        }

        char[] newChars = new char[strLen];
        newChars[0] = newChar;
        str.getChars(1, strLen, newChars, 1);
        return String.valueOf(newChars);
    }

    public static String allTrim(String str) {
        int j = 0;
        int count = 0;  // Number of extra spaces
        int lspaces = 0;// Number of left spaces
        char[] ch = str.toCharArray();
        int len = str.length();
        StringBuffer bchar = new StringBuffer();
        if (ch[0] == ' ') {
            while (ch[j] == ' ') {
                lspaces++;
                j++;
            }
        }
        for (int i = lspaces; i < len; i++) {
            if (ch[i] != ' ') {
                if (count > 1 || count == 1) {
                    bchar.append(' ');
                    count = 0;
                }
                bchar.append(ch[i]);
            } else if (ch[i] == ' ') {
                count++;
            }
        }
        return bchar.toString();
    }

    public boolean getSpecialCharacter(Context context, String str) {
        if (str == null || str.trim().isEmpty()) {
            System.out.println("format of string is Incorrect ");
//            Toast.makeText(context, "Format of string is Incorrect", Toast.LENGTH_SHORT).show();
            return false;
        }

        Pattern pattern = Pattern.compile("[^A-Za-z0-9]");
        Matcher matcher = pattern.matcher(str);

        boolean b = matcher.find();
        if (b == true) {
            System.out.println("There is a special character in my string:- " + str);
//            Toast.makeText(context, "Sorry, no places found!", Toast.LENGTH_SHORT).show();
        } else {
            System.out.println("There is no special character in my String :-  " + str);
        }
        return true;
    }

    public boolean isProbablyBangla(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (c >= 0x0980 && c <= 0x09A0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }

    public SpannableString getUnderlinedString(String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

    public CharSequence highlight(String search, String originalText) {
        // ignore case and accents
        // the same thing should have been done for the search text
        String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();

        int start = normalizedText.indexOf(search);
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(), originalText.length());

                highlighted.setSpan(new BackgroundColorSpan(0xFFFCFF48), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }

            return highlighted;
        }
    }

    public SpannableStringBuilder highlightSearchText(SpannableStringBuilder fullText, String searchText) {

        if (searchText.length() == 0) return fullText;

        SpannableStringBuilder wordSpan = new SpannableStringBuilder(fullText);
        Pattern p = Pattern.compile(searchText, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(fullText);
        while (m.find()) {

            int wordStart = m.start();
            int wordEnd = m.end();

            setWordSpan(wordSpan, wordStart, wordEnd);

        }

        return wordSpan;
    }

    private void setWordSpan(SpannableStringBuilder wordSpan, int wordStart, int wordEnd) {
        // Now highlight based on the word boundaries
        ColorStateList redColor = new ColorStateList(new int[][]{new int[]{}}, new int[]{0xffa10901});
        TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, redColor, null);

        wordSpan.setSpan(highlightSpan, wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new BackgroundColorSpan(0xFFFCFF48), wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordSpan.setSpan(new RelativeSizeSpan(1.25f), wordStart, wordEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            assert tm != null;
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                Timber.e("simcountry -> %s", simCountry.toLowerCase(Locale.US));
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    Timber.e("networkCountry -> %s", networkCountry.toLowerCase(Locale.US));
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setSubTextColor(TextView view, String fulltext, String subtext, int color, ClickableSpan clickableAction) {
        view.setText(fulltext, TextView.BufferType.SPANNABLE);
        Spannable str = (Spannable) view.getText();
        int i = fulltext.indexOf(subtext);
        str.setSpan(new ForegroundColorSpan(color), i, i + subtext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (view.isClickable())
            str.removeSpan(clickableAction);
    }

    public TextView createLink(TextView targetTextView, String completeString,
                                      String partToClick, ClickableSpan clickableAction) {

        SpannableString spannableString = new SpannableString(completeString);

        // make sure the String is exist, if it doesn't exist
        // it will throw IndexOutOfBoundException
        int startPosition = completeString.indexOf(partToClick);
        int endPosition = completeString.lastIndexOf(partToClick) + partToClick.length();

        spannableString.setSpan(clickableAction, startPosition, endPosition,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        targetTextView.setText(spannableString);
        targetTextView.setMovementMethod(LinkMovementMethod.getInstance());

        return targetTextView;
    }

    public static Spannable highlightSearchKey(String title) {
        Spannable highlight;
        Pattern pattern;
        Matcher matcher;
        int word_index;
        String title_str;
        String[] words = new String[20];

        word_index = words.length;
        title_str = Html.fromHtml(title).toString();
        highlight = (Spannable) Html.fromHtml(title);
        for (int index = 0; index < word_index; index++) {
            pattern = Pattern.compile("(?i)" + words[index]);
            matcher = pattern.matcher(title_str);
            while (matcher.find()) {
                highlight.setSpan(
                        new BackgroundColorSpan(0x44444444),
                        matcher.start(),
                        matcher.end(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return highlight;
    }
}
