package br.com.nwk.materialdesign.Fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.nwk.materialdesign.CarWashDetail;
import br.com.nwk.materialdesign.R;
import br.com.nwk.materialdesign.adapter.CarWashAdapter;
import br.com.nwk.materialdesign.model.LavaJato;
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
    protected RecyclerView recyclerView;
    private List<LavaJato> lavaJatos;
    private LinearLayoutManager mLayoutManager;
    private ProgressBar bar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LocationUtils locationUtils = new LocationUtils();
    private int idTab;

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

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //checa se o usuario liberou o acesso a localidade dele
        //caso o usuario n�o tenha liberado, mostra um dialog pedindo para ele habilitar
        //Aguarda o retorno da resposta de sua libera��o antes de continuar o aplicativo.

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
                        //Toast.makeText(getActivity(), "N�o Liberou :(", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }


    //Quando a tela voltar da libera��o de acesso a localidade, continua o app.
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

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener(ProgressBar bar) {
        final ProgressBar pbar = bar;

        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GetCarWashTask(pbar, Constants.NO).execute();
            }

        };
    }

    //starta a task que busca as informa��es no banco de dados.
    private void taskCarWash() {
        new GetCarWashTask(bar, Constants.YES).execute();
    }

    private CarWashAdapter.LavaJatoOnClickListener onClickLavaJato() {
        return new CarWashAdapter.LavaJatoOnClickListener() {
            @Override
            public void onClickLavaJato(View view, int idx) {
                LavaJato lj = lavaJatos.get(idx);
                Intent intent = new Intent(view.getContext(), CarWashDetail.class);
                intent.putExtra(Constants.LAVA_JATO, lj);
                startActivity(intent);
            }
        };
    }

    private class GetCarWashTask extends AsyncTask<Void, Integer, List<LavaJato>> {

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
        protected List<LavaJato> doInBackground(Void... params) {
            //se conecta a internet para pegar as informa��es dos lavajatos
            NetworkUtils nwk = new NetworkUtils();
            String jsonStr = nwk.doGetRequest(Constants.HTTPS_PROTOCOL, Constants.HOST_EWASH, Constants.CARWASH);
            List<LavaJato> listEcologic = new ArrayList<>();
            List<LavaJato> listReuse = new ArrayList<>();
            List<LavaJato> listTrad = new ArrayList<>();
            List<LavaJato> listFinal = new ArrayList<>();
            //Log.e("TAG","back");

            //Extrai as informa��es do meu jsonarray
            if (jsonStr != null) {
                try {
                    //Thread.sleep(1500);
                    JSONArray jsonOArray = new JSONArray(jsonStr);
                    for (int i = 0; i < jsonOArray.length(); i++) {
                        JSONObject jsonObject = jsonOArray.getJSONObject(i);
                        LavaJato lavaJato = new LavaJato(jsonObject);

                        //Aqui o aplicativo se conecta com a GoogleMatrixAPI e pega a distancia de cada um dos lava jatos para o usuario.
                        //String path = "json?origins=-22.831367-47.269207&destinations=-22.832593,-47.271755&mode=DRIVING&key=AIzaSyB6lIKzyTkvShKmb_vg19PTW1sZAKsQysg";
                        String path = "json?origins=" + User.location.getLatitude() + "," + User.location.getLongitude() + "&destinations=" + lavaJato.latitude + "," + lavaJato.longitude + "&mode=DRIVING&units=metric&key=AIzaSyB6lIKzyTkvShKmb_vg19PTW1sZAKsQysg";
                        String jsonDistancia = nwk.doGetRequest(Constants.HTTPS_PROTOCOL, Constants.HOST_GOOGLE_API, path);
                        //Log.e("jsondistancia",jsonDistancia);
                        JSONObject jsonObjectDistancia = new JSONObject(jsonDistancia);
                        lavaJato.setDistance(jsonObjectDistancia);

                        //Adiciona o lava jato em sua respectiva lista
                        if (lavaJato.ecologica == Constants.YES) {
                            listEcologic.add(lavaJato);
                        } else if (lavaJato.reuso == Constants.YES) {
                            listReuse.add(lavaJato);
                        } else {
                            listTrad.add(lavaJato);
                        }
                        //publishProgress((int) ((i/(float) jsonOArray.length())*100));
                    }

                    //organiza as listas de acordo com o local mais proximo
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
        protected void onPostExecute(List<LavaJato> lavaJatos) {
            if (lavaJatos != null) {
                CarWashNewFragment.this.lavaJatos = lavaJatos;
                recyclerView.setAdapter(new CarWashAdapter(getActivity(), lavaJatos, onClickLavaJato()));
                //Log.e("TAG", "post");
            }

            //Esconde as barras de carregamento ao terminar de carregar
            bar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
