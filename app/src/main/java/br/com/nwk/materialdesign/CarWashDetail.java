package br.com.nwk.materialdesign;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.nwk.materialdesign.model.LavaJato;
import br.com.nwk.materialdesign.util.Constants;


public class CarWashDetail extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView mClassificacao;
    private TextView mNome;
    private TextView mTelefone;
    private TextView mEmail;
    private TextView mEndereco;
    private CheckBox mSeco;
    private CheckBox mReuso;
    private CheckBox mTradicional;
    private FloatingActionButton mFAB;
    private RelativeLayout mLayoutTelefone;
    private RelativeLayout mLayoutEmail;
    private RelativeLayout mLayoutEndereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash_detail);

        //cria a toolbar nessa tela
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        mClassificacao = (ImageView) findViewById(R.id.img_lavagem_detalhes);
        mNome = (TextView) findViewById(R.id.detalhes_nome_lavajato);
        mTelefone = (TextView) findViewById(R.id.phone_details);
        mEmail = (TextView) findViewById(R.id.email);
        mEndereco = (TextView) findViewById(R.id.endereco);
        mSeco = (CheckBox) findViewById(R.id.checkbox_seco);
        mReuso = (CheckBox) findViewById(R.id.checkbox_reuso);
        mTradicional = (CheckBox) findViewById(R.id.checkbox_tradicional);
        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mLayoutTelefone = (RelativeLayout) findViewById(R.id.layout_telefone);
        mLayoutEmail = (RelativeLayout) findViewById(R.id.layout_email);
        mLayoutEndereco = (RelativeLayout) findViewById(R.id.layout_endereco);

        //configura a tela para voltar a tela anterior
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if(intent!=null){
            LavaJato lj = (LavaJato) getIntent().getSerializableExtra(Constants.LAVA_JATO);
            setLavaJato(lj);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_car_wash_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //Volta para a tela inicial
        if(id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void setLavaJato(LavaJato lj){
        mNome.setText(lj.nome);
        mTelefone.setText(lj.telefone);
        mEmail.setText(lj.email);
        mEndereco.setText(lj.endereco);
        mSeco.setChecked(setCheckBox(lj.ecologica));
        mReuso.setChecked(setCheckBox(lj.reuso));
        mTradicional.setChecked(setCheckBox(lj.tradicional));

    }

    private boolean setCheckBox (int opcao){
        if(opcao == Constants.YES){
            return true;
        }
        return false;
    }

}
