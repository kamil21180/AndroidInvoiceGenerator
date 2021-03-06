package uek.krakow.pl.androidinvoicegenerator.viewcontroller;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uek.krakow.pl.androidinvoicegenerator.R;
import uek.krakow.pl.androidinvoicegenerator.invoicemodel.Buyer;
import uek.krakow.pl.androidinvoicegenerator.invoicemodel.Invoice;
import uek.krakow.pl.androidinvoicegenerator.invoicemodel.Summary;
import uek.krakow.pl.androidinvoicegenerator.invoicemodel.Tax0;
import uek.krakow.pl.androidinvoicegenerator.invoicemodel.Tax23;
import uek.krakow.pl.androidinvoicegenerator.invoicemodel.Tax5;
import uek.krakow.pl.androidinvoicegenerator.invoicemodel.Tax8;
import uek.krakow.pl.androidinvoicegenerator.tasks.GetFromApiTask;
import uek.krakow.pl.androidinvoicegenerator.viewcontroller.interfaces.UpdatableActivity;

public class BuyerFormActivity extends AppCompatActivity implements UpdatableActivity {
    EditText ed_nazwaNabyw, ed_ulicaNabyw, ed_domNabyw, ed_lokalNabyw, ed_miejscowoscNabyw, ed_kodNabyw, ed_NIpNabyw;
    String pustePole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_form);
        getSupportActionBar().setTitle("Krok 3 z 5");
        ed_nazwaNabyw = (EditText) findViewById(R.id.ed_nazwaNabyw);
        ed_ulicaNabyw = (EditText) findViewById(R.id.ed_ulicaNabyw);
        ed_domNabyw = (EditText) findViewById(R.id.ed_domNabyw);
        ed_lokalNabyw = (EditText) findViewById(R.id.ed_lokalNabyw);
        ed_miejscowoscNabyw = (EditText) findViewById(R.id.ed_miejscowoscNabyw);
        ed_kodNabyw = (EditText) findViewById(R.id.ed_kodNabyw);
        ed_NIpNabyw = (EditText) findViewById(R.id.ed_NIpNabyw);

    }

    public void toGoods(View view) {
        if(!kodPocztowy(ed_kodNabyw.getText().toString()) && niePuste(ed_kodNabyw.getText().toString())){
            ed_kodNabyw.setError("Wymagana forma 00-000");
        }else if(!NIP(ed_NIpNabyw.getText().toString()) && niePuste(ed_NIpNabyw.getText().toString())){
            ed_NIpNabyw.setError("Wymagane 10 cyfr");
        }else if(!niePuste(ed_nazwaNabyw.getText().toString()) || !niePuste(ed_ulicaNabyw.getText().toString()) || !niePuste(ed_domNabyw.getText().toString()) || !niePuste(ed_lokalNabyw.getText().toString()) || !niePuste(ed_miejscowoscNabyw.getText().toString()) || !niePuste(ed_kodNabyw.getText().toString()) || !niePuste(ed_NIpNabyw.getText().toString())){
            if (!niePuste(ed_nazwaNabyw.getText().toString())){
                ed_nazwaNabyw.setError("Puste pole");
                pustePole +="Nazwa,\n";
            }
            if (!niePuste(ed_ulicaNabyw.getText().toString())){
                ed_ulicaNabyw.setError("Puste pole");
                pustePole +="Ulica,\n";
            }
            if (!niePuste(ed_domNabyw.getText().toString())){
                ed_domNabyw.setError("Puste pole");
                pustePole +="Nr domu,\n";
            }
            if (!niePuste(ed_lokalNabyw.getText().toString())){
                ed_lokalNabyw.setError("Puste pole");
                pustePole +="Nr lokalu,\n";
            }
            if (!niePuste(ed_miejscowoscNabyw.getText().toString())){
                ed_miejscowoscNabyw.setError("Puste pole");
                pustePole +="Miejscowość,\n";
            }
            if (!niePuste(ed_kodNabyw.getText().toString())){
                ed_kodNabyw.setError("Puste pole");
                pustePole +="Kod pocztowy,\n";
            }
            if (!niePuste(ed_NIpNabyw.getText().toString())){
                ed_NIpNabyw.setError("Puste pole");
                pustePole +="NIP,\n";
            }
            AlertDialog.Builder a_builder = new AlertDialog.Builder(BuyerFormActivity.this);
            a_builder.setMessage("Nie wszystkie pola zostały uzupełnione:\n"+pustePole+"czy chcesz kontynuować mimo to?")
                    .setCancelable(false)
                    .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            przejdzDalej();
                        }
                    })
                    .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            pustePole="";
                        }
                    });
            AlertDialog alert = a_builder.create();
            alert.setTitle("Ostrzeżenie");
            alert.show();
        }else {
            przejdzDalej();
        }


    }

    public void przejdzDalej(){
        Invoice invoice = (Invoice) getIntent().getSerializableExtra("invoice");
        Buyer buyer = new Buyer();

        buyer.buyerAppartment=ed_lokalNabyw.getText().toString();
        buyer.buyerCity=ed_miejscowoscNabyw.getText().toString();
        buyer.buyerHouse=ed_domNabyw.getText().toString();
        buyer.buyerName=ed_nazwaNabyw.getText().toString();
        buyer.buyerPostalCode=ed_kodNabyw.getText().toString();
        buyer.buyerStreet=ed_ulicaNabyw.getText().toString();

        invoice.buyer = buyer;

        invoice.summary = new Summary();

        invoice.summary.gross = 0;
        invoice.summary.net = 0;
        invoice.summary.vat = 0;
        invoice.summary.tax0 = new Tax0();
        invoice.summary.tax0.brutto=0;
        invoice.summary.tax0.netto=0;
        invoice.summary.tax0.VAT=0;
        invoice.summary.tax5= new Tax5();
        invoice.summary.tax5.brutto=0;
        invoice.summary.tax5.netto=0;
        invoice.summary.tax5.VAT=0;
        invoice.summary.tax8 = new Tax8();
        invoice.summary.tax8.brutto=0;
        invoice.summary.tax8.netto=0;
        invoice.summary.tax8.VAT=0;
        invoice.summary.tax23 = new Tax23();
        invoice.summary.tax23.brutto=0;
        invoice.summary.tax23.netto=0;
        invoice.summary.tax23.VAT=0;

        Intent intent = new Intent(this, GoodsFormActivity.class);
        intent.putExtra("invoice", invoice);
        startActivity(intent);
    }

    private boolean NIP(String pole){
        String NIP_PATTERN = "^[0-9]{10}$";
        Pattern pattern = Pattern.compile(NIP_PATTERN);
        Matcher matcher = pattern.matcher(pole);
        return matcher.matches();
    }
    private boolean niePuste(String pole){
        String NIEPUSTE_PATTERN = "^\\S.*$";
        Pattern pattern = Pattern.compile(NIEPUSTE_PATTERN);
        Matcher matcher = pattern.matcher(pole);
        return matcher.matches();
    }
    private boolean kodPocztowy(String pole){
        String KOD_PATTERN = "^\\d{2}-\\d{3}$";
        Pattern pattern = Pattern.compile(KOD_PATTERN);
        Matcher matcher = pattern.matcher(pole);
        return matcher.matches();
    }

    public void daneGUS(View view) {
        if(!NIP(ed_NIpNabyw.getText().toString())){
            ed_NIpNabyw.setError("Wymagane 10 cyfr");
        }else {
            GetFromApiTask task = new GetFromApiTask(this);
            task.execute(ed_NIpNabyw.getText().toString());
        }
    }

    @Override
    public void update(JSONObject jsonObject) {
        try {
            String nazwa = jsonObject.getString("krs_podmioty.nazwa");
            String ulica = jsonObject.getString("krs_podmioty.adres_ulica");
            String numerBudynku = jsonObject.getString("krs_podmioty.adres_numer");
            String numerLokalu = jsonObject.getString("krs_podmioty.adres_lokal");
            String miejscowosc = jsonObject.getString("krs_podmioty.adres_miejscowosc");
            String kod = jsonObject.getString("krs_podmioty.adres_kod_pocztowy");

            if (nazwa != null) {
                this.getEd_nazwaNabyw().setText(nazwa);
            }
            if (ulica != null) {
                this.getEd_ulicaNabyw().setText(ulica);
            }
            if (numerBudynku != null) {
                this.getEd_domNabyw().setText(numerBudynku);
            }
            if (numerLokalu != null) {
                this.getEd_lokalNabyw().setText(numerLokalu);
            }
            if (miejscowosc != null) {
                this.getEd_miejscowoscNabyw().setText(miejscowosc);
            }
            if (kod != null) {
                this.getEd_kodNabyw().setText(kod);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateFailure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Błąd podczas pobierania danych (sprawdź poprawność numeru NIP).")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public EditText getEd_nazwaNabyw() {
        return ed_nazwaNabyw;
    }

    public void setEd_nazwaNabyw(EditText ed_nazwaNabyw) {
        this.ed_nazwaNabyw = ed_nazwaNabyw;
    }

    public EditText getEd_ulicaNabyw() {
        return ed_ulicaNabyw;
    }

    public void setEd_ulicaNabyw(EditText ed_ulicaNabyw) {
        this.ed_ulicaNabyw = ed_ulicaNabyw;
    }

    public EditText getEd_domNabyw() {
        return ed_domNabyw;
    }

    public void setEd_domNabyw(EditText ed_domNabyw) {
        this.ed_domNabyw = ed_domNabyw;
    }

    public EditText getEd_lokalNabyw() {
        return ed_lokalNabyw;
    }

    public void setEd_lokalNabyw(EditText ed_lokalNabyw) {
        this.ed_lokalNabyw = ed_lokalNabyw;
    }

    public EditText getEd_miejscowoscNabyw() {
        return ed_miejscowoscNabyw;
    }

    public void setEd_miejscowoscNabyw(EditText ed_miejscowoscNabyw) {
        this.ed_miejscowoscNabyw = ed_miejscowoscNabyw;
    }

    public EditText getEd_kodNabyw() {
        return ed_kodNabyw;
    }

    public void setEd_kodNabyw(EditText ed_kodNabyw) {
        this.ed_kodNabyw = ed_kodNabyw;
    }

    public EditText getEd_NIpNabyw() {
        return ed_NIpNabyw;
    }

    public void setEd_NIpNabyw(EditText ed_NIpNabyw) {
        this.ed_NIpNabyw = ed_NIpNabyw;
    }

    public String getPustePole() {
        return pustePole;
    }

    public void setPustePole(String pustePole) {
        this.pustePole = pustePole;
    }
}
