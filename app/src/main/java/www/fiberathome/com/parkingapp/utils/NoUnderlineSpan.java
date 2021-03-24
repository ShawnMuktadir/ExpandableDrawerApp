package www.fiberathome.com.parkingapp.utils;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

public class NoUnderlineSpan extends ClickableSpan {

    String clicked;

    public NoUnderlineSpan(String string) {
        super();
        clicked = string;
    }

    public NoUnderlineSpan() {

    }

    @Override
    public void onClick(@NonNull View tv) {
        //Toast.makeText(MainActivity.this,clicked , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {// override updateDrawState
        ds.setUnderlineText(false); // set to false to remove underline
    }
}
