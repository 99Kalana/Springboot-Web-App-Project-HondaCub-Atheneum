package org.example.hondasupercub.entity;



import java.io.Serializable;

import java.util.Objects;



public class VinPartsId implements Serializable {



    private VinHistory vinHistory;

    private SparePart sparePart;



    public VinPartsId() {}



    public VinPartsId(VinHistory vinHistory, SparePart sparePart) {

        this.vinHistory = vinHistory;

        this.sparePart = sparePart;

    }



    public VinHistory getVinHistory() {

        return vinHistory;

    }



    public void setVinHistory(VinHistory vinHistory) {

        this.vinHistory = vinHistory;

    }



    public SparePart getSparePart() {

        return sparePart;

    }



    public void setSparePart(SparePart sparePart) {

        this.sparePart = sparePart;

    }



    @Override

    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        VinPartsId that = (VinPartsId) o;

        return Objects.equals(vinHistory, that.vinHistory) && Objects.equals(sparePart, that.sparePart);

    }



    @Override

    public int hashCode() {

        return Objects.hash(vinHistory, sparePart);

    }

}