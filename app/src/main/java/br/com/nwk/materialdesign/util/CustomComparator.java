package br.com.nwk.materialdesign.util;

import android.util.Log;

import java.util.Comparator;

import br.com.nwk.materialdesign.model.LavaJato;

/**
 * Created by rma19_000 on 07/07/2015.
 */
public class CustomComparator implements Comparator<LavaJato>{
    @Override
    public int compare(LavaJato lhs, LavaJato rhs) {
        //double lhDistancia = Double.parseDouble(lhs.distancia);
        //double rhDistancia = Double.parseDouble(rhs.distancia);
        //return Double.compare(lhs.distancia,lhs.distancia);
        //return lhs.distancia.compareTo(rhs.distancia);
        //Log.e("argh",String.valueOf(Double.compare(lhs.distancia, rhs.distancia)));
        return Double.compare(lhs.distancia, rhs.distancia);
    }
}
