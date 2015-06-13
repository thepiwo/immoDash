package models;


import com.avaje.ebean.Model;
import play.data.Form;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.*;


/**
 * Created by Philipp on 13.06.2015.
 */
@Entity
public class Immobilie extends Model {

    @Id
    private int id;

    private String name;

    private double kaufPreis;

    @Column(columnDefinition = "date")
    private Date kaufDatum;

    private String typ;

    @Lob
    public String imagePath;

    @OneToMany(mappedBy = "immobilie", cascade = CascadeType.ALL)
    List<Mieter> mieter;

    @OneToMany(mappedBy = "immobilie", cascade = CascadeType.ALL)
    List<Investition> investitionen;

    @OneToMany(mappedBy = "immobilie", cascade = CascadeType.ALL)
    List<Kredit> kredite;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Mieter> getMieter() {
        return mieter;
    }

    public void setMieter(List<Mieter> mieter) {
        this.mieter = mieter;
    }

    public List<Investition> getInvestitionen() {
        return investitionen;
    }

    public void setInvestitionen(List<Investition> investitionen) {
        this.investitionen = investitionen;
    }

    public List<Kredit> getKredite() {
        return kredite;
    }

    public void setKredite(List<Kredit> kredite) {
        this.kredite = kredite;
    }

    public double getKaufPreis() {
        return kaufPreis;
    }

    public void setKaufPreis(double kaufPreis) {
        this.kaufPreis = kaufPreis;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public Date getKaufDatum() {
        return kaufDatum;
    }

    public void setKaufDatum(Date kaufDatum) {
        this.kaufDatum = kaufDatum;
    }

    public double getInvestitionenSum() {
        double investitionssum = 0.0;
        for (Investition investition : investitionen) {
            investitionssum += investition.getKosten();
        }
        return investitionssum;
    }

    public double getKrediteSum() {
        double kreditesum = 0.0;
        for (Kredit kredit : kredite) {
            kreditesum += kredit.getBetrag();
        }
        return kreditesum;
    }

    public double getMietSum() {
        double mietsum = 0.0;
        for (Mieter miet : mieter) {
            mietsum += miet.getMiete();
        }
        return mietsum;
    }

    public static Finder<Long, Immobilie> find = new Finder<>(Long.class, Immobilie.class);

    public static Form<Immobilie> immoForm = Form.form(Immobilie.class);

    public double getWert() {
        return getKaufPreis() - getKrediteSum() - getInvestitionenSum() * Math.exp(-0.04);
        return getKaufPreis() - getKrediteSum() - getAbschreibungenSum() - getInvestitionenSum() * Math.exp(-0.04);
    }

    public ArrayList<Double> calculateWert(double wertsteigerungInProzent) {
        int quartalKaufdatum = 1 + (getKaufDatum().getMonth()-1) / 3;
        int jahrKaufdatum = getKaufDatum().getYear();
        int quartalAktuell = 1 + (new Date().getMonth()-1) / 3;
        int jahrAktuell = new Date().getYear();
        ArrayList<Double> listWerte = new ArrayList<Double>();
        for(int i=0;i<(jahrAktuell - jahrKaufdatum)*4-quartalKaufdatum+quartalAktuell;i++)
        {
            Double wert = new Double(getKaufPreis()*
                    (PreisindexVDP.find.where().eq("quartal",quartalKaufdatum).eq("jahr",jahrKaufdatum).setMaxRows(1).findUnique().getValue()-
                    PreisindexVDP.find.where().eq("quartal",((quartalKaufdatum+i+1)%4+1)).eq("jahr",jahrKaufdatum+i%4).setMaxRows(1).findUnique().getValue()));
            listWerte.add(wert);
        }
        return listWerte;
    }
}
