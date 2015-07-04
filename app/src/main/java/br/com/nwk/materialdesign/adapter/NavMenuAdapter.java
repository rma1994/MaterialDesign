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

import br.com.nwk.materialdesign.model.MenuItem;
import br.com.nwk.materialdesign.R;

/**
 * Created by rma19_000 on 14/06/2015.
 */
public class NavMenuAdapter extends RecyclerView.Adapter<NavMenuAdapter.NavViewHolder> {

    private LayoutInflater inflater;
    List<MenuItem> data = Collections.emptyList();

    public NavMenuAdapter(Context context, List<MenuItem> data){
        inflater = LayoutInflater.from(context);
        this.data = data;

    }

    @Override
    public NavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.menu_row, parent, false);
        NavViewHolder holder = new NavViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(NavViewHolder holder, int position) {
        MenuItem current = data.get(position);
        holder.mTitle.setText(current.title);
        holder.mIcon.setImageResource(current.iconId);
    }


    @Override
    public int getItemCount() {
        return data.size();
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
