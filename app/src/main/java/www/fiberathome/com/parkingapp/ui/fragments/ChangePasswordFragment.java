package www.fiberathome.com.parkingapp.ui.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.api.ApiClient;
import www.fiberathome.com.parkingapp.api.ApiService;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.utils.MSG;
import www.fiberathome.com.parkingapp.data.preference.SharedPreManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ChangePasswordFragment.class.getSimpleName();
    private TextInputLayout inputLayoutOldPassword;
    private TextInputLayout inputLayoutNewPassword;
    private TextInputLayout inputLayoutConfirmPassword;

    private EditText oldPasswordET;
    private EditText newPasswordET;
    private EditText confirmPasswordET;

    private Button changePasswordBtn;

    private ProgressDialog progressDialog;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        getActivity().setTitle(R.string.title_change_password);

        initialize(view);

        return view;
    }

    private void initialize(View view) {
        oldPasswordET = view.findViewById(R.id.input_old_password);
        newPasswordET = view.findViewById(R.id.input_new_password);
        confirmPasswordET = view.findViewById(R.id.input_confirm_password);
        changePasswordBtn = view.findViewById(R.id.change_password_btn);

        inputLayoutOldPassword = view.findViewById(R.id.input_layout_old_password);
        inputLayoutNewPassword = view.findViewById(R.id.input_layout_new_password);
        inputLayoutConfirmPassword = view.findViewById(R.id.input_layout_confirm_password);

        changePasswordBtn.setOnClickListener(this);

        oldPasswordET.addTextChangedListener(new MyTextWatcher(oldPasswordET));

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.change_password_btn:
                changePassword();
                break;
        }
    }

    private void changePassword() {
        // CHECK OLD PASSWORD
        if (!validateEditText(oldPasswordET, inputLayoutOldPassword, R.string.err_old_password)){
            return;
        }

        // CHECK NEW PASSWORD
        if (!validateEditText(newPasswordET, inputLayoutNewPassword, R.string.err_new_password)){
            return;
        }

        // CHECK CONFIRM PASSWORD
        if (!validateEditText(confirmPasswordET, inputLayoutOldPassword, R.string.err_confirm_password)){
            return;
        }



        /**
         * TODO : CHECK WEATHER PASSWORD MATCH WITH SERVER PASSWORD
         * 1. if matched, then check the new password and confirmation password
         * 2. if not matched, then show error the status
         * 3. check number of times user tried to change password.
         * ================================================================================
         */
        if (validateEditText(oldPasswordET, inputLayoutOldPassword, R.string.err_old_password)){
            // IF PASSWORD IS VALID, MOVE TO SERVER WITH user request.

            // COLLECT OLD PASSWORD | NEW PASSWORD | CONFIRMATION PASSWORD

            User user = SharedPreManager.getInstance(getContext()).getUser();
            String old_password = oldPasswordET.getText().toString().trim();
            String new_password = newPasswordET.getText().toString().trim();
            String confirm_password = confirmPasswordET.getText().toString().trim();
            String mobile_no = user.getMobileNo().trim();

            updatePassword(old_password, new_password, confirm_password, mobile_no);

        }

    }

    /**
     *
     * @param old_password
     * @param new_password
     * @param confirm_password
     * @param mobile_no
     */
    private void updatePassword(String old_password, String new_password, String confirm_password, String mobile_no) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Changing Password through UI Service.
        ApiService service = ApiClient.getClient().create(ApiService.class);
        Call<MSG> passwordUpgradeCall = service.updatePassword(old_password, new_password, confirm_password, mobile_no);

        // Gathering results.
        passwordUpgradeCall.enqueue(new Callback<MSG>() {
            @Override
            public void onResponse(Call<MSG> call, Response<MSG> response) {
                Log.e(TAG, response.message());

                progressDialog.dismiss();
                if (!response.body().getError()){
                    showMessage(response.body().getMessage());
                }else{
                    showMessage(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<MSG> call, Throwable errors) {
                Log.e("Throwable Errors: ", errors.toString());
            }
        });
    }


    private void showMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private class MyTextWatcher implements TextWatcher{

        private View view;

        public MyTextWatcher(View view){
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
            switch (view.getId()){
                case R.id.input_old_password:
                    validateEditText(oldPasswordET, inputLayoutOldPassword, R.string.err_old_password);
                    break;

                case R.id.input_new_password:
                    validateEditText(newPasswordET, inputLayoutNewPassword, R.string.err_new_password);
                    break;

                case R.id.input_confirm_password:
                    validateEditText(confirmPasswordET, inputLayoutConfirmPassword, R.string.err_confirm_password);
                    break;

            }
        }
    }

    private boolean validateEditText(EditText editText, TextInputLayout textInputLayout, int errorResource) {
        String value = editText.getText().toString().trim();
        if (value.isEmpty()){
            textInputLayout.setError(getResources().getString(errorResource));
            requestFocus(editText);
            return false;
        }else{
            textInputLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(EditText view) {
        if (view.requestFocus()){
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
