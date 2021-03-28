package www.fiberathome.com.parkingapp.utils;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import www.fiberathome.com.parkingapp.R;

public class NoUnderlineSpan extends ClickableSpan {

    private Context context;
    private String clicked;

    public NoUnderlineSpan(String string) {
        super();
        this.clicked = string;
    }

    public NoUnderlineSpan(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(@NonNull View tv) {
        //Toast.makeText(context, clicked , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateDrawState(TextPaint ds) {// override updateDrawState
        ds.setUnderlineText(false);// set to false to remove underline
        ds.setColor(context.getResources().getColor(R.color.black));
    }
}
