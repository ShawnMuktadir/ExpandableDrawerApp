package www.fiberathome.com.parkingapp.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
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

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.model.api.AppConfig;
import www.fiberathome.com.parkingapp.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.model.data.preference.SharedPreManager;
import www.fiberathome.com.parkingapp.model.loginUser.User;
import www.fiberathome.com.parkingapp.ui.booking.newBooking.BookingActivity;
import www.fiberathome.com.parkingapp.ui.changePassword.ChangePasswordFragment;
import www.fiberathome.com.parkingapp.ui.followUs.FollowUsActivity;
import www.fiberathome.com.parkingapp.ui.getDiscount.GetDiscountActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.law.LawActivity;
import www.fiberathome.com.parkingapp.ui.parking.ParkingActivity;
import www.fiberathome.com.parkingapp.ui.privacyPolicy.PrivacyPolicyActivity;
import www.fiberathome.com.parkingapp.ui.profile.ProfileActivity;
import www.fiberathome.com.parkingapp.ui.ratingReview.RatingReviewActivity;
import www.fiberathome.com.parkingapp.ui.settings.SettingsActivity;
import www.fiberathome.com.parkingapp.ui.share.ShareActivity;
import www.fiberathome.com.parkingapp.ui.signIn.LoginActivity;
import www.fiberathome.com.parkingapp.utils.ApplicationUtils;

@SuppressLint("Registered")
public class NavigationActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.tvTimeToolbar)
    public TextView tvTimeToolbar;
    @BindView(R.id.linearLayoutToolbarTime)
    public LinearLayout linearLayoutToolbarTime;

    public DrawerLayout drawerLayout;
    public NavigationView navigationView;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public Toolbar toolbar;

    private Unbinder unbinder;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        unbinder = ButterKnife.bind(this);
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(context.getResources().getColor(R.color.lightBg));
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupNavigationDrawer();

        setupNavigationDrawerHeader();

        setupNavDrawerMenuItem();

        changeDefaultActionBarDrawerToogleIcon();

        colorizeToolbarOverflowButton(toolbar, context.getResources().getColor(R.color.black));

        hideMenuItem(R.id.nav_home);
        hideMenuItem(R.id.nav_notification);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeNavDrawer();

        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                startActivity(HomeActivity.class);
                break;
            case R.id.nav_parking:
                startActivity(ParkingActivity.class);
                // Remove any previous data from SharedData's sensor Data Parking Information
                SharedData.getInstance().setSensorArea(null);
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
            case R.id.nav_get_discount:
                startActivity(GetDiscountActivity.class);
                break;
            case R.id.nav_rating_review:
                startActivity(RatingReviewActivity.class);
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
            SharedPreManager.getInstance(this).logout();
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
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
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
                inputMethodManager.hideSoftInputFromWindow(drawerLayout.getWindowToken(), 0);
            }
        });
        setDrawerState(true);
    }

    public void setNavMenuItemThemeColors(int color) {
//        Setting default colors for menu item Text and Icon
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
        User user = SharedPreManager.getInstance(this).getUser();
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserFullName = headerView.findViewById(R.id.header_fullname);
        TextView tvUserVehicleNo = headerView.findViewById(R.id.header_vehicle_no);
        ImageView ivUserProfile = headerView.findViewById(R.id.header_profile_pic);

        tvUserFullName.setText(ApplicationUtils.capitalize(user.getFullName()));

        StringBuilder stringBuilder = new StringBuilder();
        if (user.getVehicleNo() != null) {
            stringBuilder.append("Vehicle No: ").append(user.getVehicleNo());
        }
        tvUserVehicleNo.setText(stringBuilder.toString());

        RequestOptions requestOptions = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.blank_profile)
                .error(R.drawable.blank_profile);
        String url = AppConfig.IMAGES_URL + user.getProfilePic() + ".jpg";
        Timber.e("user profile photo url -> %s", url);
        Glide.with(this).load(url).apply(requestOptions).override(200, 200).into(ivUserProfile);

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

    //toolbar menu overflow icon change method
    public static boolean colorizeToolbarOverflowButton(@NonNull Toolbar toolbar, @ColorInt int color) {
        final Drawable overflowIcon = toolbar.getOverflowIcon();
        if (overflowIcon == null) return false;
        toolbar.setOverflowIcon(getTintedDrawable(toolbar.getContext(), overflowIcon, color));
        return true;//
    }

    public static Drawable getTintedDrawable(@NonNull Context context, @NonNull Drawable inputDrawable, @ColorInt int color) {
        Drawable wrapDrawable = DrawableCompat.wrap(inputDrawable);
        DrawableCompat.setTint(wrapDrawable, color);
        DrawableCompat.setTintMode(wrapDrawable, PorterDuff.Mode.SRC_IN);
        return wrapDrawable;
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "LOCC Smart Parking App");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "LOCC Smart Parking App Link\n\n" + "https://163.47.157.195/parkingapp/");
        startActivity(Intent.createChooser(shareIntent, "Share Via:"));
    }
}
