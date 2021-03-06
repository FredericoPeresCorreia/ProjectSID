package com.example.sid2020.APP;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.sid2020.APP.Connection.ConnectionHandler;
import com.example.sid2020.APP.Database.DatabaseHandler;
import com.example.sid2020.APP.Database.DatabaseReader;
import com.example.sid2020.APP.Helper.UserLogin;
import com.example.sid2020.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class AlertasGlobaisActivity extends AppCompatActivity {

    private static final String IP = UserLogin.getInstance().getIp();
    private static final String PORT = UserLogin.getInstance().getPort();
    private static final String username= UserLogin.getInstance().getUsername();
    private static final String password = UserLogin.getInstance().getPassword();
    DatabaseHandler db = new DatabaseHandler(this);
    String getAlertasGlobais = "http://" + IP + ":" + PORT + "/scripts/getAlertasGlobais.php";
    int year;
    int month;
    int day;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alertas_globais);

        if (getIntent().hasExtra("date")){
            int[] yearMonthDay = getIntent().getIntArrayExtra("date");
            year = yearMonthDay[0];
            month= yearMonthDay[1];
            day=yearMonthDay[2];
        }
        else{
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH)+1;
            day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        }
        dateToString();
        getAlertas();
        listAlertas();
    }


    private void dateToString() {
       String yearString = Integer.toString(year);
       String monthString ="";
       String dayString="";
        if (month<10){
            monthString="0"+Integer.toString(month);
        }else{
            monthString=Integer.toString(month);
        }
        if(day<10){
            dayString="0"+Integer.toString(day);
        }
        else{
            dayString=Integer.toString(day);
        }
        date = yearString+"-"+monthString+"-"+dayString;
        String formatted_date = dayString+"-"+monthString+"-"+yearString;
        TextView stringData = findViewById(R.id.diaSelecionado_tv);
        stringData.setText(formatted_date);

    }

    public void showDatePicker(View v) {
        Intent intent = new Intent(this,DatePickerActivity.class);
        intent.putExtra("global",1);
        startActivity(intent);
        finish();
    }

    private void getAlertas() {
        db.clearAlertasGlobais();
        HashMap<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("date",date);
        ConnectionHandler jParser = new ConnectionHandler();
        JSONArray medicoesTemperatura = jParser.getJSONFromUrl(getAlertasGlobais, params);
        try{
            if(medicoesTemperatura!=null){
                for (int i=0; i < medicoesTemperatura.length();i++){
                    JSONObject c = medicoesTemperatura.getJSONObject(i);
                    String dataHoraMedicao = c.getString("DataHoraMedicao");
                    String tipoSensor = c.getString("TipoSensor");
                    String valorMedicao = c.getString("ValorMedicao");
                    String limite = c.getString("Limite");
                    String descricao = c.getString("Descricao");
                    db.insert_alertaGlobal(descricao,dataHoraMedicao,tipoSensor,valorMedicao,limite);
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void listAlertas() {
        SharedPreferences sp = getApplicationContext().getSharedPreferences("appPref", MODE_PRIVATE);
        int mostRecentEntry = 0;

        TableLayout table = findViewById(R.id.tableAlertas);

        DatabaseReader dbReader = new DatabaseReader(db);
        Cursor cursorAlertasGlobais = dbReader.readAlertasGlobais();
        table.removeAllViewsInLayout();
        TableRow headerRow = new TableRow(this);
        headerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

        TextView headerHora = new TextView(this);
        headerHora.setText("Hora");
        headerHora.setTextSize(16);
        headerHora.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerTipoSensor = new TextView(this);
        headerTipoSensor.setText("Tipo Sensor");
        headerTipoSensor.setTextSize(16);
        headerTipoSensor.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerValorMedicao = new TextView(this);
        headerValorMedicao.setText("Valor Medicao");
        headerValorMedicao.setTextSize(16);
        headerValorMedicao.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerLimite = new TextView(this);
        headerLimite.setText("Limite");
        headerLimite.setTextSize(16);
        headerLimite.setPadding(dpAsPixels(16),dpAsPixels(50),0,10);

        TextView headerDescricao = new TextView(this);
        headerDescricao.setText("Descricao");
        headerDescricao.setTextSize(16);
        headerDescricao.setPadding(dpAsPixels(16),dpAsPixels(50),dpAsPixels(5),10);


        headerRow.addView(headerDescricao);
        headerRow.addView(headerHora);
        headerRow.addView(headerTipoSensor);
        headerRow.addView(headerValorMedicao);
        headerRow.addView(headerLimite);


        table.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        while (cursorAlertasGlobais.moveToNext()){
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            TextView descricao = new TextView(this);
            String valorDescricao = cursorAlertasGlobais.getString(cursorAlertasGlobais.getColumnIndex("Descricao"));
            if (valorDescricao == null || valorDescricao.equals("null")) valorDescricao = "";
            descricao.setText(valorDescricao);
            descricao.setPadding(dpAsPixels(16),dpAsPixels(5),dpAsPixels(5),0);
            TextView hora = new TextView(this);
            String horaDesformatado = cursorAlertasGlobais.getString(cursorAlertasGlobais.getColumnIndex("DataHoraMedicao"));
            String horaFormatado = horaDesformatado.split(" ")[1];
            hora.setText(horaFormatado);
            hora.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView tipoSensor = new TextView(this);
            String valorTipoSensor = cursorAlertasGlobais.getString(cursorAlertasGlobais.getColumnIndex("TipoSensor"));
            if (valorTipoSensor == null || valorTipoSensor.equals("null")) valorTipoSensor = "";
            if(valorTipoSensor.equals("tmp")) valorTipoSensor = "Temperatura";
            if(valorTipoSensor.equals("hum")) valorTipoSensor = "Humidade";
            if(valorTipoSensor.equals("mov")) valorTipoSensor = "Movimento";
            if(valorTipoSensor.equals("lum")) valorTipoSensor = "Luminosidade";
            tipoSensor.setText(valorTipoSensor);

            tipoSensor.setPadding(dpAsPixels(16),dpAsPixels(5),dpAsPixels(5),0);

            TextView valorMedicao = new TextView(this);
            String valor = Double.toString(cursorAlertasGlobais.getDouble(cursorAlertasGlobais.getColumnIndex("ValorMedicao")));
            if (valor.equals("-1000.0"))valor = "";
            valorMedicao.setText(valor);
            valorMedicao.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);

            TextView limite = new TextView(this);
            String valorLimite = cursorAlertasGlobais.getString(cursorAlertasGlobais.getColumnIndex("Limite"));
            if (valorLimite.equals("-1000.0")) valorLimite = "";
            limite.setText(valorLimite);
            limite.setPadding(dpAsPixels(16),dpAsPixels(5),0,0);



            String intHora = horaFormatado.replace(":", "");
            int newHora = Integer.parseInt(intHora);
            if (newHora > mostRecentEntry) mostRecentEntry = newHora;
            if (newHora > sp.getInt("timePref", 0)) {
                if (sp.getInt("refreshPref", 1) == 0) {
                    hora.setTextColor(Color.RED);
                    tipoSensor.setTextColor(Color.RED);
                    valorMedicao.setTextColor(Color.RED);
                    limite.setTextColor(Color.RED);
                    descricao.setTextColor(Color.RED);

                }
            }
            row.addView(descricao);
            row.addView(hora);
            row.addView(tipoSensor);
            row.addView(valorMedicao);
            row.addView(limite);


            table.addView(row, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        }
        SharedPreferences.Editor editor = sp.edit().putInt("timePref", mostRecentEntry);
        editor.apply();
        SharedPreferences.Editor editor2 = sp.edit().putInt("refreshPref", 0);
        editor2.apply();
    }

    private int dpAsPixels(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp*scale + 0.5f);
    }

    public void refreshButton(View v) {
        getAlertas();
        listAlertas();
    }

    public void medicoesTemperatura(View v) {
        Intent i = new Intent(this, MedicoesTemperaturaActivity.class);
        startActivity(i);
    }
}
