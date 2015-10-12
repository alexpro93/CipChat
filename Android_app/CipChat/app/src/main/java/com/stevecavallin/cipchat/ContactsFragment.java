package com.stevecavallin.cipchat;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Steve on 07/08/14.
 */
public class ContactsFragment extends Fragment {
    public static boolean uChipChat=true;
    private Button u_cip,u_altri;
    UtentiCipChatFragment Cfragment = new UtentiCipChatFragment();
    UtentiTelefonoFragment Tfragment = new UtentiTelefonoFragment();
    DetailFragment detailFragment=new DetailFragment();
    private boolean added=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.contacts_layout, container,false);
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment_utenti, Cfragment);
        ft.commit();
        u_cip=(Button)view.findViewById(R.id.image_chipchat);
        u_altri=(Button)view.findViewById(R.id.image_altri);
        u_altri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.hide(Cfragment);
                if(!added) {
                    ft.add(R.id.fragment_utenti, Tfragment);
                    added=true;
                }
                else
                    ft.show(Tfragment);
                ft.commit();
                u_altri.setBackgroundColor(Color.parseColor("#ff0eff36"));
                u_cip.setBackgroundColor(Color.parseColor("#ffd3d9d9"));
            }
        });
        u_cip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.hide(Tfragment);
                ft.show(Cfragment);
                ft.commit();
                u_altri.setBackgroundColor(Color.parseColor("#ffd3d9d9"));
                u_cip.setBackgroundColor(Color.parseColor("#ff0eff36"));
            }
        });
        return view;
    }

    public void switchToConversation(String destinatario,boolean cipchat){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.hide(this);
        if(!MyListFragment.added){
            fragmentTransaction.add(R.id.content_pane,detailFragment);
            MyListFragment.added=true;
        }
        else{
            DetailFragment.destinatario=destinatario;
            fragmentTransaction.show(MyListFragment.Dfragment);
        }
    }
}
