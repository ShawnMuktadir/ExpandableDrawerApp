package www.fiberathome.com.parkingapp.ui.registration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.adapter.UniversalSpinnerAdapter;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.data.model.Spinner;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.data.model.response.global.BaseResponse;
import www.fiberathome.com.parkingapp.databinding.FragmentSignUpBinding;
import www.fiberathome.com.parkingapp.ui.helper.ProgressView;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.login.LoginActivity;
import www.fiberathome.com.parkingapp.ui.privacyPolicy.PrivacyPolicyActivity;
import www.fiberathome.com.parkingapp.ui.privacyPolicy.termsConditions.TermsConditionsActivity;
import www.fiberathome.com.parkingapp.ui.verifyPhone.VerifyPhoneActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.ImageUtils;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})

public class RegistrationFragment extends BaseFragment implements View.OnClickListener, ProgressView {

    private boolean vehicleImage = false;
    private Bitmap profileBitmap;
    private Bitmap vehicleBitmap;

    private String vehicleClass = "";
    private String vehicleDiv = "";
    private String licencePlateInfo = " ";
    private long classId, cityId;
    private List<Spinner> classDataList;
    private List<Spinner> classDivList;

    private RegistrationActivity context;
    private RegistrationViewModel registrationViewModel;
    FragmentSignUpBinding binding;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (RegistrationActivity) getActivity();
        registrationViewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);
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

        binding.tvLogin.setText(spannableString);
        binding.tvLogin.setMovementMethod(LinkMovementMethod.getInstance());

        binding.textViewTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());
        binding.textViewTermsConditions.setText(addMultipleClickablePart(context.getResources().getString(R.string.by_using_this_app_you_agree_to_our_terms_and_conditions_amp_privacy_policy)));

        binding.btnSignup.setOnClickListener(this);
        binding.tvLogin.setOnClickListener(this);
        binding.imageViewUploadProfileImage.setOnClickListener(this);
        binding.imageViewCaptureImage.setOnClickListener(this);
        binding.ivVehiclePlate.setOnClickListener(this);
        binding.ivVehiclePlatePreview.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void showProgress() {
        //progressBarLogin.setVisibility(View.VISIBLE);
        binding.relativeLayoutInvisible.setVisibility(View.VISIBLE);
        binding.editTextFullName.setEnabled(false);
        binding.editTextMobileNumber.setEnabled(false);
        binding.editTextVehicleRegNumber.setEnabled(false);
        binding.editTextPassword.setEnabled(false);
        binding.btnSignup.setEnabled(false);
        binding.btnSignup.setClickable(false);
    }

    @Override
    public void hideProgress() {
        //progressBarLogin.setVisibility(View.INVISIBLE);
        binding.relativeLayoutInvisible.setVisibility(View.GONE);
        binding.editTextFullName.setEnabled(true);
        binding.editTextMobileNumber.setEnabled(true);
        binding.editTextVehicleRegNumber.setEnabled(true);
        binding.editTextPassword.setEnabled(true);
        binding.btnSignup.setEnabled(true);
        binding.btnSignup.setClickable(true);
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
                            context.getResources().getString(R.string.connect_to_internet),
                            context.getResources().getString(R.string.retry),
                            context.getResources().getString(R.string.close_app),
                            new DialogUtils.DialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    Timber.e("Positive Button clicked");
                                    if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                        submitRegistration();
                                    } else {
                                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
                                    }
                                }

                                @Override
                                public void onNegativeClick() {
                                    Timber.e("Negative Button Clicked");
                                    if (context != null) {
                                        context.finish();
                                        ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                                    }
                                }
                            }).show();
                }
                break;

            case R.id.imageViewUploadProfileImage:
            case R.id.imageViewCaptureImage:
                if (ImageUtils.getInstance().isCameraPermissionGranted(context)) {
                    vehicleImage = false;
                    showPictureDialog();
                }
                break;
            case R.id.ivVehiclePlate:
            case R.id.ivVehiclePlatePreview:
                if (ImageUtils.getInstance().isCameraPermissionGranted(context)) {
                    vehicleImage = true;
                    showPictureDialog();
                }
                break;
        }
    }

    private void setVehicleClassCategory() {
        UniversalSpinnerAdapter vehicleClassAdapter = new UniversalSpinnerAdapter(context,
                android.R.layout.simple_spinner_item,
                populateVehicleClassData());

        binding.classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        binding.classSpinner.setAdapter(vehicleClassAdapter);
    }

    private List<www.fiberathome.com.parkingapp.data.model.Spinner> populateVehicleClassData() {
        classDataList = new ArrayList<>();

        classDataList.add(new Spinner(1, "Dhaka-Metro"));
        classDataList.add(new Spinner(2, "Chattogram-Metro"));
        classDataList.add(new Spinner(3, "Bagerhat"));
        classDataList.add(new Spinner(4, "Barguna"));
        classDataList.add(new Spinner(5, "Barishal"));
        classDataList.add(new Spinner(6, "Bandarban"));
        classDataList.add(new Spinner(7, "Bhola"));
        classDataList.add(new Spinner(8, "Bogura"));
        classDataList.add(new Spinner(9, "Brahmanbaria"));
        classDataList.add(new Spinner(10, "Chapainawabganj"));
        classDataList.add(new Spinner(11, "Chandpur"));
        classDataList.add(new Spinner(12, "Chattogram"));
        classDataList.add(new Spinner(13, "Cox's Bazar"));
        classDataList.add(new Spinner(14, "Cumilla"));
        classDataList.add(new Spinner(15, "Chuadanga"));
        classDataList.add(new Spinner(16, "Dhaka"));
        classDataList.add(new Spinner(17, "Dinajpur"));
        classDataList.add(new Spinner(18, "Feni"));
        classDataList.add(new Spinner(19, "Faridpur"));
        classDataList.add(new Spinner(20, "Gaibandha"));
        classDataList.add(new Spinner(21, "Gazipur"));
        classDataList.add(new Spinner(22, "Gopalganj"));
        classDataList.add(new Spinner(23, "Habiganj"));
        classDataList.add(new Spinner(24, "Jhalokati"));
        classDataList.add(new Spinner(25, "Jashore"));
        classDataList.add(new Spinner(26, "Jhenaidah"));
        classDataList.add(new Spinner(27, "Jamalpur"));
        classDataList.add(new Spinner(28, "Joypurhat"));
        classDataList.add(new Spinner(29, "Khulna"));
        classDataList.add(new Spinner(30, "Kurigram"));
        classDataList.add(new Spinner(31, "Kushtia"));
        classDataList.add(new Spinner(32, "Khagrachhari"));
        classDataList.add(new Spinner(33, "Kishoreganj"));
        classDataList.add(new Spinner(34, "Lakshmipur"));
        classDataList.add(new Spinner(35, "Lalmonirhat"));
        classDataList.add(new Spinner(36, "Madaripur"));
        classDataList.add(new Spinner(37, "Manikganj"));
        classDataList.add(new Spinner(38, "Magura"));
        classDataList.add(new Spinner(39, "Meherpur"));
        classDataList.add(new Spinner(40, "Moulvibazar"));
        classDataList.add(new Spinner(41, "Munshiganj"));
        classDataList.add(new Spinner(42, "Mymensingh"));
        classDataList.add(new Spinner(43, "Naogaon"));
        classDataList.add(new Spinner(44, "Natore"));
        classDataList.add(new Spinner(45, "Narail"));
        classDataList.add(new Spinner(46, "Netrokona"));
        classDataList.add(new Spinner(47, "Narayanganj"));
        classDataList.add(new Spinner(48, "Narsingdi"));
        classDataList.add(new Spinner(49, "Nilphamari"));
        classDataList.add(new Spinner(50, "Noakhali"));
        classDataList.add(new Spinner(51, "Pabna"));
        classDataList.add(new Spinner(52, "Panchagarh"));
        classDataList.add(new Spinner(53, "Patuakhali"));
        classDataList.add(new Spinner(54, "Pirojpur"));
        classDataList.add(new Spinner(55, "Rajshahi"));
        classDataList.add(new Spinner(56, "Rangamati"));
        classDataList.add(new Spinner(57, "Rajbari"));
        classDataList.add(new Spinner(58, "Rangpur"));
        classDataList.add(new Spinner(59, "Shariatpur"));
        classDataList.add(new Spinner(60, "Satkhira"));
        classDataList.add(new Spinner(61, "Sherpur"));
        classDataList.add(new Spinner(62, "Sirajganj"));
        classDataList.add(new Spinner(63, "Sunamganj"));
        classDataList.add(new Spinner(64, "Sylhet"));
        classDataList.add(new Spinner(65, "Tangail"));
        classDataList.add(new Spinner(66, "Thakurgaon"));

        return classDataList;
    }

    private void setVehicleDivCategory() {
        UniversalSpinnerAdapter vehicleDivAdapter = new UniversalSpinnerAdapter(context,
                android.R.layout.simple_spinner_item,
                populateVehicleDivData());

        binding.divSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        binding.divSpinner.setAdapter(vehicleDivAdapter);
    }

    private List<Spinner> populateVehicleDivData() {

        classDivList = new ArrayList<>();

        classDivList.add(new Spinner(1, "A"));
        classDivList.add(new Spinner(2, "Ka"));
        classDivList.add(new Spinner(3, "Kha"));
        classDivList.add(new Spinner(4, "Ga"));
        classDivList.add(new Spinner(5, "Gha"));
        classDivList.add(new Spinner(6, "Uo"));
        classDivList.add(new Spinner(7, "Ca"));
        classDivList.add(new Spinner(8, "Cha"));
        classDivList.add(new Spinner(9, "Ja"));
        classDivList.add(new Spinner(10, "Jha"));
        classDivList.add(new Spinner(11, "Ta"));
        classDivList.add(new Spinner(12, "Tha"));
        classDivList.add(new Spinner(13, "D"));
        classDivList.add(new Spinner(14, "Dha"));
        classDivList.add(new Spinner(15, "Tha"));
        classDivList.add(new Spinner(16, "Da"));
        classDivList.add(new Spinner(17, "Na"));
        classDivList.add(new Spinner(18, "Pa"));
        classDivList.add(new Spinner(19, "Pha"));
        classDivList.add(new Spinner(20, "Ba"));
        classDivList.add(new Spinner(21, "Bha"));
        classDivList.add(new Spinner(22, "Ma"));
        classDivList.add(new Spinner(23, "Ja"));
        classDivList.add(new Spinner(24, "Ra"));
        classDivList.add(new Spinner(25, "La"));
        classDivList.add(new Spinner(26, "Sha"));
        classDivList.add(new Spinner(27, "Sa"));
        classDivList.add(new Spinner(28, "Ha"));
        classDivList.add(new Spinner(29, "E"));
        classDivList.add(new Spinner(30, "O"));
        classDivList.add(new Spinner(31, "AU"));

        return classDivList;
    }

    private final ActivityResultLauncher<Intent> galleryPermissionResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data != null) {
                        setGalleryPicture(data);
                    }
                } else {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
                }
            });

    private final ActivityResultLauncher<Intent> cameraPermissionResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    if (data != null) {
                        setCameraPicture(data);
                    }
                } else {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
                }
            });

    private void setGalleryPicture(Intent data) {
        Uri contentURI = data.getData();
        try {
            if (contentURI != null) {
                if (!vehicleImage) {
                    profileBitmap = ImageUtils.getInstance().convertUriToBitmap(context, contentURI);
                    Bitmap convertedImage = Bitmap.createScaledBitmap(profileBitmap, 828, 828, true);
                    profileBitmap = convertedImage;
                    binding.imageViewUploadProfileImage.setImageBitmap(convertedImage);
                } else {
                    vehicleBitmap = ImageUtils.getInstance().convertUriToBitmap(context, contentURI);
                    Bitmap convertedImage = Bitmap.createScaledBitmap(vehicleBitmap, 828, 828, true);
                    vehicleBitmap = convertedImage;
                    binding.ivVehiclePlatePreview.setImageBitmap(convertedImage);
                }
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
        }
    }

    private void setCameraPicture(Intent data) {
        try {
            if (data.getExtras() != null) {
                if (!vehicleImage) {
                    profileBitmap = (Bitmap) data.getExtras().get("data");
                    if (profileBitmap != null) {
                        profileBitmap = Bitmap.createScaledBitmap(profileBitmap, 828, 828, true);
                        binding.imageViewUploadProfileImage.setImageBitmap(profileBitmap);
                    }
                } else {
                    vehicleBitmap = (Bitmap) data.getExtras().get("data");
                    if (vehicleBitmap != null) {
                        vehicleBitmap = Bitmap.createScaledBitmap(vehicleBitmap, 828, 828, true);
                        binding.ivVehiclePlatePreview.setImageBitmap(vehicleBitmap);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
        }
    }

    private SpannableStringBuilder addMultipleClickablePart(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        ssb.setSpan(new ClickableSpan() {

            @Override
            public void onClick(@NotNull View widget) {
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                    context.startActivity(new Intent(context, TermsConditionsActivity.class));
                } else {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
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
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
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

        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.radioGeneral:
                    // do operations specific to this selection
                    binding.linearLayoutGeneralFormat.setVisibility(View.VISIBLE);
                    binding.linearLayoutMilitaryFormat.setVisibility(View.GONE);
                    break;
                case R.id.radioMilitary:
                    // do operations specific to this selection
                    binding.linearLayoutGeneralFormat.setVisibility(View.GONE);
                    binding.linearLayoutMilitaryFormat.setVisibility(View.VISIBLE);
                    break;
            }
        });

        binding.editTextPassword.setOnEditorActionListener(
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
                                    context.getResources().getString(R.string.connect_to_internet),
                                    context.getResources().getString(R.string.retry),
                                    context.getResources().getString(R.string.close_app),
                                    new DialogUtils.DialogClickListener() {
                                        @Override
                                        public void onPositiveClick() {
                                            Timber.e("Positive Button clicked");
                                            if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                                submitRegistration();
                                            } else {
                                                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet));
                                            }
                                        }

                                        @Override
                                        public void onNegativeClick() {
                                            Timber.e("Negative Button Clicked");
                                            if (context != null) {
                                                context.finish();
                                                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.thanks_message));
                                            }
                                        }
                                    }).show();
                        }
                        return true;
                    }
                    return false;
                });

        Objects.requireNonNull(binding.textInputLayoutFullName.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutFullName.setErrorEnabled(true);
                    binding.textInputLayoutFullName.setError(context.getResources().getString(R.string.err_msg_fullname));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutFullName.setError(null);
                    binding.textInputLayoutFullName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(binding.textInputLayoutMobile.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutMobile.setErrorEnabled(true);
                    binding.textInputLayoutMobile.setError(context.getResources().getString(R.string.err_msg_mobile));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutMobile.setError(null);
                    binding.textInputLayoutMobile.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(binding.textInputLayoutVehicle.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutVehicle.setErrorEnabled(true);
                    binding.textInputLayoutVehicle.setError(context.getResources().getString(R.string.err_msg_vehicle));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutVehicle.setError(null);
                    binding.textInputLayoutVehicle.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(binding.textInputLayoutPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutPassword.setErrorEnabled(true);
                    binding.textInputLayoutPassword.setError(context.getResources().getString(R.string.err_msg_password));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutPassword.setError(null);
                    binding.textInputLayoutPassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(binding.textInputLayoutVehicleMilitaryFirstTwoDigit.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutVehicleMilitaryFirstTwoDigit.setErrorEnabled(true);
                    binding.textInputLayoutVehicleMilitaryFirstTwoDigit.setError(context.getResources().getString(R.string.err_msg_vehicle));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutVehicleMilitaryFirstTwoDigit.setError(null);
                    binding.textInputLayoutVehicleMilitaryFirstTwoDigit.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(binding.textInputLayoutVehicleMilitaryLastFourDigit.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutVehicleMilitaryLastFourDigit.setErrorEnabled(true);
                    binding.textInputLayoutVehicleMilitaryLastFourDigit.setError(context.getResources().getString(R.string.err_msg_vehicle));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutVehicleMilitaryLastFourDigit.setError(null);
                    binding.textInputLayoutVehicleMilitaryLastFourDigit.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(context);
        pictureDialog.setTitle(context.getResources().getString(R.string.select_image));
        String[] pictureDialogItems = {context.getResources().getString(R.string.select_photo_from_gallery),
                context.getResources().getString(R.string.capture_photo_from_camera)
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
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryPermissionResult.launch(galleryIntent);
    }

    private void takePhotoFromCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraPermissionResult.launch(cameraIntent);
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

    private String imageToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] imageByte = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(imageByte, Base64.DEFAULT);
        } else {
            return "";
        }
    }

    private void submitRegistration() {
        if (checkFields()) {
            String fullName = binding.editTextFullName.getText().toString().trim();
            String mobileNo = binding.editTextMobileNumber.getText().toString().trim();
            String vehicleNo = binding.editTextVehicleRegNumber.getText().toString().trim();
            String password = binding.editTextPassword.getText().toString().trim();

            if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioGeneral) {
                licencePlateInfo = vehicleClass + " " + vehicleDiv + " " + vehicleNo;
                String temp = "" + vehicleNo.charAt(0) + vehicleNo.charAt(1);
                int vehicleNoInt = MathUtils.getInstance().convertToInt(temp);

                String tempForOther = "" + vehicleNo.charAt(4) + vehicleNo.charAt(5);
                int vehicleNoIntForOther = MathUtils.getInstance().convertToInt(tempForOther);

                if (vehicleNoInt < 11 || (vehicleDiv.equalsIgnoreCase("E") && vehicleNoIntForOther > 60) ||
                        (vehicleDiv.equalsIgnoreCase("Ma") && vehicleClass.equalsIgnoreCase("Munshiganj") && vehicleNoIntForOther > 50) ||
                        (vehicleDiv.equalsIgnoreCase("Ma") && vehicleClass.equalsIgnoreCase("Narayanganj") && vehicleNoInt < 51)) {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.invalid_vehicle_number));
                } else {
                    registerUser(fullName, password, mobileNo, licencePlateInfo);
                }
            } else if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
                licencePlateInfo = context.getResources().getString(R.string.army_vehicle_arrow) + binding.editTextVehicleRegNumberMilitaryFirstTwoDigit.getText().toString().trim() +
                        binding.editTextVehicleRegNumberMilitaryLastFourDigit.getText().toString().trim();
                registerUser(fullName, password, mobileNo, licencePlateInfo);
            }
        } else {
            ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.provide_valid_information));
        }
    }

    private void registerUser(final String fullName, final String password, final String mobileNo,
                              final String vehicleNo) {
        String profileImageName;
        String vehicleImageName;
        showLoading(context);
        showProgress();

        registrationViewModel.init(fullName, password, mobileNo, vehicleNo,
                imageToString(profileBitmap),
                (profileBitmap != null ? mobileNo + "_" + DateTimeUtils.getInstance().getCurrentTimeStamp() : ""),
                imageToString(vehicleBitmap), (vehicleBitmap != null ? mobileNo + "vehicle_" + DateTimeUtils.getInstance().getCurrentTimeStamp() : ""));
        registrationViewModel.getMutableData().observe(requireActivity(), (@NonNull BaseResponse baseResponse) -> {
            hideLoading();
            hideProgress();
            if (!baseResponse.getError()) {
                ToastUtils.getInstance().showToastMessage(context, baseResponse.getMessage());
                // Moving the screen to otp screen
                Intent intent = new Intent(context, VerifyPhoneActivity.class);
                intent.putExtra("mobile_no", mobileNo);
                intent.putExtra("password", password);
                startActivity(intent);
                SharedData.getInstance().setPassword(password);
            } else {
                ToastUtils.getInstance().showToastMessage(context, baseResponse.getMessage());
            }
        });
    }

    private boolean checkFields() {
        boolean isVehicleRegValid = false;
        boolean isVehicleRegValidForFirstTwoDigit = false;
        boolean isVehicleRegValidForLastFourDigit = false;
        boolean isNameValid = Validator.checkValidity(binding.textInputLayoutFullName, binding.editTextFullName.getText().toString(), context.getResources().getString(R.string.err_msg_fullname), "text");
        boolean isPhoneValid = Validator.checkValidity(binding.textInputLayoutMobile, binding.editTextMobileNumber.getText().toString(), context.getResources().getString(R.string.err_msg_mobile), "phone");
        boolean isPasswordValid = Validator.checkValidity(binding.textInputLayoutPassword, binding.editTextPassword.getText().toString(), context.getResources().getString(R.string.err_msg_password_signup), "textPassword");

        boolean isLicencePlateValid = false;
        boolean isConfirmPasswordValid = false;

        if (binding.editTextConfirmPassword.getText().toString().equals(binding.editTextPassword.getText().toString())) {
            isConfirmPasswordValid = true;
            binding.textInputLayoutConfirmPassword.setErrorEnabled(false);
        } else {
            binding.textInputLayoutConfirmPassword.setError(context.getResources().getString(R.string.err_confirm_password));
            binding.textInputLayoutConfirmPassword.setErrorEnabled(true);
        }

        if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioGeneral) {
            isVehicleRegValid = Validator.checkValidity(binding.textInputLayoutVehicle, binding.editTextVehicleRegNumber.getText().toString(), context.getResources().getString(R.string.err_msg_vehicle), "vehicleNumber");
        } else {
            isVehicleRegValidForFirstTwoDigit = Validator.checkValidity(binding.textInputLayoutVehicleMilitaryFirstTwoDigit, binding.editTextVehicleRegNumberMilitaryFirstTwoDigit.getText().toString(), context.getResources().getString(R.string.err_msg_vehicle), "vehicleMilitaryNumberForFirstTwo");
            isVehicleRegValidForLastFourDigit = Validator.checkValidity(binding.textInputLayoutVehicleMilitaryLastFourDigit, binding.editTextVehicleRegNumberMilitaryLastFourDigit.getText().toString(), context.getResources().getString(R.string.err_msg_vehicle), "vehicleMilitaryNumberForLastFour");
        }

        if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
            licencePlateInfo = binding.editTextVehicleRegNumberMilitaryFirstTwoDigit.getText().toString().trim() +
                    binding.editTextVehicleRegNumberMilitaryLastFourDigit.getText().toString().trim();
        }

        if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioGeneral) {
            if (!vehicleClass.isEmpty() && !vehicleClass.equalsIgnoreCase("Select") && !vehicleDiv.isEmpty() && !vehicleDiv.equalsIgnoreCase("Select")) {
                isLicencePlateValid = true;
            }
        } else if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
            isLicencePlateValid = !licencePlateInfo.equalsIgnoreCase("000000");
        } else {
            Toast.makeText(context, "Please give valid vehicle number", Toast.LENGTH_SHORT).show();
        }

        if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioGeneral) {
            return isNameValid && isPhoneValid && isVehicleRegValid && isPasswordValid && isConfirmPasswordValid && isLicencePlateValid;
        } else if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
            return isNameValid && isPhoneValid && isVehicleRegValidForFirstTwoDigit && isVehicleRegValidForLastFourDigit && isPasswordValid && isConfirmPasswordValid && isLicencePlateValid;
        } else {
            return false;
        }
    }
}
