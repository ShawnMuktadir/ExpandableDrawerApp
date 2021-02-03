package www.fiberathome.com.parkingapp.ui.signUp;

import android.annotation.SuppressLint;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.ui.helper.ProgressView;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.privacyPolicy.PrivacyPolicyActivity;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.ui.verifyPhone.VerifyPhoneActivity;
import www.fiberathome.com.parkingapp.ui.termsConditions.TermsConditionsActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@SuppressLint("NonConstantResourceId")
public class SignUpFragment extends BaseFragment implements View.OnClickListener, ProgressView {

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

    @BindView(R.id.login_rl_invisible)
    RelativeLayout relativeLayoutInvisible;

    private Unbinder unbinder;

    private Bitmap bitmap;

    private SignUpActivity context;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        unbinder = ButterKnife.bind(this, view);

        context = (SignUpActivity) getActivity();

        setListeners();

        // Check user is logged in
        if (Preferences.getInstance(context).isLoggedIn()) {
            Intent intent = new Intent(context, HomeActivity.class);
            startActivity(intent);
            context.finish();
            return;
        }

        //makes an underline on for Registration Click Here
        SpannableString spannableString = new SpannableString(context.getResources().getString(R.string.already_a_member_click_here));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // do some thing
                startActivity(new Intent(context, LoginActivity.class));
                context.finish();
            }
        };

        if (Locale.getDefault().getLanguage().equals("en")) {
            //spannableString.setSpan(clickableSpan, 87, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(clickableSpan, 18, 28, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvLogin.setText(spannableString);
            tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (Locale.getDefault().getLanguage().equals("bn")) {
            //spannableString.setSpan(clickableSpan, 50, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(clickableSpan, 16, 26, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvLogin.setText(spannableString);
            tvLogin.setMovementMethod(LinkMovementMethod.getInstance());
        }

        textViewTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        textViewTermsConditions.setText(addMultipleClickablePart(context.getResources().getString(R.string.by_using_this_app_you_agree_to_our_terms_and_conditions_amp_privacy_policy)));

        btnSignup.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        imageViewUploadProfileImage.setOnClickListener(this);
        imageViewCaptureImage.setOnClickListener(this);

        /*editTextFullName.addTextChangedListener(new SignUpActivity.MyTextWatcher(editTextFullName));
        editTextMobileNumber.addTextChangedListener(new SignUpActivity.MyTextWatcher(editTextMobileNumber));
        editTextVehicleRegNumber.addTextChangedListener(new SignUpActivity.MyTextWatcher(editTextVehicleRegNumber));
        editTextPassword.addTextChangedListener(new SignUpActivity.MyTextWatcher(editTextPassword));*/
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
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
                            TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                        }
                    }, (dialog, which) -> {
                        Timber.e("Negative Button Clicked");
                        dialog.dismiss();
                        if (context != null) {
                            context.finish();
                            TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                        }
                    });
                }
                break;

            case R.id.imageViewUploadProfileImage:
            case R.id.imageViewCaptureImage:
                if (isPermissionGranted()) {
                    showPictureDialog();
                }
                break;
        }
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
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contentURI);
                Bitmap convertedImage = getResizedBitmap(bitmap, 500);
                //Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                imageViewUploadProfileImage.setImageBitmap(convertedImage);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Something went wrong! File size not exceed 3 MB", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == REQUEST_PICK_CAMERA && resultCode == RESULT_OK && data != null) {
            // IF CAMERA SELECTED
            try {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageViewUploadProfileImage.setImageBitmap(bitmap);
                    /*saveImage(thumbnail);
                    Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();*/
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Image Capture Failed!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void showProgress() {
        //progressBarLogin.setVisibility(View.VISIBLE);
        relativeLayoutInvisible.setVisibility(View.VISIBLE);
        editTextFullName.setEnabled(false);
        editTextMobileNumber.setEnabled(false);
        editTextVehicleRegNumber.setEnabled(false);
        editTextPassword.setEnabled(false);
        btnSignup.setEnabled(false);
        btnSignup.setClickable(false);
    }

    @Override
    public void hideProgress() {
        //progressBarLogin.setVisibility(View.INVISIBLE);
        relativeLayoutInvisible.setVisibility(View.GONE);
        editTextFullName.setEnabled(true);
        editTextMobileNumber.setEnabled(true);
        editTextVehicleRegNumber.setEnabled(true);
        editTextPassword.setEnabled(true);
        btnSignup.setEnabled(true);
        btnSignup.setClickable(true);
    }

    private SpannableStringBuilder addMultipleClickablePart(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        ssb.setSpan(new ClickableSpan() {

            @Override
            public void onClick(@NotNull View widget) {
                if (ApplicationUtils.checkInternet(context)) {
                    context.startActivity(new Intent(context, TermsConditionsActivity.class));
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
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
                    context.startActivity(new Intent(context, PrivacyPolicyActivity.class));
                } else {
                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
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
                                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }, (dialog, which) -> {
                                Timber.e("Negative Button Clicked");
                                dialog.dismiss();
                                if (context != null) {
                                    context.finish();
                                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
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
        showLoading(context);

        HttpsTrustManager.allowAllSSL();
        if (!otp.isEmpty()) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_VERIFY_OTP, response -> {
                Timber.e("URL -> %s", AppConfig.URL_VERIFY_OTP);

                hideLoading();

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Timber.e("jsonObject login -> %s", jsonObject.toString());


                    if (!jsonObject.getBoolean("error")) {

                        // FETCHING USER INFORMATION FROM DATABASE
                        JSONObject userJson = jsonObject.getJSONObject("user");

                        if (Preferences.getInstance(context).isWaitingForSMS()) {
                            Preferences.getInstance(context).setIsWaitingForSMS(false);

                            // MOVE TO ANOTHER ACTIVITY
                            showMessage("Dear " + userJson.getString("fullname") + ", Your registration completed successfully.");
                            Intent intent = new Intent(context, LoginActivity.class);
                            startActivity(intent);
                            context.finish();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {

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
            hideLoading();
            showMessage("Please enter valid OTP.");
        }

    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(context);
        pictureDialog.setTitle("Select Image");
        /*String[] pictureDialogItems = {"Select photo from gallery",
                "Capture photo from camera"
        };*/
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

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
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

    private boolean isPermissionGranted() {
        // Check Permission for Marshmallow
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(context, new String[]{android.Manifest.permission.CAMERA}, REQUEST_PICK_CAMERA);
            return true;

        } else if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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
                //showMessage("Try Again. Please Upload Profile Photo!");
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.upload_profile_photo));
            }
        }
    }

    private void registerUser(final String fullName, final String mobileNo, final String vehicleNo, final String password) {

        showLoading(context);

        showProgress();

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_REGISTER, response -> {

            hideLoading();

            hideProgress();

            try {
                //converting response to json object
                JSONObject jsonObject = new JSONObject(response);
                Timber.e("jsonObject -> %s", jsonObject.toString());

                // if no error response
                if (!jsonObject.getBoolean("error")) {
                    Timber.e("jsonObject if called");

                    showMessage(jsonObject.getString("message"));
                    /*showMessage("Mobile Number/Vehicle Registration Number already exists or Image Size is too large");
                    if (jsonObject.getString("message").equals("Sorry! mobile number is not valid or missing mate")){
                        showMessage("Mobile Number/Vehicle Registration Number already exists or Image Size is too large");
                    }
                     boolean flag saying device is waiting for sms
                    SharedPreManager.getInstance(getApplicationContext()).setIsWaitingForSMS(true);*/

                    // Moving the screen to next pager item i.e otp screen
                    Intent intent = new Intent(context, VerifyPhoneActivity.class);
                    /*intent.putExtra("fullname",fullname);
                    intent.putExtra("password",passw
                    ord);
                    intent.putExtra("mobile_no",mobileNo);
                    intent.putExtra("vehicle_no",vehicleNo);
                    intent.putExtra("image", imageToString(bitmap));
                    intent.putExtra("image_name", mobileNo);*/
                    startActivity(intent);


                } else {
                    /*showMessage(jsonObject.getString("message"));
                    showMessage("Mobile Number/Vehicle Registration Number already exists or Image Size is too large");*/
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

        }, error -> {
            Timber.e("jsonObject onErrorResponse -> %s", error.getMessage());
            showMessage(error.getMessage());
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fullname", fullName);
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
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
