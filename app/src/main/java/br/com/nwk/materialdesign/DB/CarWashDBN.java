package br.com.nwk.materialdesign.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import br.com.nwk.materialdesign.model.CarWash;

/**
 * Created by rma19_000 on 10/07/2015.
 */
public class CarWashDBN extends SQLiteOpenHelper{
    private static final String TAG = "sql";
    private static final String NOME_BANCO = "ewash.sqlite";
    private static final int VERSAO_BANCO = 1;

    public CarWashDBN(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "criando a tabela lavajato favoritado...");
        db.execSQL("create table if not exists carwash(_id integer primary key, classificacao integer, nome text, rua text, numero text, bairro text, cep text, cidade text, estado text, email text, ecologica integer, reuso integer, tradicional integer, latitude double, longitude double, telefone text);");
        Log.d(TAG, "tabela criada com sucesso!");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //caso eu mude a versao do banco de dados, e aqui que jogo o sql
    }

    public long save(CarWash carWash){
        long id = carWash.id;
        SQLiteDatabase db = getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put("_id",carWash.id);
            values.put("classificacao",carWash.iconeClassificacao);
            values.put("nome",carWash.nome);
            values.put("rua",carWash.rua);
            values.put("numero",carWash.numero);
            values.put("bairro",carWash.bairro);
            values.put("cep",carWash.cep);
            values.put("cidade",carWash.cidade);
            values.put("estado",carWash.estado);
            values.put("email",carWash.email);
            values.put("ecologica",carWash.ecologica);
            values.put("reuso",carWash.reuso);
            values.put("tradicional",carWash.tradicional);
            values.put("latitude",carWash.latitude);
            values.put("longitude",carWash.longitude);
            values.put("telefone",carWash.telefone);
            //values.put("",);

            /*if (id !=0){
                String _id = String.valueOf(carWash.id);
                String[] whereArgs = new String[]{_id};
                //update carro set values = ... where _id=?
                int count = db.update("carwash", values, "_id=?", whereArgs);
                Log.i(TAG,"Atualizou [" + count + "] registro");

                Log.e("model",_id);
                return count;
            } else {*/
                //insert into carwash values(..._
                id = db.insert("carwash", "", values);
                Log.i(TAG,"Inseriu [" + id + "] registro");
                //Log.e("model", id+"");
                return id;
            //}
        } finally {
            db.close();
        }
    }

    //Deleta o lavajato
    public int delete (CarWash carWash){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //delete from carro where _id=?
            int count = db.delete("carwash", "_id=?", new String[]{String.valueOf(carWash.id)});
            Log.i(TAG,"Deletou [" + count + "] registro");
            return count;
        } finally {
            db.close();
        }
    }

    //Consulta a lista com todos os carros
    public List<CarWash> findAll(){
        SQLiteDatabase db = getWritableDatabase();
        try {
            //select * from carro
            Cursor c = db.query("carwash", null, null, null, null, null, null, null);
            return toList(c);
        } finally {
            db.close();
        }
    }

    private List<CarWash> toList(Cursor c){
        List<CarWash> carWashList = new ArrayList<CarWash>();
        if(c.moveToFirst()){
            do {
                CarWash carWash = new CarWash();
                carWash.id = c.getInt(c.getColumnIndex("_id"));
                carWash.iconeClassificacao = c.getInt(c.getColumnIndex("classificacao"));
                carWash.ecologica = c.getInt(c.getColumnIndex("ecologica"));
                carWash.reuso = c.getInt(c.getColumnIndex("reuso"));
                carWash.tradicional = c.getInt(c.getColumnIndex("tradicional"));
                carWash.nome = c.getString(c.getColumnIndex("nome"));
                carWash.rua = c.getString(c.getColumnIndex("rua"));
                carWash.numero = c.getString(c.getColumnIndex("numero"));
                carWash.bairro = c.getString(c.getColumnIndex("bairro"));
                carWash.cep = c.getString(c.getColumnIndex("cep"));
                carWash.cidade = c.getString(c.getColumnIndex("cidade"));
                carWash.estado = c.getString(c.getColumnIndex("estado"));
                carWash.email = c.getString(c.getColumnIndex("email"));
                carWash.telefone = c.getString(c.getColumnIndex("telefone"));
                carWash.latitude = c.getDouble(c.getColumnIndex("latitude"));
                carWash.longitude = c.getDouble(c.getColumnIndex("longitude"));
                carWash.endereco = carWash.rua + ", " + carWash.numero + " - " + carWash.bairro + "\n" + carWash.cidade + " - " + carWash.estado + "\n" + carWash.cep;
                carWash.favoritado = true;

                carWashList.add(carWash);
            } while (c.moveToNext());
        }
        return carWashList;
    }

    //chega se o lava jato existe no banco de dados.
    public boolean exist(CarWash carWash){
        boolean result = false;
        String id = String.valueOf(carWash.id);
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query("carwash", new String[]{"_id"}, "_id=?", new String[]{id}, null, null, null);

        //Se encontrou
        if(c.getCount() > 0){
            result = true;
            return result;
        }
        return result;
    }
}
