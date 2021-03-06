package uek.krakow.pl.androidinvoicegenerator.viewcontroller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import uek.krakow.pl.androidinvoicegenerator.R;

public class MainActivity extends AppCompatActivity {
    public static File dataDir;
    public static File stylesDir;
    public static File invoicesDir;
    public static File fontsDir;
    ArrayAdapter<String> adapterInv;
    ArrayList<String> invoices;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeListView();

        invoicesDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/invoice_generator", "invoices");
        stylesDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/invoice_generator", "invoice_styles");
        dataDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/invoice_generator", "invoice_data");
        fontsDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/invoice_generator", "fonts");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            createDirs();
            addStylesToDir();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToShare(parent.getItemAtPosition(position).toString());
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();

        initializeListView();

        if (invoicesDir.exists()) {
            fillInvoicesList();
            adapterInv.notifyDataSetChanged();
        }
    }

    public void add(View view) {
        Intent intent = new Intent(this, FormActivity.class);
        startActivity(intent);
    }

    public void addFromExistingData(View view) {
        Intent intent = new Intent(this, DataActivity.class);
        startActivity(intent);
    }

    public void goToPrefs(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void goToShare(String filename) {
        Intent intent = new Intent(this, Share2Activity.class);
        intent.putExtra("invoice", filename);
        startActivity(intent);
    }

    private void createDirs() {
        if (!invoicesDir.exists()) {
            if (!invoicesDir.mkdirs()) {
                Log.d("hehe", "failed");
            }
        }

        if (!stylesDir.exists()) {
            if (!stylesDir.mkdirs()) {
                Log.d("hehe", "failed");
            }
        }

        if (!dataDir.exists()) {
            if (!dataDir.mkdirs()) {
                Log.d("hehe", "failed");
            }
        }

        if (!fontsDir.exists()) {
            if (!fontsDir.mkdirs()) {
                Log.d("hehe", "failed");
            }
        }
    }

    private void initializeListView() {
        invoices = new ArrayList<>();
        adapterInv = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, invoices);
        listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapterInv);
    }

    private void addStylesToDir() {
        InputStream fileInputStream1 = getResources().openRawResource(R.raw.invoice_style);
        InputStream fileInputStream2 = getResources().openRawResource(R.raw.invoice_style2);
        InputStream fileInputStream3 = getResources().openRawResource(R.raw.invoice_style3);
        InputStream fileInputStream4 = getResources().openRawResource(R.raw.invoice_style4);


        FileOutputStream fo;
        try {


            fo = new FileOutputStream(new File(stylesDir, "default1.xsl"));
            IOUtils.copy(fileInputStream1, fo);

            fo = new FileOutputStream(new File(stylesDir, "default2.xsl"));
            IOUtils.copy(fileInputStream2, fo);

            fo = new FileOutputStream(new File(stylesDir, "default3.xsl"));
            IOUtils.copy(fileInputStream3, fo);

            fo = new FileOutputStream(new File(stylesDir, "default4.xsl"));
            IOUtils.copy(fileInputStream4, fo);

            InputStream fi1 = getAssets().open("Roboto-Regular.ttf");
            InputStream fi2 = getAssets().open("Roboto-Bold.ttf");

            fo = new FileOutputStream(new File(fontsDir, "Roboto-Regular.ttf"));
            IOUtils.copy(fi1, fo);

            fo = new FileOutputStream(new File(fontsDir, "Roboto-Bold.ttf"));
            IOUtils.copy(fi2, fo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fillInvoicesList() {
        for (File f : invoicesDir.listFiles()
                ) {
            if (FilenameUtils.getExtension(f.getName()).equals("pdf") && !invoices.contains(f.getName())) {
                invoices.add(f.getName());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createDirs();
                    fillInvoicesList();
                    addStylesToDir();
                }
                //...
            }
        }
    }
}
