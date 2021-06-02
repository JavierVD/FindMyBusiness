package mx.ita.findmybusiness;

import android.graphics.Bitmap;

public class ListElementEditProduct {
    public Bitmap image;
    public String name;
    public String marca;
    public String descripcion;
    public String categoria;
    public float precio;
    public int stock;
    public float promedio;
    public String id_producto;

    public ListElementEditProduct(Bitmap image, String name, String marca , float precio, float promedio, int stock, String id_producto, String descripcion,String categoria){
        this.image = image;
        this.name = name;
        this.promedio = promedio;
        this.precio = precio;
        this.id_producto = id_producto;
        this.marca = marca;
        this.stock = stock;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public float getPromedio() {
        return promedio;
    }

    public void setPromedio(float promedio) {
        this.promedio = promedio;
    }

    public String getId_producto() {
        return id_producto;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }
}
