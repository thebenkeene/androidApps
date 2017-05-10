package keene.drugdatabase;

import android.graphics.Bitmap;
import java.io.*;
/**
 * Created by BigBen on 5/2/17.
 */

public class Drug implements Serializable{
    private String drug;
    private String addictions;
    private String deaths;
    private String users;
    private String flag;
//    public Bitmap drugPic;

    public String getDrug(){
        return drug;
    }

    public void setDrug(String drug){
        this.drug = drug;
    }
    public String getAddictions() {
        return addictions;
    }

    public void setAddictions(String addictions) {
        this.addictions = addictions;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }
    public String getFlag() {

        return flag;
    }

    public void setFlag(String flag) {

        this.flag = flag;
    }
}

