package br.com.nwk.materialdesign.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.nwk.materialdesign.CarWashDetail;
import br.com.nwk.materialdesign.DB.CarWashDBN;
import br.com.nwk.materialdesign.R;
import br.com.nwk.materialdesign.adapter.CarWashAdapter;
import br.com.nwk.materialdesign.model.CarWash;
import br.com.nwk.materialdesign.model.User;
import br.com.nwk.materialdesign.util.Constants;
import br.com.nwk.materialdesign.util.CustomComparator;
import br.com.nwk.materialdesign.util.LocationUtils;
import br.com.nwk.materialdesign.util.NetworkUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarWashNewFragment extends android.support.v4.app.Fragment {

    private static final int LOCALIZACAO_LIBERADA = 100;
    private static final String ASYNCTASK_TAG = "ASyncTask";
    private static final String ATUALIZACAO_FAVORITOS = "Att Favorito";
    protected RecyclerView recyclerView;
    private List<CarWash> carWashs;
    private List<CarWash> favoritos;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar bar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LocationUtils locationUtils = new LocationUtils();
    private int idTab;
    private CarWashDBN carWashDBN;
    private View mainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //identica em qual aba estamos atualmente
            this.idTab = getArguments().getInt("idTab");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_wash_new, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        bar = (ProgressBar) view.findViewById(R.id.progress);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener(bar));
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        swipeRefreshLayout.setEnabled(false);

        //salva a view
        mainView = view;
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //checa se o usuario liberou o acesso a localidade dele
        //caso o usuario não tenha liberado, mostra um dialog pedindo para ele habilitar
        //Aguarda o retorno da resposta de sua liberação antes de continuar o aplicativo.

        if(idTab == Constants.ABA_LAVA_JATO) {
            if (locationUtils.isLocationEnabled(getActivity())) {
                //starta a asynctask que atualiza os dados dos lava jatos sem mostrar a minha progressbar, mostrando apenas a do swiperepreshlayout
                taskCarWash();
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

        } else if (idTab == Constants.ABA_FAVORITOS){
            setFavoriteListDB();
        }
    }


    //Quando a tela voltar da liberação de acesso a localidade, continua o app.
    //Se o usuario nao liberar. fecha o app
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCALIZACAO_LIBERADA) {
            if (locationUtils.isLocationEnabled(getActivity())) {
                //starta a AsyncTask e reabilida o botao swipe to refresh
                new GetCarWashTask(bar, Constants.YES).execute();
                swipeRefreshLayout.setEnabled(true);
            } else {
                getActivity().finish();
            }
        }
    }

    //atualiza a lista de lava jatos ao deslizar o dedo para baixo das abas
    private SwipeRefreshLayout.OnRefreshListener onRefreshListener(ProgressBar bar) {
        final ProgressBar pbar = bar;

        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetCarWashTask(pbar, Constants.NO).execute();
            }

        };
    }

    //starta a task que busca as informações no banco de dados.
    private void taskCarWash() {
        new GetCarWashTask(bar, Constants.YES).execute();
    }

    //atualiza a lista de favoritos.
    public void setFavoriteListDB(){
        carWashDBN = new CarWashDBN(getActivity());
        favoritos = new ArrayList<CarWash>();

        if (favoritos != null && idTab == Constants.ABA_FAVORITOS) {
            Log.d(ATUALIZACAO_FAVORITOS,"atualizando lista de favoritos...");
            this.favoritos = carWashDBN.findAll();
            recyclerView.setAdapter(new CarWashAdapter(getActivity(), favoritos, onClickLavaJato(), onClickFavorite()));
            Log.d(ATUALIZACAO_FAVORITOS, "lista atualizada");
            //Log.e("a", favoritos.get(0).id + "");
            //Log.e("TAG", "post");
        }
    }

    //atualiza  alista principal de lava jatos
    public void reloadCarWashList(){
        if(carWashs != null && Constants.ABA_LAVA_JATO == idTab){
            Log.d(ATUALIZACAO_FAVORITOS,"atualizando lista principal...");
            List<CarWash> result = new ArrayList<CarWash>();
            carWashDBN = new CarWashDBN(getActivity());

            //checa se o lava jato foi favoritado para atualizar a lista principal
            for(CarWash carWash : carWashs) {
                carWash.favoritado = carWashDBN.exist(carWash);
                result.add(carWash);
            }
            recyclerView.setAdapter(new CarWashAdapter(getActivity(), result, onClickLavaJato(), onClickFavorite()));
            Log.d(ATUALIZACAO_FAVORITOS, "lista atualizada");
        }
    }

    public void setRecyclerView(Context context, List<CarWash> list){
        recyclerView.setAdapter(new CarWashAdapter(context, favoritos, onClickLavaJato(), onClickFavorite()));
    }

    //listener de click nos items da lista de lavajatos
    private CarWashAdapter.LavaJatoOnClickListener onClickLavaJato() {
        return new CarWashAdapter.LavaJatoOnClickListener() {
            @Override
            public void onClickLavaJato(View view, int idx, List<CarWash> data) {
                CarWash lj = data.get(idx);
                Intent intent = new Intent(view.getContext(), CarWashDetail.class);
                intent.putExtra(Constants.LAVA_JATO, lj);
                startActivity(intent);
            }
        };
    }

    //listener de click na checkbox de favorite
    private CarWashAdapter.FavoriteOnClickListener onClickFavorite(){
        return new CarWashAdapter.FavoriteOnClickListener() {
            @Override
            public void onClickFavorite(View view, int idx, final CheckBox checkBox, List<CarWash> data) {
                //Toast.makeText(view.getContext(), "favorito: "+ String.valueOf(checkBox.isChecked()) , Toast.LENGTH_LONG).show();
                //instancia a classe que cuida de meu banco de dados
                //pega o lavajato que foi clicado
                carWashDBN = new CarWashDBN(view.getContext());
                final CarWash carWash = data.get(idx);

                if(checkBox.isChecked() == true){
                    //se o click deu um check na checkbox, insere esse lava jato nos favoritos
                    carWashDBN.save(carWash);
                    carWash.favoritado = true;
                    //Toast.makeText(view.getContext(), "favorito: "+ String.valueOf(checkBox.isChecked()) , Toast.LENGTH_LONG).show();
                    setFavoriteListDB();
                } else if(checkBox.isChecked() == false) {
                    //se o click deu um uncheck na checkbox, deleta esse lava jato dos favoritos
                    carWashDBN.delete(carWash);
                    carWash.favoritado = false;
                    //Toast.makeText(view.getContext(), "favorito: "+ String.valueOf(checkBox.isChecked()) , Toast.LENGTH_LONG).show();
                    setFavoriteListDB();

                    if(idTab == Constants.ABA_FAVORITOS) {
                        Log.d("algo", idTab+"");
                        Snackbar.make(mainView, R.string.unfavorited_successful, Snackbar.LENGTH_LONG).setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                carWash.favoritado = true;
                                carWashDBN.save(carWash);
                                setFavoriteListDB();
                            }
                        }).show();
                    }

                }

            }
        };
    }
    //------

    private class GetCarWashTask extends AsyncTask<Void, Integer, List<CarWash>> {

        private ProgressBar bar;
        int showBar;

        public GetCarWashTask(ProgressBar bar, int showBar) {
            this.showBar = showBar;
            this.bar = bar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (showBar == Constants.YES) {
                bar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<CarWash> doInBackground(Void... params) {
            //se conecta a internet para pegar as informações dos lavajatos
            NetworkUtils nwk = new NetworkUtils();
            Log.d(ASYNCTASK_TAG, "conectando ao banco de dados online...");
            String jsonStr = nwk.doGetRequest(Constants.HTTPS_PROTOCOL, Constants.HOST_EWASH, Constants.CARWASH);
            List<CarWash> listEcologic = new ArrayList<>();
            List<CarWash> listReuse = new ArrayList<>();
            List<CarWash> listTrad = new ArrayList<>();
            List<CarWash> listFinal = new ArrayList<>();
            //Log.e("TAG","back");

            //Extrai as informações do meu jsonarray
            if (jsonStr != null) {
                try {
                    //Thread.sleep(1500);
                    JSONArray jsonOArray = new JSONArray(jsonStr);
                    Log.d(ASYNCTASK_TAG, "coletando dados dos lava jatos...");
                    for (int i = 0; i < jsonOArray.length(); i++) {
                        JSONObject jsonObject = jsonOArray.getJSONObject(i);
                        CarWash carWash = new CarWash(jsonObject);

                        //Aqui o aplicativo se conecta com a GoogleMatrixAPI e pega a distancia de cada um dos lava jatos para o usuario.
                        //String path = "json?origins=-22.831367-47.269207&destinations=-22.832593,-47.271755&mode=DRIVING&key=AIzaSyB6lIKzyTkvShKmb_vg19PTW1sZAKsQysg";
                        Log.d(ASYNCTASK_TAG, "coletando distancia do lava jato [ "+ carWash.id +" ]" );
                        String path = "json?origins=" + User.location.getLatitude() + "," + User.location.getLongitude() + "&destinations=" + carWash.latitude + "," + carWash.longitude + "&mode=DRIVING&units=metric&key=AIzaSyB6lIKzyTkvShKmb_vg19PTW1sZAKsQysg";
                        String jsonDistancia = nwk.doGetRequest(Constants.HTTPS_PROTOCOL, Constants.HOST_GOOGLE_API, path);
                        //Log.e("jsondistancia",jsonDistancia);
                        JSONObject jsonObjectDistancia = new JSONObject(jsonDistancia);
                        carWash.setDistance(jsonObjectDistancia);

                        //checa se o lava jato ja foi favoritado
                        Log.d(ASYNCTASK_TAG, "verificando se o lava jato [ "+ carWash.id +" ] ja foi favoritado" );
                        carWashDBN = new CarWashDBN(getActivity());
                        carWash.favoritado = carWashDBN.exist(carWash);

                        //Adiciona o lava jato em sua respectiva lista
                        if (carWash.ecologica == Constants.YES) {
                            listEcologic.add(carWash);
                        } else if (carWash.reuso == Constants.YES) {
                            listReuse.add(carWash);
                        } else {
                            listTrad.add(carWash);
                        }
                        //publishProgress((int) ((i/(float) jsonOArray.length())*100));
                    }

                    //organiza as listas de acordo com o local mais proximo
                    Log.d(ASYNCTASK_TAG, "organizando listas..." );
                    Collections.sort(listEcologic, new CustomComparator());
                    Collections.sort(listReuse, new CustomComparator());
                    Collections.sort(listTrad, new CustomComparator());

                    listFinal.addAll(listEcologic);
                    listFinal.addAll(listReuse);
                    listFinal.addAll(listTrad);

                } catch (Exception ex) {
                    Log.e("TAG", ex.getMessage(), ex);
                }

            }
            return listFinal;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            bar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(List<CarWash> carWashs) {
            if (carWashs != null) {
                Log.d(ASYNCTASK_TAG, "exibindo lista..." );
                CarWashNewFragment.this.carWashs = carWashs;
                //coloca os clicklisteners dos items do lava jato + dos botoes de favoritar
                recyclerView.setAdapter(new CarWashAdapter(getActivity(), carWashs, onClickLavaJato(), onClickFavorite()));
                //Log.e("TAG", "post");
            }

            //Esconde as barras de carregamento ao terminar de carregar
            bar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
