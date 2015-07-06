package br.com.nwk.materialdesign;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.nwk.materialdesign.adapter.MainAdapter;
import br.com.nwk.materialdesign.model.LavaJato;
import br.com.nwk.materialdesign.tabs.SlidingTabLayout;
import br.com.nwk.materialdesign.util.Constants;
import br.com.nwk.materialdesign.util.NetworkUtils;


public class MainActivity extends AppCompatActivity {

    private static final int TABS_AMOUNT = 2;
    private Toolbar mToolbar;
    private ViewPager mPager;
    private SlidingTabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        //libera o botao home, possibilitando sua customiza��o
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Atrela-se o menu nav ao seu componente xml, depois passamos nossa toolbar e layout drawer onde ele esta
        NavigationDrawerFragment mDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        mDrawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);

        //Cria as abas
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setDistributeEvenly(true); //faz as abas ocuparem o mesmo espa�o na tela
        mTabs.setCustomTabView(R.layout.custom_tabs_view, R.id.tabText);
        mTabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.colorAccent));


        mTabs.setViewPager(mPager);
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

            //Spannable � o cara que consegue combinar textos com  imagens
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
        private MainAdapter adapter;
        private RecyclerView recyclerView;
        private ActionBarDrawerToggle mDrawerToggle;
        private DrawerLayout mDrawerLayout;
        private View lavajatoView = null;
        private List<LavaJato> lavaJato;
        private SwipeRefreshLayout swipeRefreshLayout;

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
            //Se o bundle n�o for nulo e for 1, linko ele com minha tela que tem o recycler view
            if (bundle != null) {
                if(bundle.getInt("position") == 0){
                    this.lavajatoView = inflater.inflate(R.layout.fragment_main, container, false);
                    ProgressBar bar = (ProgressBar) lavajatoView.findViewById(R.id.progress);
                    swipeRefreshLayout = (SwipeRefreshLayout) lavajatoView.findViewById(R.id.swipeToRefresh);

                    swipeRefreshLayout.setOnRefreshListener(onRefreshListener(bar));
                    swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

                    //cria um recycler view, cria seu adapter e modela esse adapter como um linear layout, que � o mais parecido com uma lista
                    recyclerView = (RecyclerView) lavajatoView.findViewById(R.id.drawerListMain);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setHasFixedSize(true);

                    //starta a asynctask que atualiza os dados dos lava jatos sem mostrar a minha progressbar, mostrando apenas a do swiperepreshlayout
                    new GetCarWashTask(bar,Constants.YES).execute();

                }

            }

            return lavajatoView;
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

            //pega as informa��es necessarias e adiciona em nossa lista chamada data, depois retorna essa data;
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

                //se conecta a internet para pegar as informa��es dos lavajatos
                NetworkUtils nwk = new NetworkUtils();
                String jsonStr = nwk.doGetRequest(Constants.HTTP_PROTOCOL,Constants.HOST,Constants.CARWASH );
                List<LavaJato> data =  new ArrayList<>();
                //Log.e("TAG","back");

                //Extrai as informa��es do meu jsonarray
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
