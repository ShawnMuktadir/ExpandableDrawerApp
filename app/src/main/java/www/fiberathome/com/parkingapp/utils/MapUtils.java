package www.fiberathome.com.parkingapp.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.google.android.gms.maps.model.LatLng;

import www.fiberathome.com.parkingapp.R;

import static java.lang.StrictMath.abs;

public class MapUtils {

    private static MapUtils mapUtils;

    public static MapUtils getInstance() {
        if (mapUtils == null) {
            mapUtils = new MapUtils();
        }

        return mapUtils;
    }

    public Bitmap getOriginDestinationMarkerBitmap() {
        int height = 20;
        int width = 20;
        Bitmap bitmap = Bitmap.createBitmap(height, width, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawRect(0F, 0F, width, height, paint);
        return bitmap;
    }

    //for Uber like car bearing
    public float getBearing(LatLng startPosition, LatLng newPos) {

        //Source
        double lat1 = startPosition.latitude;
        double lng1 = startPosition.longitude;

        // destination
        double lat2 = newPos.latitude;
        double lng2 = newPos.longitude;

        double fLat = degreeToRadians(lat1);
        double fLong = degreeToRadians(lng1);
        double tLat = degreeToRadians(lat2);
        double tLong = degreeToRadians(lng2);

        double dLon = (tLong - fLong);

        float degree = (float) (radiansToDegree(Math.atan2(Math.sin(dLon) * Math.cos(tLat),
                Math.cos(fLat) * Math.sin(tLat) - Math.sin(fLat) * Math.cos(tLat) * Math.cos(dLon))));

        if (degree >= 0) {
            return degree;
        } else {
            return 360 + degree;
        }
    }

    private double degreeToRadians(double latLong) {
        return (Math.PI * latLong / 180.0);
    }

    private double radiansToDegree(double latLong) {
        return (latLong * 180.0 / Math.PI);
    }

    public Bitmap getCarBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.map_car_running);
        return Bitmap.createScaledBitmap(bitmap, 50, 50, false);
    }

    public ValueAnimator carAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(3000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        return valueAnimator;
    }

    public float getRotation(LatLng startPosition, LatLng newPos) {
        double lat = Math.abs(startPosition.latitude - newPos.latitude);
        double lng = Math.abs(startPosition.longitude - newPos.longitude);

        if (startPosition.latitude < newPos.latitude && startPosition.longitude < newPos.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if ((startPosition.latitude >= newPos.latitude && startPosition.longitude < newPos.longitude))
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if ((startPosition.latitude >= newPos.latitude && startPosition.longitude >= newPos.longitude))
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if ((startPosition.latitude < newPos.latitude && startPosition.longitude >= newPos.longitude))
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat)) + 270));
        return -1;
    }

    public Bitmap createCustomMarker(Context context, @DrawableRes int resource, String _name) {

        View marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        ImageView markerImage = marker.findViewById(R.id.user_dp);
        markerImage.setImageResource(resource);
        TextView txt_name = marker.findViewById(R.id.name);
        txt_name.setText(_name);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }
}
