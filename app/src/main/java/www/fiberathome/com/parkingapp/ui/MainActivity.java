package www.fiberathome.com.parkingapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionAfterButtonClickEvent;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.ui.fragments.BookingDetailsFragment;
import www.fiberathome.com.parkingapp.ui.fragments.ChangePasswordFragment;
import www.fiberathome.com.parkingapp.ui.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.ui.fragments.LawFragment;
import www.fiberathome.com.parkingapp.ui.fragments.NotificationFragment;
import www.fiberathome.com.parkingapp.ui.parking.ParkingFragment;
import www.fiberathome.com.parkingapp.ui.fragments.ProfileFragment;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.utils.SharedPreManager;

public class MainActivity extends AppCompatActivity implements MainView, BottomNavigationView.OnNavigationItemSelectedListener, DialogForm.DialogFormListener {

    //    NavigationView.OnNavigationItemSelectedListener,
    private Context context;
    private Toolbar toolbar;
    private NavigationView navigationView;
//    private DrawerLayout drawerLayout;

    private ImageView QRCode;
    private TextView userFullName;
    private TextView userVehicleNo;
    private ImageView userProfilePic;
    private BottomNavigationView bottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getting bottom navigation view and attaching the listener
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
//            navigationView.setCheckedItem(R.id.nav_home);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
//        final Drawable upArrow = context.getResources().getDrawable(R.drawable.ic_action_back_white);
//        upArrow.setColorFilter(context.getResources().getColor(R.color.whiteColor), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);
//        try {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
//        } catch (NullPointerException ignore) {
//        }

        // check if sharedPreference has any value
        // Check user is logged in
        if (!SharedPreManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        colorizeToolbarOverflowButton(toolbar, context.getResources().getColor(R.color.whiteColor));
//        showMessage("Done");
//        navigationView = findViewById(R.id.nav_view_fragment);
        // getting User Information
        User user = SharedPreManager.getInstance(this).getUser();
//        View headerView = navigationView.getHeaderView(0);

//        userFullName = headerView.findViewById(R.id.header_fullname);
//        userVehicleNo = headerView.findViewById(R.id.header_vehicle_no);
//        userProfilePic = headerView.findViewById(R.id.header_profile_pic);

        // update user fullname & vehicle no information.
//        userFullName.setText(user.getFullName());
//        userVehicleNo.setText("Vehicle No: " + user.getVehicleNo());

        String url = AppConfig.IMAGES_URL + user.getProfilePic() + ".jpg";
        Timber.e(url);
//        Glide.with(this).load(url).placeholder(R.drawable.blank_profile_pic).into(userProfilePic);  //Todo
//

//        QRCode = headerView.findViewById(R.id.header_qrcode);

//        QRCode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Close the Drawer first
////                drawerLayout.closeDrawer(GravityCompat.START);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new QRCodeFragment()).commit();
//            }
//        });


        //show QR on create
        String text = user.getMobileNo() + " - ";
        text = text + user.getVehicleNo();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
//            QRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
//        drawerLayout = findViewById(R.id.drawer_layout);

//        NavigationView navigationView = findViewById(R.id.nav_view_fragment);
//        navigationView.setNavigationItemSelectedListener(this);


//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

//        drawerLayout.addDrawerListener(toggle);

        // swap gesture menu toggle
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


//        toggle.syncState();
//        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));

        // To Open This Fragment When the app Run

    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

//        if (menu instanceof MenuBuilder) {
//            ((MenuBuilder) menu).setOptionalIconsVisible(true);
//        }
//
//        //change menu icon color programmatically & changing a particular icon of one of menus, use break in the for loop
//        for (int i = 0; i < menu.size(); i++) {
//            Drawable drawable = menu.getItem(i).getIcon();
//            if (drawable != null) {
//                drawable.mutate();
//                drawable.setColorFilter(context.getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
//                break;
//            }
//        }
        return true;
    }

    //toolbar menu overflow icon change method
    public static boolean colorizeToolbarOverflowButton(@NonNull Toolbar toolbar, @ColorInt int color) {
        final Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon == null) return false;
        toolbar.setOverflowIcon(getTintedDrawable(toolbar.getContext(), overflowIcon, color));
        return true;
    }

    public static Drawable getTintedDrawable(@NonNull Context context, @NonNull Drawable inputDrawable, @ColorInt int color) {
        Drawable wrapDrawable = DrawableCompat.wrap(inputDrawable);
        DrawableCompat.setTint(wrapDrawable, color);
        DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.SRC_IN);
        return wrapDrawable;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_change_password) {
            // do your code
            toolbar.setTitle("Change Password");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChangePasswordFragment()).commit();
            return true;
        } else if (item.getItemId() == R.id.menu_logout) {
            // do your code
            SharedPreManager.getInstance(this).logout();
            Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intentLogout);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                toolbar.setTitle("Home");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;

            case R.id.nav_parking:
                toolbar.setTitle("Parking");
//                ParkingPresenter parkingPresenter = new ParkingPresenterImpl(context,getSupportFragmentManager());
//                HomeFragment homeFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ParkingFragment()).commit();
                break;

            case R.id.nav_law:
                toolbar.setTitle("Law");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LawFragment()).commit();
                break;

            case R.id.nav_notification:
                toolbar.setTitle("Notification");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).commit();
                break;


            case R.id.nav_profile:
                toolbar.setTitle("Profile");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;
//            case R.id.nav_qrcode:
//                toolbar.setTitle("QR Code");
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new QRCodeFragment()).commit();
//                break;

//            case R.id.nav_blank_map:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new BlankDialogMapFragment()).commit();
//
//                break;

//            case R.id.nav_change_password:
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChangePasswordFragment()).commit();
//
//                break;

//            case R.id.nav_logout:
//                showMessage("Logout Successfully");
//                SharedPreManager.getInstance(this).logout();
//                Intent intentLogout = new Intent(MainActivity.this, LoginActivity.class);
//                startActivity(intentLogout);
//                finish();
//                break;

        }
//        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GetDirectionEvent event) {
//        Toast.makeText(getApplicationContext(), event.message, Toast.LENGTH_SHORT).show();
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 2s = 2000ms
                bottomNavigationView.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new SetMarkerEvent(event.location));
            }
        }, 1000);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    private void showMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        ApplicationUtils.showExitDialog(this);
//        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//        ApplicationUtils.showMessageDialog("Please click BACK again to exit", context);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void applyTexts(String username, String password, String mobile) {

        Timber.e(username);
        Timber.e(password);
        Timber.e(mobile);
    }

    public void replaceFragmentWithBundle(String s) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction t1 = fragmentManager.beginTransaction();
        BookingDetailsFragment bookingDetailsFragment = new BookingDetailsFragment();

        Bundle b2 = new Bundle();
        b2.putString("s", s);

        bookingDetailsFragment.setArguments(b2);
//        t1.replace(R.id.frame1, bookingDetailsFragment);
//        t1.commit();

        // Move the MainActivity with Map Fragement
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, bookingDetailsFragment).commit();
    }

    @Override
    public void setTitle() {
        toolbar.setTitle(context.getResources().getString(R.string.home));
    }
}
