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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.adapter.UniversalSpinnerAdapter;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.ui.helper.ProgressView;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.privacyPolicy.PrivacyPolicyActivity;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.ui.termsConditions.TermsConditionsActivity;
import www.fiberathome.com.parkingapp.ui.verifyPhone.VerifyPhoneActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings("unused")
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

    @BindView(R.id.ivVehiclePlate)
    CircleImageView ivVehiclePlate;

    @BindView(R.id.ivVehiclePlatePreview)
    ImageView ivVehiclePlatePreview;

    @BindView(R.id.textViewTermsConditions)
    TextView textViewTermsConditions;

    @BindView(R.id.login_rl_invisible)
    RelativeLayout relativeLayoutInvisible;

    @BindView(R.id.classSpinner)
    Spinner classSpinner;

    @BindView(R.id.divSpinner)
    Spinner divSpinner;

    private Unbinder unbinder;

    private Bitmap bitmap;

    private SignUpActivity context;

    private String vehicleClass = "";
    private String vehicleDiv = "";
    private long classId, cityId;
    private boolean vehicleImage = false;
    private Bitmap bitmap2;

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

        setVehicleClassCategory();
        setVehicleDivCategory();

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

        int s1 = spannableString.toString().codePointAt(0);
        if (s1 >= 0x0980 && s1 <= 0x09E0) {
            spannableString.setSpan(clickableSpan, 16, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            spannableString.setSpan(clickableSpan, 18, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }

        tvLogin.setText(spannableString);
        tvLogin.setMovementMethod(LinkMovementMethod.getInstance());

        textViewTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        textViewTermsConditions.setText(addMultipleClickablePart(context.getResources().getString(R.string.by_using_this_app_you_agree_to_our_terms_and_conditions_amp_privacy_policy)));


        btnSignup.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        imageViewUploadProfileImage.setOnClickListener(this);
        imageViewCaptureImage.setOnClickListener(this);
        ivVehiclePlate.setOnClickListener(this);
        ivVehiclePlatePreview.setOnClickListener(this);
    }

    private UniversalSpinnerAdapter vehicleClassAdapter;
    private List<www.fiberathome.com.parkingapp.model.Spinner> classDataList;
    private List<www.fiberathome.com.parkingapp.model.Spinner> classDivList;

    private void setVehicleClassCategory() {
        vehicleClassAdapter =
                new UniversalSpinnerAdapter(context,
                        android.R.layout.simple_spinner_item,
                        populateVehicleClassData());

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                classId = id;
                vehicleClass = classDataList.get(position).getValue();
                Preferences.getInstance(context).saveVehicleClassData(vehicleClass);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classSpinner.setAdapter(vehicleClassAdapter);
    }

    private List<www.fiberathome.com.parkingapp.model.Spinner> populateVehicleClassData() {
        classDataList = new ArrayList<>();

        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(1, "Dhaka-Metro"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(2, "Chattogram-Metro"));

        return classDataList;
    }

    private UniversalSpinnerAdapter vehicleDivAdapter;

    private void setVehicleDivCategory() {
        vehicleDivAdapter =
                new UniversalSpinnerAdapter(context,
                        android.R.layout.simple_spinner_item,
                        populateVehicleDivData());

        divSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                cityId = id;
                vehicleDiv = classDivList.get(position).getValue();
                Preferences.getInstance(context).saveVehicleDivData(vehicleDiv);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        divSpinner.setAdapter(vehicleDivAdapter);
    }

    private List<www.fiberathome.com.parkingapp.model.Spinner> populateVehicleDivData() {

        classDivList = new ArrayList<>();

        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(1, "Ka"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(2, "kha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(3, "Ga"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(4, "Gha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(5, "Ch"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(6, "Cha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(7, "Ja"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(8, "Jha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(9, "Ta"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(10, "Tha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(11, "DA"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(12, "No"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(13, "Po"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(14, "Vo"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(15, "Mo"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(16, "Da"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(17, "Th"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(18, "Ha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(19, "La"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(20, "E"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(21, "Zo"));

        return classDivList;
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
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                    submitRegistration();
                } else {
                    DialogUtils.getInstance().alertDialog(context,
                            context,
                            context.getString(R.string.connect_to_internet),
                            context.getString(R.string.retry),
                            context.getString(R.string.close_app),
                            new DialogUtils.DialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    Timber.e("Positive Button clicked");
                                    if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                        submitRegistration();
                                    } else {
                                        TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                    }
                                }

                                @Override
                                public void onNegativeClick() {
                                    Timber.e("Negative Button Clicked");
                                    if (context != null) {
                                        context.finish();
                                        TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                    }
                                }
                            }).show();
                }
                break;

            case R.id.imageViewUploadProfileImage:
            case R.id.imageViewCaptureImage:
                if (isPermissionGranted()) {
                    vehicleImage = false;
                    showPictureDialog();
                }
                break;
            case R.id.ivVehiclePlate:
            case R.id.ivVehiclePlatePreview:
                if (isPermissionGranted()) {
                    vehicleImage = true;
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
                if (!vehicleImage) {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contentURI);
                    Bitmap convertedImage = getResizedBitmap(bitmap, 500);
                    imageViewUploadProfileImage.setImageBitmap(convertedImage);
                } else {
                    bitmap2 = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contentURI);
                    Bitmap convertedImage = getResizedBitmap(bitmap2, 500);
                    ivVehiclePlatePreview.setImageBitmap(convertedImage);
                }

                //Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();


            } catch (IOException e) {
                e.printStackTrace();
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }

        } else if (requestCode == REQUEST_PICK_CAMERA && resultCode == RESULT_OK && data != null) {

            try {
                if (data.getExtras() != null) {

                    if (!vehicleImage) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        imageViewUploadProfileImage.setImageBitmap(bitmap);
                    } else {
                        bitmap2 = (Bitmap) data.getExtras().get("data");
                        ivVehiclePlatePreview.setImageBitmap(bitmap2);
                    }
                }
                /*saveImage(thumbnail);
                 Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();*/
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
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
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
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
                        if (ConnectivityUtils.getInstance().checkInternet(context)) {
                            submitRegistration();
                        } else {
                            DialogUtils.getInstance().alertDialog(context,
                                    context,
                                    context.getString(R.string.connect_to_internet),
                                    context.getString(R.string.retry),
                                    context.getString(R.string.close_app),
                                    new DialogUtils.DialogClickListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            Timber.e("Positive Button clicked");
                                            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                                submitRegistration();
                                            } else {
                                                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_internet));
                                            }
                                        }

                                        @Override
                                        public void onNegativeClick() {
                                            Timber.e("Negative Button Clicked");
                                            if (context != null) {
                                                context.finish();
                                                TastyToastUtils.showTastySuccessToast(context, context.getResources().getString(R.string.thanks_message));
                                            }
                                        }
                                    }).show();
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

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(context);
        pictureDialog.setTitle("Select Image");
        String[] pictureDialogItems = {"Select photo from gallery",
                "Capture photo from camera"
        };

        pictureDialog.setItems(pictureDialogItems, (dialog, which) -> {
            switch (which) {
                case 0:
                    choosePhotoFromGallery();
                    break;
                case 1:
                    takePhotoFromCamera();
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

    @SuppressWarnings("SameParameterValue")
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
            String licencePlateInfo = vehicleClass + " " + vehicleDiv + " " + vehicleNo;

            if (bitmap != null && bitmap2 != null) {
                registerUser(fullName, password, mobileNo, licencePlateInfo);
            } else if (bitmap2 == null) {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.upload_vehicle_pic));
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.upload_profile_photo));
            }
        }
    }

    private void registerUser(final String fullName, final String password, final String mobileNo, final String vehicleNo) {

        showLoading(context);

        showProgress();

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<BaseResponse> call = service.createUser(fullName, password, mobileNo, vehicleNo,
                imageToString(bitmap),
                mobileNo + "_" + DateTimeUtils.getInstance().getCurrentTimeStamp(),
                imageToString(bitmap2),
                mobileNo + "vehicle_" + DateTimeUtils.getInstance().getCurrentTimeStamp());

        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                Timber.e("registration response body-> %s", new Gson().toJson(response.body()));

                hideLoading();

                hideProgress();

                if (response.body() != null) {
                    if (!response.body().getError()) {
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());

                        // Moving the screen to next pager item i.e otp screen
                        Intent intent = new Intent(context, VerifyPhoneActivity.class);
                        intent.putExtra("mobile_no",mobileNo);
                        intent.putExtra("password",password);
                        startActivity(intent);
                        SharedData.getInstance().setPassword(password);

                    } else {
                        Timber.e("jsonObject else called");
                        if (response.body().getMessage().equalsIgnoreCase("Sorry! mobile number is not valid or missing mate")) {
                            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
                        } else if (!response.body().getMessage().equalsIgnoreCase("Sorry! mobile number is not valid or missing mate")) {
                            ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                        }
                    }
                } else {
                    ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
                hideLoading();
                hideProgress();
            }
        });
    }

    private boolean checkFields() {
        boolean isNameValid = Validator.checkValidity(textInputLayoutFullName, editTextFullName.getText().toString(), context.getString(R.string.err_msg_fullname), "text");
        boolean isPhoneValid = Validator.checkValidity(textInputLayoutMobile, editTextMobileNumber.getText().toString(), context.getString(R.string.err_msg_mobile), "phone");
        boolean isVehicleRegValid = Validator.checkValidity(textInputLayoutVehicle, editTextVehicleRegNumber.getText().toString(), context.getString(R.string.err_msg_vehicle), "vehicleNumber");
        boolean isPasswordValid = Validator.checkValidity(textInputLayoutPassword, editTextPassword.getText().toString(), context.getString(R.string.err_msg_password_signup), "textPassword");
        boolean isLicencePlateValid = false;
        if (!vehicleClass.isEmpty() && !vehicleClass.equalsIgnoreCase("Select") && !vehicleDiv.isEmpty() && !vehicleDiv.equalsIgnoreCase("Select")) {
            isLicencePlateValid = true;
        } else {
            Toast.makeText(context, "Select Vehicle City and Class", Toast.LENGTH_SHORT).show();
        }

        return isNameValid && isPhoneValid && isVehicleRegValid && isPasswordValid && isLicencePlateValid;
    }
}
