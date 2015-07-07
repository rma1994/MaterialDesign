package br.com.nwk.materialdesign.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import br.com.nwk.materialdesign.R;
import br.com.nwk.materialdesign.model.LavaJato;

/**
 * Created by rma19_000 on 02/07/2015.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private LayoutInflater inflater;
    private LavaJatoOnClickListener lavaJatoOnClickListener;
    List<LavaJato> data = Collections.emptyList();

    public MainAdapter(Context context, List<LavaJato> data, LavaJatoOnClickListener lavaJatoOnClickListener){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.lavaJatoOnClickListener = lavaJatoOnClickListener;
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.car_wash_row, parent, false);
        MainViewHolder holder = new MainViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, final int position) {
        LavaJato current = data.get(position);
        holder.mNome.setText(current.nome);
        holder.mTelefone.setText(current.telefone);
        holder.mIcon.setImageResource(current.iconeClassificacao);

        if(current.distancia != 0.000) {
            holder.mDist.setText(String.valueOf(current.distancia) + " km");
        } else {
            holder.mDist.setText("Erro ao calcular");
        }


        if(lavaJatoOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lavaJatoOnClickListener.onClickLavaJato(holder.itemView,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        TextView mNome;
        TextView mTelefone;
        TextView mDist;
        ImageView mIcon;

        public MainViewHolder(View itemView) {
            super(itemView);
            mNome = (TextView) itemView.findViewById(R.id.value_nome_list);
            mTelefone = (TextView) itemView.findViewById(R.id.value_tel_list);
            mDist = (TextView) itemView.findViewById(R.id.value_distancia_list);
            mIcon = (ImageView) itemView.findViewById(R.id.image_list);

        }
    }

    public interface LavaJatoOnClickListener{
        public void onClickLavaJato(View view, int idx);
    }
}
