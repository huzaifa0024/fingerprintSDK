package com.fprint.fingerprintaar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by GlobalCharge.
 */
public class ViewUtils {

    private static final int WIDTH_INDEX = 0;
    private static final int HEIGHT_INDEX = 1;

    public static int[] getScreenSize(Context context) {
        int[] widthHeight = new int[2];
        widthHeight[WIDTH_INDEX] = 0;
        widthHeight[HEIGHT_INDEX] = 0;

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        widthHeight[WIDTH_INDEX] = size.x;
        widthHeight[HEIGHT_INDEX] = size.y;

        if (!isScreenSizeRetrieved(widthHeight)) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            widthHeight[0] = metrics.widthPixels;
            widthHeight[1] = metrics.heightPixels;
        }

        // Last defense. Use deprecated API that was introduced in lower than API 13
        if (!isScreenSizeRetrieved(widthHeight)) {
            widthHeight[0] = display.getWidth(); // deprecated
            widthHeight[1] = display.getHeight(); // deprecated
        }

        return widthHeight;
    }

    private static boolean isScreenSizeRetrieved(int[] widthHeight) {
        return widthHeight[WIDTH_INDEX] != 0 && widthHeight[HEIGHT_INDEX] != 0;
    }

    public static void initToolbar(AppCompatActivity context, Toolbar toolbar, int homeButton, int title, int menu, Toolbar.OnMenuItemClickListener menuItemClickListener) {

        initToolbar(context.getApplicationContext(), toolbar, homeButton, title, menu, menuItemClickListener);

        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

    }

    public static void initToolbar(final Context context, Toolbar toolbar, int homeButton, int title, int menu, Toolbar.OnMenuItemClickListener menuItemClickListener) {


        final Drawable upArrow = context.getResources().getDrawable(homeButton);
        upArrow.setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);

        toolbar.setNavigationIcon(upArrow);
        toolbar.setTitle(context.getString(title));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setBackgroundColor(Color.parseColor("#f9d649"));

        if (menu > 0) {
            toolbar.inflateMenu(menu);
            toolbar.setOnMenuItemClickListener(menuItemClickListener);

            Menu thisMenu = toolbar.getMenu();

            for (int i = 0; i < thisMenu.size(); i++) {
                Drawable drawable = thisMenu.getItem(i).getIcon();
                if (drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
                }
            }
        }

    }
    public static void initToolbar(AppCompatActivity context, Toolbar toolbar, int homeButton, String title, int menu, Toolbar.OnMenuItemClickListener menuItemClickListener) {

        initToolbar(context.getApplicationContext(), toolbar, homeButton, title, menu, menuItemClickListener);

        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }

    }

    public static void initToolbar(final Context context, Toolbar toolbar, int homeButton, String title, int menu, Toolbar.OnMenuItemClickListener menuItemClickListener) {


        toolbar.setNavigationIcon(homeButton);
        toolbar.setTitle(title);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setBackgroundColor(Color.parseColor("#009688"));

        if (menu > 0) {
            toolbar.inflateMenu(menu);
            toolbar.setOnMenuItemClickListener(menuItemClickListener);
        }
    }


    public static void setStatusBar(Activity activity, int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            try {

                Window window = activity.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(color);
            }catch (Exception e){
            }
        }
    }
}
