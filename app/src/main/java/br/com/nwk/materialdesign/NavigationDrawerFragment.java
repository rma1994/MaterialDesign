package br.com.nwk.materialdesign;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private NavMenuAdapter adapter;
    private RecyclerView mRecyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containterView;

    //permite ver se o usuario sabe da nav ou não
    private boolean mUserLearnedDrawer;

    //permite saber que a nav abre pela primeira vez ou se esta voltando de uma rotação
    private boolean mFromSavedInstanceState;


    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        //verifica se ele voltou de uma rotação
        if(savedInstanceState != null){
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        //cria um recycler view, cria seu adapter e modela esse adapter como um linear layout, que é o mais parecido com uma lista
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new NavMenuAdapter(getActivity(),getData());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return layout;
    }


    public static List<MenuItem> getData(){
        List<MenuItem> data = new ArrayList<>();
        int[] icons = {R.drawable.car_wash, R.drawable.send};
        String[] titles = {"Lava Jatos","Fale Conosco"};

        //pega as informações necessarias e adiciona em nossa lista chamada data, depois retorna essa data;
        for(int i=0;i<titles.length && i<icons.length;i++){
            MenuItem current = new MenuItem();
            current.iconId = icons[i];
            current.title = titles[i];

            data.add(current);

        }

        return data;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {

        containterView = getActivity().findViewById(fragmentId);

        this.mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar,R.string.open, R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                //se o usuario nunca viu a nav bar antes, mostre ela a ele
                if(!mUserLearnedDrawer){
                    mUserLearnedDrawer = true;
                    saveToPreferences(getActivity(),KEY_USER_LEARNED_DRAWER, String.valueOf(mUserLearnedDrawer));
                }
                //invalida os botoes da app_bar
                getActivity().invalidateOptionsMenu();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getActivity().invalidateOptionsMenu();
            }
        };

        if(!mUserLearnedDrawer && !mFromSavedInstanceState){
            //usando o id da nossa nav view, ele sabera qual tela abrir
            mDrawerLayout.openDrawer(containterView);


        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //mantem a nav bar sincronizada com o resto do app
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public static void saveToPreferences(Context context, String preferenceName, String preferenceValue){

        //cria um arquivo de preferencias que somente este app pode alterar
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceName, preferenceValue);
        editor.apply();
    }

    //pega o conceito e o arquivo que queremos ler e retornamos ele, o default e para o caso de nao haver nenhum valor dentro dela.
    public static String readFromPreferences (Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }
}
