package br.com.nwk.materialdesign;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.nwk.materialdesign.model.LavaJato;
import br.com.nwk.materialdesign.model.User;
import br.com.nwk.materialdesign.util.Constants;


public class CarWashDetail extends AppCompatActivity {

    public static final String ASSUNTO_EMAIL = "Contato via eWash";
    public static final int OPCAO_EMAIL = 0;
    public static final int OPCAO_MAPS = 1;
    public static final int OPCAO_ROTA = 2;

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
    private LavaJato lj;
    //private LjDetailsOnClickListener ljDetailsOnClickListener;

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

        /*setListener(mLayoutTelefone);
        setListener(mLayoutEmail);
        setListener(mLayoutEndereco);*/

        //configura os clicklistener dos layouts da minha tela
        //abre o dialer do celular com o numero do lava jato
        mLayoutTelefone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(),"algo",Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse("tel:" + lj.telefone);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
            }
        });

        //Starta uma AsyncTask que se encarregará de abrir o email.
        //Esta operaçao leva alguns segundos, por isso estou usando uma nota thread.
        mLayoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OptionAsyncTask(v, OPCAO_EMAIL).execute();
            }
        });

        //Abre o GoogleMaps de acordo com a latitude e longitude do lava jato
        mLayoutEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OptionAsyncTask( v, OPCAO_MAPS).execute();
            }
        });

        //ClickListener do FAB
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OptionAsyncTask( v, OPCAO_ROTA).execute();
            }
        });

        //configura a tela para voltar a tela anterior
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //intent com as informações do lava jato
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
        this.lj = lj;

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

    private class OptionAsyncTask extends AsyncTask<Void, Void, Boolean> {
        View view;
        int opcao;

        public OptionAsyncTask( View view, int opcao){
            this.view = view;
            this.opcao = opcao;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Boolean result = false;
            //direciona para o email
            if (opcao == OPCAO_EMAIL) {
                try {
                    Uri uri = Uri.parse("mailto:" + lj.email);
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, uri);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, ASSUNTO_EMAIL);
                    //emailIntent.setType("text/html");
                    startActivity(emailIntent);
                    result = true;
                } catch (Exception e) {
                    Log.e("EMAIL", e.getMessage());
                }
                //mostra no mapa
            } else if(opcao == OPCAO_MAPS){
                try {
                    String GEO_URI = "http://maps.google.com/maps?q="+lj.latitude+","+lj.longitude+"(" + lj.nome +")&iwloc=A&hl=es";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GEO_URI));
                    startActivity(intent);
                    //Log.e("TEST", Double.toString(lj.latitude) + "," + Double.toString(lj.longitude));
                    result = true;
                }catch (Exception e){
                    Log.e("MAPS", e.getMessage());
                }
                //traça rota, opção do meu fab
            } else if (opcao == OPCAO_ROTA){
                try{
                    String origem = User.location.getLatitude() + "," + User.location.getLongitude() ;
                    String destino = lj.latitude + "," + lj.longitude;
                    String url = "http://maps.google.com/maps?f=d&saddr="+origem+"&daddr="+destino+"&hl=pt";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    result = true;
                } catch (Exception e){
                    Log.e("MAPS", e.getMessage());
                }
            }

            return result;
        }


        @Override
        protected void onPostExecute(Boolean b) {
            if(b==false){
                Toast.makeText(view.getContext(),R.string.error_execute_asynctask,Toast.LENGTH_LONG).show();
            }
        }
    }
}
