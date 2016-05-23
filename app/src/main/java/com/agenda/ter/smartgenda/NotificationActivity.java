package com.agenda.ter.smartgenda;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.agenda.ter.database.NotificationContract;
import com.agenda.ter.database.ReminderContract;
import com.agenda.ter.database.SmartgendaDbHelper;
import com.agenda.ter.model.SmartNotification;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author SmartGenda Team
 * NotificationActivity la calsse qui permet la personnalisation des notifications
 */
public class NotificationActivity extends AppCompatActivity {

    /** La variable qui récupére le chemain de la sonnerie de la notification*/
    public static String chemin_son;
    /** le dialogue ou on affiche toutes les sonnerie du terminal */
    private AlertDialog alertDialog;
    /** La liste qui enregistre les rappels*/
    private ArrayList<String> listRappel;
    /** une liste temporaire pour enregistre les rappels*/
    private ArrayList<String> listRappelTemp;
    /** L'adapter pour l'affichage des rappels dans une ListView*/
    private MyCustomAdapter adapter;
    /** Une ArrayList qui enregistre les sonneries*/
    private ArrayList<HashMap<String, String>> songsList;
    /** Enregistre la couleur choisie dans le Spinner des couleur */
    private String selectedColor;
    private static String selectedRappel="" ;
    private int codeCouleurR=0;
    private int codeCouleurG=0;
    private int codeCouleurB=255;
    /** La zone de texte pour le nom de la notification*/
    private EditText notificationNameEditText;
    /** Enregistre le nom de la notification*/
    private String notifName;
    /** L'instance du dbhelper de la base de données*/
    private SmartgendaDbHelper dbHelper;
    /** Liste ou en récupére les notifications personnalisées*/
    public static ArrayList<SmartNotification> listNotifPerso ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        dbHelper = new SmartgendaDbHelper(getApplicationContext());

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); // Enlever le focus

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ListView listView = (ListView) findViewById(R.id.listView_id);
        notificationNameEditText = (EditText) findViewById(R.id.notification_edit_text_name_id);
        listNotifPerso = new ArrayList<>();

        listRappel = new ArrayList<String>();
        listRappelTemp = new ArrayList<String>();
        adapter = new MyCustomAdapter(listRappel, this);
        listView.setAdapter(adapter);

        //Spinner Color
        Spinner spinner = (Spinner) findViewById(R.id.spinnerColor);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.array_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedColor = (String) parent.getItemAtPosition(position);
                switch (selectedColor){
                    case "Bleu" : codeCouleurR =0 ; codeCouleurG =0 ;codeCouleurB =255 ; break;
                    case "Rouge" : codeCouleurR =255 ; codeCouleurG =0 ;codeCouleurB =0 ; break;
                    case "Vert" : codeCouleurR =0 ; codeCouleurG =255 ;codeCouleurB =0 ; break;
                    case "Orange" : codeCouleurR =237 ; codeCouleurG =127 ;codeCouleurB =16 ; break;
                    case "Violet" : codeCouleurR =136 ; codeCouleurG =6 ;codeCouleurB =206 ; break;

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Spinner Rappel
        Spinner spinnerRappel = (Spinner) findViewById(R.id.spinneRappel);
        ArrayAdapter<CharSequence> adapterRappel = ArrayAdapter.createFromResource(
                this, R.array.array_rappel, android.R.layout.simple_spinner_item);
        adapterRappel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRappel.setAdapter(adapterRappel);
        spinnerRappel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRappel = (String) parent.getItemAtPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    /**
     * La méthode qui enregistre une notification dans la base de données
     * @param view
     */
    public void saveNotification(View view){
        notifName = notificationNameEditText.getText().toString();
        if(notifName .equals("") || notifName==null ) {Toast.makeText(getApplicationContext(), "Entrer le nom de la notification !!", Toast.LENGTH_SHORT).show(); return;}
        if(listRappel.size() == 0){Toast.makeText(getApplicationContext(), "Choisir au moins un rappel !!", Toast.LENGTH_SHORT).show();return;}
        new InsertNotificationTask(this).execute(
                notifName,
                String.valueOf(codeCouleurR),
                String.valueOf(codeCouleurG),
                String.valueOf(codeCouleurB),
                chemin_son
        );
        Toast.makeText(getApplicationContext(), "Notification enegistrer avec succée ", Toast.LENGTH_SHORT).show();

    }

    /**
     * Permet de fermer la page des notifications
     * @param view
     */
    public void CloseSonWindow(View view) {

        alertDialog.dismiss();
    }

    /**
     * Ajoute un rappel dans la liste des rappels
     * @param v
     */
    public void AddToList(View v) {

        String newItem = selectedRappel;

        if(newItem == ""){Toast.makeText(getApplicationContext(), "Choisir un rappel !!", Toast.LENGTH_SHORT).show();}
        else{
            listRappel.add("Rappel avant : "+newItem);
            listRappelTemp.add(newItem);
            adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Rappel avant : "+newItem, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Récupére et affiche les sonnerie du terminal
     * @param view
     */
    public void browseAudioFiles(View view) {

        ArrayList<String> namess=new ArrayList<>();
        final ArrayList<String> listeSonPath=new ArrayList<>();
        String value = null;
        songsList = new ArrayList<>();
        String[] STAR = { "*" };

        Cursor cursor;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        cursor = managedQuery(uri, STAR, selection, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String songName = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));


                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));


                    String albumName = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    int albumId = cursor
                            .getInt(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    HashMap<String, String> song = new HashMap<String, String>();
                    song.put("songTitle",albumName+" "+songName+"___"+albumId);
                    song.put("songPath",path );
                    songsList.add(song);

                } while (cursor.moveToNext());

            }

        }

        for(int i = 0; i < songsList.size();i++){
            HashMap<String,String> h = songsList.get(i);
            for (String name: h.keySet()){

                String key =name.toString();
                 value = h.get(name).toString();
                if(key.equals("songPath")) {
                    String[] parts = value.split("/");
                    namess.add(parts[parts.length - 1]);
                    listeSonPath.add(value);
                }
            }
        }

        alertDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.custom, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Son de notification");
        ListView listViewSon = (ListView) convertView.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,namess);
        listViewSon.setAdapter(adapter);
        alertDialog.show();


        // Select Item in listView
    final ListView listTemp = listViewSon;
        listViewSon.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = (String) listTemp.getItemAtPosition(position);

                for (int i=0;i<parent.getChildCount();i++){
                    parent.getChildAt(i).setBackgroundColor(Color.rgb(238,240,231));
                }
                view.setBackgroundColor(Color.rgb(204,233,243));

                Toast.makeText(getApplicationContext(), "Sonnerie : "+item, Toast.LENGTH_SHORT).show();
                chemin_son = listeSonPath.get(position);
            }
        });

    }

    /**
     *
     * Récupére le chemin de la sonnerie choisie dans la liste des sonneries
     */
    public void GetSon(View v){

        TextView sonPath = (TextView) findViewById(R.id.textView_sonPath_id);
        String[] parts = chemin_son.split("/");
        String titreSonnerie = parts[parts.length - 1];
        String titreSonnerieAfficher="";
       try{
           for (int i=0 ; i<=15 ; i++){
               titreSonnerieAfficher = titreSonnerieAfficher+titreSonnerie.charAt(i);
           }
       }catch(Exception e){}

        sonPath.setText(titreSonnerieAfficher+"...");
        alertDialog.dismiss();
    }

    public class MyCustomAdapter extends BaseAdapter implements ListAdapter {
        private ArrayList<String> list = new ArrayList<String>();
        private Context context;

        public MyCustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.reminder_list_text_view);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.reminder_list_delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
    /**
     * Classe qui permet l'insertion d'une notification dans la base de données
     */
    public class InsertNotificationTask extends AsyncTask<String, Void, Void> {

        long newRowId;
        SmartNotification newNotif;

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;
        /** application context. */
        private NotificationActivity activity;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        public InsertNotificationTask(NotificationActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            dialog.setMessage("Progress start");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            ContentValues values = new ContentValues();

            values.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_NAME,params[0]);
            values.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_RED,params[1]);
            values.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_GREEN,params[2]);
            values.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_COLOR_BLUE,params[3]);
            values.put(NotificationContract.NotificationEntry.COLUMN_NAME_NOTIFICATION_SOUND,params[4]);


           newRowId = db.insert(
                    NotificationContract.NotificationEntry.TABLE_NAME,
                    null,
                    values);

            String nom = params[0];
            int couleurR = Integer.valueOf(params[1]);
            int couleurG = Integer.valueOf(params[2]);
            int couleurB = Integer.valueOf(params[3]);
            String sound = params[4];

            newNotif=new SmartNotification((int)newRowId,nom,couleurR,couleurG,couleurB);
            listNotifPerso.add(newNotif);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            for(int i=0 ; i<listRappelTemp.size() ; i++){
                String rappel = listRappelTemp.get(i);
                String[] rap = rappel.split(" ");
                new InsertReminderTask(NotificationActivity.this).execute(String.valueOf(Integer.valueOf(rap[0])*3600000),String.valueOf(newRowId));
            }
        }
    }
    /**
     * Classe qui permet l'insertion des rappels dans la base de données
     */
    public class InsertReminderTask extends AsyncTask<String, Void, Void> {

        /** progress dialog to show user that the backup is processing. */
        private ProgressDialog dialog;
        /** application context. */
        private NotificationActivity activity;

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        public InsertReminderTask(NotificationActivity activity) {
            this.activity = activity;
            dialog = new ProgressDialog(activity);
        }

        protected void onPreExecute() {
            dialog.setMessage("Progress start");
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            ContentValues values = new ContentValues();

            values.put(ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_TIME,params[0]);
            values.put(ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_DISPLAY_MAP,0);
            values.put(ReminderContract.ReminderEntry.COLUMN_NAME_REMINDER_NOTIFICATION_ID,params[1]);


            db.insert(
                    ReminderContract.ReminderEntry.TABLE_NAME,
                    null,
                    values);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            dbHelper.close();
            //Toast.makeText(activity,"Notification ajoutée avec succés !",Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

}
