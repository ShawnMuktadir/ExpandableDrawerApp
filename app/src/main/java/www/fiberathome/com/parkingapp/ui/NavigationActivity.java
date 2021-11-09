package www.fiberathome.com.parkingapp.ui;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.databinding.ActivityNavigationBinding;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.user.User;
import www.fiberathome.com.parkingapp.ui.booking.BookingActivity;
import www.fiberathome.com.parkingapp.ui.changePassword.ChangePasswordFragment;
import www.fiberathome.com.parkingapp.ui.followUs.FollowUsActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.law.LawActivity;
import www.fiberathome.com.parkingapp.ui.parking.ParkingActivity;
import www.fiberathome.com.parkingapp.ui.privacyPolicy.PrivacyPolicyActivity;
import www.fiberathome.com.parkingapp.ui.profile.ProfileActivity;
import www.fiberathome.com.parkingapp.ui.settings.SettingsActivity;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class NavigationActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public Toolbar toolbar;

    private Context context;

    ActivityNavigationBinding binding;

    public static Drawable getTintedDrawable(@NonNull Context context, @NonNull Drawable inputDrawable,
                                             @ColorInt int color) {
        Drawable wrapDrawable = DrawableCompat.wrap(inputDrawable);
        DrawableCompat.setTint(wrapDrawable, color);
        DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.SRC_IN);
        return wrapDrawable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.gray_update));
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupNavigationDrawer();

        //setupNavigationDrawerHeader();

        setupNavDrawerMenuItem();

        changeDefaultActionBarDrawerToogleIcon();

        colorizeToolbarOverflowButton(toolbar, context.getResources().getColor(R.color.black));

        setNavMenuItemThemeColors(context.getResources().getColor(R.color.black));

        hideMenuItem(R.id.nav_home);

        hideMenuItem(R.id.nav_notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_change_password) {
            // do your code
            toolbar.setTitle(context.getResources().getString(R.string.title_change_password));
            navigationView.getMenu().getItem(0).setChecked(true);
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, ChangePasswordFragment.newInstance()).commit();
            return true;
        } else if (item.getItemId() == R.id.menu_logout) {
            // do your code
            Preferences.getInstance(context).logout();
            startActivityWithFinish(LoginActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupNavigationDrawerHeader();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNavDrawerItem(R.id.nav_home);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    public void setSubtitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(title);
        }
    }

    protected void setNavDrawerItem(int resId) {
        navigationView.setCheckedItem(resId);
    }

    private void setupNavDrawerMenuItem() {
        Menu menu = navigationView.getMenu();

        MenuItem home = menu.findItem(R.id.nav_home);
        MenuItem parkingSlot = menu.findItem(R.id.nav_parking);
        MenuItem booking = menu.findItem(R.id.nav_booking);
        MenuItem law = menu.findItem(R.id.nav_law);
        MenuItem profile = menu.findItem(R.id.nav_profile);
        MenuItem settings = menu.findItem(R.id.nav_settings);
        MenuItem getDiscount = menu.findItem(R.id.nav_get_discount);
        MenuItem ratingReview = menu.findItem(R.id.nav_rating_review);
        MenuItem followUs = menu.findItem(R.id.nav_follow_us);
        MenuItem privacyPolicy = menu.findItem(R.id.nav_privacy_policy);
        MenuItem share = menu.findItem(R.id.nav_share);
    }

    protected void closeNavDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }
    }

    private void setupNavigationDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.openDrawer, R.string.closeDrawer);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //Called when a drawer's position changes.

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //Called when a drawer has settled in a completely open state.
                //The drawer is interactive at this point.
                // If you have 2 drawers (left and right) you can distinguish
                // them by using id of the drawerView. int id = drawerView.getId();
                // id will be your layout's id: for example R.id.left_drawer
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Called when a drawer has settled in a completely closed state.
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Called when the drawer motion state changes. The new state will be one of STATE_IDLE, STATE_DRAGGING or STATE_SETTLING.
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(drawerLayout.getWindowToken(), 0);
            }
        });
        setDrawerState(true);
    }

    public void setNavMenuItemThemeColors(int color) {
        //Setting default colors for menu item Text and Icon
        int navDefaultTextColor = Color.parseColor("#000000");

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

        navigationView.setItemTextColor(navMenuTextList);
    }

    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
            actionBarDrawerToggle.syncState();
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(null);
        }
    }

    private void setupNavigationDrawerHeader() {
        User user = Preferences.getInstance(context).getUser();
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserFullName = headerView.findViewById(R.id.header_fullname);
        TextView tvUserVehicleNo = headerView.findViewById(R.id.header_vehicle_no);
        ImageView ivUserProfile = headerView.findViewById(R.id.header_profile_pic);

        tvUserFullName.setText(TextUtils.getInstance().capitalizeFirstLetter(user.getFullName()));

        StringBuilder stringBuilder = new StringBuilder();
        if (user.getVehicleNo() != null) {
            if (TextUtils.getInstance().isNumeric(user.getVehicleNo())) {
                stringBuilder.append(context.getResources().getString(R.string.vehicle_no)).append(" ").append("^").append(user.getVehicleNo());
                tvUserVehicleNo.setText(stringBuilder.toString());
            } else {
                stringBuilder.append(context.getResources().getString(R.string.vehicle_no)).append(" ").append(user.getVehicleNo());
                tvUserVehicleNo.setText(stringBuilder.toString());
            }
        }

        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.blank_profile)
                .error(R.drawable.blank_profile);

        String url;
        if (!user.getImage().endsWith(".jpg")) {
            url = AppConfig.IMAGES_URL + user.getImage() + ".jpg";
        } else {
            url = AppConfig.IMAGES_URL + user.getImage();
        }
        Timber.e("user profile photo url -> %s", url);
        Glide.with(this).load(url).apply(requestOptions).override(200, 200).into(ivUserProfile);


        String text = user.getMobileNo() + " - ";
        text = text + user.getVehicleNo();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            //QRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void changeDefaultActionBarDrawerToogleIconWithBackButton() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(context.getResources().getColor(R.color.black));
        actionBarDrawerToggle.setToolbarNavigationClickListener(v -> {
            onBackPressed();
            //actionBarDrawerToggle.syncState();
        });
    }

    private void hideMenuItem(int id) {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(id).setVisible(false);
    }

    public void changeDefaultActionBarDrawerToogleIcon() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(context.getResources().getColor(R.color.transparent_black));
        actionBarDrawerToggle.setToolbarNavigationClickListener(v -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void setActionToolBarVisibilityGone() {
        toolbar.setNavigationIcon(null);
        toolbar.setTitle("");
        binding.appBarMain.toolbarTitle.setVisibility(View.VISIBLE);
        binding.appBarMain.linearLayoutToolbarTime.setVisibility(View.GONE);
    }

    //toolbar menu overflow icon change method
    public static boolean colorizeToolbarOverflowButton(@NonNull Toolbar toolbar, @ColorInt int color) {
        final Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon == null) return false;
        toolbar.setOverflowIcon(getTintedDrawable(toolbar.getContext(), overflowIcon, color));
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeNavDrawer();

        navigationView.setCheckedItem(item.getItemId());

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                startActivity(HomeActivity.class);
                break;

            case R.id.nav_parking:
                if (ConnectivityUtils.getInstance().checkInternet(context) && isGPSEnabled()) {
                    startActivity(ParkingActivity.class);
                    // Remove any previous data from SharedData's sensor Data Parking Information
                    SharedData.getInstance().setSensorArea(null);
                } else {
                    ToastUtils.getInstance().showToastMessage(context, context.getResources().getString(R.string.connect_to_internet_gps));
                }
                break;

            case R.id.nav_booking:
                startActivity(BookingActivity.class);
                break;

            case R.id.nav_law:
                startActivity(LawActivity.class);
                break;

            case R.id.nav_profile:
                startActivity(ProfileActivity.class);
                break;

            case R.id.nav_settings:
                startActivity(SettingsActivity.class);
                break;

            case R.id.nav_rating_review:
                openAppRating(context);
                break;

            case R.id.nav_follow_us:
                startActivity(FollowUsActivity.class);
                break;

            case R.id.nav_privacy_policy:
                startActivity(PrivacyPolicyActivity.class);
                break;

            case R.id.nav_share:
                navigationView.getMenu().getItem(0).setChecked(true);
                shareApp();
                break;

            default:
                return false;
        }

        return true;
    }

    @SuppressWarnings("rawtypes")
    public void startActivity(Class activityClass) {
        startActivity(new Intent(getApplicationContext(), activityClass));
    }

    @SuppressWarnings("rawtypes")
    public void startActivity(Class activityClass, Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @SuppressWarnings("rawtypes")
    public void startActivityWithFinishBundle(Class activityClass, Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), activityClass);
        intent.putExtras(bundle);
        startActivity(intent);
        finishAffinity();
    }

    @SuppressWarnings("rawtypes")
    public void startActivityWithFinishAffinity(Class activityClass) {
        startActivity(new Intent(getApplicationContext(), activityClass));
        finishAffinity();
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "LOCC Smart Parking App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "LOCC Smart Parking App Link\n\n" +
                "https://play.google.com/store/apps/details?id=www.fiberathome.com.parkingapp");
        startActivity(Intent.createChooser(shareIntent, "Share Via:"));
    }

    @SuppressLint("ObsoleteSdkInt")
    public void setAppLocale(String localeCode) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(localeCode.toLowerCase()));
        } else {
            config.locale = new Locale(localeCode.toLowerCase());
        }
        resources.updateConfiguration(config, dm);
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void openAppRating(Context context) {
        // you can also use BuildConfig.APPLICATION_ID/appId
        String appId = context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;
            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            context.startActivity(webIntent);
        }
    }
}
