package www.fiberathome.com.parkingapp.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.utils.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.SharedPreManager;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = SignUpActivity.class.getSimpleName();
    // image permission
    private static final int REQUEST_PICK_GALLERY = 1001;
    private static final int REQUEST_PICK_IMAGE_CAMERA = 1002;
    private static final int MY_CAMERA_REQUEST_CODE = 1003;
    private Context context;
    private Button signupBtn;
    private EditText fullnameET;
    private EditText mobileET;
    private EditText vehicleET;
    private EditText passwordET;
    private TextView link_login;
    // ViewPager information
    // OTP Verification Page
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Button btnVerifyOTP;
    private EditText inputOTP;
    private CountDownTimer countDownTimer;
    private TextView countdown;
    private Button editPhoneNumber;
    private Bitmap bitmap;

    private ProgressDialog progressDialog;
    private TextInputLayout inputLayoutFullName;
    private TextInputLayout inputLayoutMobile;
    private TextInputLayout inputLayoutVehicle;
    private TextInputLayout inputLayoutPassword;
    private ImageView upload_profile_image;
    private LinearLayout layout_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;

        // Check user is logged in
        if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn()) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize Components
        initializeComponent();

        //makes an underline on for Registration Click Here
        SpannableString spannableString = new SpannableString(context.getResources().getString(R.string.already_a_member_click_here));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                startActivity(new Intent(context, LoginActivity.class));
            }
        };
        spannableString.setSpan(clickableSpan, 18, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        link_login.setText(spannableString);
        link_login.setMovementMethod(LinkMovementMethod.getInstance());


        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        // check preference is waiting for SMS
        if (SharedPreManager.getInstance(this).isWaitingForSMS()) {
            showMessage("exists");
            viewPager.setCurrentItem(1);
        }
    }

    private void initializeComponent() {
        signupBtn = findViewById(R.id.signup_final_btn);
        fullnameET = findViewById(R.id.input_fullname);
        mobileET = findViewById(R.id.input_mobile_number);
        vehicleET = findViewById(R.id.input_vehicle_no);
        passwordET = findViewById(R.id.input_password);
        link_login = findViewById(R.id.link_login);
        upload_profile_image = findViewById(R.id.upload_profile_image);
        btnVerifyOTP = findViewById(R.id.btn_verify_otp);
        inputOTP = findViewById(R.id.inputOtp);
        layout_otp = findViewById(R.id.layout_otp);

        // init viewpager
        viewPager = findViewById(R.id.viewPagerVertical);
        countdown = findViewById(R.id.countdown);

        inputLayoutFullName = findViewById(R.id.layout_fullname);
        inputLayoutMobile = findViewById(R.id.layout_mobile_number);
        inputLayoutVehicle = findViewById(R.id.layout_vehicle_no);
        inputLayoutPassword = findViewById(R.id.layout_password);

    }


    @Override
    protected void onStart() {
        super.onStart();

        signupBtn.setOnClickListener(this);
        link_login.setOnClickListener(this);
        upload_profile_image.setOnClickListener(this);
        btnVerifyOTP.setOnClickListener(this);

        fullnameET.addTextChangedListener(new MyTextWatcher(fullnameET));
        mobileET.addTextChangedListener(new MyTextWatcher(mobileET));
        vehicleET.addTextChangedListener(new MyTextWatcher(vehicleET));
        passwordET.addTextChangedListener(new MyTextWatcher(passwordET));
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.signup_final_btn:
                submitRegistration();
                break;

            case R.id.link_login:
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.upload_profile_image:
//                showMessage("CAMERA!");
                if (isPermissionGranted()) {
                    showPictureDialog();
                }
                //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blank_profile_pic);
                break;

            case R.id.btn_verify_otp:
                // GETTING THE OTP VALUE FROM USER.
                String otp = inputOTP.getText().toString().trim();
                submitOTPVerification(otp);
                break;

        }

    }

    private void submitOTPVerification(final String otp) {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Verifying OTP...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        HttpsTrustManager.allowAllSSL();
        if (!otp.isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_VERIFY_OTP, response -> {
                Log.e("URL", AppConfig.URL_VERIFY_OTP);
                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("object", jsonObject.toString());


                    if (!jsonObject.getBoolean("error")) {

                        // FETCHING USER INFORMATION FROM DATABASE
                        JSONObject userJson = jsonObject.getJSONObject("user");

                        if (SharedPreManager.getInstance(getApplicationContext()).isWaitingForSMS()) {
                            SharedPreManager.getInstance(getApplicationContext()).setIsWaitingForSMS(false);

                            // MOVE TO ANOTHER ACTIVITY
                            showMessage("Dear " + userJson.getString("fullname") + ", Your registration completed successfully.");
                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("otp", otp);
                    return params;
                }
            };

            ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
        } else {
            // OTP IS EMPTY.
            progressDialog.dismiss();
            showMessage("Please enter valid OTP.");
        }

    }

    /**
     * showPictureDialog
     * -------------------------------------------------
     * Selecting picture from Gallery
     * Selecting picture from Camera
     * -------------------------------------------------
     */
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Image");
        String[] pictureDialogItems = {"Select photo from gallery",
                // "Capture photo from camera"
        };

        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        choosePhotoFromGallary();
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }


    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, REQUEST_PICK_GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_PICK_IMAGE_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        // IF GALLERY SELECTED
        if (requestCode == REQUEST_PICK_GALLERY && resultCode == RESULT_OK && data != null) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    upload_profile_image.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }


        } else if (requestCode == REQUEST_PICK_IMAGE_CAMERA && resultCode == RESULT_OK && data != null) {
            // IF CAMERA SELECTED
            bitmap = (Bitmap) data.getExtras().get("data");
            upload_profile_image.setImageBitmap(bitmap);
            //saveImage(thumbnail);
            Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Check Manifest Camera Permission
     *
     * @return
     */

    private boolean isPermissionGranted() {
        // Check Permission for Marshmallow
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_PICK_IMAGE_CAMERA);
            return true;

        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return true;
        }
    }


    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageByte, Base64.DEFAULT);

    }


    private void submitRegistration() {
        // Loading Progress
        if (!validateEditText(fullnameET, inputLayoutFullName, R.string.err_msg_fullname)) {
            return;
        }

        if (!validateEditText(mobileET, inputLayoutMobile, R.string.err_msg_mobile)) {
            return;
        }

        if (!validateEditText(vehicleET, inputLayoutVehicle, R.string.err_msg_vehicle)) {
            return;
        }

        if (!validateEditText(passwordET, inputLayoutPassword, R.string.err_msg_password)) {
            return;
        }

        String fullname = fullnameET.getText().toString().trim();
        String mobileNo = mobileET.getText().toString().trim();
        String vehicleNo = vehicleET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();

        //showMessage(mobileNo + " " + vehicleNo + " " + password);
        if (bitmap != null) {
            registerUser(fullname, mobileNo, vehicleNo, password);
        } else {
            showMessage("Try Again. Please Upload Vehicle Photo!");
        }
    }

    private void registerUser(final String fullname, final String mobileNo, final String vehicleNo, final String password) {
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();

                try {
                    //converting response to json object
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("Object", jsonObject.toString());

                    // if no error response
                    if (!jsonObject.getBoolean("error")) {
                        showMessage(jsonObject.getString("message"));

                        // getting user object
                        JSONObject userJson = jsonObject.getJSONObject("user");

                        //showMessage(userJson.getString("image"));

                        // creating new User Object
                        User user = new User();
                        user.setFullName(userJson.getString("fullname"));
                        user.setMobileNo(userJson.getString("mobile_no"));
                        user.setVehicleNo(userJson.getString("vehicle_no"));
                        user.setProfilePic(userJson.getString("image"));


                        // Store to share preference
                        SharedPreManager.getInstance(getApplicationContext()).userLogin(user);

                        // boolean flag saying device is waiting for sms
                        SharedPreManager.getInstance(getApplicationContext()).setIsWaitingForSMS(true);

                        // Moving the screen to next pager item i.e otp screen
//                        viewPager.setCurrentItem(1);
                        startCountDown();
                        viewPager.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                viewPager.setCurrentItem(1);
                            }
                        }, 100);
                        // set edit layout with layout visibility.

                    } else {
                        showMessage(jsonObject.getString("message"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showMessage(error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fullname", fullname);
                params.put("password", password);
                params.put("mobile_no", mobileNo);
                params.put("vehicle_no", vehicleNo);
                params.put("image", imageToString(bitmap));
                params.put("image_name", mobileNo);

                return params;
            }
        };
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showMessage(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void startCountDown() {
        new CountDownTimer(10000, 1000) {//CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long

            public void onTick(long millisUntilFinished) {
                countdown.setText("seconds remaining: " + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                countdown.setText("finished!");
                // enable the edit alert dialog
            }
        }.start();

    }

    private boolean validateEditText(EditText editText, TextInputLayout inputLayout, int errorMessage) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty()) {
            inputLayout.setError(getResources().getString(errorMessage));
            requestFocus(editText);
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(EditText view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_mobile_number:
                    validateEditText(fullnameET, inputLayoutFullName, R.string.err_msg_fullname);
                    break;
            }
        }
    }

    /**
     * class ViewPager
     * ==================================================
     */

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View) object);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            int resID = 1;
            switch (position) {
                case 0:
                    resID = R.id.layout_signup;
                    break;

                case 1:
                    resID = R.id.layout_otp;
                    break;
            }

            return findViewById(resID);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
