package www.fiberathome.com.parkingapp.ui.navigation;

import static www.fiberathome.com.parkingapp.data.model.data.Constants.LANGUAGE_BN;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;
import www.fiberathome.com.parkingapp.BuildConfig;
import www.fiberathome.com.parkingapp.R;
import www.fiberathome.com.parkingapp.adapter.ExpandableListAdapter;
import www.fiberathome.com.parkingapp.base.BaseActivity;
import www.fiberathome.com.parkingapp.data.model.MenuModel;
import www.fiberathome.com.parkingapp.data.model.data.preference.LanguagePreferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.Preferences;
import www.fiberathome.com.parkingapp.data.model.data.preference.SharedData;
import www.fiberathome.com.parkingapp.data.model.user.User;
import www.fiberathome.com.parkingapp.databinding.ActivityNavigationBinding;
import www.fiberathome.com.parkingapp.ui.auth.changePassword.ChangePasswordFragment;
import www.fiberathome.com.parkingapp.ui.auth.login.LoginActivity;
import www.fiberathome.com.parkingapp.ui.home.HomeActivity;
import www.fiberathome.com.parkingapp.ui.navigation.law.LawActivity;
import www.fiberathome.com.parkingapp.ui.navigation.parking.ParkingActivity;
import www.fiberathome.com.parkingapp.ui.navigation.privacyPolicy.PrivacyPolicyActivity;
import www.fiberathome.com.parkingapp.ui.navigation.profile.ProfileActivity;
import www.fiberathome.com.parkingapp.ui.navigation.settings.SettingsActivity;
import www.fiberathome.com.parkingapp.ui.reservation.ReservationActivity;
import www.fiberathome.com.parkingapp.utils.ConnectivityUtils;
import www.fiberathome.com.parkingapp.utils.TextUtils;
import www.fiberathome.com.parkingapp.utils.ToastUtils;

@SuppressLint("NonConstantResourceId")
@SuppressWarnings({"unused", "RedundantSuppression"})
public class NavigationActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public ActionBarDrawerToggle actionBarDrawerToggle;

    private BaseActivity context;

    public ActivityNavigationBinding binding;

    ExpandableListAdapter expandableListAdapter;
    List<MenuModel> headerList = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> childList = new HashMap<>();

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
        context = (BaseActivity) this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(context, R.color.white));
        }
        setSupportActionBar(binding.appBarMain.toolbar);
        binding.navView.setNavigationItemSelectedListener(this);

        setupNavigationDrawer();

        //setupNavigationDrawerHeader();

        setupNavDrawerMenuItem();

        changeDefaultActionBarDrawerToogleIcon();

        colorizeToolbarOverflowButton(binding.appBarMain.toolbar, context.getResources().getColor(R.color.black));

        setNavMenuItemThemeColors(context.getResources().getColor(R.color.black));

        //hideMenuItem(R.id.nav_home);

        //hideMenuItem(R.id.nav_notification);

        prepareNavigationMenuData();
        populateNavigationExpandableList();
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
            context.onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.menu_change_password) {
            // do your code
            binding.appBarMain.toolbar.setTitle(context.getResources().getString(R.string.title_change_password));
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
        if (LanguagePreferences.getInstance(context).getAppLanguage().equalsIgnoreCase(LANGUAGE_BN)) {
            setAppLocale(LANGUAGE_BN);
        } else {
            setAppLocale(LanguagePreferences.getInstance(context).getAppLanguage());
        }
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
        binding.navView.setCheckedItem(resId);
    }

    private void setupNavDrawerMenuItem() {
        Menu menu = binding.navView.getMenu();

        MenuItem home = menu.findItem(R.id.nav_home);
        MenuItem parkingSlot = menu.findItem(R.id.nav_parking);
        MenuItem booking = menu.findItem(R.id.nav_booking);
        MenuItem law = menu.findItem(R.id.nav_law);
        MenuItem profile = menu.findItem(R.id.nav_profile);
        MenuItem settings = menu.findItem(R.id.nav_settings);
        MenuItem getDiscount = menu.findItem(R.id.nav_get_discount);
        MenuItem ratingReview = menu.findItem(R.id.nav_rating_review);
        MenuItem aboutUs = menu.findItem(R.id.nav_about_us);
        MenuItem privacyPolicy = menu.findItem(R.id.nav_privacy_policy);
        MenuItem share = menu.findItem(R.id.nav_share);
    }

    protected void closeNavDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawers();
        }
    }

    private void setupNavigationDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.appBarMain.toolbar, R.string.openDrawer, R.string.closeDrawer);

        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        binding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

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
                inputMethodManager.hideSoftInputFromWindow(binding.drawerLayout.getWindowToken(), 0);
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

        binding.navView.setItemTextColor(navMenuTextList);
    }

    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START);
            actionBarDrawerToggle.syncState();
        } else {
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, GravityCompat.START);
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(null);
        }
    }

    private void setupNavigationDrawerHeader() {
        User user = Preferences.getInstance(context).getUser();
        View headerView = binding.navView.getHeaderView(0);
        TextView tvUserFullName = headerView.findViewById(R.id.header_fullname);
        TextView tvUserVehicleNo = headerView.findViewById(R.id.header_vehicle_no);
        ImageView ivUserProfile = headerView.findViewById(R.id.header_profile_pic);
        RelativeLayout navHeaderView = headerView.findViewById(R.id.view_container);

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
        if (user.getImage() != null && !user.getImage().equals("")) {
            try {
                if (!user.getImage().endsWith(".jpg")) {
                    url = BuildConfig.IMAGES_URL + user.getImage() + ".jpg";
                } else {
                    url = BuildConfig.IMAGES_URL + user.getImage();
                }
                Timber.e("user profile photo url -> %s", url);
                Glide.with(this).load(url).apply(requestOptions).override(200, 200).into(ivUserProfile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        navHeaderView.setOnClickListener(v -> startActivity(ProfileActivity.class));
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
        Menu nav_Menu = binding.navView.getMenu();
        //nav_Menu.findItem(id).setVisible(false);
    }

    public void changeDefaultActionBarDrawerToogleIcon() {
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        actionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_drawer);
        actionBarDrawerToggle.getDrawerArrowDrawable().setColor(context.getResources().getColor(R.color.transparent_black));
        actionBarDrawerToggle.setToolbarNavigationClickListener(v -> {
            if (binding.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    public void setActionToolBarVisibilityGone() {
        binding.appBarMain.toolbar.setNavigationIcon(null);
        binding.appBarMain.toolbar.setTitle("");
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
        binding.navView.setCheckedItem(item.getItemId());
        binding.navView.getMenu().setGroupVisible(R.id.grp2, false);
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
                startActivity(ReservationActivity.class);
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

            case R.id.nav_share:
                binding.navView.getMenu().getItem(0).setChecked(true);
                shareApp();
                break;

            default:
                return false;
        }

        return true;
    }

    public void onGroupItemClick(MenuItem item) {
        // One of the group items (using the onClick attribute) was clicked
        // The item parameter passed here indicates which item it is
        // All other menu item clicks are handled by <code><a href="/reference/android/app/Activity.html#onOptionsItemSelected(android.view.MenuItem)">onOptionsItemSelected()</a></code>

        int id = item.getItemId();
        switch (id) {
            case R.id.grp2:
                binding.navView.getMenu().setGroupVisible(R.id.grp2, true);
                break;
        }
    }

    private void prepareNavigationMenuData() {

        MenuModel menuModel;

        menuModel = new MenuModel(context.getResources().getString(R.string.parking), R.drawable.ic_parking_gray, true, false);
        headerList.add(menuModel);
        menuModel = new MenuModel(context.getResources().getString(R.string.bookings), R.drawable.ic_your_books, true, false);
        headerList.add(menuModel);
        menuModel = new MenuModel(context.getResources().getString(R.string.law), R.drawable.ic_laws, true, false);
        headerList.add(menuModel);
        menuModel = new MenuModel(context.getResources().getString(R.string.profile), R.drawable.ic_profile_settings, true, false);
        headerList.add(menuModel);
        menuModel = new MenuModel(context.getResources().getString(R.string.settings), R.drawable.ic_settings, true, false);
        headerList.add(menuModel);
        menuModel = new MenuModel(context.getResources().getString(R.string.give_review_rating), R.drawable.ic_rating, true, false);
        headerList.add(menuModel);
        menuModel = new MenuModel(context.getResources().getString(R.string.share), R.drawable.ic_share, true, false);
        headerList.add(menuModel);

        if (!menuModel.hasChildren) {
            childList.put(menuModel, null);
        }

        menuModel = new MenuModel(context.getResources().getString(R.string.smart_parking), R.drawable.ic_parking_spot, true, true);
        headerList.add(menuModel);
        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel(context.getResources().getString(R.string.privacy_policy), R.drawable.ic_privacy_policy, false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel(context.getResources().getString(R.string.contact_us), R.drawable.ic_phone_msg, false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel(context.getResources().getString(R.string.user_guide), R.drawable.ic_user_guide, false, false);
        childModelsList.add(childModel);

        childModel = new MenuModel(context.getResources().getString(R.string.parallel_parking), R.drawable.ic_car, false, false);
        childModelsList.add(childModel);


        if (menuModel.hasChildren) {
            childList.put(menuModel, childModelsList);
        }
    }

    private void populateNavigationExpandableList() {

        expandableListAdapter = new ExpandableListAdapter(this, headerList, childList);
        binding.expandableListView.setAdapter(expandableListAdapter);

        binding.expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            if (headerList.get(groupPosition).isGroup) {
                if (!headerList.get(groupPosition).hasChildren) {
                    closeNavDrawer();
                    v.setSelected(true);
                    switch (groupPosition) {
                        case 0:
                            startActivity(ParkingActivity.class);
                            break;
                        case 1:
                            startActivity(ReservationActivity.class);
                            break;
                        case 2:
                            startActivity(LawActivity.class);
                            break;
                        case 3:
                            startActivity(ProfileActivity.class);
                            break;
                        case 4:
                            startActivity(SettingsActivity.class);
                            break;
                        case 5:
                            openAppRating(context);
                            break;
                        case 6:
                            shareApp();
                            break;
                    }
                }
            } else {
                Timber.e("else called");
            }
            return false;
        });

        binding.expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {

            if (childList.get(headerList.get(groupPosition)) != null) {
                closeNavDrawer();
                MenuModel model = childList.get(headerList.get(groupPosition)).get(childPosition);
                switch (childPosition) {
                    case 0:
                        startActivity(PrivacyPolicyActivity.class);
                        break;
                    case 1:
                        Intent contactUsIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://smartparking.fiberathome.net/parkingapp/web/contact_us.php"));
                        startActivity(contactUsIntent);
                        break;
                    case 2:
                        Intent userGuideIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://smartparking.fiberathome.net/parkingapp/web/user_guide.php"));
                        startActivity(userGuideIntent);
                        break;
                    case 3:
                        Intent parallelParkingIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/watch?v=l4LcfZeS4qw&ab_channel=ParkingTutorial"));
                        startActivity(parallelParkingIntent);
                        break;
                }
            } else {
                Timber.e("else called");
            }

            return false;
        });
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
