package br.com.nwk.materialdesign.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rma19_000 on 02/07/2015.
 */
public class LavaJato {
    public int icone;
    public String nome;
    public String telefone;
    public String distancia;

    public LavaJato (JSONObject object){
        nome = getString(object,"nome");
        telefone = getString(object,"telefone");
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
}
