package br.com.nwk.materialdesign.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by rma19_000 on 02/07/2015.
 */
public class CarWash implements Serializable {
    public int iconeClassificacao;
    public int id;
    public String nome;
    public String rua;
    public String numero;
    public String bairro;
    public String cep;
    public String cidade;
    public String estado;
    public String endereco;
    public String email;
    public int ecologica;
    public int reuso;
    public int tradicional;
    public double latitude;
    public double longitude;
    public String telefone;
    public double distancia;

    public CarWash(JSONObject object) {
        id = getInt(object,"_id");
        nome = getString(object, "nome");
        rua = getString(object, "rua");
        numero = getString(object,"numero");
        bairro = getString(object,"bairro");
        cep = getString(object,"cep");
        cidade = getString(object,"cidade");
        estado = getString(object,"estado");
        email = getString(object,"email");
        ecologica = getInt(object, "ecologica");
        reuso = getInt(object, "reuso");
        tradicional = getInt(object, "tradicional");
        telefone = getString(object,"telefone");
        latitude = getDouble(object, "latitude");
        longitude = getDouble(object, "longitude");
        //distancia = getString(object,"_id") + "Km";
        endereco = rua + ", " + numero + " - " + bairro + "\n" + cidade + " - " + estado + "\n" + cep;
    }

    private String getString(JSONObject object, String objectName){
        String result = "";

        if(object.has(objectName)){
            try{
                if(!object.isNull(objectName)){
                    result = object.getString(objectName);
                    //System.out.println(object.getString(objectName));
                }

            }catch (JSONException e){

            }
        }
        return result;
    }

    private int getInt(JSONObject object, String objectName){
        int result = 0;

        if(object.has(objectName)){
            try{
                if(!object.isNull(objectName)){
                    result = object.getInt(objectName);
                    //System.out.println(object.getString(objectName));
                }

            }catch (JSONException e){

            }
        }
        return result;
    }

    private double getDouble(JSONObject object, String objectName){
        double result = 0;

        if(object.has(objectName)){
            try{
                if(!object.isNull(objectName)){
                    result = object.getDouble(objectName);

                    //Log.e("Test", )
                }

            }catch (JSONException e){

            }
        }
        return result;
    }

    public void setDistance(JSONObject object){

        try {
            //Pega o array rows do json object
            JSONArray rowsArray = object.getJSONArray("rows");
            JSONObject row = rowsArray.getJSONObject(0);

            //pega o array elements do json row
            JSONArray elements = row.getJSONArray("elements");
            JSONObject element = elements.getJSONObject(0);

            //pega a distancia
            JSONObject distance = element.getJSONObject("distance");

            if(distance.has("text")){
                this.distancia = Double.parseDouble(distance.getString("text").replace(" km","").replace(",",""));
                //Log.e("Distancia",String.valueOf(this.distancia));
            } else {
                this.distancia = 0.000;
            }

        } catch (JSONException e){
            this.distancia = 0.000;
            Log.e("Erro Distancia", e.getMessage());
        }

    }
}
