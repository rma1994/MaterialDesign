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

import br.com.nwk.materialdesign.model.CarWash;
import br.com.nwk.materialdesign.model.MenuItem;
import br.com.nwk.materialdesign.R;

/**
 * Created by rma19_000 on 14/06/2015.
 */
public class NavMenuAdapter extends RecyclerView.Adapter<NavMenuAdapter.NavViewHolder> {

    private LayoutInflater inflater;
    private NavMenuOnClickListener navMenuOnClickListener;
    List<MenuItem> data = Collections.emptyList();

    public NavMenuAdapter(Context context, List<MenuItem> data, NavMenuOnClickListener navMenuOnClickListener){
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.navMenuOnClickListener = navMenuOnClickListener;
    }

    @Override
    public NavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.menu_row, parent, false);
        NavViewHolder holder = new NavViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final NavViewHolder holder, final int position) {
        MenuItem current = data.get(position);
        holder.mTitle.setText(current.title);
        holder.mIcon.setImageResource(current.iconId);

        //coloca um click listener no recycler view.
        if(navMenuOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navMenuOnClickListener.onClickItem(holder.itemView, position);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    //listener para escutar os clicks no RV
    public interface NavMenuOnClickListener{
        public void onClickItem(View view, int idx);
    }


    class NavViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        ImageView mIcon;

        public NavViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.listText);
            mIcon = (ImageView) itemView.findViewById(R.id.listIcon);

        }


    }


}
