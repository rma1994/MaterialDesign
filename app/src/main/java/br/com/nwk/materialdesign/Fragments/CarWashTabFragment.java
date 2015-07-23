package br.com.nwk.materialdesign.Fragments;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import br.com.nwk.materialdesign.R;
import br.com.nwk.materialdesign.adapter.TabsAdapter;
import br.com.nwk.materialdesign.util.ToPixels;


/**
 * A simple {@link Fragment} subclass.
 */
public class CarWashTabFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private ToPixels toPixels = new ToPixels();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_car_wash_tab, container, false);

        //ViewPager
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new TabsAdapter(getActivity(), getChildFragmentManager()));

        //Tabs
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        int color = getActivity().getResources().getColor(R.color.black);
        //Cor preta base no texto
        tabLayout.setTabTextColors(color, color);

        //faz as abas ocuparem a tela toda.
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //Adiciona as tabs
        //drawable.setBounds(toPixels.toPixels(getActivity(), 0), toPixels.toPixels(getActivity(), 0), toPixels.toPixels(getActivity(), 24), toPixels.toPixels(getActivity(), 24));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.car_washes));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.favorites));
        //Listener Para tratar os eventos de cliques nas tabs
        tabLayout.setOnTabSelectedListener(this);


        //Se mudar o view pager atualiza a tab selecionada
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        return view;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //Caso a pessoa clique na segunda aba, roda a função no fragment de favoritos que atualiza
        //a lista de favoritos, assim esta lista sempre exibira uma lista atualizada
        if(tab.getPosition() == 1){
            TabsAdapter adapter = (TabsAdapter) mViewPager.getAdapter();
            CarWashNewFragment fragment = (CarWashNewFragment) adapter.carWashFavorite;
            fragment.setFavoriteListDB(getActivity());

        }
        //Se alterar a tab, atualiza o viewpager
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
