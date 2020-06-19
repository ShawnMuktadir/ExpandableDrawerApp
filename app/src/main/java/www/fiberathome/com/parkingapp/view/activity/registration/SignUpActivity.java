package www.fiberathome.com.parkingapp.view.activity.registration;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.api.ApiClient;
import www.fiberathome.com.parkingapp.api.ApiService;
import www.fiberathome.com.parkingapp.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.common.Common;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.view.activity.login.LoginActivity;
import www.fiberathome.com.parkingapp.view.activity.main.MainActivity;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = SignUpActivity.class.getSimpleName();
    // image permission
    private static final int REQUEST_PICK_GALLERY = 1001;
    private static final int REQUEST_PICK_IMAGE_CAMERA = 1002;
    private static final int MY_CAMERA_REQUEST_CODE = 1003;
    private Context context;
    private Button btnSignup;
    private EditText editTextFullName;
    private EditText editTextMobileNumber;
    private EditText editTextVehicleRegNumber;
    private EditText editTextPassword;
    private TextView link_login;
    // ViewPager information
    // OTP Verification Page
//    private MyViewPager viewPager;
//    private ViewPagerAdapter adapter;
    //    private Button btnVerifyOTP;
//    private EditText inputOTP;
//    private CountDownTimer countDownTimer;
//    private TextView countdown;
    private Button editPhoneNumber;
    private Bitmap bitmap;

    private ProgressDialog progressDialog;
    private TextInputLayout textInputLayoutFullName;
    private TextInputLayout textInputLayoutMobile;
    private TextInputLayout textInputLayoutVehicle;
    private TextInputLayout textInputLayoutPassword;
    private ImageView uploadProfileImage, imageViewCaptureImage;
    private LinearLayout layout_otp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        context = this;
//        viewPager = new MyViewPager(context);
        // Initialize Components
        initUI();
        setListeners();
        // Check user is logged in
        if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn()) {
            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

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

//        adapter = new ViewPagerAdapter();
//        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });


        // check preference is waiting for SMS
        if (SharedPreManager.getInstance(this).isWaitingForSMS()) {
            showMessage("User Exists");
            Intent intent = new Intent(SignUpActivity.this, VerifyPhoneActivity.class);
            startActivity(intent);
//            adapter.notifyDataSetChanged();
//            viewPager.setCurrentItem(1);
//            viewPager.setCurrentItem (adapter.getItemPosition (1), true);
        }

//        if (ApplicationUtils.getUserCountry(context).equals("bd")) {
//            Toast.makeText(context, "You are from Bangladesh!!!", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnSignup.setOnClickListener(this);
        link_login.setOnClickListener(this);
        uploadProfileImage.setOnClickListener(this);
        imageViewCaptureImage.setOnClickListener(this);
//        btnVerifyOTP.setOnClickListener(this);

        editTextFullName.addTextChangedListener(new MyTextWatcher(editTextFullName));
        editTextMobileNumber.addTextChangedListener(new MyTextWatcher(editTextMobileNumber));
        editTextVehicleRegNumber.addTextChangedListener(new MyTextWatcher(editTextVehicleRegNumber));
        editTextPassword.addTextChangedListener(new MyTextWatcher(editTextPassword));
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnSignup:
                submitRegistration();
//                startActivity(new Intent(context, VerifyPhoneActivity.class));
                break;

            case R.id.link_login:
                Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                break;

//            case R.id.upload_profile_image:
            case R.id.imageViewCaptureImage:
//                showMessage("CAMERA!");
                if (isPermissionGranted()) {
                    showPictureDialog();
                }
                //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blank_profile_pic);
                break;

//            case R.id.btn_verify_otp:
//                // GETTING THE OTP VALUE FROM USER.
//                String otp = inputOTP.getText().toString().trim();
//                submitOTPVerification(otp);
//                break;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        SignUpActivity.super.onBackPressed();
                    }
                }).create();
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.red));
                //dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
            }
        });
        dialog.show();
    }

    private void initUI() {
        btnSignup = findViewById(R.id.btnSignup);
        editTextFullName = findViewById(R.id.editTextFullName);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);
        editTextVehicleRegNumber = findViewById(R.id.editTextVehicleRegNumber);
        editTextPassword = findViewById(R.id.editTextPassword);
        link_login = findViewById(R.id.link_login);
        uploadProfileImage = findViewById(R.id.upload_profile_image);
        imageViewCaptureImage = findViewById(R.id.imageViewCaptureImage);
//        btnVerifyOTP = findViewById(R.id.btn_verify_otp);
//        inputOTP = findViewById(R.id.inputOtp);
//        layout_otp = findViewById(R.id.layout_otp);

        // init viewpager
//        viewPager = findViewById(R.id.viewPagerVertical);
//        countdown = findViewById(R.id.countdown);

        textInputLayoutFullName = findViewById(R.id.layout_fullname);
        textInputLayoutMobile = findViewById(R.id.layout_mobile_number);
        textInputLayoutVehicle = findViewById(R.id.layout_vehicle_no);
        textInputLayoutPassword = findViewById(R.id.layout_password);

    }

    private void setListeners() {
        Objects.requireNonNull(textInputLayoutFullName.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutFullName.setErrorEnabled(true);
                    textInputLayoutFullName.setError(context.getString(R.string.err_msg_fullname));
                }

                if (s.length() > 0) {
                    textInputLayoutFullName.setError(null);
                    textInputLayoutFullName.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(textInputLayoutMobile.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutMobile.setErrorEnabled(true);
                    textInputLayoutMobile.setError(context.getString(R.string.err_msg_mobile));
                }

                if (s.length() > 0) {
                    textInputLayoutMobile.setError(null);
                    textInputLayoutMobile.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(textInputLayoutVehicle.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutVehicle.setErrorEnabled(true);
                    textInputLayoutVehicle.setError(context.getString(R.string.err_msg_vehicle));
                }

                if (s.length() > 0) {
                    textInputLayoutVehicle.setError(null);
                    textInputLayoutVehicle.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(textInputLayoutPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    textInputLayoutPassword.setErrorEnabled(true);
                    textInputLayoutPassword.setError(context.getString(R.string.err_msg_password));
                }

                if (s.length() > 0) {
                    textInputLayoutPassword.setError(null);
                    textInputLayoutPassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                "Capture photo from camera"
        };

        pictureDialog.setItems(pictureDialogItems, (dialog, which) -> {
            switch (which) {
                case 0:
                    choosePhotoFromGallary();
                    break;
                case 1:
                    takePhotoFromCamera();
                    break;
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
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        // IF GALLERY SELECTED
        if (requestCode == REQUEST_PICK_GALLERY && resultCode == RESULT_OK && data != null) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    uploadProfileImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(SignUpActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }


        } else if (requestCode == REQUEST_PICK_IMAGE_CAMERA && resultCode == RESULT_OK && data != null) {
            // IF CAMERA SELECTED
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                uploadProfileImage.setImageBitmap(bitmap);
//                saveImage(thumbnail);
//                Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Image Capture Failed!", Toast.LENGTH_SHORT).show();
            }
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
        if (checkFields()) {
            String fullName = editTextFullName.getText().toString().trim();
            String mobileNo = editTextMobileNumber.getText().toString().trim();
            String vehicleNo = editTextVehicleRegNumber.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            //showMessage(mobileNo + " " + vehicleNo + " " + password);
            if (bitmap != null) {
                registerUser(fullName, mobileNo, vehicleNo, password);
            } else {
                showMessage("Try Again. Please Upload Profile Photo!");
//            TastyToastUtils.showTastyWarningToast(context, "Try Again. Please Upload Profile Photo!");
            }
        }
    }

    private void registerUser(final String fullname, final String mobileNo, final String vehicleNo, final String password) {

        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, response -> {

            progressDialog.dismiss();

            try {
                //converting response to json object
                JSONObject jsonObject = new JSONObject(response);
                Timber.e("jsonObject -> %s", jsonObject.toString());

                // if no error response
                if (!jsonObject.getBoolean("error")) {
                    Timber.e("jsonObject if e dhukche");

                    showMessage(jsonObject.getString("message"));
                    // boolean flag saying device is waiting for sms
                    SharedPreManager.getInstance(getApplicationContext()).setIsWaitingForSMS(true);

                    // Moving the screen to next pager item i.e otp screen
                    Intent intent = new Intent(SignUpActivity.this, VerifyPhoneActivity.class);
                    startActivity(intent);

                    // getting user object
//                    JSONObject userJson = jsonObject.getJSONObject("user");
//                    Timber.e("jsonObject userJson e dhukche");
//
//                    //showMessage(userJson.getString("image"));
//
//                    // creating new User Object
//                    User user = new User();
//                    Timber.e("user -> %s",user);
//                    user.setFullName(userJson.getString("fullname"));
//                    Timber.e("user fullname -> %s",userJson.getString("fullname"));
//                    user.setMobileNo(userJson.getString("mobile_no"));
//                    Timber.e("user mobile_no -> %s",userJson.getString("mobile_no"));
//                    user.setVehicleNo(userJson.getString("vehicle_no"));
//                    Timber.e("user vehicle_no -> %s",userJson.getString("vehicle_no"));
//                    user.setProfilePic(userJson.getString("image"));
//                    Timber.e("user image -> %s",userJson.getString("image"));
//                    user.setPassword(userJson.getString("password"));
//                    Timber.e("user password -> %s",userJson.getString("password"));

                    // Store to share preference
//                    SharedPreManager.getInstance(getApplicationContext()).userLogin(user);



//                    viewPager.setCurrentItem(1);

//                    startCountDown();
                } else {
                    showMessage(jsonObject.getString("message"));
                    Timber.e("jsonObject else e dhukche");
                }
            } catch (JSONException e) {
                Timber.e("jsonObject catch -> %s",e.getMessage());
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("jsonObject onErrorResponse -> %s",error.getMessage());
                SignUpActivity.this.showMessage(error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fullname", fullname);
                params.put("password", password);
                SharedData.getInstance().setPassword(password);
                params.put("mobile_no", mobileNo);
                params.put("vehicle_no", vehicleNo);
                params.put("image", imageToString(bitmap));
                params.put("image_name", mobileNo);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

//    private void registerUser(final String fullName, final String password, final String mobileNo, final String vehicleNo, final String image) {
//
//        progressDialog = new ProgressDialog(SignUpActivity.this);
//        progressDialog.setMessage("Please wait...");
//        progressDialog.setCancelable(false);
//        progressDialog.setIndeterminate(false);
//        progressDialog.show();
//
//        // Registering User through UI Service.
//        ApiService service = ApiClient.getRetrofitInstance(AppConfig.URL_REGISTER).create(ApiService.class);
//        Call<Common> call = service.createUser(fullName, password, mobileNo, vehicleNo, imageToString(bitmap));
//
//        // Gathering results.
//        call.enqueue(new Callback<Common>() {
//            @Override
//            public void onResponse(Call<Common> call, retrofit2.Response<Common> response) {
//                Timber.e("response -> %s", response.message());
//
//                progressDialog.dismiss();
//                if (response.body() != null) {
//                    if (!response.body().getError()) {
//                        showMessage(response.body().getMessage());
//                        Intent intent = new Intent(SignUpActivity.this, VerifyPhoneActivity.class);
//                        startActivity(intent);
//                        // creating new User Object
//                        User user = new User();
//                        user.setFullName(fullName);
//                        user.setMobileNo(mobileNo);
//                        user.setVehicleNo(vehicleNo);
//                        user.setProfilePic(image);
//                        user.setPassword(password);
//                    } else {
//                        showMessage(response.body().getMessage());
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Common> call, Throwable errors) {
//                Timber.e("Throwable Errors: -> %s", errors.toString());
//            }
//        });
//    }

    private boolean checkFields() {
        editTextFullName.requestFocus();
        boolean isNameValid = Validator.checkValidity(textInputLayoutFullName, editTextFullName.getText().toString(), context.getString(R.string.err_msg_fullname), "text");
        boolean isPhoneValid = Validator.checkValidity(textInputLayoutMobile, editTextMobileNumber.getText().toString(), context.getString(R.string.err_msg_mobile), "phone");
        boolean isVehicleRegValid = Validator.checkValidity(textInputLayoutVehicle, editTextVehicleRegNumber.getText().toString(), context.getString(R.string.err_msg_vehicle), "text");
        boolean isPasswordValid = Validator.checkValidity(textInputLayoutPassword, editTextPassword.getText().toString(), context.getString(R.string.err_msg_password), "textPassword");
        return isNameValid && isPhoneValid && isVehicleRegValid && isPasswordValid;
    }

    private void showMessage(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
    }

//    private void startCountDown() {
//        new CountDownTimer(10000, 1000) {
//            //CountDownTimer(edittext1.getText()+edittext2.getText()) also parse it to long
//
//            public void onTick(long millisUntilFinished) {
//                countdown.setText("seconds remaining: " + millisUntilFinished / 1000);
//                //here you can have your logic to set text to edittext
//            }
//
//            public void onFinish() {
//                countdown.setText("finished!");
//                // enable the edit alert dialog
//            }
//        }.start();
//
//    }

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
                case R.id.editTextMobileNumber:
                    validateEditText(editTextFullName, textInputLayoutFullName, R.string.err_msg_fullname);
                    break;
            }
        }

    }

    /**
     * class ViewPager
     * ==================================================
     */

//    public class ViewPagerAdapter extends PagerAdapter {
//
//        @Override
//        public int getCount() {
//            return viewPager.getChildCount();
//        }
//
//        @Override
//        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//            return view == ((View) object);
//        }
//
//        @NonNull
//        @Override
//        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            int resID = 1;
//            switch (position) {
//                case 0:
//                    Timber.e("position -> %s", position);
//                    resID = R.id.layout_signup;
//                    break;
//
//                case 1:
//                    Timber.e("position -> %s", position);
//                    resID = R.id.layout_otp;
//                    break;
//            }
//
//            return findViewById(resID);
//        }
//
//        @Override
//        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
//            container.removeView((View) object);
//        }
//    }
}
