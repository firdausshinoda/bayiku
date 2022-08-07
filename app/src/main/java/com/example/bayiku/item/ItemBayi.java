package com.example.bayiku.item;

public class ItemBayi {
    private String id, nama, tgl_lahir, jk;

    public ItemBayi(String id, String nama, String tgl_lahir, String jk) {
        this.id = id;
        this.nama = nama;
        this.tgl_lahir = tgl_lahir;
        this.jk = jk;
    }

    public String getNama() {
        return nama;
    }

    public String getId() {
        return id;
    }

    public String getJk() {
        return jk;
    }

    public String getTgl_lahir() {
        return tgl_lahir;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJk(String jk) {
        this.jk = jk;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setTgl_lahir(String tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }
}
