package io.endeavour.stocks.entity.stocks;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "subsector_lookup", schema = "endeavour")
public class SubSector {
    @Column(name = "subsector_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int subSectorID;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @Column(name = "subsector_name")
    private String subSectorName;

    public int getSubSectorID() {
        return subSectorID;
    }

    public void setSubSectorID(int subSectorID) {
        this.subSectorID = subSectorID;
    }

    public String getSubSectorName() {
        return subSectorName;
    }

    public void setSubSectorName(String subSectorName) {
        this.subSectorName = subSectorName;
    }

    //Custom method written in the Child Entity to do join with Parent table and get parent table values
    public String getSectorName(){
        return sector.getSectorName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubSector subSector = (SubSector) o;
        return subSectorID == subSector.subSectorID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subSectorID);
    }
}
