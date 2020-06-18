package www.fiberathome.com.parkingapp.view.activity.forgetPassword;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import www.fiberathome.com.parkingapp.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private EditText phoneNumberET;
    private Button btnSendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
    }
}
