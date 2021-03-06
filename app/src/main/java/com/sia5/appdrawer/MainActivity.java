package com.sia5.appdrawer;

import android.app.AlertDialog;
import android.app.StatusBarManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    OnSwipeTouchListener onSwipeHomepage;
    OnSwipeTouchListener OnSwipeAppList;
    private List<AppList> installedApps;
    private AppAdapter installedAppAdapter;
    GridView userInstalledApps;
    private static int loadHomepage = 1;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private View grid_element;

    // layout-urile pe care se vor face modificarile
    RelativeLayout AppListLayout;
    RelativeLayout HomeLayout;

    public RelativeLayout getAppListLayout(){
        return AppListLayout;
    }
    public RelativeLayout getHomeLayout(){
        return HomeLayout;
    }

    // parametrii pt lista de aplicatii
    RelativeLayout.LayoutParams param_list_show = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    RelativeLayout.LayoutParams param_list_hide = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
    // parametrii pentru homepage
    RelativeLayout.LayoutParams param_home_show = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    RelativeLayout.LayoutParams param_home_hide = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInstalledApps = (GridView) findViewById(R.id.installed_app_list);

        installedApps = getInstalledApps();
        installedAppAdapter = new AppAdapter(MainActivity.this, installedApps);
        userInstalledApps.setAdapter(installedAppAdapter);
        userInstalledApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                String[] colors = {" Open App", " App Info"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose Action")
                        .setItems(colors, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position of the selected item
                                if (which==0){
                                    Intent intent = getPackageManager().getLaunchIntentForPackage(installedApps.get(i).packages);
                                    if(intent != null){
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, installedApps.get(i).packages + " Error, Please Try Again...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                if (which==1){
                                    Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    intent.setData(Uri.parse("package:" + installedApps.get(i).packages));
                                    Toast.makeText(MainActivity.this, installedApps.get(i).packages, Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                }
                            }
                        });
                builder.show();
            }
        });

        onSwipeHomepage = new OnSwipeTouchListener(this, findViewById(R.id.viewpager));
        //onSwipeHomepage = new OnSwipeTouchListener(this, findViewById(R.id.installed_app_list));

        //userInstalledApps.setOnTouchListener(new MyTouchListener());
        //findViewById(R.id.app_list_layout).setOnDragListener(new MyDragListener());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new CustomPagerAdapter(this));
        View homepage = (View) findViewById(R.id.viewpager);

        HomeLayout = findViewById(R.id.viewpager_layout);
        AppListLayout = findViewById(R.id.app_list_layout);
        grid_element = findViewById(R.id.grid1_1);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(loadHomepage == 0){
            AppListLayout.setLayoutParams(param_list_hide);
            HomeLayout.setLayoutParams(param_home_show);
            loadHomepage = 1;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            grid_element.setScaleX(mScaleFactor);
            grid_element.setScaleY(mScaleFactor);
            return true;
        }
    }

    public class AppAdapter extends BaseAdapter {

        public LayoutInflater layoutInflater;
        public List<AppList> listStorage;

        public AppAdapter(Context context, List<AppList> customizedListView) {
            layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listStorage = customizedListView;
        }

        @Override
        public int getCount() {
            return listStorage.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder listViewHolder;
            if(convertView == null){
                listViewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.installed_app_list, parent, false);

                listViewHolder.textInListView = (TextView)convertView.findViewById(R.id.list_app_name);
                listViewHolder.imageInListView = (ImageView) convertView.findViewById(R.id.app_icon);
                convertView.setTag(listViewHolder);
            }else{
                listViewHolder = (ViewHolder)convertView.getTag();
            }
            listViewHolder.textInListView.setText(listStorage.get(position).getName());
            listViewHolder.imageInListView.setImageDrawable(listStorage.get(position).getIcon());
            convertView.setOnTouchListener(new MyTouchListener());
            return convertView;
        }

        class ViewHolder{
            TextView textInListView;
            ImageView imageInListView;
        }
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        String standard_package = "com.android";
        String package_info = pkgInfo.packageName;
        String[] package_string_array = package_info.split("\\.");
        if(package_string_array.length < 2){
            return true;
        }else{
            String package_source = package_string_array[0] + "." + package_string_array[1];
            return standard_package.equals(package_source);
        }
        //return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
    }

    private List<AppList> getInstalledApps() {
        PackageManager pm = getPackageManager();
        List<AppList> apps = new ArrayList<AppList>();
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!isSystemPackage(p))) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                apps.add(new AppList(appName, icon));
            }
        }
        return apps;
    }

    public class AppList {
        private String name;
        Drawable icon;
        private String packages;
        public AppList(String name, Drawable icon) {
            this.name = name;
            this.icon = icon;
        }
        public String getName() {
            return name;
        }
        public Drawable getIcon() {
            return icon;
        }
    }

    public final class OnSwipeTouchListener implements View.OnTouchListener {
        private final GestureDetector gestureDetector;
        private StatusBarManager statusbar;
        onSwipeListener onSwipe;
        Context context;
        OnSwipeTouchListener(Context ctx, View mainView) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
            mainView.setOnTouchListener(this);
            context = ctx;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }
        public class GestureListener extends
                GestureDetector.SimpleOnGestureListener {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
        void onSwipeRight() {
            //Toast.makeText(context, "Swiped Right", Toast.LENGTH_SHORT).show();
            this.onSwipe.swipeRight();
        }
        void onSwipeLeft() {
            //Toast.makeText(context, "Swiped Left", Toast.LENGTH_SHORT).show();
            this.onSwipe.swipeLeft();
        }
        void onSwipeTop() {
            //Toast.makeText(context, "Swiped Up", Toast.LENGTH_SHORT).show();
            if(MainActivity.loadHomepage == 1){
                // daca este pe homepage, se afiseaza lista de aplicatii la swipe up
                HomeLayout.setLayoutParams(param_home_hide);
                AppListLayout.setLayoutParams(param_list_show);
                MainActivity.loadHomepage = 0;
            }
            this.onSwipe.swipeTop();
        }
        void onSwipeBottom() {
            //Toast.makeText(context, "Swiped Down", Toast.LENGTH_SHORT).show();
            if(MainActivity.loadHomepage == 1){
                // daca este pe homepage, se afiseaza bara de notificari
                expandPanel(context);
            }
            this.onSwipe.swipeBottom();
        }
        abstract class onSwipeListener {
            public abstract void swipeRight();
            public abstract void swipeTop();
            public abstract void swipeBottom();
            public abstract void swipeLeft();
        }

        private void expandPanel(Context _context) {
            try {
                Object sbservice = _context.getSystemService("statusbar");
                Class<?> statusbarManager;
                statusbarManager = Class.forName("android.app.StatusBarManager");
                Method showsb;
                if (Build.VERSION.SDK_INT >= 17) {
                    showsb = statusbarManager.getMethod("expandNotificationsPanel");
                } else {
                    showsb = statusbarManager.getMethod("expand");
                }
                showsb.invoke(sbservice);
            } catch (ClassNotFoundException _e) {
                _e.printStackTrace();
            } catch (NoSuchMethodException _e) {
                _e.printStackTrace();
            } catch (IllegalArgumentException _e) {
                _e.printStackTrace();
            } catch (IllegalAccessException _e) {
                _e.printStackTrace();
            } catch (InvocationTargetException _e) {
                _e.printStackTrace();
            }
        }
    }

    public final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }
}


