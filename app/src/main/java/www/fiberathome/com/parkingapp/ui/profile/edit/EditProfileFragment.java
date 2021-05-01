package www.fiberathome.com.parkingapp.ui.profile.edit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseFragment;
import www.fiberathome.com.parkingapp.model.api.ApiClient;
import www.fiberathome.com.parkingapp.model.api.ApiService;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.response.BaseResponse;
import www.fiberathome.com.parkingapp.model.response.editProfile.EditProfileResponse;
import www.fiberathome.com.parkingapp.model.response.login.LoginResponse;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.helper.ProgressView;
import www.fiberathome.com.parkingapp.ui.home.HomeFragment;
import www.fiberathome.com.parkingapp.ui.verifyPhone.VerifyPhoneActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.DateTimeUtils;
import www.fiberathome.com.parkingapp.utils.DialogUtils;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.IOnBackPressListener;
import www.fiberathome.com.parkingapp.utils.ImageUtils;
import www.fiberathome.com.parkingapp.utils.TastyToastUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;
import www.fiberathome.com.parkingapp.utils.Validator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings("unused")
public class EditProfileFragment extends BaseFragment implements IOnBackPressListener, View.OnClickListener, ProgressView {

    private static final int REQUEST_PICK_GALLERY = 1001;
    private static final int REQUEST_PICK_CAMERA = 1002;

    @BindView(R.id.textInputLayoutFullName)
    TextInputLayout textInputLayoutFullName;

    @BindView(R.id.editTextFullName)
    EditText editTextFullName;

    @BindView(R.id.textInputLayoutCarNumber)
    TextInputLayout textInputLayoutCarNumber;

    @BindView(R.id.editTextCarNumber)
    EditText editTextCarNumber;

    @BindView(R.id.tvUserMobileNo)
    TextView tvUserMobileNo;

    @BindView(R.id.imageViewEditProfileImage)
    CircleImageView imageViewEditProfileImage;

    @BindView(R.id.imageViewCaptureImage)
    ImageView imageViewCaptureImage;

    @BindView(R.id.btn_update_info)
    Button btnUpdateInfo;

    @BindView(R.id.login_rl_invisible)
    RelativeLayout relativeLayoutInvisible;

    private Unbinder unbinder;

    private EditProfileActivity context;

    private Bitmap bitmap;

    private User user;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_new, container, false);

        unbinder = ButterKnife.bind(this, view);

        context = (EditProfileActivity) getActivity();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        user = Preferences.getInstance(context).getUser();

        setData(user);

        btnUpdateInfo.setOnClickListener(this);
        imageViewEditProfileImage.setOnClickListener(this);
        imageViewCaptureImage.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
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
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), contentURI);
                Bitmap convertedImage = getResizedBitmap(bitmap, 500);
                //Toast.makeText(SignUpActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                imageViewEditProfileImage.setImageBitmap(convertedImage);

            } catch (IOException e) {
                e.printStackTrace();
                ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.something_went_wrong));
            }

        } else if (requestCode == REQUEST_PICK_CAMERA && resultCode == RESULT_OK && data != null) {

            try {
                if (data.getExtras() != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imageViewEditProfileImage.setImageBitmap(bitmap);
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
    public boolean onBackPressed() {
        if (isGPSEnabled()) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, HomeFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.connect_to_gps));
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
                            context.getString(R.string.connect_to_internet),
                            context.getString(R.string.retry),
                            context.getString(R.string.close_app),
                            new DialogUtils.DialogClickListener() {
                                @Override
                                public void onPositiveClick() {
                                    Timber.e("Positive Button clicked");
                                    if (ConnectivityUtils.getInstance().checkInternet(context)) {
                                        submitEditProfileInfo();
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

            case R.id.imageViewEditProfileImage:
            case R.id.imageViewCaptureImage:
                if (isPermissionGranted()) {
                    showPictureDialog();
                }
                break;
        }
    }

    private void setData(User user) {

        String name = user.getFullName();
        name = TextUtils.getInstance().capitalizeFirstLetter(name);
        editTextFullName.setText(name);

        tvUserMobileNo.setText(TextUtils.getInstance().addCountryPrefix(user.getMobileNo()));
        Timber.e("Mobile no -> %s", user.getMobileNo());

        editTextCarNumber.setText(user.getVehicleNo());

        String url = AppConfig.IMAGES_URL + user.getImage() + ".jpg";
        Timber.e("Image URL -> %s", url);
        Glide.with(context).load(url).placeholder(R.drawable.ic_account_settings).dontAnimate().into(imageViewEditProfileImage);
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
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(imageByte, Base64.DEFAULT);

    }

    private void editProfile(final String fullName, final String password, final String mobileNo, final String vehicleNo) {

        showLoading(context);

        showProgress();

        ApiService service = ApiClient.getRetrofitInstance(AppConfig.BASE_URL).create(ApiService.class);
        Call<LoginResponse> call = service.editProfile(fullName, password, mobileNo, vehicleNo, bitmap!=null? imageToString(bitmap) :
                imageToString(ImageUtils.getInstance().imageUrlToBitmap(AppConfig.IMAGES_URL + user.getImage() + ".jpg")),
                mobileNo + "_" + DateTimeUtils.getInstance().getCurrentTimeStamp());

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {

                Timber.e("edit profile response body-> %s", new Gson().toJson(response.body()));
                assert response.body() != null;
                Timber.e("edit profile response user-> %s", new Gson().toJson(response.body().getUser()));

                hideLoading();

                hideProgress();

                if (response.body() != null) {
                    if (!response.body().getError()) {
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());

                        User user = new User();
                        user.setId(Preferences.getInstance(context).getUser().getId());
                        user.setFullName(fullName);
                        user.setMobileNo(mobileNo);
                        user.setVehicleNo(vehicleNo);
                        user.setImage(bitmap!=null? imageToString(bitmap) :
                                imageToString(ImageUtils.getInstance().imageUrlToBitmap(AppConfig.IMAGES_URL + user.getImage() + ".jpg")));

                        // storing the user in sharedPreference
                        Preferences.getInstance(context).userLogin(user);
                        Timber.e("user after update -> %s", new Gson().toJson(user));

                        /*if (response.body().getUser() != null) {
                            User user = new User();
                            user.setId(Preferences.getInstance(context).getUser().getId());
                            //user.setId(response.body().getUser().getId());
                            user.setFullName(response.body().getUser().getFullName());
                            user.setMobileNo(response.body().getUser().getMobileNo());
                            user.setVehicleNo(response.body().getUser().getVehicleNo());
                            user.setImage(response.body().getUser().getImage());

                            // storing the user in sharedPreference
                            Preferences.getInstance(context).userLogin(user);
                            Timber.e("user after update -> %s", new Gson().toJson(user));
                        }*/

                    } else {
                        Timber.e("jsonObject else called");
                        ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
                    }
                } else {
                    ToastUtils.getInstance().showToastMessage(context, response.body().getMessage());
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


    /*
    private void registerUser(final String fullname, final String mobileNo, final String vehicleNo, final String password) {

        showProgress();

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
                    Intent intent = new Intent(SignUpActivity.this, VerifyPhoneActivity.class);
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
*/

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
            String fullName = editTextFullName.getText().toString().trim();
            String vehicleNo = editTextCarNumber.getText().toString().trim();
            String password = SharedData.getInstance().getPassword();
            String mobileNo = TextUtils.getInstance().addCountryPrefix(user.getMobileNo());

            //if (bitmap != null) {
                editProfile(fullName, password, mobileNo, vehicleNo);
           /* } else {
                TastyToastUtils.showTastyWarningToast(context, context.getResources().getString(R.string.upload_profile_photo));
            }*/
        }
    }

    private boolean checkFields() {
        boolean isNameValid = Validator.checkValidity(textInputLayoutFullName, editTextFullName.getText().toString(), context.getString(R.string.err_msg_fullname), "text");
        boolean isVehicleRegValid = Validator.checkValidity(textInputLayoutCarNumber, editTextCarNumber.getText().toString(), context.getString(R.string.err_msg_vehicle), "text");

        return isNameValid && isVehicleRegValid;
    }

    @Override
    public void showProgress() {
        relativeLayoutInvisible.setVisibility(View.VISIBLE);
        editTextFullName.setEnabled(false);
        editTextCarNumber.setEnabled(false);
        btnUpdateInfo.setEnabled(false);
        btnUpdateInfo.setClickable(false);
    }

    @Override
    public void hideProgress() {
        relativeLayoutInvisible.setVisibility(View.GONE);
        editTextFullName.setEnabled(true);
        editTextCarNumber.setEnabled(true);
        btnUpdateInfo.setEnabled(true);
        btnUpdateInfo.setClickable(true);
    }
}
