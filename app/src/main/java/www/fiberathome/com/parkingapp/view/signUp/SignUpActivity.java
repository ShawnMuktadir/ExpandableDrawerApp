package www.fiberathome.com.parkingapp.view.signUp;

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
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.view.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.view.main.MainActivity;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.utils.Validator;
import www.fiberathome.com.parkingapp.view.privacyPolicy.PrivacyPolicyActivity;
import www.fiberathome.com.parkingapp.view.termsConditions.TermsConditionsActivity;
import www.fiberathome.com.parkingapp.view.signUp.verifyPhone.VerifyPhoneActivity;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = SignUpActivity.class.getSimpleName();
    private static final int REQUEST_PICK_GALLERY = 1001;
    private static final int REQUEST_PICK_CAMERA = 1002;

    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.btnSignup)
    Button btnSignup;
    @BindView(R.id.textInputLayoutFullName)
    TextInputLayout textInputLayoutFullName;
    @BindView(R.id.editTextFullName)
    EditText editTextFullName;
    @BindView(R.id.textInputLayoutMobile)
    TextInputLayout textInputLayoutMobile;
    @BindView(R.id.editTextMobileNumber)
    EditText editTextMobileNumber;
    @BindView(R.id.textInputLayoutVehicle)
    TextInputLayout textInputLayoutVehicle;
    @BindView(R.id.editTextVehicleRegNumber)
    EditText editTextVehicleRegNumber;
    @BindView(R.id.textInputLayoutPassword)
    TextInputLayout textInputLayoutPassword;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @BindView(R.id.imageViewUploadProfileImage)
    CircleImageView imageViewUploadProfileImage;
    @BindView(R.id.imageViewCaptureImage)
    CircleImageView imageViewCaptureImage;
    @BindView(R.id.textViewTermsConditions)
    TextView textViewTermsConditions;

    private Context context;
    private Bitmap bitmap;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        context = this;
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
                finish();
            }
        };
        spannableString.setSpan(clickableSpan, 18, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvLogin.setText(spannableString);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());

        textViewTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        textViewTermsConditions.setText(addMultipleClickablePart(context.getResources().getString(R.string.by_using_this_app_you_agree_to_our_terms_and_conditions_amp_privacy_policy)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnSignup.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        imageViewUploadProfileImage.setOnClickListener(this);
        imageViewCaptureImage.setOnClickListener(this);

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
                if (ApplicationUtils.checkInternet(context)) {
                    submitRegistration();
                } else {
                    ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                        Timber.e("Positive Button clicked");
                        if (ApplicationUtils.checkInternet(context)) {
                            submitRegistration();
                        } else {
                            TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                        }
                    }, (dialog, which) -> {
                        Timber.e("Negative Button Clicked");
                        dialog.dismiss();
                        if (context != null) {
                            finish();
                            TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
                        }
                    });
                }
                break;

//            case R.id.tvLogin:
//                Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
//                startActivity(loginIntent);
//                break;

            case R.id.imageViewUploadProfileImage:
            case R.id.imageViewCaptureImage:
                if (isPermissionGranted()) {
                    showPictureDialog();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
//                        SignUpActivity.super.onBackPressed();
                        finish();
                        TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
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

    private SpannableStringBuilder addMultipleClickablePart(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        ssb.setSpan(new ClickableSpan() {

            @Override
            public void onClick(@NotNull View widget) {
                if (ApplicationUtils.checkInternet(context)) {
                    context.startActivity(new Intent(SignUpActivity.this, TermsConditionsActivity.class));
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet!");
                }
            }

            @Override
            public void updateDrawState(@NotNull TextPaint ds) {
                super.updateDrawState(ds);
                int linkColor = ContextCompat.getColor(context, R.color.light_blue);
                ds.setColor(linkColor);
                ds.setUnderlineText(false);
            }
        }, 35, 55, 0);

        ssb.setSpan(new ClickableSpan() {

            @Override
            public void onClick(@NotNull View widget) {
                if (ApplicationUtils.checkInternet(context)) {
                    context.startActivity(new Intent(SignUpActivity.this, PrivacyPolicyActivity.class));
                } else {
                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet!");
                }
            }

            @Override
            public void updateDrawState(@NotNull TextPaint ds) {
                super.updateDrawState(ds);
                int linkColor = ContextCompat.getColor(context, R.color.light_blue);
                ds.setColor(linkColor);
                ds.setUnderlineText(false);
            }
        }, 58, 72, 0);
        return ssb;
    }

    private void setListeners() {
        editTextPassword.setOnEditorActionListener(
                (v, actionId, event) -> {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        if (ApplicationUtils.checkInternet(context)) {
                            submitRegistration();
                        } else {
                            ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                                Timber.e("Positive Button clicked");
                                if (ApplicationUtils.checkInternet(context)) {
                                    submitRegistration();
                                } else {
                                    TastyToastUtils.showTastyWarningToast(context, "Please connect to internet");
                                }
                            }, (dialog, which) -> {
                                Timber.e("Negative Button Clicked");
                                dialog.dismiss();
                                if (context != null) {
                                    finish();
                                    TastyToastUtils.showTastySuccessToast(context, "Thanks for being with us");
                                }
                            });
                        }
                        return true;
                    }
                    return false;
                });

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
        progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");

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
//        String[] pictureDialogItems = {"Select photo from gallery",
//                "Capture photo from camera"
//        };
        String[] pictureDialogItems = {"Capture photo from camera"};

        pictureDialog.setItems(pictureDialogItems, (dialog, which) -> {
            switch (which) {
                case 0:
                    takePhotoFromCamera();
                    break;
                case 1:
                    choosePhotoFromGallery();
                    break;
            }
        });
        pictureDialog.show();
    }

    @SuppressLint("IntentReset")
    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_PICK_GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_PICK_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == REQUEST_PICK_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri contentURI = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(SignUpActivity.this.getContentResolver(), contentURI);
                Bitmap convertedImage = getResizedBitmap(bitmap, 500);
//                    Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                imageViewUploadProfileImage.setImageBitmap(convertedImage);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Something went wrong! File size not exceed 3 MB", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_PICK_CAMERA && resultCode == RESULT_OK && data != null) {
            // IF CAMERA SELECTED
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageViewUploadProfileImage.setImageBitmap(bitmap);
//                saveImage(thumbnail);
//                Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(SignUpActivity.this, "Image Capture Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    /**
     * Check Manifest Camera Permission
     *
     * @return
     */

    private boolean isPermissionGranted() {
        // Check Permission for Marshmallow
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_PICK_CAMERA);
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

            if (bitmap != null) {
                registerUser(fullName, mobileNo, vehicleNo, password);
            } else {
//                showMessage("Try Again. Please Upload Profile Photo!");
                TastyToastUtils.showTastyWarningToast(context, "Try Again. Please Upload Profile Photo!");
            }
        }
    }

    private void registerUser(final String fullname, final String mobileNo, final String vehicleNo, final String password) {

        progressDialog = ApplicationUtils.progressDialog(context, "Please wait...");

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, response -> {

            progressDialog.dismiss();

            try {
                //converting response to json object
                JSONObject jsonObject = new JSONObject(response);
                Timber.e("jsonObject -> %s", jsonObject.toString());

                // if no error response
                if (!jsonObject.getBoolean("error")) {
                    Timber.e("jsonObject if called");

                    showMessage(jsonObject.getString("message"));
//                    showMessage("Mobile Number/Vehicle Registration Number already exists or Image Size is too large");
//                    if (jsonObject.getString("message").equals("Sorry! mobile number is not valid or missing mate")){
//                        showMessage("Mobile Number/Vehicle Registration Number already exists or Image Size is too large");
//                    }
                    // boolean flag saying device is waiting for sms
//                    SharedPreManager.getInstance(getApplicationContext()).setIsWaitingForSMS(true);

                    // Moving the screen to next pager item i.e otp screen
                    Intent intent = new Intent(SignUpActivity.this, VerifyPhoneActivity.class);
//                    intent.putExtra("fullname",fullname);
//                    intent.putExtra("password",passw
//                    ord);
//                    intent.putExtra("mobile_no",mobileNo);
//                    intent.putExtra("vehicle_no",vehicleNo);
//                    intent.putExtra("image", imageToString(bitmap));
//                    intent.putExtra("image_name", mobileNo);
                    startActivity(intent);


                } else {
//                    showMessage(jsonObject.getString("message"));
//                    showMessage("Mobile Number/Vehicle Registration Number already exists or Image Size is too large");
                    Timber.e("jsonObject else called");
                    if (jsonObject.getString("message").equals("Sorry! mobile number is not valid or missing mate")) {
                        showMessage("Mobile Number/Vehicle Registration Number already exists or Image Size is too large");
                    } else if (!jsonObject.getString("message").equals("Sorry! mobile number is not valid or missing mate")) {
                        showMessage(jsonObject.getString("message"));
                    }
                }
            } catch (JSONException e) {
                Timber.e("jsonObject catch -> %s", e.getMessage());
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("jsonObject onErrorResponse -> %s", error.getMessage());
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

    private boolean checkFields() {
        boolean isNameValid = Validator.checkValidity(textInputLayoutFullName, editTextFullName.getText().toString(), context.getString(R.string.err_msg_fullname), "text");
        boolean isPhoneValid = Validator.checkValidity(textInputLayoutMobile, editTextMobileNumber.getText().toString(), context.getString(R.string.err_msg_mobile), "phone");
        boolean isVehicleRegValid = Validator.checkValidity(textInputLayoutVehicle, editTextVehicleRegNumber.getText().toString(), context.getString(R.string.err_msg_vehicle), "text");
        boolean isPasswordValid = Validator.checkValidity(textInputLayoutPassword, editTextPassword.getText().toString(), context.getString(R.string.err_msg_password), "textPassword");

        return isNameValid && isPhoneValid && isVehicleRegValid && isPasswordValid;
    }

    private void showMessage(String message) {
        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
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
                case R.id.editTextMobileNumber:
                    validateEditText(editTextFullName, textInputLayoutFullName, R.string.err_msg_fullname);
                    break;
            }
        }

    }
}
