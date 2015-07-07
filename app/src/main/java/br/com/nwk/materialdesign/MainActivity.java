package br.com.nwk.materialdesign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.nwk.materialdesign.adapter.MainAdapter;
import br.com.nwk.materialdesign.model.LavaJato;
import br.com.nwk.materialdesign.model.User;
import br.com.nwk.materialdesign.tabs.SlidingTabLayout;
import br.com.nwk.materialdesign.util.Constants;
import br.com.nwk.materialdesign.util.LocationUtils;
import br.com.nwk.materialdesign.util.NetworkUtils;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , com.google.android.gms.location.LocationListener{

    private static final int TABS_AMOUNT = 2;
    public static final int LOCALIZACAO_LIBERADA = 100;
    private Toolbar mToolbar;
    private ViewPager mPager;
    private SlidingTabLayout mTabs;
    protected GoogleMap map;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        //libera o botao home, possibilitando sua customização
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Atrela-se o menu nav ao seu componente xml, depois passamos nossa toolbar e layout drawer onde ele esta
        NavigationDrawerFragment mDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        //Cria as abas
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true); //faz as abas ocuparem o mesmo espaço na tela
        mTabs.setCustomTabView(R.layout.custom_tabs_view, R.id.tabText);
        mTabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));

        mTabs.setViewPager(mPager);

        //Conecta aos serviços da Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.filter_btn) {
            //startActivity(new Intent(this, CarWashDetail.class));
        }

        return super.onOptionsItemSelected(item);
    }

    //pega quantos dps vc quer e converte para pxs, tendo em mente o tamanho da tela.
    public int toPixels(float dip) {
        Resources r = getResources();
        float densid = r.getDisplayMetrics().density;
        int px = (int) (dip * densid + 0.5f);
        return px;
    }

    //Inicia a conexao com os serviços da google
    @Override
    protected void onStart() {
        //Toast.makeText(this, "Startando", Toast.LENGTH_SHORT).show();
        super.onStart();
        mGoogleApiClient.connect();

    }

    //para a conexao com os serviços da google
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //pausa a conexao com os serviçlos da google
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    //ao se conectar com a google, inicia o metodo que pega a localização
    @Override
    public void onConnected(Bundle bundle) {
        //Toast.makeText(this,"Conectado ao googleplay services",Toast.LENGTH_SHORT).show();
        startLocationUpdates();
    }

    //Caso a conexao tenha sido interrompida
    @Override
    public void onConnectionSuspended(int i) {
        //Toast.makeText(this,"Conexao interrompida",Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"Erro ao conectar" + connectionResult,Toast.LENGTH_LONG).show();
        Log.e("Erro de conexao", "Erro ao conectar" + connectionResult);
    }

    //Começa a pegar a atualização da localidade do usuario, com tempo normal de um minuto e minimo de 30sec
    protected void startLocationUpdates(){
        Log.d("TAG", "startLocationUpdates()");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(60000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates(){
        //Log.d("StopLocation", "stopLocationUpdates()");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public void onLocationChanged(Location location){
        User.location = location;
        //Toast.makeText(this,User.location.getLatitude() + "," + User.location.getLongitude(),Toast.LENGTH_LONG).show();
        //Log.e("Localização atual", String.valueOf(latitude) + "," + String.valueOf(longitude));
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        String tabs[] = getResources().getStringArray(R.array.tabs);
        int icons[] = {R.mipmap.home, R.mipmap.favorite};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            CarWashListFragment carWashListFragment = CarWashListFragment.getInstance(position);
            return carWashListFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = getResources().getDrawable(icons[position]);
            drawable.setBounds(toPixels(0), toPixels(0), toPixels(24), toPixels(24));
            ImageSpan imageSpan = new ImageSpan(drawable);

            //Spannable é o cara que consegue combinar textos com  imagens
            SpannableString spannableString = new SpannableString(" ");
            spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return spannableString;
        }

        @Override
        public int getCount() {
            return TABS_AMOUNT;
        }
    }


    public static class CarWashListFragment extends android.support.v4.app.Fragment {
        private TextView textView;
        private LocationUtils locationUtils = new LocationUtils();
        private MainAdapter adapter;
        private RecyclerView recyclerView;
        private ActionBarDrawerToggle mDrawerToggle;
        private DrawerLayout mDrawerLayout;
        private View lavajatoView = null;
        private List<LavaJato> lavaJato;
        private SwipeRefreshLayout swipeRefreshLayout;
        private ProgressBar bar;

        public static CarWashListFragment getInstance(int position) {
            CarWashListFragment carWashListFragment = new CarWashListFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            carWashListFragment.setArguments(args);

            return carWashListFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            Bundle bundle = getArguments();


            //View layout = null;
            //layout = inflater.inflate(R.layout.fragment_main, container, false);
            //Se o bundle não for nulo e for 1, linko ele com minha tela que tem o recycler view
            if (bundle != null) {
                if(bundle.getInt("position") == 0){
                    this.lavajatoView = inflater.inflate(R.layout.fragment_main, container, false);
                    bar = (ProgressBar) lavajatoView.findViewById(R.id.progress);

                    //cria um recycler view, cria seu adapter e modela esse adapter como um linear layout, que é o mais parecido com uma lista
                    recyclerView = (RecyclerView) lavajatoView.findViewById(R.id.drawerListMain);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setHasFixedSize(true);

                    //layout para atualizar a lista de acordo com o usuario
                    swipeRefreshLayout = (SwipeRefreshLayout) lavajatoView.findViewById(R.id.swipeToRefresh);
                    swipeRefreshLayout.setOnRefreshListener(onRefreshListener(bar));
                    swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
                    swipeRefreshLayout.setEnabled(false);

                    //checa se o usuario liberou o acesso a localidade dele
                    //caso o usuario não tenha liberado, mostra um dialog pedindo para ele habilitar
                    if(locationUtils.isLocationEnabled(getActivity())) {
                        //starta a asynctask que atualiza os dados dos lava jatos sem mostrar a minha progressbar, mostrando apenas a do swiperepreshlayout
                        new GetCarWashTask(bar, Constants.YES).execute();
                        swipeRefreshLayout.setEnabled(true);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle(R.string.location_not_allowed_title);
                        builder.setMessage(R.string.location_not_allowed_message);
                        builder.setCancelable(false);
                        builder.setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                                //Toast.makeText(getActivity(), "Liberou!", Toast.LENGTH_LONG).show();
                                startActivityForResult(intent, LOCALIZACAO_LIBERADA);
                            }
                        });

                        builder.setNegativeButton(R.string.dont_allow, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Toast.makeText(getActivity(), "Não Liberou :(", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }

                }

            }

            return lavajatoView;
        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(requestCode == LOCALIZACAO_LIBERADA){
                if(locationUtils.isLocationEnabled(getActivity())){
                    //starta a AsyncTask e reabilida o botao swipe to refresh
                    new GetCarWashTask(bar, Constants.YES).execute();
                    swipeRefreshLayout.setEnabled(true);
                } else{
                    getActivity().finish();
                }
            }
        }


        private SwipeRefreshLayout.OnRefreshListener onRefreshListener(ProgressBar bar){
            final ProgressBar pbar = bar;

            return new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new GetCarWashTask(pbar,Constants.NO).execute();

                }

            };
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
        }

        //Listener para escutar os cliques nos itens do menu
        private MainAdapter.LavaJatoOnClickListener onClickLavaJato(){
            return new MainAdapter.LavaJatoOnClickListener() {
                @Override
                public void onClickLavaJato(View view, int idx) {
                    LavaJato c = lavaJato.get(idx);
                    //Toast.makeText(getActivity(),"LavaJato: " + c.id,Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(view.getContext(),CarWashDetail.class);
                    intent.putExtra(Constants.LAVA_JATO, c);
                    startActivity(intent);
                }
            };
        }

        /*public static List<LavaJato> getData(){
            List<LavaJato> data = new ArrayList<>();
            int icons[] = {R.mipmap.ic_launcher};
            String[] nome = {"Heisenberg"};
            String[] telefone = {"(19) 3322-1155"};
            String[] distancia = {"53km"};

            //pega as informações necessarias e adiciona em nossa lista chamada data, depois retorna essa data;
            for(int i=0;/*i<nome.length && i<icons.length;i<10;i++){
                LavaJato current = new LavaJato();
                current.iconeClassificacao = icons[0];
                current.nome = nome[0];
                current.telefone = telefone[0];
                current.distancia = distancia[0];

                data.add(current);
            }

            return data;
        }*/

        private class GetCarWashTask extends AsyncTask<Void, Integer, List<LavaJato>>{

            private ProgressBar bar;
            int showBar;

            public GetCarWashTask (ProgressBar bar, int showBar){
                this.showBar = showBar;
                this.bar = bar;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if(showBar == Constants.YES) {
                    bar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            protected List<LavaJato> doInBackground(Void... params) {

                //se conecta a internet para pegar as informações dos lavajatos
                NetworkUtils nwk = new NetworkUtils();
                String jsonStr = nwk.doGetRequest(Constants.HTTP_PROTOCOL,Constants.HOST,Constants.CARWASH );
                List<LavaJato> data =  new ArrayList<>();
                //Log.e("TAG","back");

                //Extrai as informações do meu jsonarray
                if (jsonStr!=null){
                    try{
                        //Thread.sleep(1500);
                        JSONArray jsonOArray = new JSONArray(jsonStr);
                        for(int i=0;i<jsonOArray.length();i++){
                            JSONObject jsonObject = jsonOArray.getJSONObject(i);
                            LavaJato lavaJato = new LavaJato(jsonObject);
                            data.add(lavaJato);
                            //publishProgress((int) ((i/(float) jsonOArray.length())*100));
                        }


                    }catch (Exception ex){
                        Log.e("TAG",ex.getMessage(),ex);
                    }

                }
                return data;
            }


            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                bar.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(List<LavaJato> lavaJatos) {
                if(lavaJatos != null){
                    CarWashListFragment.this.lavaJato = lavaJatos;
                    recyclerView.setAdapter(new MainAdapter(getActivity(),lavaJatos,onClickLavaJato()));
                    Log.e("TAG", "post");
                }

                //Esconde as barras de carregamento ao terminar de carregar
                bar.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }

        }

    }
}
