package www.fiberathome.com.parkingapp.ui.signUp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.BuildConfig;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.FileCompressor;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.ui.verifyPhone.VerifyPhoneActivity;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = RegistrationActivity.class.getSimpleName();
    @BindView(R.id.circleImageViewUploadProfileImage)
    CircleImageView circleImageViewUploadProfileImage;
    @BindView(R.id.imageViewCaptureImage)
    ImageView imageViewCaptureImage;
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
    @BindView(R.id.btnSignup)
    Button btnSignup;
    @BindView(R.id.btnLinkLogin)
    Button btnLinkLogin;

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_GALLERY_PHOTO = 2;
    File mPhotoFile;
    FileCompressor mCompressor;

    private Context context;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        context = this;
        ButterKnife.bind(this);
        mCompressor = new FileCompressor(this);
        setListeners();
        // Check user is logged in
        if (SharedPreManager.getInstance(getApplicationContext()).isLoggedIn()) {
            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
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
        btnLinkLogin.setText(spannableString);
        btnLinkLogin.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();
        circleImageViewUploadProfileImage.setOnClickListener(this);
        imageViewCaptureImage.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        RegistrationActivity.super.onBackPressed();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circleImageViewUploadProfileImage:
            case R.id.imageViewCaptureImage:
                if (isPermissionGranted()) {
                    selectImage();
                }
                break;
        }
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
                                if (ApplicationUtils.checkInternet(context)){
                                    submitRegistration();
                                } else {
                                    TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                }
                            }, (dialog, which) -> {
                                Timber.e("Negative Button Clicked");
                                dialog.dismiss();
                                if (context != null) {
                                    finish();
                                    TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                }
                            });
                        }
                        return true;
                    }
                    return false;
                });

        btnSignup.setOnClickListener(v -> {
            if (ApplicationUtils.checkInternet(context)) {
                submitRegistration();
            } else {
                ApplicationUtils.showAlertDialog(context.getString(R.string.connect_to_internet), context, context.getString(R.string.retry), context.getString(R.string.close_app), (dialog, which) -> {
                    Timber.e("Positive Button clicked");
                    if (ApplicationUtils.checkInternet(context)){
                        submitRegistration();
                    } else {
                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                    }
                }, (dialog, which) -> {
                    Timber.e("Negative Button Clicked");
                    dialog.dismiss();
                    if (context != null) {
                        finish();
                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                    }
                });
            }
        });

        btnLinkLogin.setOnClickListener(v -> {
            Intent loginIntent = new Intent(RegistrationActivity.this, LoginActivity.class);
            startActivity(loginIntent);
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

    private void submitRegistration() {
        if (checkFields()) {
            String fullName = editTextFullName.getText().toString().trim();
            String mobileNo = editTextMobileNumber.getText().toString().trim();
            String vehicleNo = editTextVehicleRegNumber.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            if (mPhotoFile != null) {
                registerUser(fullName, mobileNo, vehicleNo, password);
            } else {
//                showMessage("Try Again. Please Upload Profile Photo!");
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.upload_profile_photo));
            }
        }
    }

    private void registerUser(final String fullName, final String mobileNo, final String vehicleNo, final String password) {

        progressDialog = new ProgressDialog(RegistrationActivity.this);
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
//                    SharedPreManager.getInstance(getApplicationContext()).setIsWaitingForSMS(true);

                    // Moving the screen to next pager item i.e otp screen
                    Intent intent = new Intent(RegistrationActivity.this, VerifyPhoneActivity.class);
//                    intent.putExtra("fullname",fullname);
//                    intent.putExtra("password",password);
//                    intent.putExtra("mobile_no",mobileNo);
//                    intent.putExtra("vehicle_no",vehicleNo);
//                    intent.putExtra("image", imageToString(bitmap));
//                    intent.putExtra("image_name", mobileNo);
                    startActivity(intent);


                } else {
                    showMessage(jsonObject.getString("message"));
                    Timber.e("jsonObject else e dhukche");
                }
            } catch (JSONException e) {
                Timber.e("jsonObject catch -> %s", e.getMessage());
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Timber.e("jsonObject onErrorResponse -> %s", error.getMessage());
                RegistrationActivity.this.showMessage(error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fullname", fullName);
                params.put("password", password);
                SharedData.getInstance().setPassword(password);
                params.put("mobile_no", mobileNo);
                params.put("vehicle_no", vehicleNo);
//                params.put("image", imageToString(bitmap));
                params.put("image", String.valueOf(mPhotoFile));
                params.put("image_name", mobileNo);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private boolean checkFields() {
//        editTextFullName.requestFocus();
        boolean isNameValid = Validator.checkValidity(textInputLayoutFullName, editTextFullName.getText().toString(), context.getString(R.string.err_msg_fullname), "text");
        boolean isPhoneValid = Validator.checkValidity(textInputLayoutMobile, editTextMobileNumber.getText().toString(), context.getString(R.string.err_msg_mobile), "phone");
        boolean isVehicleRegValid = Validator.checkValidity(textInputLayoutVehicle, editTextVehicleRegNumber.getText().toString(), context.getString(R.string.err_msg_vehicle), "text");
        boolean isPasswordValid = Validator.checkValidity(textInputLayoutPassword, editTextPassword.getText().toString(), context.getString(R.string.err_msg_password), "textPassword");

        return isNameValid && isPhoneValid && isVehicleRegValid && isPasswordValid;
    }

    private void showMessage(String message) {
//        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
        final Toast toast = Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT);
        toast.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, 2000);
    }

    /**
     * Alert dialog for capture or select from galley
     */
    private void selectImage() {
        final CharSequence[] items = {"Capture photo from camera", "Select photo from gallery"};
//        final CharSequence[] items = {"Capture photo from camera", "Select photo from gallery",
//                "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RegistrationActivity.this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Capture photo from camera")) {
                requestStoragePermission(true);
            } else if (items[item].equals("Select photo from gallery")) {
                requestStoragePermission(false);
            }
//            else if (items[item].equals("Cancel")) {
//                dialog.dismiss();
//            }
        });
        builder.show();
    }

    /**
     * Capture image from camera
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Timber.e("dispatchTakePictureIntent length try called -> %s", photoFile.length());
            } catch (IOException ex) {
                ex.printStackTrace();
                // Error occurred while creating the File
                Timber.e("dispatchTakePictureIntent catch -> %s", ex.getCause());
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                mPhotoFile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    /**
     * Select image from gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        // if gallery selected
        if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                //file value in long format and length() gives value at bytes
                Timber.e("mPhotoFile size gallery long -> %s", String.valueOf(mPhotoFile.length()));
            } catch (IOException e) {
                Timber.e("mPhotoFile gallery catch -> %s", e.getCause());
                e.printStackTrace();
            }
            Glide.with(RegistrationActivity.this).load(mPhotoFile).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.blank_profile)).into(circleImageViewUploadProfileImage);
        }
        //if camera selected
        else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            try {
                mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                Timber.e("mPhotoFile size camera long ->%s", mPhotoFile.length());
            } catch (IOException e) {
                Timber.e("mPhotoFile camera catch -> %s", e.getCause());
                e.printStackTrace();
            }
            Glide.with(RegistrationActivity.this).load(mPhotoFile).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.blank_profile)).into(circleImageViewUploadProfileImage);
        }
    }

    /**
     * Requesting multiple permissions (storage and camera) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestStoragePermission(boolean isCamera) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else {
                                dispatchGalleryIntent();
                            }
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    /**
     * Create file with current timestamp name
     *
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        Timber.e("createImageFile called -> %s", mFile.length());
        return mFile;
    }

    /**
     * Get real file path from URI
     *
     * @param contentUri
     * @return
     */
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private boolean isPermissionGranted() {
        // Check Permission for Marshmallow
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_TAKE_PHOTO);
            return true;

        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return true;
        }
    }
}