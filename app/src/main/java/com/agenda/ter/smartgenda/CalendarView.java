package com.agenda.ter.smartgenda;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.agenda.ter.model.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Classe du widget Calendrier personalisé, créée par SmartgendaTeam.
 * @author Smartgenda Team
 */
public class CalendarView extends LinearLayout
{
    /**Nombre de jour par défaut à afficher dans la grille du calendrier*/
    private static final int DAYS_COUNT = 42;

    /**Format par défaut de la date Mois/Année*/
    private static final String DATE_FORMAT = "MMM yyyy";

    /**Format de la date*/
    private String dateFormat;

    /**Mois courant à afficher*/
    private Calendar currentDate = Calendar.getInstance();

    /**Handler personalisé d'évènements*/
    private EventHandler eventHandler = null;

    /**Liste d'évènements à afficher sur le calendrier*/
    private ArrayList<Event> eventListSet = new ArrayList<>();

    /**layout permettant de regrouper le nom des jours de la semaine*/
    private LinearLayout header;

    /**ImageView du bouton permettant de passer au mois précédent*/
    private ImageView btnPrev;

    /**ImageView du bouton permettant de passer au mois suivant*/
    private ImageView btnNext;

    /**TextView comportant le mois et l'année courramment affiché*/
    private TextView txtDate;

    /**La GridView regroupant les jours du mois affiché*/
    private GridView grid;

    /**Couleurs personalisées pour les saisons*/
    int[] rainbow = new int[] {
            R.color.summer,
            R.color.fall,
            R.color.winter,
            R.color.spring
    };

    /**Tableau des saisons des mois (basé sur le cycle de l'hémisphère nord)*/
    int[] monthSeason = new int[] {2, 2, 3, 3, 3, 0, 0, 0, 1, 1, 1, 2};

    /**Le détector personnalisé permettant de détecter les interections de l'utilisateur avec la grille du calendrier*/
    private GestureDetectorCompat detector;

    public CalendarView(Context context)
    {
        super(context);
    }

    public CalendarView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initControl(context, attrs);
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initControl(context, attrs);
    }

    /**
     * Initialise la vue
     * @param context Le contexte courant
     * @param attrs La liste d'attribut de la vue
     */
    private void initControl(Context context, AttributeSet attrs)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.control_calendar, this);

        loadDateFormat(attrs);
        assignUiElements();
        assignClickHandlers();
    }

    /**
     * Charge le format de la vue à partir de la liste d'attributs passé en paramêtre
     * @param attrs Liste des attributs de la vue
     */
    private void loadDateFormat(AttributeSet attrs)
    {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);

        try
        {
            dateFormat = ta.getString(R.styleable.CalendarView_dateFormat);
            if (dateFormat == null)
                dateFormat = DATE_FORMAT;
        }
        finally
        {
            ta.recycle();
        }
    }

    /**
     * Instancie les composants graphiques de la vue
     */
    private void assignUiElements()
    {
        // layout is inflated, assign local variables to components
        header = (LinearLayout)findViewById(R.id.calendar_header);
        btnPrev = (ImageView)findViewById(R.id.calendar_previous_month_button);
        btnNext = (ImageView)findViewById(R.id.calendar_next_month_button);
        txtDate = (TextView)findViewById(R.id.calendar_date_display);
        grid = (GridView)findViewById(R.id.calendar_grid);
    }

    /**
     * Assigne les différents handlers de pression aux composants de la vue ainsi que les méthodes à appeler en cas d'appuie
     */
    private void assignClickHandlers()
    {
        txtDate.setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v) {
                showDialogDateSelector();
                return true;
            }
        });

        // add one month and refresh UI
        btnNext.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        // subtract one month and refresh UI
        btnPrev.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                currentDate.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        // long-pressing a day
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {

            @Override
            public boolean onItemLongClick(AdapterView<?> view, View cell, int position, long id)
            {
                // handle long-press
                if (eventHandler == null)
                    return false;

                eventHandler.onDayLongPress((Date)view.getItemAtPosition(position));
                return true;
            }
        });

        detector = new GestureDetectorCompat(getContext(), new CalendarGestureListener());
        grid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    /**
     * Met à jour le calendrier (Rafraichis la vue en métant à jour le mois et l'année à afficher ainsi que les jours possédant des évènements)
     */
    public void updateCalendar()
    {
        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells
        while (cells.size() < DAYS_COUNT)
        {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        grid.setAdapter(new CalendarAdapter(getContext(), cells));

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.FRANCE);
        txtDate.setText(sdf.format(currentDate.getTime()));

        // set header color according to current season
        int month = currentDate.get(Calendar.MONTH);
        int season = monthSeason[month];
        int color = rainbow[season];

        header.setBackgroundColor(ContextCompat.getColor(getContext(), color));
    }


    /**
     * Adapter personnalisé de la grille du Calendrier
     */
    private class CalendarAdapter extends ArrayAdapter<Date>
    {
        // for view inflation
        private LayoutInflater inflater;

        public CalendarAdapter(Context context, ArrayList<Date> days)
        {
            super(context, R.layout.control_calendar_day, days);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent)
        {
            // day in question
            Date date = getItem(position);
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();

            // today
            Date today = new Date();

            // inflate item if it does not exist yet
            if (view == null)
                view = inflater.inflate(R.layout.control_calendar_day, parent, false);

            //Get the cell view
            TextView cell = ((TextView)view);

            //clear styling
            cell.setTypeface(null, Typeface.NORMAL);
            cell.setTextColor(Color.BLACK);

            //grey out all days before the current day
            if (    year < today.getYear() ||
                    month < today.getMonth() && year <= today.getYear() ||
                    day < today.getDate() && month == today.getMonth() && year <= today.getYear())
            {

                cell.setTextColor(ContextCompat.getColor(getContext(),R.color.greyed_out));
            }
            //set the color of the current day at blue and style as bold
            else if (day == today.getDate() && month == today.getMonth() && year == today.getYear())
            {
                cell.setTypeface(null, Typeface.BOLD);
                cell.setTextColor(ContextCompat.getColor(getContext(),R.color.today));
            }

            if(position >= monthBeginningCell() && position <= monthBeginningCell()+ getMonthNumberOfDays() -1)
                cell.setText(String.valueOf(date.getDate()));
            else
                cell.setText(" ");

            // if this day has an event, specify event image
            view.setBackgroundResource(0);
            for (Event event : eventListSet)
            {
                Date eventDate = event.getmEventDate();
                if (eventDate.getDate() == day &&
                        eventDate.getMonth() == month &&
                        eventDate.getYear() == year)
                {
                    // mark this day for event
                    cell.setTextColor(Color.GREEN);
                    break;
                }
            }

            return view;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public boolean isEnabled(int position) {
            // day in question
            Date date = getItem(position);
            int day = date.getDate();
            int month = date.getMonth();
            int year = date.getYear();

            // today
            Date today = new Date();
            //grey out all days before the current day
            if (    year < today.getYear() ||
                    month < today.getMonth() && year <= today.getYear() ||
                    day < today.getDate() && month == today.getMonth() && year <= today.getYear())
            {

                return false;
            }
            //set the color of the current day at blue and style as bold
            else if (day == today.getDate() && month == today.getMonth() && year == today.getYear())
            {
                return true;
            }

            if(position >= monthBeginningCell() && position <= monthBeginningCell()+ getMonthNumberOfDays() -1)
                return true;
            else
                return false;
        }
    }

    /**
     * Instance le event Handler en cas de création d'évènement
     */
    public void setEventHandler(EventHandler eventHandler)
    {
        this.eventHandler = eventHandler;
    }

    /**
     * Interface déclarant les méthodes à appeler par les élements de type EventHandler
     */
    public interface EventHandler
    {
        void onDayLongPress(Date date);
    }

    /**
     * Classe du détector personnalisé. Elle gère les intérections de swipe sur le Calendrier.
     */
    private class CalendarGestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,float velocityX, float velocityY) {
            float diffX = event2.getX() - event1.getX();
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
            }
            return true;
        }

        private void onSwipeLeft() {
            currentDate.add(Calendar.MONTH, 1);
            updateCalendar();
        }

        private void onSwipeRight() {
            currentDate.add(Calendar.MONTH, -1);
            updateCalendar();
        }
    }

    /**
     * Méthode calculant le premier jour de la semaine d'un Calendrier.
     * @return Le nombre de cellules à déclarée "vide" avant d'afficher le premier jour du mois
     */
    public int monthBeginningCell(){
        Calendar calendar = (Calendar)currentDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }

    /**
     * Le nombre de jour dans un mois
     * @return Le nombre de jour dans un mois
     */
    public int getMonthNumberOfDays(){
        Calendar calendar = (Calendar)currentDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Methodé affichant un Calendrier Android après une pression longue sur le mois et l'année du calendrier. Permet à l'utilsiateur de choisir plus facilement une date.
     */
    public void showDialogDateSelector(){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                currentDate.set(Calendar.MONTH,monthOfYear);
                currentDate.set(Calendar.YEAR,year);
                updateCalendar();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    public ArrayList<Event> getEventListSet() {
        return eventListSet;
    }

    public void setEventListSet(ArrayList<Event> eventListSet) {
        this.eventListSet = eventListSet;
    }
}
