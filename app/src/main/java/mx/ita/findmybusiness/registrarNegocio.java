package mx.ita.findmybusiness;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTabHost;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.pchmn.materialchips.ChipsInput;

public class registrarNegocio extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_negocio);
        tab_bienes taf = new tab_bienes();
        TabLayout  tabLayout = (TabLayout) findViewById(R.id.tabLayout); // get the reference of TabLayout
        TabLayout.Tab firstTab = tabLayout.newTab(); // Create a new Tab names
        firstTab.setText("Bienes"); // set the Text for the first Tab
        firstTab.setIcon(R.drawable.ic_menu_negocios); // set an icon for the first tab
        TabLayout.Tab secondTab = tabLayout.newTab(); // Create a new Tab names
        secondTab.setText("Servicios"); // set the Text for the first Tab
        secondTab.setIcon(R.drawable.ic_menu_servicios); // set an icon for the first tab
        tabLayout.addTab(firstTab);
        tabLayout.addTab(secondTab);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout, taf);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            // get the current selected tab's position and replace the fragment accordingly
                Fragment fragment = new tab_bienes();
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new tab_bienes();
                        break;
                    case 1:
                        fragment = new tab_servicios();
                        break;
                }


                ft.replace(R.id.frameLayout, fragment);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }



        });
    }
    public String getNombre(){
        EditText nombrenegocio = findViewById(R.id.txtNombreEmpresa);
        return nombrenegocio.getText().toString();
    }

}