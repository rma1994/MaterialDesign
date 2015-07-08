package br.com.nwk.materialdesign.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;

import br.com.nwk.materialdesign.Fragments.CarWashNewFragment;
import br.com.nwk.materialdesign.Fragments.CarWashTabFragment;
import br.com.nwk.materialdesign.R;
import br.com.nwk.materialdesign.util.Constants;
import br.com.nwk.materialdesign.util.ToPixels;

/**
 * Created by rma19_000 on 08/07/2015.
 */
public class TabsAdapter extends FragmentPagerAdapter{
    private Context context;
    private ToPixels toPixels = new ToPixels();

    public TabsAdapter(Context context,FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        if (position == 0){
            args.putInt("nome", Constants.ABA_LAVA_JATO);
        } else {
            args.putInt("nome", Constants.ABA_FAVORITOS);
        }

        Fragment f = new CarWashNewFragment();
        f.setArguments(args);

        return f;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0){
            return context.getString(R.string.car_washes);
        } else if (position == 1){
            return context.getString(R.string.favorites);
        }

        return super.getPageTitle(position);
        /*int icons[] = {R.mipmap.home, R.mipmap.favorite};

        Drawable drawable = context.getResources().getDrawable(icons[position]);
        drawable.setBounds(toPixels.toPixels(context, 0), toPixels.toPixels(context, 0), toPixels.toPixels(context, 24), toPixels.toPixels(context, 24));
        ImageSpan imageSpan = new ImageSpan(drawable);

        //Spannable é o cara que consegue combinar textos com  imagens
        SpannableString spannableString = new SpannableString(" ");
        spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;*/
    }

}
