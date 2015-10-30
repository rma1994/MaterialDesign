package br.com.nwk.materialdesign.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.com.nwk.materialdesign.R;
import br.com.nwk.materialdesign.adapter.NavMenuAdapter;
import br.com.nwk.materialdesign.model.MenuItem;
import br.com.nwk.materialdesign.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    public static final String PREF_FILE_NAME = "testpref";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    public static final String ASSUNTO_EMAIL = "Feedback";
    public static final int LISTA = 0;
    public static final int EMAIL = 1;


    private NavMenuAdapter adapter;
    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private View containterView;
    private View mainView;

    //permite ver se o usuario sabe da nav ou nao
    private boolean mUserLearnedDrawer;

    //permite saber que a nav abre pela primeira vez ou se esta voltando de uma rotacao
    private boolean mFromSavedInstanceState;


    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserLearnedDrawer = Boolean.valueOf(readFromPreferences(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));

        //verifica se ele voltou de uma rotacao
        if(savedInstanceState != null){
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        //cria um recycler view, cria seu adapter e modela esse adapter como um linear layout, que e o mais parecido com uma lista
        recyclerView = (RecyclerView) layout.findViewById(R.id.drawerList);
        adapter = new NavMenuAdapter(getActivity(), getData(), onClickItem());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //pega a view principal
        mainView = layout;
        return layout;
    }


    public static List<MenuItem> getData(){
        List<MenuItem> data = new ArrayList<>();
        int[] icons = {/*R.mipmap.car_wash,*/ R.mipmap.send_menu};
        int[] titles = {/*R.string.car_washes,*/R.string.talk_to_us};

        //pega as informacoes necessarias e adiciona em nossa lista chamada data, depois retorna essa data;
        for(int i=0;i<titles.length && i<icons.length;i++){
            MenuItem current = new MenuItem();
            current.iconId = icons[i];
            current.title = titles[i];

            data.add(current);

        }

        return data;
    }

    //Listener que trata os clicks nos items do recycler view
    private NavMenuAdapter.NavMenuOnClickListener onClickItem(){
        return new NavMenuAdapter.NavMenuOnClickListener() {
            @Override
            public void onClickItem(View view, int idx) {
                /*if (idx == LISTA){
                    Snackbar.make(mainView,"Função em desenvolvimento",Snackbar.LENGTH_LONG).show();
                } else if (idx == EMAIL){*/
                    //pergunta pro sistema quem pode enviar um e-mail
                    //informando o nosso e-mail e o assunto do e-mail em questao
                    Log.e("tag",getString(R.string.our_mail));
                    new EmailAsyncTask(getString(R.string.our_mail), getString(R.string.feedback)).execute();
                //}
            }
        };
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

    //pega o contexto e o arquivo que queremos ler e retornamos ele, o default e para o caso de nao haver nenhum valor dentro dela.
    public static String readFromPreferences (Context context, String preferenceName, String defaultValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(preferenceName, defaultValue);
    }


    //Uma interface para escutar os cliques do usuario
    public static interface ClickListener{
        public void onClick(View v, int position);
        public void onLongClick(View v, int position);
    }

    private class EmailAsyncTask extends AsyncTask<Void, Void, Boolean>{

        String email;
        String assunto;

        public EmailAsyncTask(String email, String assunto){
            this.email = email;
            this.assunto = assunto;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            try {
                Uri uri = Uri.parse("mailto:" + email);
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, assunto);
                //emailIntent.setType("text/html");
                startActivity(emailIntent);
                result = true;
            }catch (Exception e){
                Log.e("TAG", e.getMessage());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            if(b==false){
                Toast.makeText(getActivity(),R.string.error_execute_asynctask,Toast.LENGTH_LONG).show();
            }
        }
    }
}
