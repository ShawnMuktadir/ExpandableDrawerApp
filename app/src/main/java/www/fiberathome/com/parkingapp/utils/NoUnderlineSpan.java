package www.fiberathome.com.parkingapp.utils;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

public class NoUnderlineSpan extends ClickableSpan {
    // extend ClickableSpan

    String clicked;

    public NoUnderlineSpan(String string) {
        super();
        clicked = string;
    }

    public NoUnderlineSpan() {

    }

    @Override
    public void onClick(View tv) {
        //Toast.makeText(MainActivity.this,clicked , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {// override updateDrawState
        ds.setUnderlineText(false); // set to false to remove underline
    }
}
