package www.fiberathome.com.parkingapp.ui.profile.edit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.adapter.UniversalSpinnerAdapter;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.databinding.FragmentProfileEditBinding;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.helper.ProgressView;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.MathUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class EditProfileFragment extends BaseFragment implements IOnBackPressListener, View.OnClickListener, ProgressView {

    private static final int REQUEST_PICK_GALLERY = 1001;
    private static final int REQUEST_PICK_CAMERA = 1002;

    private EditProfileActivity context;

    private String vehicleClass = "";
    private String vehicleDiv = "";
    private long classId, divId;

    private Bitmap bitmap;

    private User user;
    private Bitmap bitmap2;
    private boolean vehicleImage = false;

    FragmentProfileEditBinding binding;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance() {
        return new EditProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileEditBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = (EditProfileActivity) getActivity();
        user = Preferences.getInstance(context).getUser();

        setVehicleClassCategory();
        setVehicleDivCategory();

        setData(user);
        binding.editTextFullName.setSelection(binding.editTextFullName.getText().length());

        setListeners();

        binding.btnUpdateInfo.setOnClickListener(this);
        binding.imageViewEditProfileImage.setOnClickListener(this);
        binding.imageViewCaptureImage.setOnClickListener(this);
        binding.ivVehiclePlateEdit.setOnClickListener(this);
        binding.ivVehicleEditPlatePreview.setOnClickListener(this);
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

        if (TextUtils.getInstance().isNumeric(Preferences.getInstance(context).getUser().getVehicleNo())) {
            binding.radioMilitary.setChecked(true);
            binding.linearLayoutMilitaryFormat.setVisibility(View.VISIBLE);
            binding.linearLayoutGeneralFormat.setVisibility(View.GONE);
        } else {
            binding.radioGeneral.setChecked(true);
            binding.linearLayoutGeneralFormat.setVisibility(View.VISIBLE);
            binding.linearLayoutMilitaryFormat.setVisibility(View.GONE);
        }

        binding.radioGeneral.setOnCheckedChangeListener((buttonView, isChecked) -> Preferences.getInstance(context).setRadioButtonVehicleFormat("general", isChecked));
        binding.radioMilitary.setOnCheckedChangeListener((buttonView, isChecked) -> Preferences.getInstance(context).setRadioButtonVehicleFormat("military", isChecked));

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

        Objects.requireNonNull(binding.textInputLayoutCarNumber.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 1) {
                    binding.textInputLayoutCarNumber.setErrorEnabled(true);
                    binding.textInputLayoutCarNumber.setError(context.getResources().getString(R.string.err_msg_vehicle));
                }

                if (s.length() > 0) {
                    binding.textInputLayoutCarNumber.setError(null);
                    binding.textInputLayoutCarNumber.setErrorEnabled(false);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
                    binding.imageViewEditProfileImage.setImageBitmap(convertedImage);
                } else {
                    bitmap2 = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contentURI);
                    Bitmap convertedImage = getResizedBitmap(bitmap2, 500);
                    binding.ivVehicleEditPlatePreview.setImageBitmap(convertedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }
        } else if (requestCode == REQUEST_PICK_CAMERA && resultCode == RESULT_OK && data != null) {
            try {
                if (data.getExtras() != null) {
                    if (!vehicleImage) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        binding.imageViewEditProfileImage.setImageBitmap(bitmap);
                    } else {
                        bitmap2 = (Bitmap) data.getExtras().get("data");
                        binding.ivVehicleEditPlatePreview.setImageBitmap(bitmap2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, context.getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (isGPSEnabled()) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_gps));
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update_info:
                if (ConnectivityUtils.getInstance().checkInternet(context)) {
                    submitEditProfileInfo();
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
                                        submitEditProfileInfo();
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

            case R.id.imageViewEditProfileImage:
            case R.id.imageViewCaptureImage:
                if (isPermissionGranted()) {
                    vehicleImage = false;
                    showPictureDialog();
                }
                break;
            case R.id.ivVehiclePlateEdit:
            case R.id.ivVehicleEditPlatePreview:
                if (isPermissionGranted()) {
                    vehicleImage = true;
                    showPictureDialog();
                }
                break;
        }
    }

    private void setData(User user) {

        String name = user.getFullName();
        name = TextUtils.getInstance().capitalizeFirstLetter(name);
        binding.editTextFullName.setText(name);

        binding.tvUserMobileNo.setText(TextUtils.getInstance().addCountryPrefixWithPlus(user.getMobileNo()));
        //Timber.e("Mobile no -> %s", user.getMobileNo());

        if (TextUtils.getInstance().isNumeric(Preferences.getInstance(context).getUser().getVehicleNo())) {
            binding.editTextVehicleRegNumberMilitaryFirstTwoDigit.setText(user.getVehicleNo().substring(0, 2));
            binding.editTextVehicleRegNumberMilitaryLastFourDigit.setText(user.getVehicleNo().substring(2, 6));
        }

        try {
            String currentString = user.getVehicleNo().trim();
            String[] separated = currentString.split(" ");
            String carPlateNumber = separated[2];
            binding.editTextCarNumber.setText(carPlateNumber);
        } catch (Exception e) {
            binding.editTextCarNumber.setText(user.getVehicleNo().trim());
        }

        selectSpinnerItemByValue(binding.classSpinner, Preferences.getInstance(context).getVehicleClassData());

        selectSpinnerItemByValue(binding.divSpinner, Preferences.getInstance(context).getVehicleDivData());

        if (!user.getImage().endsWith(".jpg")) {
            if (user.getImage() != null) {
                String url = AppConfig.IMAGES_URL + user.getImage() + ".jpg";
                Timber.e("Image URL -> %s", url);
                Glide.with(context).load(url).placeholder(R.drawable.ic_account_settings).dontAnimate().into(binding.imageViewEditProfileImage);
            } else {
                Timber.e("Image value -> %s", user.getImage());
            }
            if (user.getVehicleImage() != null) {
                String url = AppConfig.IMAGES_URL + user.getVehicleImage() + ".jpg";
                Timber.e("Vehicle Image URL -> %s", url);
                Glide.with(context).load(url).placeholder(R.drawable.ic_image_place_holder).dontAnimate().into(binding.ivVehicleEditPlatePreview);
            } else {
                Timber.e("Vehicle Image value -> %s", user.getVehicleImage());
            }
        } else {
            if (user.getImage() != null) {
                String url = AppConfig.IMAGES_URL + user.getImage();
                Timber.e("Image URL -> %s", url);
                Glide.with(context).load(url).placeholder(R.drawable.ic_account_settings).dontAnimate().into(binding.imageViewEditProfileImage);
            } else {
                Timber.e("Image value -> %s", user.getImage());
            }

            if (user.getVehicleImage() != null) {
                String url = AppConfig.IMAGES_URL + user.getVehicleImage();
                Timber.e("Vehicle Image URL -> %s", url);
                Glide.with(context).load(url).placeholder(R.drawable.ic_image_place_holder).dontAnimate().into(binding.ivVehicleEditPlatePreview);
            } else {
                Timber.e("Vehicle Image value -> %s", user.getVehicleImage());
            }
        }
    }

    public void selectSpinnerItemByValue(Spinner spinner, String value) {
        UniversalSpinnerAdapter adapter = (UniversalSpinnerAdapter) spinner.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++) {
            if (adapter.getItem(position).getValue().equalsIgnoreCase(value)) {
                spinner.setSelection(position);
                return;
            }
        }
    }

    private List<www.fiberathome.com.parkingapp.model.Spinner> classDataList;
    private List<www.fiberathome.com.parkingapp.model.Spinner> classDivList;

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

    private List<www.fiberathome.com.parkingapp.model.Spinner> populateVehicleClassData() {
        classDataList = new ArrayList<>();

        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(1, "Dhaka-Metro"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(2, "Chattogram-Metro"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(3, "Bagerhat"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(4, "Barguna"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(5, "Barishal"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(6, "Bandarban"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(7, "Bhola"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(8, "Bogura"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(9, "Brahmanbaria"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(10, "Chapainawabganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(11, "Chandpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(12, "Chattogram"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(13, "Cox's Bazar"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(14, "Cumilla"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(15, "Chuadanga"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(16, "Dhaka"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(17, "Dinajpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(18, "Feni"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(19, "Faridpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(20, "Gaibandha"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(21, "Gazipur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(22, "Gopalganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(23, "Habiganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(24, "Jhalokati"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(25, "Jashore"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(26, "Jhenaidah"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(27, "Jamalpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(28, "Joypurhat"));

        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(29, "Khulna"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(30, "Kurigram"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(31, "Kushtia"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(32, "Khagrachhari"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(33, "Kishoreganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(34, "Lakshmipur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(35, "Lalmonirhat"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(36, "Madaripur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(37, "Manikganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(38, "Magura"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(39, "Meherpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(40, "Moulvibazar"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(41, "Munshiganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(42, "Mymensingh"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(43, "Naogaon"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(44, "Natore"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(45, "Narail"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(46, "Netrokona"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(47, "Narayanganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(48, "Narsingdi"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(49, "Nilphamari"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(50, "Noakhali"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(51, "Pabna"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(52, "Panchagarh"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(53, "Patuakhali"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(54, "Pirojpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(55, "Rajshahi"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(56, "Rangamati"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(57, "Rajbari"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(58, "Rangpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(59, "Shariatpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(60, "Satkhira"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(61, "Sherpur"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(62, "Sirajganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(63, "Sunamganj"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(64, "Sylhet"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(65, "Tangail"));
        classDataList.add(new www.fiberathome.com.parkingapp.model.Spinner(66, "Thakurgaon"));

        return classDataList;
    }

    private void setVehicleDivCategory() {
        UniversalSpinnerAdapter vehicleDivAdapter = new UniversalSpinnerAdapter(context,
                android.R.layout.simple_spinner_item,
                populateVehicleDivData());

        binding.divSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                divId = id;
                vehicleDiv = classDivList.get(position).getValue();
                Preferences.getInstance(context).saveVehicleDivData(vehicleDiv);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.divSpinner.setAdapter(vehicleDivAdapter);
    }

    private List<www.fiberathome.com.parkingapp.model.Spinner> populateVehicleDivData() {

        classDivList = new ArrayList<>();

        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(1, "A"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(2, "Ka"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(3, "Kha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(4, "Ga"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(5, "Gha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(6, "Uo"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(7, "Ca"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(8, "Cha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(9, "Ja"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(10, "Jha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(11, "Ta"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(12, "Tha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(13, "D"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(14, "Dha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(15, "Tha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(16, "Da"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(17, "Na"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(18, "Pa"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(19, "Pha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(20, "Ba"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(21, "Bha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(22, "Ma"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(23, "Ja"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(24, "Ra"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(25, "La"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(26, "Sha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(27, "Sa"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(28, "Ha"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(29, "E"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(30, "O"));
        classDivList.add(new www.fiberathome.com.parkingapp.model.Spinner(31, "AU"));

        return classDivList;
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

    private boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (providerEnabled) {
            return true;
        } else {
            Timber.e("else called");
        }
        return false;
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

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageByte, Base64.DEFAULT);
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private void editProfile(final String fullName, final String password, final String mobileNo, final String vehicleNo) {

        showLoading(context);

        showProgress();

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<LoginResponse> call = service.editProfile(fullName,
                password,
                user.getMobileNo(),
                vehicleNo,
                bitmap != null ? imageToString(bitmap) :
                        imageToString(((BitmapDrawable) binding.imageViewEditProfileImage.getDrawable()).getBitmap()),
                mobileNo + "_" + DateTimeUtils.getInstance().getCurrentTimeStamp(),
                bitmap2 != null ? imageToString(bitmap2) :
                        imageToString(((BitmapDrawable) binding.ivVehicleEditPlatePreview.getDrawable()).getBitmap()),
                mobileNo + "vehicle_" + DateTimeUtils.getInstance().getCurrentTimeStamp());

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                hideLoading();

                hideProgress();

                try {
                    Timber.e("Response -> %s", new Gson().toJson(response.body()));
                    Timber.e("ResponseCall -> %s", new Gson().toJson(call.request().body()));
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());

                            User user = new User();
                            user.setId(Preferences.getInstance(context).getUser().getId());
                            user.setFullName(response.body().getUser().getFullName());
                            user.setMobileNo(response.body().getUser().getMobileNo());
                            user.setVehicleNo(response.body().getUser().getVehicleNo());
                            user.setImage(response.body().getUser().getImage());
                            user.setVehicleImage(response.body().getUser().getVehicleImage());

                            // storing the user in sharedPreference
                            Preferences.getInstance(context).setUser(user);
                            Timber.e("user after update -> %s", new Gson().toJson(user));
                            ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                            context.onBackPressed();
                        } else {
                            Timber.e("jsonObject else called");
                            ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                        }
                    } else {
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable errors) {
                Timber.e("Throwable Errors: -> %s", errors.toString());
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
                hideLoading();
                hideProgress();
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

    private void submitEditProfileInfo() {
        if (checkFields()) {
            String fullName = binding.editTextFullName.getText().toString().trim();
            String vehicleNo = binding.editTextCarNumber.getText().toString().trim();
            String licencePlateInfo;
            String password = SharedData.getInstance().getPassword();
            String mobileNo = user.getMobileNo();

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
                    editProfile(fullName, password, mobileNo, licencePlateInfo);
                }
            } else if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
                licencePlateInfo = binding.editTextVehicleRegNumberMilitaryFirstTwoDigit.getText().toString().trim() +
                        binding.editTextVehicleRegNumberMilitaryLastFourDigit.getText().toString().trim();
                editProfile(fullName, password, mobileNo, licencePlateInfo);

            }
        }
    }

    private String licencePlateInfo = " ";

    private boolean checkFields() {
        boolean isVehicleRegValid = false;
        boolean isVehicleRegValidForFirstTwoDigit = false;
        boolean isVehicleRegValidForLastFourDigit = false;
        boolean isNameValid = Validator.checkValidity(binding.textInputLayoutFullName, binding.editTextFullName.getText().toString(), context.getResources().getString(R.string.err_msg_fullname), "text");
        boolean isLicencePlateValid = false;

        if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioGeneral) {
            isVehicleRegValid = Validator.checkValidity(binding.textInputLayoutCarNumber, binding.editTextCarNumber.getText().toString(), context.getResources().getString(R.string.err_msg_vehicle), "vehicleNumber");
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
            return isNameValid && isVehicleRegValid && isLicencePlateValid;
        } else if (binding.radioGroup.getCheckedRadioButtonId() == R.id.radioMilitary) {
            return isNameValid && isVehicleRegValidForFirstTwoDigit && isVehicleRegValidForLastFourDigit && isLicencePlateValid;
        } else {
            return false;
        }
    }

    @Override
    public void showProgress() {
        binding.relativeLayoutInvisible.setVisibility(View.VISIBLE);
        binding.editTextFullName.setEnabled(false);
        binding.editTextCarNumber.setEnabled(false);
        binding.btnUpdateInfo.setEnabled(false);
        binding.btnUpdateInfo.setClickable(false);
    }

    @Override
    public void hideProgress() {
        binding.relativeLayoutInvisible.setVisibility(View.GONE);
        binding.editTextFullName.setEnabled(true);
        binding.editTextCarNumber.setEnabled(true);
        binding.btnUpdateInfo.setEnabled(true);
        binding.btnUpdateInfo.setClickable(true);
    }
}
