package io.endeavour.stocks.entity.stocks;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "sector_lookup", schema = "endeavour")
public class Sector {
    @Column(name = "sector_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sectorID;
    @Column(name = "sector_name")
    private String sectorName;

    @OneToMany(mappedBy = "sector", fetch = FetchType.EAGER)
    List<SubSector> subSectorList;

    public List<SubSector> getSubSectorList() {
        return subSectorList;
    }

    public void setSubSectorList(List<SubSector> subSectorList) {
        this.subSectorList = subSectorList;
    }

    public int getSectorID() {
        return sectorID;
    }

    public void setSectorID(int sectorID) {
        this.sectorID = sectorID;
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sector sector = (Sector) o;
        return sectorID == sector.sectorID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectorID);
    }
}
