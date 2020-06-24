package www.fiberathome.com.parkingapp.view.activity.forgetPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.base.ParkingApp;
import www.fiberathome.com.parkingapp.data.preference.SharedData;
import www.fiberathome.com.parkingapp.utils.HttpsTrustManager;
import www.fiberathome.com.parkingapp.utils.Validator;

public class ForgetPasswordActivity extends AppCompatActivity {

    private static String TAG = ForgetPasswordActivity.class.getSimpleName();

    @BindView(R.id.textInputLayoutMobile)
    TextInputLayout textInputLayoutMobile;
    @BindView(R.id.editTextMobileNumber)
    EditText editTextMobileNumber;
    @BindView(R.id.btnForgetPassword)
    Button btnForgetPassword;

    private Context context;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        context = this;
        ButterKnife.bind(this);
        setListener();
    }

    private void setListener() {
        btnForgetPassword.setOnClickListener(v -> {
            submitLogin();
        });
    }

    private boolean checkFields() {
        boolean isPhoneValid = Validator.checkValidity(textInputLayoutMobile, editTextMobileNumber.getText().toString(), context.getString(R.string.err_msg_mobile), "phone");
        return isPhoneValid;

    }

    private void submitLogin() {
        if (checkFields()) {
            String mobileNo = editTextMobileNumber.getText().toString().trim();

            checkForgetPassword(mobileNo);
        }
    }

    private void checkForgetPassword(final String mobileNo) {

        progressDialog = new ProgressDialog(ForgetPasswordActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(false);
        progressDialog.show();


        HttpsTrustManager.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppConfig.URL_FORGET_PASSWORD, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                // remove the progress bar
                Timber.e("URL -> %s", new Gson().toJson(AppConfig.URL_FORGET_PASSWORD));
                progressDialog.dismiss();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    Timber.e("jsonObject -> %s", jsonObject.toString());

                    if (jsonObject.getBoolean("error")) {

                        progressDialog.dismiss();
                        showMessage(jsonObject.getString("message"));

                        Intent intent = new Intent(ForgetPasswordActivity.this, ChangePasswordActivityForOTP.class);
                        startActivity(intent);
                        finish();

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
                Timber.e("Error Message -> %s ", error.getMessage());
                if (progressDialog != null) progressDialog.dismiss();
                showMessage(error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("mobile_no", mobileNo);
                SharedData.getInstance().setForgetPasswordMobile(mobileNo);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        ParkingApp.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private void showMessage(String message) {
        Toast.makeText(ForgetPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
