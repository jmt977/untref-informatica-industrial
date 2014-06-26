package ar.edu.untref.ingcomputacion.industrial.arduino;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;

public class Content implements Serializable {

	private static final long serialVersionUID = -2694154154740576534L;
	
	private List<String> registration_ids;
    private Map<String,String> data;

    public void addRegId(String regId){
        if(getRegistration_ids() == null)
            setRegistration_ids(new LinkedList<String>());
        getRegistration_ids().add(regId);
    }

    public void createData(String title, String message){
        if(getData() == null)
            setData(new HashMap<String,String>());

        getData().put("titulo", title);
        getData().put("mensaje", message);
    }

	public List<String> getRegistration_ids() {
		return registration_ids;
	}

	public void setRegistration_ids(List<String> registration_ids) {
		this.registration_ids = registration_ids;
	}

	public Map<String,String> getData() {
		return data;
	}

	public void setData(Map<String,String> data) {
		this.data = data;
	}

}