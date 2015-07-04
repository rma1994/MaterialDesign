package br.com.nwk.materialdesign.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rma19_000 on 02/07/2015.
 */
public class LavaJato {
    public int iconeClassificacao;
    public int id;
    public String nome;
    public String rua;
    public String numero;
    public String bairro;
    public String cep;
    public String cidade;
    public String estado;
    public String email;
    public String lavagem;
    public double latitude;
    public double longitude;
    public String telefone;
    public String distancia;

    public LavaJato (JSONObject object) {
        id = getInt(object,"_id");
        nome = getString(object, "nome");
        rua = getString(object, "rua");
        numero = getString(object,"numero");
        bairro = getString(object,"bairro");
        cep = getString(object,"cep");
        cidade = getString(object,"cidade");
        estado = getString(object,"estado");
        email = getString(object,"email");
        lavagem = getString(object,"lavagem");
        telefone = getString(object,"telefone");
        latitude = getDouble(object, "latitude");
        longitude = getDouble(object, "longitude");
        distancia = getString(object,"_id") + "Km";
    }

    private String getString(JSONObject object, String objectName){
        String result = "";

        if(object.has(objectName)){
            try{
                if(!object.isNull(objectName)){
                    result = object.getString(objectName);
                    System.out.println(object.getString(objectName));
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
                    System.out.println(object.getString(objectName));
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
                    result = object.getInt(objectName);
                    System.out.println(object.getString(objectName));
                }

            }catch (JSONException e){

            }
        }
        return result;
    }
}
