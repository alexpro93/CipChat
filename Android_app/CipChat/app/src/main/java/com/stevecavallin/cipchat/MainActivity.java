package com.stevecavallin.cipchat;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Steve on 22/07/14.
 */
public class MainActivity extends ActionBarActivity implements MyListFragment.ListFragmentItemClickListener{

    public static SlidingPaneLayout mSlidingLayout;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "GCMDemo";
    //ActionBar actionBar;
    private boolean add=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my);
        //actionBar=getSupportActionBar();
        checkPlayServices();
        int dpValue = 50; // margin in dips
        float d = this.getResources().getDisplayMetrics().density;
        final int margin = (int)(dpValue * d);

        mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);
        final View Fmargin=mSlidingLayout.findViewById(R.id.content_pane);
        final ViewGroup.MarginLayoutParams lp=(ViewGroup.MarginLayoutParams)Fmargin.getLayoutParams();

        mSlidingLayout.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if(add){

                }else{

                }
            }

            @Override
            public void onPanelOpened(View panel) {
                getSupportFragmentManager().findFragmentById(R.id.content_pane).setHasOptionsMenu(false);
                getSupportFragmentManager().findFragmentById(R.id.list_pane).setHasOptionsMenu(true);
                //actionBar.removeAllTabs();
                //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                add=true;
                lp.setMargins(margin,0,0,0);
                Fmargin.setLayoutParams(lp);

            }

            @Override
            public void onPanelClosed(View panel) {
                getSupportFragmentManager().findFragmentById(R.id.content_pane).setHasOptionsMenu(true);
                getSupportFragmentManager().findFragmentById(R.id.list_pane).setHasOptionsMenu(false);
                if(!MyListFragment.contatti){
                    //actionBar.removeAllTabs();
                    //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    add=true;
                    lp.setMargins(margin,0,0,0);
                    Fmargin.setLayoutParams(lp);
                }
                else if(add){
                    //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    //actionBar.addTab(actionBar.newTab().setText("Utenti ChipChat").setTabListener(tabListener));
                    //actionBar.addTab(actionBar.newTab().setText("Altri Utenti").setTabListener(tabListener));
                    Log.i(TAG,"addTab");
                    add=false;
                    lp.setMargins(0,0,0,0);
                    Fmargin.setLayoutParams(lp);
                }
            }
        });
        if(getIntent().getStringExtra("Destinatario")!=null){
            MyListFragment.contatti=false;
            mSlidingLayout.closePane();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DetailFragment fragment = new DetailFragment();
            fragmentTransaction.replace(R.id.content_pane, fragment);
            fragmentTransaction.commit();
            DetailFragment.destinatario = getIntent().getStringExtra("Destinatario");
            DetailFragment.animato = getIntent().getBooleanExtra("Animato", true);
        }
        else mSlidingLayout.openPane();
        /*findViewById(R.id.chat_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlidingLayout.closePane();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }


    @Override
    public void onListFragmentItemClick(View view, int position) {

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    @Override
    public void onBackPressed(){
        if(!mSlidingLayout.isOpen()){
            mSlidingLayout.openPane();
        }
        else{
            MyListFragment.contatti=true;
            super.onBackPressed();
        }
    }
}

