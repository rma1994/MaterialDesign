package br.com.nwk.materialdesign.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.nwk.materialdesign.R;
import br.com.nwk.materialdesign.model.LavaJato;

/**
 * Created by rma19_000 on 02/07/2015.
 */
public class CarWashAdapter extends RecyclerView.Adapter<CarWashAdapter.CarWashViewHolder> {
    protected static final String TAG = "eWash";
    private final List<LavaJato> data;
    private final Context context;
    //private CarWashOnClickListener carWashOnClickListener;
    //private LayoutInflater inflater;
    private LavaJatoOnClickListener lavaJatoOnClickListener;


    public CarWashAdapter(Context context, List<LavaJato> data, LavaJatoOnClickListener lavaJatoOnClickListener){
        //inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        this.lavaJatoOnClickListener = lavaJatoOnClickListener;
    }

    @Override
    public CarWashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //View view = inflater.inflate(R.layout.car_wash_row, parent, false);
        View view = LayoutInflater.from(context).inflate(R.layout.car_wash_row, parent, false);
        CarWashViewHolder holder = new CarWashViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CarWashViewHolder holder, final int position) {
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

    public interface LavaJatoOnClickListener{
        public void onClickLavaJato(View view, int idx);
    }

    @Override
    public int getItemCount() {
        //se data for nulo, returna zero
        return this.data != null ? this.data.size() : 0;
    }

    class CarWashViewHolder extends RecyclerView.ViewHolder {
        TextView mNome;
        TextView mTelefone;
        TextView mDist;
        ImageView mIcon;

        public CarWashViewHolder(View itemView) {
            super(itemView);
            mNome = (TextView) itemView.findViewById(R.id.value_nome_list);
            mTelefone = (TextView) itemView.findViewById(R.id.value_tel_list);
            mDist = (TextView) itemView.findViewById(R.id.value_distancia_list);
            mIcon = (ImageView) itemView.findViewById(R.id.image_list);

        }
    }


}
