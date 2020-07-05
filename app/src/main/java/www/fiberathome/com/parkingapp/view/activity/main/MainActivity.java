package www.fiberathome.com.parkingapp.view.activity.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.util.Objects;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.eventBus.GetDirectionEvent;
import www.fiberathome.com.parkingapp.eventBus.SetMarkerEvent;
import www.fiberathome.com.parkingapp.model.User;
import www.fiberathome.com.parkingapp.view.booking.newBooking.NewBookedFragment;
import www.fiberathome.com.parkingapp.view.booking.listener.FragmentChangeListener;
import www.fiberathome.com.parkingapp.view.dialog.DialogForm;
import www.fiberathome.com.parkingapp.view.activity.login.LoginActivity;
import www.fiberathome.com.parkingapp.view.booking.oldBooking.OldBookingDetailsFragment;
import www.fiberathome.com.parkingapp.view.followUs.FollowUsFragment;
import www.fiberathome.com.parkingapp.view.fragments.ChangePasswordFragment;
import www.fiberathome.com.parkingapp.view.fragments.HomeFragment;
import www.fiberathome.com.parkingapp.view.fragments.LawFragment;
import www.fiberathome.com.parkingapp.view.fragments.NotificationFragment;
import www.fiberathome.com.parkingapp.view.getDiscount.GetDiscountFragment;
import www.fiberathome.com.parkingapp.view.parking.ParkingFragment;
import www.fiberathome.com.parkingapp.view.fragments.ProfileFragment;
import www.fiberathome.com.parkingapp.base.AppConfig;
import www.fiberathome.com.parkingapp.view.privacyPolicy.PrivacyPolicyFragment;
import www.fiberathome.com.parkingapp.view.ratingReview.RatingReviewFragment;
import www.fiberathome.com.parkingapp.view.settings.SettingsFragment;
import www.fiberathome.com.parkingapp.view.share.ShareFragment;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;
import www.fiberathome.com.parkingapp.data.preference.SharedData;
import www.fiberathome.com.parkingapp.data.preference.SharedPreManager;

public class MainActivity extends AppCompatActivity implements MainView, NavigationView.OnNavigationItemSelectedListener, DialogForm.DialogFormListener, FragmentChangeListener {
    // BottomNavigationView.OnNavigationItemSelectedListener

    private Context context;
    public Toolbar toolbar;
    public NavigationView navigationView;
    private DrawerLayout drawerlayoutMain;

    private ImageView QRCode;
    private TextView userFullName;
    private TextView userVehicleNo;
    private ImageView userProfilePic;
    private BottomNavigationView bottomNavigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerlayoutMain = findViewById(R.id.drawerlayoutMain);
        navigationView = findViewById(R.id.nav_view_fragment);
        setupNavigationDrawer();
        //hide home menu
        hideHomeMenuItem();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        setupNavigationHeader();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            navigationView.getMenu().getItem(0).setChecked(true);
//            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }

        if (!SharedPreManager.getInstance(this).isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        colorizeToolbarOverflowButton(toolbar, context.getResources().getColor(R.color.white));
    }

    private void setupNavigationHeader() {
        User user = SharedPreManager.getInstance(this).getUser();
        View headerView = navigationView.getHeaderView(0);
        userFullName = headerView.findViewById(R.id.header_fullname);
        userVehicleNo = headerView.findViewById(R.id.header_vehicle_no);
        userProfilePic = headerView.findViewById(R.id.header_profile_pic);

        // update user fullname & vehicle no information.
        userFullName.setText(ApplicationUtils.capitalize(user.getFullName()));
        userVehicleNo.setText("Vehicle No: " + user.getVehicleNo());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.blank_profile_pic)
                .error(R.drawable.ic_image_person);
        String url = AppConfig.IMAGES_URL + user.getProfilePic() + ".jpg";
        Timber.e("user profile photo url -> %s", url);
        Glide.with(this).load(url).apply(options).override(200, 200).into(userProfilePic);
//        Glide.with(this).load(url).placeholder(R.drawable.blank_profile_pic).into(userProfilePic);  //Todo

//Qr Code Code

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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupNavigationDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerlayoutMain, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerlayoutMain.addDrawerListener(actionBarDrawerToggle);
        drawerlayoutMain.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                //Called when a drawer's position changes.

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getId();
                // id will be your layout's id: for example R.id.left_drawer
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                // Called when a drawer has settled in a completely closed state.
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(drawerlayoutMain.getWindowToken(), 0);
            }
        });
        actionBarDrawerToggle.syncState();
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(context.getResources().getColor(android.R.color.white));
//        toolbar.setNavigationIcon(R.drawable.camera_icon);
        //drawer menu text color while pressed
        setNavMenuItemThemeColors(getResources().getColor(R.color.gray));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        setDrawerState(true);
    }

    public void setNavMenuItemThemeColors(int color) {
//        Setting default colors for menu item Text and Icon
        int navDefaultTextColor = Color.parseColor("#000000");
//        int navDefaultIconColor = Color.parseColor("#25A848");

        //Defining ColorStateList for menu item Text
        ColorStateList navMenuTextList = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed}
                },
                new int[]{
                        color,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor,
                        navDefaultTextColor
                }
        );

        //Defining ColorStateList for menu item Icon
//        ColorStateList navMenuIconList = new ColorStateList(
//                new int[][]{
//                        new int[]{android.R.attr.state_checked},
//                        new int[]{android.R.attr.state_enabled},
//                        new int[]{android.R.attr.state_pressed},
//                        new int[]{android.R.attr.state_focused},
//                        new int[]{android.R.attr.state_pressed}
//                },
//                new int[]{
//                        color,
//                        navDefaultIconColor,
//                        navDefaultIconColor,
//                        navDefaultIconColor,
//                        navDefaultIconColor
//                }
//        );

        navigationView.setItemTextColor(navMenuTextList);
//        mainNavigationView.setItemIconTintList(navMenuIconList);
    }

    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            drawerlayoutMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
            actionBarDrawerToggle.syncState();
        } else {
            drawerlayoutMain.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(null);
        }
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
        drawerlayoutMain.closeDrawers();
        navigationView.setCheckedItem(item.getItemId());
        switch (item.getItemId()) {
            case R.id.nav_home:
                toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                SharedData.getInstance().setOnConnectedLocation(null);
                break;

            case R.id.nav_parking:
                toolbar.setTitle(context.getResources().getString(R.string.parking));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ParkingFragment()).commit();
                // Remove any previous data from SharedData's sensor Data Parking Information
                SharedData.getInstance().setSensorArea(null);
                break;

            case R.id.nav_booking:
                toolbar.setTitle(context.getResources().getString(R.string.bookings));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewBookedFragment()).commit();
                break;

            case R.id.nav_law:
                toolbar.setTitle(context.getResources().getString(R.string.law));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LawFragment()).commit();
                break;

            case R.id.nav_notification:
                toolbar.setTitle(context.getResources().getString(R.string.notification));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).commit();
                break;

            case R.id.nav_profile:
                toolbar.setTitle(context.getResources().getString(R.string.profile));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                break;

            case R.id.nav_settings:
                toolbar.setTitle(context.getResources().getString(R.string.action_settings));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;

            case R.id.nav_get_discount:
                toolbar.setTitle(context.getResources().getString(R.string.get_discount));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GetDiscountFragment()).commit();
                break;

            case R.id.nav_rating_review:
                toolbar.setTitle(context.getResources().getString(R.string.give_review_rating));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RatingReviewFragment()).commit();
                break;

            case R.id.nav_follow_us:
                toolbar.setTitle(context.getResources().getString(R.string.follow_us));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FollowUsFragment()).commit();
                break;

            case R.id.nav_privacy_policy:
                toolbar.setTitle(context.getResources().getString(R.string.privacy_policy));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PrivacyPolicyFragment()).commit();
                break;

            case R.id.nav_share:
                toolbar.setTitle(context.getResources().getString(R.string.share));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ShareFragment()).commit();
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
        return true;
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

    @Override
    public void onBackPressed() {
        if (exit) {
            super.onBackPressed();
            return;
        }

        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentByTag(context.getResources().getString(R.string.welcome_to_locc_parking));
            if (fragment != null) {
                if (fragment.isVisible()) {
                    this.exit = true;
                    ApplicationUtils.showExitDialog(this);
//                    Toast.makeText(this, "Press Back again to Exit", Toast.LENGTH_SHORT).show();
                }
            } else {
                fragment = HomeFragment.class.newInstance();
                getFragmentManager().popBackStack();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment, context.getResources().getString(R.string.welcome_to_locc_parking)).commit();
                drawerlayoutMain.closeDrawers();
                navigationView.getMenu().getItem(0).setChecked(true);
                toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
                if (SharedData.getInstance().getOnConnectedLocation() != null) {
                    HomeFragment.class.newInstance().animateCamera(SharedData.getInstance().getOnConnectedLocation());
//                    HomeFragment.class.newInstance().fetchSensors(SharedData.getInstance().getOnConnectedLocation());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                exit = false;
            }
        }, 2000);
    }

    private void showMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean exit = false;

    @Override
    public void applyTexts(String username, String password, String mobile) {

        Timber.e(username);
        Timber.e(password);
        Timber.e(mobile);
    }

    public void replaceFragmentWithBundle(String s) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction t1 = fragmentManager.beginTransaction();
        OldBookingDetailsFragment oldBookingDetailsFragment = new OldBookingDetailsFragment();

        Bundle b2 = new Bundle();
        b2.putString("s", s);

        oldBookingDetailsFragment.setArguments(b2);
//        t1.replace(R.id.frame1, bookingDetailsFragment);
//        t1.commit();

        // Move the MainActivity with Map Fragement
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, oldBookingDetailsFragment).commit();
    }

//    @Override
//    public void setTitle() {
//        toolbar.setTitle(context.getResources().getString(R.string.home));
//    }

    public void replaceFragment() {
        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GetDirectionEvent event) {
        navigationView.getMenu().getItem(0).setChecked(true);
        toolbar.setTitle(context.getResources().getString(R.string.welcome_to_locc_parking));
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Timber.e("GetDirectionEvent MainActivity called");
                // Do something after 2s = 2000ms
                EventBus.getDefault().post(new SetMarkerEvent(event.location));
            }
        }, 2000);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.e("onActivityResult MainActivity called");
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void fragmentChange(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void hideHomeMenuItem() {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_home).setVisible(false);
    }
}
