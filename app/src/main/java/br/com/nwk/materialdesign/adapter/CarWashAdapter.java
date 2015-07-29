package br.com.nwk.materialdesign.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.nwk.materialdesign.R;
import br.com.nwk.materialdesign.model.CarWash;
import br.com.nwk.materialdesign.util.ToPixels;

/**
 * Created by rma19_000 on 02/07/2015.
 */
public class CarWashAdapter extends RecyclerView.Adapter<CarWashAdapter.CarWashViewHolder> {
    protected static final String TAG = "CarWashAdapter";
    private final List<CarWash> data;
    private final Context context;
    private LavaJatoOnClickListener lavaJatoOnClickListener;
    private FavoriteOnClickListener favoriteOnClickListener;
    //private CarWashOnClickListener carWashOnClickListener;
    //private LayoutInflater inflater;


    public CarWashAdapter(Context context, List<CarWash> data, LavaJatoOnClickListener lavaJatoOnClickListener, FavoriteOnClickListener favoriteOnClickListener){
        //inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        this.lavaJatoOnClickListener = lavaJatoOnClickListener;
        this.favoriteOnClickListener = favoriteOnClickListener;
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
        CarWash current = data.get(position);
        holder.mNome.setText(current.nome);
        holder.mTelefone.setText(current.telefone);
        holder.mIcon.setImageResource(current.iconeClassificacao);
        holder.mFavorite.setChecked(current.favoritado);

        if(current.distancia != 0.000) {
            holder.mDist.setText(String.valueOf(current.distancia) + " km");
        } else {
            holder.mDist.setText(R.string.not_available);
        }

        //LISTENER para abrir os detalhes
        if(lavaJatoOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lavaJatoOnClickListener.onClickLavaJato(holder.itemView, position, data);
                }
            });
        }

        //listener do botao favorito
        if(favoriteOnClickListener != null){
            holder.mFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteOnClickListener.onClickFavorite(holder.itemView, position, holder.mFavorite, data);

                }
            });
        }
    }

    public interface LavaJatoOnClickListener{
        public void onClickLavaJato(View view, int idx, List<CarWash> list);
    }

    public interface FavoriteOnClickListener{
        public void onClickFavorite(View view, int idx, CheckBox checkBox, List<CarWash> list);
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
        CheckBox mFavorite;
        ImageView mIcon;
        //CheckBox mFavorite;
        //Drawable[] d;

        public CarWashViewHolder(View itemView) {
            super(itemView);
            mNome = (TextView) itemView.findViewById(R.id.value_nome_list);
            mTelefone = (TextView) itemView.findViewById(R.id.value_tel_list);
            mDist = (TextView) itemView.findViewById(R.id.value_distancia_list);
            mIcon = (ImageView) itemView.findViewById(R.id.image_list);
            mFavorite = (CheckBox) itemView.findViewById(R.id.checkbox_favorite);
            /*mFavorite = (CheckBox) itemView.findViewById(R.id.checkbox_tradicional);
            d = mFavorite.getCompoundDrawables();
            d[0].setBounds(0,0, 24,24);
            mFavorite.setCompoundDrawables(d[0],null,null,null);*/
        }
    }


}