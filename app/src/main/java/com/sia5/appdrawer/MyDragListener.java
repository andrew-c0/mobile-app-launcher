package com.sia5.appdrawer;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MyDragListener implements View.OnDragListener {

    MainActivity main_el = new MainActivity();
    RelativeLayout AppListLayout = main_el.getAppListLayout();
    RelativeLayout HomeLayout = main_el.getHomeLayout();

    // parametrii pt lista de aplicatii
    RelativeLayout.LayoutParams param_list_show = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    RelativeLayout.LayoutParams param_list_hide = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
    // parametrii pentru homepage
    RelativeLayout.LayoutParams param_home_show = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
    RelativeLayout.LayoutParams param_home_hide = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
    @Override
    public boolean onDrag(View v, DragEvent event) {
        View view;
        int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                // do nothing
                Log.d("Drag Event", "Drag event started");
                AppListLayout.setLayoutParams(param_list_hide);
                HomeLayout.setLayoutParams(param_home_show);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                // Dropped, reassign View to ViewGroup
                view = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) view.getParent();
                owner.removeView(view);
                LinearLayout container = (LinearLayout) v;
                container.addView(view);
                view.setVisibility(View.VISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                view = (View) event.getLocalState();
                view.setVisibility(View.VISIBLE);
            default:
                break;
        }
        return true;
    }
}
