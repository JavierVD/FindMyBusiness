package mx.ita.findmybusiness;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class ListElement {
    public Bitmap logo;
    public String name;
    public String direccion;
    public float promedio;
    public boolean servicio;
    public boolean estado;
    public String id;
    public ListElement(Bitmap logo, String name, String direccion, float promedio, boolean estado, boolean servicio, String id){
        this.logo = logo;
        this.name = name;
        this.direccion = direccion;
        this.promedio = promedio;
        this.estado = estado;
        this.servicio = servicio;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return logo;
    }

    public void setBitmap(Bitmap logo) {
        this.logo = logo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public float getPromedio() {
        return promedio;
    }

    public void setPromedio(float promedio) {
        this.promedio = promedio;
    }

    public boolean isServicio() {
        return servicio;
    }

    public void setServicio(boolean servicio) {
        this.servicio = servicio;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
